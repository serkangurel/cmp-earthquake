import Foundation
import Shared

@MainActor
final class SettingsStore: ObservableObject {
    @Published private(set) var state: SettingsState

    private let application: SharedApplication
    private let controller: SettingsController
    private var observation: Observation?

    init(application: SharedApplication) {
        self.application = application
        controller = application.makeSettingsController()
        state = controller.currentState
        observation = controller.observe { [weak self] state in
            Task { @MainActor [weak self] in
                self?.state = state
            }
        }
    }

    func select(theme: AppTheme) {
        controller.selectTheme(theme: theme)
    }

    func select(defaultMagnitude: MagnitudeThreshold) {
        controller.selectDefaultMagnitude(magnitude: defaultMagnitude)
    }

    func select(defaultCountry: CountryOption) {
        controller.selectDefaultCountry(countryCode: defaultCountry.code)
    }

    func select(timeRange: EarthquakeTimeRange) {
        controller.selectTimeRange(timeRange: timeRange)
    }

    func filterCountries(query: String) -> [CountryOption] {
        application.filterCountries(countries: state.countries, query: query)
    }

    deinit {
        observation?.cancel()
        controller.close()
    }
}
