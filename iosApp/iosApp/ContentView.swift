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
    @State private var overviewFiltersHeight: CGFloat = 0
    @Environment(\.dynamicTypeSize) private var dynamicTypeSize
    @Environment(\.locale) private var locale

    init(application: SharedApplication) {
        self.application = application
        _store = StateObject(
            wrappedValue: EarthquakeOverviewStore(application: application)
        )
    }

    var body: some View {
        ZStack {
            GeometryReader { geometry in
                earthquakeList(viewportHeight: geometry.size.height)
            }

            if store.state.isLoading && store.state.earthquakes.isEmpty {
                ProgressView("loading")
                    .controlSize(.large)
                    .padding(24)
                    .background(.regularMaterial, in: RoundedRectangle(cornerRadius: 16))
                    .accessibilityLabel(Text("loading"))
            }
        }
        .navigationTitle("earthquakes")
        .navigationBarTitleDisplayMode(.inline)
        .safeAreaInset(edge: .top, spacing: 0) {
            if !dynamicTypeSize.isAccessibilitySize {
                overviewFilters
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

    private func earthquakeList(viewportHeight: CGFloat) -> some View {
        List {
            if dynamicTypeSize.isAccessibilitySize {
                overviewFilters
                    .onGeometryChange(for: CGFloat.self) { geometry in
                        geometry.size.height
                    } action: { height in
                        overviewFiltersHeight = height
                    }
                    .listRowInsets(EdgeInsets())
                    .listRowSeparator(.hidden)
            }

            if store.state.earthquakes.isEmpty &&
                !store.state.isLoading &&
                !store.state.isPullToRefresh {
                EmptyStateView(
                    title: "no_earthquakes_found",
                    systemImage: "magnifyingglass",
                    message: "no_earthquakes_found_description"
                )
                .frame(
                    maxWidth: .infinity,
                    minHeight: max(
                        0,
                        viewportHeight - (dynamicTypeSize.isAccessibilitySize ? overviewFiltersHeight : 0)
                    )
                )
                .listRowInsets(EdgeInsets(top: 0, leading: 16, bottom: 0, trailing: 16))
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
                    .accessibilityElement(children: .ignore)
                    .accessibilityAddTraits(.isButton)
                    .accessibilityLabel(
                        Text(
                            String(
                                format: NSLocalizedString("earthquake_row_accessibility", comment: ""),
                                earthquake.place,
                                earthquake.magnitude,
                                EarthquakeTimestampDisplay.accessibilityDescription(for: earthquake.timestamp, locale: locale)
                            )
                        )
                    )
                    .accessibilityHint(Text("earthquake_details_hint"))
                    .onAppear {
                        if earthquake.id == store.state.earthquakes.last?.id {
                            store.loadMore()
                        }
                    }
                }

                if store.state.isLoading && !store.state.earthquakes.isEmpty {
                    ProgressView("loading")
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 16)
                        .listRowSeparator(.hidden)
                }
            }
        }
        .listStyle(.plain)
        .refreshable {
            await store.refresh()
        }
    }

    private var overviewFilters: some View {
        OverviewFilters(
            selectedCountry: store.state.selectedCountry,
            selectedMagnitude: Binding(
                get: { store.state.selectedMagnitude.label },
                set: { label in
                    store.select(
                        magnitude: MagnitudeThreshold.companion.fromLabel(label: label)
                    )
                }
            ),
            onSelectCountry: { isCountrySheetPresented = true }
        )
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .background(.bar)
        .overlay(alignment: .bottom) {
            Color(uiColor: .separator)
                .frame(height: 0.5)
        }
    }
}

private struct OverviewFilters: View {
    let selectedCountry: CountryOption?
    @Binding var selectedMagnitude: String
    let onSelectCountry: () -> Void

    @Environment(\.dynamicTypeSize) private var dynamicTypeSize

    var body: some View {
        if dynamicTypeSize.isAccessibilitySize {
            VStack(spacing: 8) {
                countryButton
                magnitudeMenu
            }
        } else {
            HStack(alignment: .top, spacing: 12) {
                countryButton
                magnitudeMenu
            }
        }
    }

    private var countryButton: some View {
        Button(action: onSelectCountry) {
            filterLabel(title: "country") {
                HStack(spacing: 6) {
                    if let flag = selectedCountry?.flag, !flag.isEmpty {
                        Text(flag)
                            .accessibilityHidden(true)
                    }
                    if let country = selectedCountry {
                        Text(country.name)
                    } else {
                        Text("select_country")
                    }
                }
            }
        }
        .buttonStyle(.plain)
        .accessibilityLabel(Text("country_filter"))
        .accessibilityValue(Text(selectedCountry?.name ?? ""))
        .accessibilityHint(Text("select_country"))
    }

