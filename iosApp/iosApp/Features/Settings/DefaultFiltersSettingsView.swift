import Shared
import SwiftUI

struct DefaultFiltersSettingsView: View {
    @EnvironmentObject private var store: SettingsStore

    var body: some View {
        Form {
            Section("minimum_magnitude") {
                ForEach(store.state.magnitudeOptions, id: \.self) { magnitude in
                    SettingsOptionRow(
                        title: Text(verbatim: magnitude.label),
                        isSelected: magnitude == store.state.preferences.defaultMagnitude
                    ) {
                        store.select(defaultMagnitude: magnitude)
                    }
                }
            }
            Section {
                NavigationLink(value: NavigationRoute(key: SettingsRoutes.DefaultCountry.shared.key)) {
                    HStack(spacing: 16) {
                        if let country = store.state.defaultCountry {
                            Text(country.flag)
                                .accessibilityHidden(true)
                            Text(country.name)
                        } else {
                            Text("select_country")
                        }
                    }
                }
            } header: {
                Text("country")
            } footer: {
                Text("settings_default_filters_footer")
            }
        }
        .navigationTitle("settings_default_filters")
        .navigationBarTitleDisplayMode(.inline)
    }
}
