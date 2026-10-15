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
                    .padding(AppSpacing.space24)
                    .background(.regularMaterial, in: RoundedRectangle(cornerRadius: AppRadius.lg))
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
        .onChange(of: store.state.showsErrorState) { isShown in
            if isShown { announce("earthquakes_load_failed") }
        }
        .onChange(of: store.state.showsLoadMoreError) { isShown in
            if isShown { announce("load_more_failed") }
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

            if store.state.showsErrorState {
                LoadErrorStateView(
                    sourceLabel: store.state.selectedSource.label,
                    alternativeSource: store.state.alternativeSource,
                    onRetry: { store.retry() },
                    onSelectSource: { store.select(source: $0) }
                )
                .modifier(FullPageListRow(minHeight: fullPageRowHeight(viewportHeight: viewportHeight)))
            } else if store.state.showsEmptyState {
                EmptyStateView(
                    title: "no_earthquakes_found",
                    systemImage: "magnifyingglass",
                    message: "no_earthquakes_found_description"
                )
                .modifier(FullPageListRow(minHeight: fullPageRowHeight(viewportHeight: viewportHeight)))
            } else {
                ForEach(store.state.earthquakes, id: \.id) { earthquake in
                    NavigationLink(value: NavigationRoute(key: EarthquakeRoutes.Detail(id: earthquake.id).key)) {
                        EarthquakeRow(earthquake: earthquake)
                    }
                    .listRowInsets(EdgeInsets(top: 0, leading: AppSpacing.space16, bottom: 0, trailing: AppSpacing.space16))
                    .listRowSeparator(
                        earthquake.id == store.state.earthquakes.first?.id ? .hidden : .automatic,
                        edges: .top
                    )
                    // Start the separator at the magnitude badge instead of the first text.
                    .alignmentGuide(.listRowSeparatorLeading) { _ in 0 }
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
                        .padding(.vertical, AppSpacing.space16)
                        .listRowSeparator(.hidden)
                }

                if store.state.showsLoadMoreError {
                    LoadMoreErrorRow(onRetry: { store.retry() })
                        .listRowInsets(EdgeInsets(top: 0, leading: AppSpacing.space16, bottom: 0, trailing: AppSpacing.space16))
                        .listRowSeparator(.hidden)
                }
            }
        }
        .listStyle(.plain)
        .refreshable {
            await store.refresh()
        }
    }

    /// Fills the list below the filters, which only scroll with the list at accessibility text sizes.
    private func fullPageRowHeight(viewportHeight: CGFloat) -> CGFloat {
        max(0, viewportHeight - (dynamicTypeSize.isAccessibilitySize ? overviewFiltersHeight : 0))
    }

    private func announce(_ key: String) {
        UIAccessibility.post(notification: .announcement, argument: NSLocalizedString(key, comment: ""))
    }

    private var overviewFilters: some View {
        OverviewFilters(
            selectedCountry: store.state.selectedCountry,
            magnitudeOptions: store.state.magnitudeOptions,
            selectedMagnitude: Binding(
                get: { store.state.selectedMagnitude },
                set: { store.select(magnitude: $0) }
            ),
            sourceOptions: store.state.sourceOptions,
            selectedSource: Binding(
                get: { store.state.selectedSource },
                set: { store.select(source: $0) }
            ),
            onSelectCountry: { isCountrySheetPresented = true }
        )
        .padding(.horizontal, AppSpacing.space16)
        .padding(.vertical, AppSpacing.space12)
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
    let sourceOptions: [EarthquakeSource]
    @Binding var selectedSource: EarthquakeSource
    let onSelectCountry: () -> Void

    @Environment(\.dynamicTypeSize) private var dynamicTypeSize

    var body: some View {
        VStack(spacing: AppSpacing.space8) {
            SourceSegmentedControl(options: sourceOptions, selection: $selectedSource)
            Group {
                if dynamicTypeSize.isAccessibilitySize {
                    VStack(spacing: AppSpacing.space8) {
                        countryButton
                        magnitudeMenu
                    }
                } else {
                    HStack(alignment: .top, spacing: AppSpacing.space12) {
                        countryButton
                        magnitudeMenu
                    }
                }
            }
        }
        // Size to content, so side-by-side cards share the taller card's height when a title wraps.
        .fixedSize(horizontal: false, vertical: true)
    }

    private var countryButton: some View {
        Button(action: onSelectCountry) {
            filterLabel(title: "country") {
                HStack(spacing: AppSpacing.space6) {
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
        VStack(alignment: .leading, spacing: AppSpacing.space4) {
            Text(title)
                .appFont(.caption)
                .foregroundStyle(.secondary)
            HStack(alignment: .firstTextBaseline, spacing: AppSpacing.space8) {
                value()
                    .appFont(.subheadline, weight: .semibold)
                    .foregroundStyle(AppColors.primary)
                Spacer(minLength: 0)
                Image(systemName: "chevron.down")
                    .appFont(.caption, weight: .semibold)
                    .foregroundStyle(.secondary)
                    .accessibilityHidden(true)
            }
        }
        .fixedSize(horizontal: false, vertical: true)
        .frame(maxWidth: .infinity, minHeight: 44, alignment: .leading)
        .frame(maxHeight: .infinity, alignment: .top)
        .padding(AppSpacing.space12)
        .background(Color(uiColor: .secondarySystemGroupedBackground), in: RoundedRectangle(cornerRadius: AppRadius.md))
        .contentShape(Rectangle())
    }
}

/// Text segments rather than `Picker(.segmented)`, whose `UISegmentedControl` ignores `appFont`.
private struct SourceSegmentedControl: View {
    let options: [EarthquakeSource]
    @Binding var selection: EarthquakeSource

    var body: some View {
        HStack(spacing: 0) {
            ForEach(options, id: \.self) { option in
                segment(option)
            }
        }
        .padding(AppSpacing.space4)
        .background(Color(uiColor: .secondarySystemGroupedBackground), in: RoundedRectangle(cornerRadius: AppRadius.md))
        .accessibilityElement(children: .contain)
        .accessibilityLabel(Text("data_source"))
    }

    private func segment(_ option: EarthquakeSource) -> some View {
        let isSelected = option == selection
        return Button {
            if !isSelected { selection = option }
        } label: {
            Text(option.label)
                .appFont(.subheadline, weight: .semibold)
                .lineLimit(1)
                .foregroundStyle(isSelected ? AppColors.onPrimary : AppColors.primary)
                .frame(maxWidth: .infinity, minHeight: 44)
                .background {
                    if isSelected {
                        RoundedRectangle(cornerRadius: AppRadius.sm).fill(AppColors.primary)
                    }
                }
                .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .accessibilityAddTraits(isSelected ? .isSelected : [])
    }
}

private struct EarthquakeRow: View {
    let earthquake: EarthquakeListItem

    @Environment(\.dynamicTypeSize) private var dynamicTypeSize

    var body: some View {
        Group {
            if dynamicTypeSize.isAccessibilitySize {
                VStack(alignment: .leading, spacing: AppSpacing.space12) {
                    magnitudeBadge
                    locationAndTime
                }
            } else {
                HStack(alignment: .center, spacing: AppSpacing.space12) {
                    magnitudeBadge
                    locationAndTime
                }
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.vertical, AppSpacing.space12)
        .contentShape(Rectangle())
    }

    private var locationAndTime: some View {
        VStack(alignment: .leading, spacing: AppSpacing.space6) {
            Text(earthquake.place)
                .appFont(.headline, weight: .semibold)
                .foregroundStyle(.primary)
            EarthquakeTimestampView(timestamp: earthquake.timestamp)
        }
        .fixedSize(horizontal: false, vertical: true)
        .frame(maxWidth: .infinity, alignment: .leading)
    }

    private var magnitudeBadge: some View {
        VStack(spacing: AppSpacing.space2) {
            Text("magnitude_short")
                .appFont(.caption2, weight: .semibold)
            Text(earthquake.magnitude)
                .appFont(.title3, weight: .bold)
                .monospacedDigit()
        }
        .fixedSize()
        .frame(minWidth: 56)
        .padding(.vertical, AppSpacing.space8)
        .padding(.horizontal, AppSpacing.space6)
        .foregroundStyle(magnitudeColor)
        .background(
            AppColors.magnitudeTint(earthquake.magnitudeThreshold),
            in: RoundedRectangle(cornerRadius: AppRadius.md)
        )
    }

    private var magnitudeColor: Color {
        AppColors.magnitudeText(earthquake.magnitudeThreshold)
    }
}

private struct FullPageListRow: ViewModifier {
    let minHeight: CGFloat

    func body(content: Content) -> some View {
        content
            .frame(maxWidth: .infinity, minHeight: minHeight)
            .listRowInsets(EdgeInsets(top: 0, leading: AppSpacing.space16, bottom: 0, trailing: AppSpacing.space16))
            .listRowSeparator(.hidden)
            .listRowBackground(Color.clear)
    }
}

private struct LoadErrorStateView: View {
    let sourceLabel: String
    let alternativeSource: EarthquakeSource?
    let onRetry: () -> Void
    let onSelectSource: (EarthquakeSource) -> Void

    var body: some View {
        EmptyStateView(
            title: "earthquakes_load_failed",
            systemImage: "exclamationmark.icloud",
            verbatimMessage: String(
                format: NSLocalizedString("earthquakes_load_failed_description", comment: ""),
                sourceLabel
            )
        ) {
            // Explicit styles also keep each button's tap separate inside a List row.
            Button(action: onRetry) {
                Text("retry")
                    .appFont(.body, weight: .semibold)
                    .foregroundStyle(AppColors.onPrimary)
                    .frame(maxWidth: .infinity)
            }
            .buttonStyle(.borderedProminent)
            if let source = alternativeSource {
                Button {
                    onSelectSource(source)
                } label: {
                    Text(String(format: NSLocalizedString("try_source", comment: ""), source.label))
                        .appFont(.body, weight: .semibold)
                        .frame(maxWidth: .infinity)
                }
                .buttonStyle(.bordered)
            }
        }
        .controlSize(.large)
    }
}

private struct LoadMoreErrorRow: View {
    let onRetry: () -> Void

    @Environment(\.dynamicTypeSize) private var dynamicTypeSize

    var body: some View {
        Group {
            if dynamicTypeSize.isAccessibilitySize {
                VStack(alignment: .leading, spacing: AppSpacing.space8) {
                    message
                    retryButton
                }
            } else {
                HStack(spacing: AppSpacing.space12) {
                    message
                    Spacer()
                    retryButton
                }
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.vertical, AppSpacing.space8)
    }

    private var message: some View {
        Text("load_more_failed")
            .appFont(.subheadline)
            .foregroundStyle(.secondary)
    }

    private var retryButton: some View {
        Button(action: onRetry) {
            Text("retry")
                .appFont(.subheadline, weight: .semibold)
        }
        .buttonStyle(.borderless)
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
                    HStack(spacing: AppSpacing.space16) {
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
private let previewSourceOptions: [EarthquakeSource] = [.usgs, .emsc]

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
            sourceOptions: previewSourceOptions,
            selectedSource: .constant(.usgs),
            onSelectCountry: {}
        )
        .padding()
        .previewDisplayName("Filters • Long country name")
        OverviewFilters(
            selectedCountry: CountryOption(code: "AE", name: "United Arab Emirates", flag: "🇦🇪"),
            magnitudeOptions: previewMagnitudeOptions,
            selectedMagnitude: .constant(.fourPlus),
            sourceOptions: previewSourceOptions,
            selectedSource: .constant(.usgs),
            onSelectCountry: {}
        )
        .padding()
        .dynamicTypeSize(.accessibility3)
        .previewDisplayName("Filters • Large text")
        emscFilters
            .previewDisplayName("Filters • EMSC")
        emscFilters
            .preferredColorScheme(.dark)
            .previewDisplayName("Filters • EMSC • Dark")
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
        loadError
            .previewDisplayName("Load error")
        loadError
            .dynamicTypeSize(.accessibility3)
            .previewDisplayName("Load error • Large text")
        loadError
            .preferredColorScheme(.dark)
            .previewDisplayName("Load error • Dark")
        loadMoreError
            .previewDisplayName("Load more error")
        loadMoreError
            .dynamicTypeSize(.accessibility3)
            .previewDisplayName("Load more error • Large text")
    }

    private static var loadError: some View {
        LoadErrorStateView(sourceLabel: "EMSC", alternativeSource: .usgs, onRetry: {}, onSelectSource: { _ in })
            .frame(maxWidth: .infinity, maxHeight: .infinity)
    }

    private static var loadMoreError: some View {
        LoadMoreErrorRow(onRetry: {})
            .padding(.horizontal, AppSpacing.space16)
    }

    private static var emscFilters: some View {
        OverviewFilters(
            selectedCountry: CountryOption(code: "TR", name: "Türkiye", flag: "🇹🇷"),
            magnitudeOptions: previewMagnitudeOptions,
            selectedMagnitude: .constant(.twoPlus),
            sourceOptions: previewSourceOptions,
            selectedSource: .constant(.emsc),
            onSelectCountry: {}
        )
        .padding()
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
            sourceOptions: previewSourceOptions,
            selectedSource: .constant(.usgs),
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
