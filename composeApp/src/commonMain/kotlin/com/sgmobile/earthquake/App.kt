package com.sgmobile.earthquake

import androidx.compose.runtime.Composable
import com.sgmobile.earthquake.core.navigation.SGNavHost
import com.sgmobile.earthquake.core.ui.theme.AppTheme
import com.sgmobile.earthquake.core.ui.util.SetSystemBarsLightAppearance
import com.sgmobile.earthquake.di.getKoinConfiguration
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.github.aakira.napier.log
import org.koin.compose.KoinApplication
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.logger.Level

@OptIn(KoinExperimentalAPI::class)
@Composable
fun App(
    darkTheme: Boolean,
    dynamicColor: Boolean
) {
    KoinApplication(
        configuration = getKoinConfiguration(),
        logLevel = Level.INFO,
        content = {
            AppTheme(
                darkTheme = darkTheme,
                dynamicColor = dynamicColor
            ) {
                SetSystemBarsLightAppearance(
                    isAppearanceLightStatusBars = darkTheme,
                    isAppearanceLightNavigationBars = !darkTheme
                )
                SGNavHost()
            }
        })
}

fun appInit() {
    Napier.base(DebugAntilog())
    log(tag = "Napier") { "Application Started" }
}