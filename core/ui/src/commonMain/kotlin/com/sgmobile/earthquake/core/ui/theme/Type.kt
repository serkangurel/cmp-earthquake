package com.sgmobile.earthquake.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.sgmobile.earthquake.core.resource.Montserrat_Black
import com.sgmobile.earthquake.core.resource.Montserrat_Bold
import com.sgmobile.earthquake.core.resource.Montserrat_ExtraBold
import com.sgmobile.earthquake.core.resource.Montserrat_ExtraLight
import com.sgmobile.earthquake.core.resource.Montserrat_Light
import com.sgmobile.earthquake.core.resource.Montserrat_Medium
import com.sgmobile.earthquake.core.resource.Montserrat_Regular
import com.sgmobile.earthquake.core.resource.Montserrat_SemiBold
import com.sgmobile.earthquake.core.resource.Res
import org.jetbrains.compose.resources.Font

private val MontserratFontFamily
    @Composable get() = FontFamily(
        Font(Res.font.Montserrat_ExtraLight, FontWeight.ExtraLight), // W200
        Font(Res.font.Montserrat_Light, FontWeight.Light), // W300
        Font(Res.font.Montserrat_Regular, FontWeight.Normal), // W400
        Font(Res.font.Montserrat_Medium, FontWeight.Medium), // W500
        Font(Res.font.Montserrat_SemiBold, FontWeight.SemiBold), // W600
        Font(Res.font.Montserrat_Bold, FontWeight.Bold), // W700
        Font(Res.font.Montserrat_ExtraBold, FontWeight.ExtraBold), // W800
        Font(Res.font.Montserrat_Black, FontWeight.Black), // W900
    )

// Default Material 3 typography values
val baseline = Typography()

val AppTypography
    @Composable get() = Typography(
        displayLarge = baseline.displayLarge.copy(fontFamily = MontserratFontFamily),
        displayMedium = baseline.displayMedium.copy(fontFamily = MontserratFontFamily),
        displaySmall = baseline.displaySmall.copy(fontFamily = MontserratFontFamily),
        headlineLarge = baseline.headlineLarge.copy(fontFamily = MontserratFontFamily),
        headlineMedium = baseline.headlineMedium.copy(fontFamily = MontserratFontFamily),
        headlineSmall = baseline.headlineSmall.copy(fontFamily = MontserratFontFamily),
        titleLarge = baseline.titleLarge.copy(fontFamily = MontserratFontFamily),
        titleMedium = baseline.titleMedium.copy(fontFamily = MontserratFontFamily),
        titleSmall = baseline.titleSmall.copy(fontFamily = MontserratFontFamily),
        bodyLarge = baseline.bodyLarge.copy(fontFamily = MontserratFontFamily),
        bodyMedium = baseline.bodyMedium.copy(fontFamily = MontserratFontFamily),
        bodySmall = baseline.bodySmall.copy(fontFamily = MontserratFontFamily),
        labelLarge = baseline.labelLarge.copy(fontFamily = MontserratFontFamily),
        labelMedium = baseline.labelMedium.copy(fontFamily = MontserratFontFamily),
        labelSmall = baseline.labelSmall.copy(fontFamily = MontserratFontFamily),
    )