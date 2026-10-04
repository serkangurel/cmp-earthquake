import Foundation
import Shared
import SwiftUI

enum AppTab: CaseIterable, Hashable {
    case earthquakes
    case map
    case settings

    var destination: NavigationDestination {
        switch self {
        case .earthquakes: return EarthquakeRoutes.Overview.shared
        case .map: return MapRoutes.Overview.shared
        case .settings: return SettingsRoutes.Overview.shared
        }
    }
}

// SwiftUI needs Hashable path values; stable identities are owned by the shared routes.
struct NavigationRoute: Hashable {
    let key: String
}

@MainActor
final class NavigationStore: ObservableObject {
    @Published private(set) var state: NavigationSnapshot
    private let navigator: Navigator
    private var observation: NavigationObservation?

    init() {
        navigator = AppNavigationKt.createAppNavigator()
        state = navigator.currentState
        observation = navigator.observe { [weak self] _ in
            Task { @MainActor [weak self] in
                // A native action may already have advanced state before this callback runs.
                self?.synchronize()
            }
        }
    }

    var tabSelection: Binding<AppTab> {
        Binding(
            get: {
                AppTab.allCases.first { $0.destination.key == self.state.selectedRoot.key } ?? .earthquakes
            },
            set: { tab in
                self.navigator.navigate(destination: tab.destination)
                self.synchronize()
            }
        )
    }

    func path(for tab: AppTab) -> Binding<[NavigationRoute]> {
        Binding(
            get: {
                self.state.backStacks.first { $0.root.key == tab.destination.key }?
                    .destinations.dropFirst().map { NavigationRoute(key: $0.key) } ?? []
            },
            set: { routes in
                self.navigator.setBackStack(
                    rootKey: tab.destination.key,
                    destinationKeys: routes.map(\.key)
                )
                self.synchronize()
            }
        )
    }

    func destination(for route: NavigationRoute) -> NavigationDestination? {
        navigator.resolveDestination(key: route.key)
    }

    func goBack() {
        navigator.goBack()
        synchronize()
    }

    private func synchronize() {
        state = navigator.currentState
    }

    deinit {
        observation?.cancel()
        navigator.close()
    }
}
