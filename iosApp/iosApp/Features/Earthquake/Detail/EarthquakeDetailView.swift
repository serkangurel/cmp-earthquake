import Shared
import SwiftUI

struct EarthquakeDetailView: View {
    @StateObject private var store: EarthquakeDetailStore
    @State private var recenterRequest = 0
    @State private var detailContentHeight: CGFloat = 0
    @EnvironmentObject private var navigation: NavigationStore

    init(application: SharedApplication, earthquakeID: String) {
        _store = StateObject(
            wrappedValue: EarthquakeDetailStore(
                application: application,
                earthquakeID: earthquakeID
            )
        )
    }

    var body: some View {
        GeometryReader { geometry in
            let cardHeight = min(detailContentHeight, geometry.size.height * 0.55)

            ZStack(alignment: .bottom) {
                if store.state.isLoading {
                    ProgressView("loading")
                        .controlSize(.large)
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else if let earthquake = store.state.earthquake {
                    EarthquakeMapView(
                        earthquake: earthquake,
                        recenterRequest: recenterRequest,
                        topInset: geometry.safeAreaInsets.top + 80,
                        bottomInset: cardHeight + geometry.safeAreaInsets.bottom
                    )
                    .accessibilityElement(children: .ignore)
                    .accessibilityLabel(Text("epicenter_map"))
                    .accessibilityValue(Text(earthquake.place))
                    .ignoresSafeArea(edges: [.top, .bottom])
                    .overlay(alignment: .bottomTrailing) {
                        Button {
                            recenterRequest += 1
                        } label: {
                            MapControlLabel(systemImage: "scope")
                        }
                        .buttonStyle(.plain)
                        .accessibilityLabel(Text("center_map_on_earthquake"))
                        .padding(16)
                        .padding(.bottom, cardHeight)
                    }

                    ScrollView {
                        EarthquakeDetailCard(earthquake: earthquake)
                            .background(
                                GeometryReader { proxy in
                                    Color.clear.preference(
                                        key: DetailSheetHeightKey.self,
                                        value: proxy.size.height
                                    )
                                }
                            )
                    }
                    .frame(height: cardHeight)
                    .background(Color(uiColor: .systemGroupedBackground))
                    .clipShape(
                        UnevenRoundedRectangle(
                            topLeadingRadius: 24,
                            topTrailingRadius: 24
                        )
                    )
                    .overlay(alignment: .bottom) {
                        Color(uiColor: .systemGroupedBackground)
                            .frame(height: geometry.safeAreaInsets.bottom)
                            .offset(y: geometry.safeAreaInsets.bottom)
                            .allowsHitTesting(false)
                    }
                } else {
                    EmptyStateView(
                        title: "earthquake_not_found",
                        systemImage: "exclamationmark.triangle",
                        message: nil
                    )
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                }
            }
            .overlay(alignment: .top) {
                EarthquakeDetailActions(
                    onBack: navigation.goBack,
                    shareSummary: store.state.canShare ? store.state.earthquake?.shareSummary : nil
                )
                .padding(16)
            }
        }
        .onPreferenceChange(DetailSheetHeightKey.self) {
            detailContentHeight = $0
        }
        .background(SwipeBackGesture().allowsHitTesting(false))
        .toolbar(.hidden, for: .navigationBar)
    }
}

private struct EarthquakeDetailActions: View {
    let onBack: () -> Void
    let shareSummary: String?

    var body: some View {
        HStack {
            Button(action: onBack) {
                MapControlLabel(systemImage: "arrow.backward")
            }
            .accessibilityLabel(Text("back_button"))

            Spacer()

            if let shareSummary {
                ShareLink(item: shareSummary) {
                    MapControlLabel(systemImage: "square.and.arrow.up")
                }
                .accessibilityLabel(Text("share"))
            }
        }
        .buttonStyle(.plain)
    }
}

private struct EarthquakeDetailCard: View {
    let earthquake: EarthquakeDetail

    @Environment(\.dynamicTypeSize) private var dynamicTypeSize
    @Environment(\.locale) private var locale
    @ScaledMetric(relativeTo: .largeTitle) private var magnitudeSize: CGFloat = 40

    var body: some View {
        VStack(alignment: .leading, spacing: 20) {
            Group {
                if dynamicTypeSize.isAccessibilitySize {
                    VStack(alignment: .leading, spacing: 16) {
                        magnitude
                        location
                    }
                } else {
                    HStack(alignment: .center, spacing: 16) {
                        magnitude
                        location
                    }
                }
            }

            VStack(spacing: 0) {
                DetailRow(
                    systemImage: "calendar",
                    label: "date_and_time_local",
                    value: earthquake.formattedDate(locale: locale)
                )
                Divider().padding(.horizontal, 16)
                DetailRow(
                    systemImage: "arrow.down.to.line",
                    label: "depth",
                    value: earthquake.depthDisplay
                )
                Divider().padding(.horizontal, 16)
                DetailRow(
                    systemImage: "globe",
                    label: "source",
                    value: earthquake.source
                )
            }
            .background(
                Color(uiColor: .secondarySystemGroupedBackground),
                in: RoundedRectangle(cornerRadius: 16)
            )
        }
        .padding(20)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color(uiColor: .systemGroupedBackground))
    }

