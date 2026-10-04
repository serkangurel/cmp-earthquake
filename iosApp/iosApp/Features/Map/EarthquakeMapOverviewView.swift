import Shared
import SwiftUI

struct EarthquakeMapOverviewView: View {
    let application: SharedApplication
    @StateObject private var store: EarthquakeMapStore
    @StateObject private var cameraMemory = EarthquakeMapCameraMemory()
    @State private var recenterRequest = 0
    @State private var summaryHeight: CGFloat = 0

    init(application: SharedApplication) {
        self.application = application
        _store = StateObject(wrappedValue: EarthquakeMapStore(application: application))
    }

    var body: some View {
        GeometryReader { geometry in
            let selected = store.state.selectedEarthquake?.earthquake
            let cardHeight = selected == nil ? 0 : min(summaryHeight, geometry.size.height * 0.45)
            let overlayHeight = selected == nil ? 0 : cardHeight + 32

            ZStack(alignment: .bottom) {
                EarthquakeOverviewMapView(
                    state: store.state,
                    cameraMemory: cameraMemory,
                    recenterRequest: recenterRequest,
                    topInset: geometry.safeAreaInsets.top + 80,
                    bottomInset: geometry.safeAreaInsets.bottom + overlayHeight,
                    onSelect: store.selectEarthquake,
                    onDismiss: store.dismissSelection
                )
                .ignoresSafeArea(edges: [.top, .bottom])

                if let selected {
                    ScrollView {
                        EarthquakeMapSummary(earthquake: selected, onDismiss: store.dismissSelection) {
                            NavigationLink(value: NavigationRoute(key: EarthquakeRoutes.Detail(id: selected.id).key)) {
                                Text("view_earthquake_details")
                                    .font(.body.weight(.semibold))
                                    .frame(minHeight: 48)
                            }
                        }
                        .background(GeometryReader { proxy in
                            Color.clear.preference(key: MapSummaryHeightKey.self, value: proxy.size.height)
                        })
                    }
                    .frame(height: cardHeight)
                    .background(.regularMaterial, in: RoundedRectangle(cornerRadius: 24))
                    .padding(16)
                    .id(selected.id)
                }

                if store.state.snapshot.isLoading && store.state.snapshot.pins.isEmpty {
                    ProgressView("loading")
                        .controlSize(.large)
                        .padding(24)
                        .background(.regularMaterial, in: RoundedRectangle(cornerRadius: 16))
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else if store.state.snapshot.pins.isEmpty {
                    EarthquakeMapEmptyState(hasCountry: store.state.snapshot.country != nil)
                        .padding(16)
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else if store.state.snapshot.isLoading {
                    ProgressView("loading")
                        .padding(16)
                        .background(.regularMaterial, in: RoundedRectangle(cornerRadius: 16))
                        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
                        .padding(16)
                        .allowsHitTesting(false)
                }
            }
            .overlay(alignment: .topTrailing) {
                Button { recenterRequest += 1 } label: {
                    MapControlLabel(systemImage: "scope")
                }
                .buttonStyle(.plain)
                .disabled(store.state.snapshot.country == nil)
                .accessibilityLabel(Text("center_map_on_country"))
                .padding(16)
            }
        }
        .onPreferenceChange(MapSummaryHeightKey.self) { summaryHeight = $0 }
        .toolbar(.hidden, for: .navigationBar)
    }
}

private struct EarthquakeMapSummary<DetailsAction: View>: View {
    let earthquake: EarthquakeMapItem
    let onDismiss: () -> Void
    @ViewBuilder let detailsAction: () -> DetailsAction
    @Environment(\.dynamicTypeSize) private var dynamicTypeSize

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            if dynamicTypeSize.isAccessibilitySize {
                VStack(alignment: .leading, spacing: 8) { magnitude; locationAndTime }
            } else {
                HStack(alignment: .top, spacing: 16) { magnitude; locationAndTime }
            }
            HStack {
                detailsAction()
                Spacer(minLength: 8)
                Button(action: onDismiss) {
                    Image(systemName: "xmark")
                        .font(.system(size: 20, weight: .medium))
                        .frame(width: 48, height: 48)
                }
                .buttonStyle(.plain)
                .accessibilityLabel(Text("dismiss_earthquake_summary"))
            }
            .tint(AppColors.primary)
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
    }

    private var magnitude: some View {
        Text(earthquake.magnitude)
            .font(.title.weight(.semibold))
            .monospacedDigit()
            .foregroundStyle(AppColors.magnitudeText(earthquake.magnitudeThreshold))
            .fixedSize()
            .accessibilityLabel(Text("magnitude"))
            .accessibilityValue(Text(earthquake.magnitude))
    }

    private var locationAndTime: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(earthquake.place)
                .font(.headline)
                .fixedSize(horizontal: false, vertical: true)
            EarthquakeTimestampView(timestamp: earthquake.timestamp)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

private struct EarthquakeMapEmptyState: View {
    let hasCountry: Bool

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            if hasCountry {
                Text("map_no_loaded_earthquakes").font(.headline)
                Text("map_no_loaded_earthquakes_description").font(.subheadline)
            } else {
                Text("map_load_earthquakes").font(.subheadline)
            }
        }
        .padding(16)
        .background(.regularMaterial, in: RoundedRectangle(cornerRadius: 20))
    }
}

private struct MapSummaryHeightKey: PreferenceKey {
    static let defaultValue: CGFloat = 0
    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) { value = max(value, nextValue()) }
}

private struct EarthquakeMapOverlayPreviews: PreviewProvider {
    static var previews: some View {
        summary.previewDisplayName("Map summary • Light")
        summary.preferredColorScheme(.dark).previewDisplayName("Map summary • Dark")
        summary.dynamicTypeSize(.accessibility3).previewDisplayName("Map summary • Large text")
        EarthquakeMapEmptyState(hasCountry: true).padding().previewDisplayName("Map • Empty")
        EarthquakeMapEmptyState(hasCountry: false).padding().previewDisplayName("Map • Initial")
        ProgressView("loading").padding().previewDisplayName("Map • Loading")
    }

    private static var summary: some View {
        EarthquakeMapSummary(
            earthquake: EarthquakeMapItem(
                id: "preview", place: "24 km northeast of a long earthquake location name",
                magnitude: "4.80", magnitudeThreshold: .fourPlus, date: "03.10.2026 09:05"
            ),
            onDismiss: {}
        ) {
            Button("view_earthquake_details") {}
        }
        .background(.regularMaterial, in: RoundedRectangle(cornerRadius: 24))
        .padding()
    }
}
