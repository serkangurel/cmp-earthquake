import Shared
import SwiftUI

enum AppColors {
    static let primary = Color(
        light: UIColor(red: 160 / 255, green: 62 / 255, blue: 58 / 255, alpha: 1),
        dark: UIColor(red: 255 / 255, green: 179 / 255, blue: 174 / 255, alpha: 1)
    )

    // Magnitude colors mirror core:ui MagnitudeColors on Android; keep the two in sync.
    private static let magnitudeMinor = Color(
        light: UIColor(red: 35 / 255, green: 25 / 255, blue: 24 / 255, alpha: 1),
        dark: UIColor(red: 242 / 255, green: 222 / 255, blue: 220 / 255, alpha: 1)
    )

    /// Magnitude labels and badges.
    static func magnitudeText(_ threshold: MagnitudeThreshold) -> Color {
        switch threshold {
        case .fourPlus:
            return Color(
                light: UIColor(red: 141 / 255, green: 85 / 255, blue: 0, alpha: 1),
                dark: UIColor(red: 1, green: 190 / 255, blue: 64 / 255, alpha: 1)
            )
        case .fivePlus:
            return Color(
                light: UIColor(red: 180 / 255, green: 35 / 255, blue: 35 / 255, alpha: 1),
                dark: UIColor(red: 1, green: 136 / 255, blue: 128 / 255, alpha: 1)
            )
        default:
            return magnitudeMinor
        }
    }

    /// Badge background behind `magnitudeText`.
    static func magnitudeTint(_ threshold: MagnitudeThreshold) -> Color {
        magnitudeText(threshold).opacity(0.12)
    }

    /// Map pins and epicenter markers.
    static func magnitudeFill(_ threshold: MagnitudeThreshold) -> Color {
        switch threshold {
        case .fourPlus:
            return Color(red: 1, green: 179 / 255, blue: 0)
        case .fivePlus:
            return Color(
                light: UIColor(red: 211 / 255, green: 47 / 255, blue: 47 / 255, alpha: 1),
                dark: UIColor(red: 229 / 255, green: 57 / 255, blue: 53 / 255, alpha: 1)
            )
        default:
            return magnitudeMinor
        }
    }
}

private extension Color {
    init(light: UIColor, dark: UIColor) {
        self.init(uiColor: UIColor { traits in
            traits.userInterfaceStyle == .dark ? dark : light
        })
    }
}
