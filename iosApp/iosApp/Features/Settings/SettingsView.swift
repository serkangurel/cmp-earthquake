import Shared
import SwiftUI

struct SettingsView: View {
    @EnvironmentObject private var store: SettingsStore
    @Environment(\.colorScheme) private var colorScheme

    var body: some View {
        Form {
            Section("settings_section_general") {
                SettingsNavigationRow(
                    destination: SettingsRoutes.Appearance.shared,
                    title: "settings_appearance",
                    value: Text(selectedTheme.titleKey)
                )
            }
            Section("settings_section_earthquakes") {
                SettingsNavigationRow(
                    destination: SettingsRoutes.DefaultFilters.shared,
                    title: "settings_default_filters",
                    value: Text(verbatim: store.state.defaultFiltersSummary)
                )
                SettingsNavigationRow(
                    destination: SettingsRoutes.TimeRange.shared,
                    title: "settings_time_range",
                    value: Text(store.state.preferences.timeRange.titleKey)
                )
            }
            Section {
                SettingsNavigationRow(
                    destination: SettingsRoutes.About.shared,
                    title: "settings_about",
                    value: Text(verbatim: AppVersion.current)
                )
            }
        }
        .navigationTitle("settings")
    }

    // While no theme is stored, the environment reports the device appearance.
    private var selectedTheme: AppTheme {
        store.state.preferences.effectiveTheme(isSystemInDarkTheme: colorScheme == .dark)
    }
}
