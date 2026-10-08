package com.sgmobile.earthquake.core.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sgmobile.earthquake.core.designsystem.DesignTokens

/** Spacing scale from the shared design tokens, named by value. */
object Spacing {
    val space2: Dp = DesignTokens.spacing.space2.dp
    val space4: Dp = DesignTokens.spacing.space4.dp
    val space6: Dp = DesignTokens.spacing.space6.dp
    val space8: Dp = DesignTokens.spacing.space8.dp
    val space12: Dp = DesignTokens.spacing.space12.dp
    val space16: Dp = DesignTokens.spacing.space16.dp
    val space20: Dp = DesignTokens.spacing.space20.dp
    val space24: Dp = DesignTokens.spacing.space24.dp
    val space32: Dp = DesignTokens.spacing.space32.dp
}

/** Corner radii from the shared design tokens. Fully round shapes use CircleShape. */
object Radius {
    val sm: Dp = DesignTokens.radius.sm.dp
    val md: Dp = DesignTokens.radius.md.dp
    val lg: Dp = DesignTokens.radius.lg.dp
    val xl: Dp = DesignTokens.radius.xl.dp
    val xxl: Dp = DesignTokens.radius.xxl.dp
}
