package com.sgmobile.earthquake.core.designsystem

// Minor magnitudes are drawn in each theme's onSurface color.
private const val ON_SURFACE_LIGHT = 0xFF231918
private const val ON_SURFACE_DARK = 0xFFF2DEDC

object DesignTokens {
    val light = ColorTokens(
        primary = 0xFFA03E3A,
        onPrimary = 0xFFFFFFFF,
        primaryContainer = 0xFFFF867F,
        onPrimaryContainer = 0xFF751E1E,
        secondary = 0xFF84504D,
        onSecondary = 0xFFFFFFFF,
        secondaryContainer = 0xFFFFBBB6,
        onSecondaryContainer = 0xFF7B4845,
        tertiary = 0xFF6F5D00,
        onTertiary = 0xFFFFFFFF,
        tertiaryContainer = 0xFFC4A81C,
        onTertiaryContainer = 0xFF4A3D00,
        error = 0xFFBA1A1A,
        onError = 0xFFFFFFFF,
        errorContainer = 0xFFFFDAD6,
        onErrorContainer = 0xFF93000A,
        background = 0xFFFFF8F7,
        onBackground = 0xFF231918,
        surface = 0xFFFFF8F7,
        onSurface = ON_SURFACE_LIGHT,
        surfaceVariant = 0xFFFADCD9,
        onSurfaceVariant = 0xFF564240,
        outline = 0xFF89726F,
        outlineVariant = 0xFFDCC0BD,
        scrim = 0xFF000000,
        inverseSurface = 0xFF392E2D,
        inverseOnSurface = 0xFFFFEDEB,
        inversePrimary = 0xFFFFB3AE,
        surfaceDim = 0xFFE9D6D4,
        surfaceBright = 0xFFFFF8F7,
        surfaceContainerLowest = 0xFFFFFFFF,
        surfaceContainerLow = 0xFFFFF0EF,
        surfaceContainer = 0xFFFDE9E7,
        surfaceContainerHigh = 0xFFF7E4E2,
        surfaceContainerHighest = 0xFFF2DEDC,
        magnitude = MagnitudeTokens(
            minor = MagnitudeTierTokens(text = ON_SURFACE_LIGHT, fill = ON_SURFACE_LIGHT),
            moderate = MagnitudeTierTokens(text = 0xFF8D5500, fill = 0xFFFFB300),
            strong = MagnitudeTierTokens(text = 0xFFB42323, fill = 0xFFD32F2F),
        ),
    )

    val dark = ColorTokens(
        primary = 0xFFFFB3AE,
        onPrimary = 0xFF620F12,
        primaryContainer = 0xFFFF867F,
        onPrimaryContainer = 0xFF751E1E,
        secondary = 0xFFF9B6B1,
        onSecondary = 0xFF4E2421,
        secondaryContainer = 0xFF693936,
        onSecondaryContainer = 0xFFE6A5A0,
        tertiary = 0xFFE3C53B,
        onTertiary = 0xFF3A3000,
        tertiaryContainer = 0xFFC4A81C,
        onTertiaryContainer = 0xFF4A3D00,
        error = 0xFFFFB4AB,
        onError = 0xFF690005,
        errorContainer = 0xFF93000A,
        onErrorContainer = 0xFFFFDAD6,
        background = 0xFF1A1110,
        onBackground = 0xFFF2DEDC,
        surface = 0xFF1A1110,
        onSurface = ON_SURFACE_DARK,
        surfaceVariant = 0xFF564240,
        onSurfaceVariant = 0xFFDCC0BD,
        outline = 0xFFA48B89,
        outlineVariant = 0xFF564240,
        scrim = 0xFF000000,
        inverseSurface = 0xFFF2DEDC,
        inverseOnSurface = 0xFF392E2D,
        inversePrimary = 0xFFA03E3A,
        surfaceDim = 0xFF1A1110,
        surfaceBright = 0xFF423635,
        surfaceContainerLowest = 0xFF150C0B,
        surfaceContainerLow = 0xFF231918,
        surfaceContainer = 0xFF271D1C,
        surfaceContainerHigh = 0xFF322726,
        surfaceContainerHighest = 0xFF3E3231,
        magnitude = MagnitudeTokens(
            minor = MagnitudeTierTokens(text = ON_SURFACE_DARK, fill = ON_SURFACE_DARK),
            moderate = MagnitudeTierTokens(text = 0xFFFFBE40, fill = 0xFFFFB300),
            strong = MagnitudeTierTokens(text = 0xFFFF8880, fill = 0xFFE53935),
        ),
    )

    /** Alpha of a magnitude badge background, applied to the tier's text color. */
    const val MAGNITUDE_TINT_ALPHA: Float = 0.12f

    val spacing = SpacingTokens(
        space2 = 2,
        space4 = 4,
        space6 = 6,
        space8 = 8,
        space12 = 12,
        space16 = 16,
        space20 = 20,
        space24 = 24,
        space32 = 32,
    )

    val radius = RadiusTokens(
        sm = 8,
        md = 12,
        lg = 16,
        xl = 20,
        xxl = 24,
    )
}
