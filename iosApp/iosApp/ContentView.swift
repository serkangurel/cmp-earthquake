import Shared
import SwiftUI

struct ContentView: View {
    let application: SharedApplication

    var body: some View {
        TabView {
            NavigationStack {
                EarthquakeOverviewView(application: application)
            }
            .tabItem {
                Label("earthquakes", systemImage: "house.fill")
            }

            NavigationStack {
                PlaceholderView(message: "map_placeholder")
            }
            .tabItem {
                Label("map", systemImage: "map.fill")
            }

            NavigationStack {
                PlaceholderView(message: "settings_placeholder")
            }
            .tabItem {
                Label("settings", systemImage: "gearshape.fill")
            }
        }
        .tint(AppColors.primary)
    }
}

private struct PlaceholderView: View {
    let message: LocalizedStringKey

    var body: some View {
        Text(message)
    }
}

private struct EarthquakeOverviewView: View {
    let application: SharedApplication

    @StateObject private var store: EarthquakeOverviewStore
    @State private var isCountrySheetPresented = false

    init(application: SharedApplication) {
        self.application = application
        _store = StateObject(
            wrappedValue: EarthquakeOverviewStore(application: application)
        )
    }

    var body: some View {
        ZStack {
            earthquakeList

            if store.state.isLoading {
                ProgressView()
                    .controlSize(.large)
                    .padding(24)
                    .background(.regularMaterial, in: RoundedRectangle(cornerRadius: 16))
                    .accessibilityLabel(Text("loading"))
            }
        }
        .toolbar {
            ToolbarItem(placement: .topBarLeading) {
                Button {
                    isCountrySheetPresented = true
                } label: {
                    if let flag = store.state.selectedCountry?.flag, !flag.isEmpty {
                        Text(flag)
                            .font(.title2)
                    } else {
                        Image(systemName: "line.3.horizontal.decrease")
                    }
                }
                .accessibilityLabel(Text("filter"))
            }

            ToolbarItem(placement: .topBarTrailing) {
                magnitudeMenu
            }
        }
        .sheet(isPresented: $isCountrySheetPresented) {
            CountrySelectionView(
                countries: store.state.countries,
                selectedCountry: store.state.selectedCountry,
                onSelect: { country in
                    store.select(country: country)
                    isCountrySheetPresented = false
                }
            )
        }
    }

    private var earthquakeList: some View {
        List {
            if store.state.earthquakes.isEmpty &&
                !store.state.isLoading &&
                !store.state.isPullToRefresh {
                EmptyStateView(
                    title: "no_earthquakes_found",
                    systemImage: "magnifyingglass",
                    message: "no_earthquakes_found_description"
                )
                .frame(maxWidth: .infinity, minHeight: 420)
                .listRowSeparator(.hidden)
                .listRowBackground(Color.clear)
            } else {
                ForEach(store.state.earthquakes, id: \.id) { earthquake in
                    NavigationLink {
                        EarthquakeDetailView(
                            application: application,
                            earthquakeID: earthquake.id
                        )
                    } label: {
                        EarthquakeRow(earthquake: earthquake)
                    }
                    .onAppear {
                        if earthquake.id == store.state.earthquakes.last?.id {
                            store.loadMore()
                        }
                    }
                }
            }
        }
        .listStyle(.plain)
        .refreshable {
            await store.refresh()
        }
    }

    private var magnitudeMenu: some View {
        Menu {
            Picker(
                "magnitude",
                selection: Binding(
                    get: { store.state.selectedMagnitude.label },
                    set: { label in
                        store.select(
                            magnitude: MagnitudeThreshold.companion.fromLabel(label: label)
                        )
                    }
                )
            ) {
                ForEach(MagnitudeThreshold.companion.labels, id: \.self) { label in
                    Text(label).tag(label)
                }
            }
        } label: {
            HStack(spacing: 6) {
                Text(store.state.selectedMagnitude.label)
                    .font(.body.weight(.semibold))
                Image(systemName: "chevron.down")
                    .font(.caption.weight(.semibold))
                    .accessibilityHidden(true)
            }
            .padding(.horizontal, 8)
            .padding(.vertical, 8)
        }
        .accessibilityLabel(
            Text(
                String(
                    format: NSLocalizedString("magnitude_filter", comment: ""),
                    store.state.selectedMagnitude.label
                )
            )
        )
    }
}

struct EmptyStateView: View {
    let title: LocalizedStringKey
    let systemImage: String
    var message: LocalizedStringKey?

    var body: some View {
        VStack(spacing: 12) {
            Image(systemName: systemImage)
                .font(.system(size: 56))
                .foregroundStyle(AppColors.primary)
                .accessibilityHidden(true)
            Text(title)
                .font(.title3.weight(.semibold))
                .multilineTextAlignment(.center)
            if let message {
                Text(message)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                    .multilineTextAlignment(.center)
            }
        }
        .padding(32)
    }
}

private struct EarthquakeRow: View {
    let earthquake: EarthquakeListItem

    var body: some View {
        HStack(alignment: .center, spacing: 12) {
            VStack(alignment: .leading, spacing: 4) {
                Text(earthquake.place)
                    .font(.subheadline.weight(.bold))
                Text(earthquake.date)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
            }
            Spacer()
            Text(earthquake.magnitude)
                .font(.body.weight(.semibold))
                .foregroundStyle(AppColors.magnitude(earthquake.magnitudeThreshold))
        }
        .padding(.vertical, 6)
        .contentShape(Rectangle())
    }
}

private struct CountrySelectionView: View {
    let countries: [CountryOption]
    let selectedCountry: CountryOption?
    let onSelect: (CountryOption) -> Void

    @Environment(\.dismiss) private var dismiss

    var body: some View {
        NavigationStack {
            List(countries, id: \.code) { country in
                Button {
                    onSelect(country)
                } label: {
                    HStack(spacing: 16) {
                        Text(country.flag)
                            .font(.title2)
                            .frame(width: 28)
                            .accessibilityHidden(true)
                        Text(country.name)
                            .foregroundStyle(.primary)
                        Spacer()
                        if country.code == selectedCountry?.code {
                            Image(systemName: "checkmark")
                                .foregroundStyle(AppColors.primary)
                                .accessibilityHidden(true)
                        }
                    }
                }
                .accessibilityAddTraits(
                    country.code == selectedCountry?.code ? .isSelected : []
                )
            }
            .navigationTitle("select_country")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("done") {
                        dismiss()
                    }
                }
            }
        }
        .presentationDetents([.medium, .large])
    }
}

enum AppColors {
    static let primary = Color(
        light: UIColor(red: 160 / 255, green: 62 / 255, blue: 58 / 255, alpha: 1),
        dark: UIColor(red: 255 / 255, green: 179 / 255, blue: 174 / 255, alpha: 1)
    )

    static func magnitude(_ threshold: MagnitudeThreshold) -> Color {
        switch threshold {
        case .fourPlus:
            return Color(red: 1, green: 179 / 255, blue: 0)
        case .fivePlus:
            return Color(
                light: UIColor(red: 211 / 255, green: 47 / 255, blue: 47 / 255, alpha: 1),
                dark: UIColor(red: 229 / 255, green: 57 / 255, blue: 53 / 255, alpha: 1)
            )
        default:
            return .primary
        }
    }
}

private extension Color {
    init(light: UIColor, dark: UIColor) {
        self.init(uiColor: UIColor { traits in
            traits.userInterfaceStyle == .dark ? dark : light
        })
    }
}
