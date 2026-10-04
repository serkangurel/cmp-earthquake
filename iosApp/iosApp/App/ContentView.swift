import Shared
import SwiftUI

struct ContentView: View {
    let application: SharedApplication
    @StateObject private var navigation = NavigationStore()
    @StateObject private var settings: SettingsStore

    init(application: SharedApplication) {
        self.application = application
        _settings = StateObject(wrappedValue: SettingsStore(application: application))
    }

    var body: some View {
        TabView(selection: navigation.tabSelection) {
            NavigationStack(path: navigation.path(for: .earthquakes)) {
                EarthquakeOverviewView(application: application)
                    .navigationDestination(for: NavigationRoute.self, destination: destination)
            }
            .toolbar(navigation.state.showsBottomBar ? .visible : .hidden, for: .tabBar)
            .tabItem {
                Label("earthquakes", systemImage: "waveform.path.ecg.rectangle.fill")
            }
            .tag(AppTab.earthquakes)

            NavigationStack(path: navigation.path(for: .map)) {
                EarthquakeMapOverviewView(application: application)
                    .navigationDestination(for: NavigationRoute.self, destination: destination)
            }
            .toolbar(navigation.state.showsBottomBar ? .visible : .hidden, for: .tabBar)
            .tabItem {
                Label("map", systemImage: "map.fill")
            }
            .tag(AppTab.map)

            NavigationStack(path: navigation.path(for: .settings)) {
                SettingsView()
                    .navigationDestination(for: NavigationRoute.self, destination: destination)
            }
            .toolbar(navigation.state.showsBottomBar ? .visible : .hidden, for: .tabBar)
            .tabItem {
                Label("settings", systemImage: "gearshape.fill")
            }
            .tag(AppTab.settings)
        }
        .environmentObject(navigation)
        .environmentObject(settings)
        .tint(AppColors.primary)
        // Nil until the user picks a theme, so the app follows the device appearance.
        .preferredColorScheme(settings.state.preferences.theme?.colorScheme)
    }

    @ViewBuilder
    private func destination(_ route: NavigationRoute) -> some View {
        let destination = navigation.destination(for: route)
        if let detail = destination as? EarthquakeRoutes.Detail {
            EarthquakeDetailView(application: application, earthquakeID: detail.id)
        } else if destination is SettingsRoutes.Appearance {
            AppearanceSettingsView()
        } else if destination is SettingsRoutes.DefaultFilters {
            DefaultFiltersSettingsView()
        } else if destination is SettingsRoutes.DefaultCountry {
            DefaultCountrySettingsView()
        } else if destination is SettingsRoutes.TimeRange {
            TimeRangeSettingsView()
        } else if destination is SettingsRoutes.About {
            AboutSettingsView()
        }
    }
}
