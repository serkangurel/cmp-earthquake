import Shared
import SwiftUI

struct ContentView: View {
    let application: SharedApplication
    @StateObject private var navigation = NavigationStore()

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
                PlaceholderView(message: "settings_placeholder")
                    .navigationDestination(for: NavigationRoute.self, destination: destination)
            }
            .toolbar(navigation.state.showsBottomBar ? .visible : .hidden, for: .tabBar)
            .tabItem {
                Label("settings", systemImage: "gearshape.fill")
            }
            .tag(AppTab.settings)
        }
        .environmentObject(navigation)
        .tint(AppColors.primary)
    }

    @ViewBuilder
    private func destination(_ route: NavigationRoute) -> some View {
        if let detail = navigation.destination(for: route) as? EarthquakeRoutes.Detail {
            EarthquakeDetailView(application: application, earthquakeID: detail.id)
        }
    }
}
