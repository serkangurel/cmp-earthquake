import SwiftUI
import UIKit

/// Montserrat for iOS text, loaded from the same font files Android bundles (`core:resource`) and
/// sized by iOS text styles so Dynamic Type keeps working.
enum AppFont {
    enum Weight {
        case regular, medium, semibold, bold

        var postScriptName: String {
            switch self {
            case .regular: return "Montserrat-Regular"
            case .medium: return "Montserrat-Medium"
            case .semibold: return "Montserrat-SemiBold"
            case .bold: return "Montserrat-Bold"
            }
        }

        /// One bundled face heavier, for the Bold Text accessibility setting.
        var bolder: Weight {
            switch self {
            case .regular: return .semibold
            case .medium, .semibold, .bold: return .bold
            }
        }
    }

    /// Montserrat at the text style's iOS size, scaling with Dynamic Type.
    static func font(_ style: Font.TextStyle, weight: Weight = .regular) -> Font {
        verify(weight)
        return Font.custom(weight.postScriptName, size: baseSize(of: style), relativeTo: style)
    }

    /// For sizes that are already scaled, such as `@ScaledMetric` values.
    static func fixed(size: CGFloat, weight: Weight) -> Font {
        verify(weight)
        return Font.custom(weight.postScriptName, fixedSize: size)
    }

    /// UIKit variant for `UIAppearance`.
    static func uiFont(size: CGFloat, weight: Weight) -> UIFont {
        verify(weight)
        return UIFont(name: weight.postScriptName, size: size) ?? .systemFont(ofSize: size)
    }

    /// The text style's point size at the default content size, so sizes match the system styles.
    private static func baseSize(of style: Font.TextStyle) -> CGFloat {
        UIFontDescriptor.preferredFontDescriptor(
            withTextStyle: uiTextStyle(style),
            compatibleWith: UITraitCollection(preferredContentSizeCategory: .large)
        ).pointSize
    }

    private static func uiTextStyle(_ style: Font.TextStyle) -> UIFont.TextStyle {
        switch style {
        case .largeTitle: return .largeTitle
        case .title: return .title1
        case .title2: return .title2
        case .title3: return .title3
        case .headline: return .headline
        case .subheadline: return .subheadline
        case .body: return .body
        case .callout: return .callout
        case .footnote: return .footnote
        case .caption: return .caption1
        case .caption2: return .caption2
        @unknown default: return .body
        }
    }

    /// SwiftUI silently falls back to the system font when a face is missing; fail loudly in debug.
    private static func verify(_ weight: Weight) {
        #if DEBUG
        if UIFont(name: weight.postScriptName, size: 12) == nil {
            assertionFailure("Missing font \(weight.postScriptName): check UIAppFonts and the Resources phase")
        }
        #endif
    }
}

/// Faces chosen by name don't follow Bold Text on their own, so this reads `legibilityWeight`.
private struct AppFontModifier: ViewModifier {
    @Environment(\.legibilityWeight) private var legibilityWeight
    let style: Font.TextStyle
    let weight: AppFont.Weight

    func body(content: Content) -> some View {
        content.font(AppFont.font(style, weight: legibilityWeight == .bold ? weight.bolder : weight))
    }
}

extension View {
    /// Montserrat for a text style; one face heavier when Bold Text is on.
    func appFont(_ style: Font.TextStyle, weight: AppFont.Weight = .regular) -> some View {
        modifier(AppFontModifier(style: style, weight: weight))
    }
}
