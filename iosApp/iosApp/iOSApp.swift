import ComposeApp
import SwiftUI

@main
struct iOSApp: App {
    init() {
		AppKt.appInit { _ in
        }
    }

    var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}