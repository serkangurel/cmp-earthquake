import Shared
import SwiftUI

struct EarthquakeOverviewView: View {
    let application: SharedApplication

    @StateObject private var store: EarthquakeOverviewStore
    @State private var isCountrySheetPresented = false
    @State private var overviewFiltersHeight: CGFloat = 0
    @State private var collapsedFiltersHeight: CGFloat = 0
    @State private var filterDragStart: CGFloat?
    @State private var isOverviewScrolling = false
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
                scrollAwareEarthquakeList(viewportHeight: geometry.size.height)
            }
            .clipShape(TopEdgeClip())

            if store.state.showsBlockingLoader {
                ProgressView("loading")
                    .controlSize(.large)
                    .padding(24)
                    .background(.regularMaterial, in: RoundedRectangle(cornerRadius: 16))
                    .accessibilityLabel(Text("loading"))
            }
        }
        .toolbar(.hidden, for: .navigationBar)
        .safeAreaInset(edge: .top, spacing: 0) {
            if !dynamicTypeSize.isAccessibilitySize {
                overviewFilters
                    .modifier(CollapsingFilterHeader(
                        fullHeight: $overviewFiltersHeight,
                        collapsedHeight: collapsedFiltersHeight
                    ))
            }
        }
        .onChange(of: store.state.earthquakes.isEmpty) { isEmpty in
            if isEmpty { collapsedFiltersHeight = 0 }
        }
        .onChange(of: dynamicTypeSize) { _ in
            collapsedFiltersHeight = 0
            filterDragStart = nil
        }
        .sheet(isPresented: $isCountrySheetPresented) {
            CountrySelectionView(
                countries: store.state.countries,
                countrySearch: { application.filterCountries(countries: $0, query: $1) },
                selectedCountry: store.state.selectedCountry,
                onSelect: { country in
                    store.select(country: country)
                    isCountrySheetPresented = false
                }
            )
        }
    }

    @ViewBuilder
    private func scrollAwareEarthquakeList(viewportHeight: CGFloat) -> some View {
        if #available(iOS 18.0, *) {
            earthquakeList(viewportHeight: viewportHeight)
                .onScrollGeometryChange(for: OverviewScrollMetrics.self) { geometry in
                    OverviewScrollMetrics(
                        position: geometry.contentOffset.y + geometry.contentInsets.top,
                        topInset: geometry.contentInsets.top,
                        viewportHeight: geometry.containerSize.height,
                        canScroll: geometry.contentSize.height + geometry.contentInsets.top +
                            geometry.contentInsets.bottom > geometry.containerSize.height + 1
                    )
                } action: { old, new in
                    if !new.canScroll || new.position <= 0 {
                        collapsedFiltersHeight = 0
                    } else if isOverviewScrolling && canCollapseFilters &&
                        old.topInset == new.topInset && old.viewportHeight == new.viewportHeight {
                        // Header resizing changes insets; only consume actual list movement.
                        updateFilterCollapse(by: new.position - max(0, old.position))
                    }
                }
                .onScrollPhaseChange { _, phase in
                    isOverviewScrolling = phase.isScrolling
                    if phase == .idle { settleFilterCollapse() }
                }
        } else {
            // Keep the same gesture behavior on iOS 16–17, before scroll geometry is available.
            earthquakeList(viewportHeight: viewportHeight)
                .simultaneousGesture(
                    DragGesture()
                        .onChanged { value in
                            guard canCollapseFilters,
                                abs(value.translation.height) > abs(value.translation.width) else { return }
                            if filterDragStart == nil { filterDragStart = collapsedFiltersHeight }
                            collapsedFiltersHeight = min(
                                overviewFiltersHeight,
                                max(0, (filterDragStart ?? 0) - value.translation.height)
                            )
                        }
                        .onEnded { _ in
                            filterDragStart = nil
                            settleFilterCollapse()
                        }
                )
        }
    }

    private var canCollapseFilters: Bool {
        !dynamicTypeSize.isAccessibilitySize && store.state.canCollapseFilters
    }

    private func updateFilterCollapse(by delta: CGFloat) {
        collapsedFiltersHeight = min(overviewFiltersHeight, max(0, collapsedFiltersHeight + delta))
    }

    private func settleFilterCollapse() {
        guard canCollapseFilters else { return }
        withAnimation(.easeOut(duration: 0.2)) {
            collapsedFiltersHeight = collapsedFiltersHeight > overviewFiltersHeight / 2
                ? overviewFiltersHeight : 0
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

            if store.state.showsEmptyState {
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
                    NavigationLink(value: NavigationRoute(key: EarthquakeRoutes.Detail(id: earthquake.id).key)) {
                        EarthquakeRow(earthquake: earthquake)
                    }
                    .listRowSeparator(
                        earthquake.id == store.state.earthquakes.first?.id ? .hidden : .automatic,
                        edges: .top
                    )
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
                        store.earthquakeDisplayed(id: earthquake.id)
                    }
                }

                if store.state.showsPagingLoader {
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
            magnitudeOptions: store.state.magnitudeOptions,
            selectedMagnitude: Binding(
                get: { store.state.selectedMagnitude },
                set: { store.select(magnitude: $0) }
            ),
            onSelectCountry: { isCountrySheetPresented = true }
        )
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .background(.bar)
    }
}

