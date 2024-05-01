import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cmp_template.composeapp.generated.resources.Res
import cmp_template.composeapp.generated.resources.screen_detail
import cmp_template.composeapp.generated.resources.screen_home
import di.commonModule
import di.platformModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.github.aakira.napier.log
import org.jetbrains.compose.resources.StringResource
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import ui.component.SGAppBar
import ui.screens.detail.DetailScreen
import ui.screens.home.HomeScreen
import ui.theme.AppTheme

enum class SGScreen(val title: StringResource) {
    Home(title = Res.string.screen_home),
    Detail(title = Res.string.screen_detail)
}

@Composable
fun App(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    navController: NavHostController = rememberNavController()
) {
    AppTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor
    ) {
        // Get current back stack entry
        val backStackEntry by navController.currentBackStackEntryAsState()
        // Get the name of the current screen
        val currentScreen = SGScreen.valueOf(
            backStackEntry?.destination?.route ?: SGScreen.Home.name
        )

        Scaffold(
            topBar = {
                SGAppBar(
                    currentScreen = currentScreen,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = SGScreen.Home.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                composable(route = SGScreen.Home.name) {
                    HomeScreen {
                        navController.navigate(SGScreen.Detail.name)
                    }
                }
                composable(route = SGScreen.Detail.name) {
                    DetailScreen()
                }
            }
        }
    }
}

fun appInit(appDeclaration: KoinAppDeclaration = {}) {
    Napier.base(DebugAntilog())

    log(tag = "Napier") { "Application Started" }

    initKoin(
        appDeclaration
    )
}

private fun initKoin(
    appDeclaration: KoinAppDeclaration = {}
) {
    startKoin {
        appDeclaration()
        modules(commonModule(), platformModule())
    }
}