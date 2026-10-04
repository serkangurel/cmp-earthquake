import Foundation
import Shared

struct EarthquakeTimestampDisplay: Equatable {
    let date: String
    let time: String

    init?(timestamp: EarthquakeTimestamp, locale: Locale = .current) {
        guard let milliseconds = timestamp.formattingEpochMilliseconds else { return nil }
        let parsed = Date(timeIntervalSince1970: milliseconds.doubleValue / 1_000)

        func formatted(_ template: String) -> String {
            let formatter = DateFormatter()
            formatter.locale = locale
            formatter.calendar = Calendar(identifier: .gregorian)
            formatter.timeZone = TimeZone(secondsFromGMT: 0)
            formatter.setLocalizedDateFormatFromTemplate(template)
            return formatter.string(from: parsed)
        }

        date = formatted("yMMMd")
        time = formatted("jmm")
    }

    static func accessibilityDescription(for value: EarthquakeTimestamp, locale: Locale = .current) -> String {
        guard let timestamp = EarthquakeTimestampDisplay(timestamp: value, locale: locale) else {
            return value.fallbackText ?? NSLocalizedString("earthquake_time_unavailable", comment: "")
        }
        return String(
            format: NSLocalizedString("earthquake_timestamp_accessibility", comment: ""),
            timestamp.date,
            timestamp.time
        )
    }
}