    private var magnitude: some View {
        VStack(spacing: 2) {
            Text("magnitude")
                .font(.caption.weight(.semibold))
            Text(earthquake.magnitude)
                .font(.system(size: magnitudeSize, weight: .bold, design: .rounded))
                .monospacedDigit()
        }
        .foregroundStyle(AppColors.magnitudeText(earthquake.magnitudeThreshold))
        .fixedSize()
        .padding(12)
        .background(
            AppColors.magnitudeText(earthquake.magnitudeThreshold).opacity(0.12),
            in: RoundedRectangle(cornerRadius: 16)
        )
        .accessibilityElement(children: .combine)
    }

    private var location: some View {
        Text(earthquake.place)
            .font(.title3.weight(.semibold))
            .fixedSize(horizontal: false, vertical: true)
            .frame(maxWidth: .infinity, alignment: .leading)
            .accessibilityAddTraits(.isHeader)
    }
}

private struct DetailRow: View {
    let systemImage: String
    let label: LocalizedStringKey
    let value: String

    var body: some View {
        ViewThatFits(in: .horizontal) {
            HStack(alignment: .firstTextBaseline, spacing: 16) {
                rowLabel
                    .fixedSize(horizontal: true, vertical: false)
                Spacer(minLength: 8)
                rowValue
                    .multilineTextAlignment(.trailing)
                    .fixedSize(horizontal: true, vertical: false)
            }

            VStack(alignment: .leading, spacing: 8) {
                rowLabel
                rowValue
            }
        }
        .font(.subheadline)
        .frame(maxWidth: .infinity, minHeight: 28, alignment: .leading)
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .accessibilityElement(children: .combine)
    }

    private var rowLabel: some View {
        Label {
            Text(label)
                .foregroundStyle(.secondary)
        } icon: {
            Image(systemName: systemImage)
                .foregroundStyle(AppColors.primary)
                .accessibilityHidden(true)
        }
    }

    private var rowValue: some View {
        Text(value)
            .fontWeight(.semibold)
            .fixedSize(horizontal: false, vertical: true)
    }
}

private extension EarthquakeDetail {
    var depthDisplay: String {
        guard let depthKm else { return NSLocalizedString("detail_value_unavailable", comment: "") }
        return String(format: NSLocalizedString("depth_value", comment: ""), depthKm)
    }

    func formattedDate(locale: Locale) -> String {
        guard let display = EarthquakeTimestampDisplay(timestamp: timestamp, locale: locale) else {
            return EarthquakeTimestampDisplay.accessibilityDescription(for: timestamp, locale: locale)
        }
        return "\(display.date) · \(display.time)"
    }

    var shareSummary: String {
        String(
            format: NSLocalizedString("earthquake_share_summary", comment: ""),
            place,
            magnitude,
            formattedDate(locale: .current),
            depthDisplay
        )
    }
}

private struct DetailSheetHeightKey: PreferenceKey {
    static let defaultValue: CGFloat = 0

    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value = max(value, nextValue())
    }
}

private struct EarthquakeDetailRows_Previews: PreviewProvider {
    static var previews: some View {
        EarthquakeDetailActions(onBack: {}, shareSummary: "Preview earthquake summary")
            .padding()
            .previewDisplayName("Map controls • Light")
        EarthquakeDetailActions(onBack: {}, shareSummary: "Preview earthquake summary")
            .padding()
            .preferredColorScheme(.dark)
            .previewDisplayName("Map controls • Dark")
        EarthquakeDetailActions(onBack: {}, shareSummary: nil)
            .padding()
            .previewDisplayName("Map controls • Loading or unavailable")
        rows
            .previewDisplayName("Detail facts • Light")
        rows
            .preferredColorScheme(.dark)
            .previewDisplayName("Detail facts • Dark")
        rows
            .dynamicTypeSize(.accessibility5)
            .previewDisplayName("Detail facts • Large text")
    }

    private static var rows: some View {
        VStack(spacing: 0) {
            DetailRow(systemImage: "calendar", label: "date_and_time_local", value: "Oct 2, 2026 · 15:07")
            Divider()
            DetailRow(systemImage: "arrow.down.to.line", label: "depth", value: "16.3 km")
            Divider()
            DetailRow(systemImage: "globe", label: "source", value: "USGS")
        }
        .padding()
        .frame(width: 375)
    }
}
