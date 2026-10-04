import Shared
import SwiftUI

extension AppTheme {
    var titleKey: LocalizedStringKey {
        self == .dark ? "theme_dark" : "theme_light"
    }

    var colorScheme: ColorScheme {
        self == .dark ? .dark : .light
    }
}

extension EarthquakeTimeRange {
    var titleKey: LocalizedStringKey {
        switch self {
        case .lastDay:
            return "time_range_last_day"
        case .lastMonth:
            return "time_range_last_month"
        default:
            return "time_range_last_week"
        }
    }
}

extension AboutLink {
    var titleKey: LocalizedStringKey {
        switch self {
        case .usgsCopyrightsAndCredits:
            return "about_usgs_copyrights_and_credits"
        case .usgsPrivacyPolicy:
            return "about_usgs_privacy_policy"
        default:
            return "about_usgs_earthquake_hazards"
        }
    }
}

extension SettingsState {
    var defaultFiltersSummary: String {
        let magnitude = preferences.defaultMagnitude.label
        guard let country = defaultCountry?.name else { return magnitude }
        return String(
            format: NSLocalizedString("settings_default_filters_summary", comment: ""),
            magnitude,
            country
        )
    }
}

enum AppVersion {
    static var current: String {
        Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString") as? String ?? ""
    }
}
