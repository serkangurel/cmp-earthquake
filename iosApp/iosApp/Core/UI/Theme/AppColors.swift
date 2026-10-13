import Shared
import SwiftUI

/// App colors, read from the shared `DesignTokens` (core:designsystem) so iOS matches Android.
enum AppColors {
    static let primary = tokenColor { $0.primary }
    static let onPrimary = tokenColor { $0.onPrimary }

    /// Magnitude labels and badges.
    static func magnitudeText(_ threshold: MagnitudeThreshold) -> Color {
        tokenColor { tier(threshold, in: $0).text }
    }

    /// Badge background behind `magnitudeText`.
    static func magnitudeTint(_ threshold: MagnitudeThreshold) -> Color {
        magnitudeText(threshold).opacity(Double(DesignTokens.shared.MAGNITUDE_TINT_ALPHA))
    }

    /// Map pins and epicenter markers.
    static func magnitudeFill(_ threshold: MagnitudeThreshold) -> Color {
        tokenColor { tier(threshold, in: $0).fill }
    }

    private static func tokenColor(_ value: (ColorTokens) -> Int64) -> Color {
        Color(
            light: UIColor(argb: value(DesignTokens.shared.light)),
            dark: UIColor(argb: value(DesignTokens.shared.dark))
        )
    }

    private static func tier(_ threshold: MagnitudeThreshold, in tokens: ColorTokens) -> MagnitudeTierTokens {
        switch threshold {
        case .fourPlus:
            return tokens.magnitude.moderate
        case .fivePlus:
            return tokens.magnitude.strong
        default:
            return tokens.magnitude.minor
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

private extension UIColor {
    /// `argb` is 0xAARRGGBB, as Kotlin `Long` tokens arrive in Swift.
    convenience init(argb: Int64) {
        let value = UInt32(truncatingIfNeeded: argb)
        self.init(
            red: CGFloat((value >> 16) & 0xFF) / 255,
            green: CGFloat((value >> 8) & 0xFF) / 255,
            blue: CGFloat(value & 0xFF) / 255,
            alpha: CGFloat((value >> 24) & 0xFF) / 255
        )
    }
}
