import Shared
import SwiftUI

struct EarthquakeTimestampView: View {
    let timestamp: EarthquakeTimestamp
    @Environment(\.locale) private var locale
    @ScaledMetric(relativeTo: .footnote) private var iconSize: CGFloat = 14

    var body: some View {
        Group {
            if let display = EarthquakeTimestampDisplay(timestamp: timestamp, locale: locale) {
                ViewThatFits(in: .horizontal) {
                    HStack(spacing: 12) {
                        timestampPart(display.date, systemImage: "calendar")
                        timestampPart(display.time, systemImage: "clock")
                    }
                    .fixedSize(horizontal: true, vertical: true)
                    VStack(alignment: .leading, spacing: 4) {
                        timestampPart(display.date, systemImage: "calendar")
                        timestampPart(display.time, systemImage: "clock")
                    }
                }
            } else {
                timestampPart(
                    EarthquakeTimestampDisplay.accessibilityDescription(for: timestamp, locale: locale),
                    systemImage: "clock"
                )
            }
        }
        .font(.footnote.weight(.medium))
        .foregroundStyle(.secondary)
        .monospacedDigit()
    }

    private func timestampPart(_ text: String, systemImage: String) -> some View {
        HStack(spacing: 4) {
            Image(systemName: systemImage)
                .resizable()
                .frame(width: iconSize, height: iconSize)
                .accessibilityHidden(true)
            Text(text)
        }
    }
}