    private var magnitudeMenu: some View {
        Menu {
            Picker("minimum_magnitude", selection: $selectedMagnitude) {
                ForEach(MagnitudeThreshold.companion.labels, id: \.self) { label in
                    Text(label).tag(label)
                }
            }
        } label: {
            filterLabel(title: "minimum_magnitude") {
                Text(selectedMagnitude)
                    .monospacedDigit()
            }
        }
        .buttonStyle(.plain)
        .accessibilityLabel(
            Text(
                String(
                    format: NSLocalizedString("magnitude_filter", comment: ""),
                    selectedMagnitude
                )
            )
        )
    }

    private func filterLabel<Value: View>(
        title: LocalizedStringKey,
        @ViewBuilder value: () -> Value
    ) -> some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(title)
                .font(.caption)
                .foregroundStyle(.secondary)
            HStack(alignment: .firstTextBaseline, spacing: 8) {
                value()
                    .font(.subheadline.weight(.semibold))
                    .foregroundStyle(AppColors.primary)
                Spacer(minLength: 0)
                Image(systemName: "chevron.down")
                    .font(.caption.weight(.semibold))
                    .foregroundStyle(.secondary)
                    .accessibilityHidden(true)
            }
        }
        .fixedSize(horizontal: false, vertical: true)
        .frame(maxWidth: .infinity, minHeight: 44, alignment: .leading)
        .padding(10)
        .background(Color(uiColor: .secondarySystemGroupedBackground), in: RoundedRectangle(cornerRadius: 12))
        .contentShape(Rectangle())
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

    @Environment(\.dynamicTypeSize) private var dynamicTypeSize

    var body: some View {
        Group {
            if dynamicTypeSize.isAccessibilitySize {
                VStack(alignment: .leading, spacing: 12) {
                    magnitudeBadge
                    locationAndTime
                }
            } else {
                HStack(alignment: .center, spacing: 14) {
                    magnitudeBadge
                    locationAndTime
                }
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.vertical, 8)
        .contentShape(Rectangle())
    }

    private var locationAndTime: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(earthquake.place)
                .font(.headline)
                .foregroundStyle(.primary)
            EarthquakeTimestampView(timestamp: earthquake.timestamp)
        }
        .fixedSize(horizontal: false, vertical: true)
        .frame(maxWidth: .infinity, alignment: .leading)
    }

    private var magnitudeBadge: some View {
        VStack(spacing: 2) {
            Text("magnitude_short")
                .font(.caption2.weight(.semibold))
            Text(earthquake.magnitude)
                .font(.title3.weight(.bold))
                .monospacedDigit()
        }
        .fixedSize()
        .frame(minWidth: 56)
        .padding(.vertical, 8)
        .padding(.horizontal, 6)
        .foregroundStyle(magnitudeColor)
        .background(magnitudeColor.opacity(0.12), in: RoundedRectangle(cornerRadius: 12))
    }

    private var magnitudeColor: Color {
        switch earthquake.magnitudeThreshold {
        case .fourPlus:
            return Color(
                light: UIColor(red: 141 / 255, green: 85 / 255, blue: 0, alpha: 1),
                dark: UIColor(red: 1, green: 190 / 255, blue: 64 / 255, alpha: 1)
            )
        case .fivePlus:
            return Color(
                light: UIColor(red: 180 / 255, green: 35 / 255, blue: 35 / 255, alpha: 1),
                dark: UIColor(red: 1, green: 136 / 255, blue: 128 / 255, alpha: 1)
            )
        default:
            return AppColors.magnitude(earthquake.magnitudeThreshold)
        }
    }
}

private struct EarthquakeTimestampView: View {
    let timestamp: EarthquakeTimestamp
    @Environment(\.locale) private var locale
    @ScaledMetric(relativeTo: .footnote) private var iconSize: CGFloat = 14

    var body: some View {
        Group {
            if let display = EarthquakeTimestampDisplay(timestamp: timestamp, locale: locale) {
                ViewThatFits(in: .horizontal) {
                    HStack(spacing: 12) {
                        timestampPart(display.date, systemImage: "calendar")
                        timestampPart(display.time, systemImage: "clock")
                    }
                    .fixedSize(horizontal: true, vertical: true)
                    VStack(alignment: .leading, spacing: 4) {
                        timestampPart(display.date, systemImage: "calendar")
                        timestampPart(display.time, systemImage: "clock")
                    }
                }
            } else {
                timestampPart(
                    EarthquakeTimestampDisplay.accessibilityDescription(for: timestamp, locale: locale),
                    systemImage: "clock"
                )
            }
        }
        .font(.footnote.weight(.medium))
        .foregroundStyle(.secondary)
        .monospacedDigit()
    }

