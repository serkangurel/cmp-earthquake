import Shared
import SwiftUI

/// Spacing scale from the shared `DesignTokens` (core:designsystem), named by value.
enum AppSpacing {
    static let space2 = CGFloat(DesignTokens.shared.spacing.space2)
    static let space4 = CGFloat(DesignTokens.shared.spacing.space4)
    static let space6 = CGFloat(DesignTokens.shared.spacing.space6)
    static let space8 = CGFloat(DesignTokens.shared.spacing.space8)
    static let space12 = CGFloat(DesignTokens.shared.spacing.space12)
    static let space16 = CGFloat(DesignTokens.shared.spacing.space16)
    static let space20 = CGFloat(DesignTokens.shared.spacing.space20)
    static let space24 = CGFloat(DesignTokens.shared.spacing.space24)
    static let space32 = CGFloat(DesignTokens.shared.spacing.space32)
}

/// Corner radii from the shared `DesignTokens`. Fully round shapes use `Circle` / `Capsule`.
enum AppRadius {
    static let sm = CGFloat(DesignTokens.shared.radius.sm)
    static let md = CGFloat(DesignTokens.shared.radius.md)
    static let lg = CGFloat(DesignTokens.shared.radius.lg)
    static let xl = CGFloat(DesignTokens.shared.radius.xl)
    static let xxl = CGFloat(DesignTokens.shared.radius.xxl)
}
