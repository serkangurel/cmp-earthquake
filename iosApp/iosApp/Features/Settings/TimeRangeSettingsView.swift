import Shared
import SwiftUI

struct TimeRangeSettingsView: View {
    @EnvironmentObject private var store: SettingsStore

    var body: some View {
        Form {
            Section {
                ForEach(store.state.timeRangeOptions, id: \.self) { timeRange in
                    SettingsOptionRow(
                        title: Text(timeRange.titleKey),
                        isSelected: timeRange == store.state.preferences.timeRange
                    ) {
                        store.select(timeRange: timeRange)
                    }
                }
            } footer: {
                Text("settings_time_range_footer")
            }
        }
        .navigationTitle("settings_time_range")
        .navigationBarTitleDisplayMode(.inline)
    }
}
