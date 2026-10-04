import Shared
import SwiftUI

struct AboutSettingsView: View {
    @EnvironmentObject private var store: SettingsStore

    var body: some View {
        Form {
            Section {
                LabeledContent("about_version", value: AppVersion.current)
            }
            Section("about_data_source") {
                Text("about_data_source_description")
                    .foregroundStyle(.secondary)
                ForEach(store.state.aboutLinks, id: \.self) { link in
                    if let url = URL(string: link.url) {
                        Link(destination: url) {
                            HStack {
                                Text(link.titleKey)
                                Spacer()
                                Image(systemName: "arrow.up.right.square")
                                    .accessibilityHidden(true)
                            }
                        }
                        .accessibilityHint(Text("opens_in_browser"))
                    }
                }
            }
        }
        .navigationTitle("settings_about")
        .navigationBarTitleDisplayMode(.inline)
    }
}