    private func timestampPart(_ text: String, systemImage: String) -> some View {
        HStack(spacing: 4) {
            Image(systemName: systemImage)
                .resizable()
                .frame(width: iconSize, height: iconSize)
                .accessibilityHidden(true)
            Text(text)
        }
    }
}

private struct CountrySelectionView: View {
    let countries: [CountryOption]
    let selectedCountry: CountryOption?
    let onSelect: (CountryOption) -> Void

    @Environment(\.dismiss) private var dismiss
    @State private var searchText = ""

    private var filteredCountries: [CountryOption] {
        let query = searchText.trimmingCharacters(in: .whitespacesAndNewlines)
        return query.isEmpty ? countries : countries.filter {
            $0.name.localizedStandardContains(query) || $0.code.localizedStandardContains(query)
        }
    }

    var body: some View {
        NavigationStack {
            List(filteredCountries, id: \.code) { country in
                Button {
                    onSelect(country)
                } label: {
                    HStack(spacing: 16) {
                        Text(country.flag)
                            .font(.title2)
                            .fixedSize()
                            .frame(minWidth: 28)
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
                    .frame(minHeight: 44)
                }
                .buttonStyle(.plain)
                .accessibilityAddTraits(
                    country.code == selectedCountry?.code ? .isSelected : []
                )
            }
            .navigationTitle("select_country")
            .navigationBarTitleDisplayMode(.inline)
            .searchable(
                text: $searchText,
                placement: .navigationBarDrawer(displayMode: .always),
                prompt: "search_countries"
            )
            .overlay {
                if filteredCountries.isEmpty {
                    EmptyStateView(
                        title: "no_countries_found",
                        systemImage: "magnifyingglass",
                        message: "no_countries_found_description"
                    )
                    .allowsHitTesting(false)
                }
            }
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("done") {
                        dismiss()
                    }
                }
            }
        }
        .presentationDetents([.large])
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

private struct EarthquakeOverviewComponents_Previews: PreviewProvider {
    static var previews: some View {
        rows
            .previewDisplayName("Earthquake rows • Light")
        rows
            .preferredColorScheme(.dark)
            .previewDisplayName("Earthquake rows • Dark")
        rows
            .dynamicTypeSize(.accessibility3)
            .previewDisplayName("Earthquake rows • Large text")
        EarthquakeTimestampView(timestamp: EarthquakeTimestamp.companion.fromDisplayValue(value: "31.12.2025 23:59"))
            .frame(width: 220, alignment: .leading)
            .padding()
            .previewDisplayName("Timestamp • Narrow")
        EarthquakeTimestampView(timestamp: EarthquakeTimestamp.companion.fromDisplayValue(value: "01.10.2026 00:05"))
            .frame(width: 280, alignment: .leading)
            .padding()
            .dynamicTypeSize(.accessibility3)
            .previewDisplayName("Timestamp • Large text")
        EarthquakeTimestampView(timestamp: EarthquakeTimestamp.companion.fromDisplayValue(value: ""))
            .padding()
            .previewDisplayName("Timestamp • Unavailable")
        OverviewFilters(
            selectedCountry: CountryOption(code: "AE", name: "United Arab Emirates", flag: "🇦🇪"),
            selectedMagnitude: .constant("4+"),
            onSelectCountry: {}
        )
        .padding()
        .previewDisplayName("Filters • Long country name")
        OverviewFilters(
            selectedCountry: CountryOption(code: "AE", name: "United Arab Emirates", flag: "🇦🇪"),
            selectedMagnitude: .constant("4+"),
            onSelectCountry: {}
        )
        .padding()
        .dynamicTypeSize(.accessibility3)
        .previewDisplayName("Filters • Large text")
        EmptyStateView(
            title: "no_earthquakes_found",
            systemImage: "magnifyingglass",
            message: "no_earthquakes_found_description"
        )
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .previewDisplayName("Empty results")
        EmptyStateView(
            title: "no_earthquakes_found",
            systemImage: "magnifyingglass",
            message: "no_earthquakes_found_description"
        )
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .dynamicTypeSize(.accessibility3)
        .previewDisplayName("Empty results • Large text")
    }

    private static var rows: some View {
        List {
            EarthquakeRow(earthquake: sample(magnitude: "2.60", threshold: .twoPlus))
            EarthquakeRow(earthquake: sample(magnitude: "4.60", threshold: .fourPlus))
            EarthquakeRow(earthquake: sample(magnitude: "5.10", threshold: .fivePlus))
        }
        .listStyle(.plain)
    }

    private static func sample(magnitude: String, threshold: MagnitudeThreshold) -> EarthquakeListItem {
        EarthquakeListItem(
            id: magnitude,
            place: "58 km SSW of Whites City, New Mexico",
            magnitude: magnitude,
            magnitudeThreshold: threshold,
            date: "01.10.2026 21:53"
        )
    }
}
