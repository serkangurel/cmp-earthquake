import Shared
import SwiftUI

struct AppearanceSettingsView: View {
    @EnvironmentObject private var store: SettingsStore
    @Environment(\.colorScheme) private var colorScheme

    var body: some View {
        Form {
            Section {
                ForEach(store.state.themeOptions, id: \.self) { theme in
                    SettingsOptionRow(
                        title: Text(theme.titleKey),
                        isSelected: theme == selectedTheme
                    ) {
                        store.select(theme: theme)
                    }
                }
            } header: {
                Text("settings_theme")
            } footer: {
                Text("settings_theme_footer")
            }
        }
        .navigationTitle("settings_appearance")
        .navigationBarTitleDisplayMode(.inline)
    }

    private var selectedTheme: AppTheme {
        store.state.preferences.effectiveTheme(isSystemInDarkTheme: colorScheme == .dark)
    }
}
