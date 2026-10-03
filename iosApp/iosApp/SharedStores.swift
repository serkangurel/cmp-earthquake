import Foundation
import Shared

@MainActor
final class EarthquakeOverviewStore: ObservableObject {
    @Published private(set) var state: EarthquakeOverviewState

    private let controller: EarthquakeOverviewController
    private var observation: Observation?

    init(application: SharedApplication) {
        controller = application.makeEarthquakeOverviewController()
        state = controller.currentState
        observation = controller.observe { [weak self] state in
            Task { @MainActor [weak self] in
                self?.state = state
            }
        }
    }

    func refresh() async {
        await withCheckedContinuation { continuation in
            controller.refresh {
                continuation.resume()
            }
        }
    }

    func earthquakeDisplayed(id: String) {
        controller.onEarthquakeDisplayed(id: id)
    }

    func select(country: CountryOption) {
        controller.selectCountry(countryCode: country.code)
    }

    func select(magnitude: MagnitudeThreshold) {
        controller.selectMagnitude(magnitude: magnitude)
    }

    deinit {
        observation?.cancel()
        controller.close()
    }
}

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