private struct TopEdgeClip: Shape {
    func path(in rect: CGRect) -> Path {
        var clippingRect = rect
        // Keep bottom overflow visible beneath the floating tab bar.
        clippingRect.size.height += rect.height
        return Path(clippingRect)
    }
}

private struct OverviewScrollMetrics: Equatable {
    let position: CGFloat
    let topInset: CGFloat
    let viewportHeight: CGFloat
    let canScroll: Bool
}

private struct CollapsingFilterHeader: ViewModifier {
    @Binding var fullHeight: CGFloat
    let collapsedHeight: CGFloat

    func body(content: Content) -> some View {
        content
            .fixedSize(horizontal: false, vertical: true)
            .onGeometryChange(for: CGFloat.self) { geometry in
                geometry.size.height
            } action: { height in
                fullHeight = height
            }
            .offset(y: -collapsedHeight)
            .frame(height: max(0, fullHeight - collapsedHeight), alignment: .top)
            .clipped()
            .accessibilityHidden(fullHeight > 0 && collapsedHeight >= fullHeight)
    }
}

private struct OverviewFilters: View {
    let selectedCountry: CountryOption?
    let magnitudeOptions: [MagnitudeThreshold]
    @Binding var selectedMagnitude: MagnitudeThreshold
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
                ForEach(magnitudeOptions, id: \.self) { threshold in
                    Text(threshold.label).tag(threshold)
                }
            }
        } label: {
            filterLabel(title: "minimum_magnitude") {
                Text(selectedMagnitude.label)
                    .monospacedDigit()
            }
        }
        .buttonStyle(.plain)
        .accessibilityLabel(
            Text(
                String(
                    format: NSLocalizedString("magnitude_filter", comment: ""),
                    selectedMagnitude.label
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
        AppColors.magnitudeText(earthquake.magnitudeThreshold)
    }
}

private struct CountrySelectionView: View {
    let countries: [CountryOption]
    let countrySearch: ([CountryOption], String) -> [CountryOption]
    let selectedCountry: CountryOption?
    let onSelect: (CountryOption) -> Void

    @Environment(\.dismiss) private var dismiss
    @State private var searchText = ""

    private var filteredCountries: [CountryOption] {
        countrySearch(countries, searchText)
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

private let previewMagnitudeOptions: [MagnitudeThreshold] = [.twoPlus, .fourPlus, .fivePlus]

private struct EarthquakeOverviewComponents_Previews: PreviewProvider {
    static var previews: some View {
        CollapsingFiltersPreview(collapsedFraction: 0)
            .previewDisplayName("Filters • Expanded")
        CollapsingFiltersPreview(collapsedFraction: 0.6)
            .previewDisplayName("Filters • Collapsing")
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
            magnitudeOptions: previewMagnitudeOptions,
            selectedMagnitude: .constant(.fourPlus),
            onSelectCountry: {}
        )
        .padding()
        .previewDisplayName("Filters • Long country name")
        OverviewFilters(
            selectedCountry: CountryOption(code: "AE", name: "United Arab Emirates", flag: "🇦🇪"),
            magnitudeOptions: previewMagnitudeOptions,
            selectedMagnitude: .constant(.fourPlus),
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

private struct CollapsingFiltersPreview: View {
    let collapsedFraction: CGFloat
    @State private var headerHeight: CGFloat = 0

    var body: some View {
        OverviewFilters(
            selectedCountry: CountryOption(code: "GLOBAL", name: "Global", flag: "🌍"),
            magnitudeOptions: previewMagnitudeOptions,
            selectedMagnitude: .constant(.twoPlus),
            onSelectCountry: {}
        )
        .padding()
        .modifier(CollapsingFilterHeader(
            fullHeight: $headerHeight,
            collapsedHeight: headerHeight * collapsedFraction
        ))
        .frame(width: 375)
    }
}
