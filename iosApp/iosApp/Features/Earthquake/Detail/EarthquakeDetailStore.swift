import Foundation
import Shared

@MainActor
final class EarthquakeDetailStore: ObservableObject {
    @Published private(set) var state: EarthquakeDetailState

    private let controller: EarthquakeDetailController
    private var observation: Observation?

    init(application: SharedApplication, earthquakeID: String) {
        controller = application.makeEarthquakeDetailController(earthquakeId: earthquakeID)
        state = controller.currentState
        observation = controller.observe { [weak self] state in
            Task { @MainActor [weak self] in
                self?.state = state
            }
        }
    }

    deinit {
        observation?.cancel()
        controller.close()
    }
}
