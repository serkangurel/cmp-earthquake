import GoogleMaps
import Shared
import SwiftUI

@main
struct iOSApp: App {
    private let sharedApplication: SharedApplication

    init() {
        guard let resourceURL = Bundle.main.url(
            forResource: "country-list",
            withExtension: "json"
        ) else {
            fatalError("Missing country-list.json application resource")
        }
        guard let countryResource = try? String(
            contentsOf: resourceURL,
            encoding: .utf8
        ) else {
            fatalError("Unable to read country-list.json application resource")
        }

        sharedApplication = SharedApplicationKt.startSharedApplication(
            countryResource: countryResource
        )

        let apiKey = SharedApplicationKt.mapsApiKey()
        if !apiKey.isEmpty {
            GMSServices.provideAPIKey(apiKey)
        }

        // Tab labels are drawn by UIKit, so they take Montserrat through UIAppearance (the iOS 26 tab
        // bar ignores these attributes). Bold Text is read once at launch.
        let bold = UIAccessibility.isBoldTextEnabled
        let tabItem = UITabBarItem.appearance()
        tabItem.setTitleTextAttributes([.font: AppFont.uiFont(size: 10, weight: bold ? .bold : .medium)], for: .normal)
        tabItem.setTitleTextAttributes([.font: AppFont.uiFont(size: 10, weight: bold ? .bold : .semibold)], for: .selected)
    }

    var body: some Scene {
        WindowGroup {
            ContentView(application: sharedApplication)
        }
    }
}
