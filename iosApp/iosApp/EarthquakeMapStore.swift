import Foundation
import Shared

@MainActor
final class EarthquakeMapStore: ObservableObject {
    @Published private(set) var state: EarthquakeMapState

    private let controller: EarthquakeMapController
    private var observation: Observation?

    init(application: SharedApplication) {
        controller = application.makeEarthquakeMapController()
        state = controller.currentState
        observation = controller.observe { [weak self] state in
            Task { @MainActor [weak self] in
                self?.state = state
            }
        }
    }

    func selectEarthquake(id: String) {
        controller.selectEarthquake(id: id)
    }

    func dismissSelection() {
        controller.dismissSelection()
    }

    deinit {
        observation?.cancel()
        controller.close()
    }
}
