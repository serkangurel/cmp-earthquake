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

        guard let preferencesDirectory = try? FileManager.default.url(
            for: .applicationSupportDirectory,
            in: .userDomainMask,
            appropriateFor: nil,
            create: true
        ) else {
            fatalError("Unable to access the Application Support directory")
        }

        sharedApplication = SharedApplicationKt.startSharedApplication(
            countryResource: countryResource,
            preferencesDirectory: preferencesDirectory.path(percentEncoded: false)
        )

        let apiKey = SharedApplicationKt.mapsApiKey()
        if !apiKey.isEmpty {
            GMSServices.provideAPIKey(apiKey)
        }
    }

    var body: some Scene {
        WindowGroup {
            ContentView(application: sharedApplication)
        }
    }
}
