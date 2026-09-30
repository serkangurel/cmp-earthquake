import Shared
import SwiftUI

struct EarthquakeDetailView: View {
    @StateObject private var store: EarthquakeDetailStore
    @State private var recenterRequest = 0
    @State private var detailSheetHeight: CGFloat = 0

    init(application: SharedApplication, earthquakeID: String) {
        _store = StateObject(
            wrappedValue: EarthquakeDetailStore(
                application: application,
                earthquakeID: earthquakeID
            )
        )
    }

    var body: some View {
        ZStack(alignment: .bottom) {
            if store.state.isLoading {
                ProgressView()
                    .controlSize(.large)
            } else if let earthquake = store.state.earthquake {
                EarthquakeMapView(
                    earthquake: earthquake,
                    recenterRequest: recenterRequest,
                    bottomInset: detailSheetHeight
                )
                .ignoresSafeArea(edges: .bottom)

                VStack {
                    HStack {
                        Spacer()
                        Button {
                            recenterRequest += 1
                        } label: {
                            Image(systemName: "scope")
                                .frame(width: 48, height: 48)
                                .background(AppColors.primary, in: Circle())
                                .foregroundStyle(.white)
                        }
                        .accessibilityLabel(Text("center_map_on_earthquake"))
                        .padding()
                    }
                    Spacer()
                }

                EarthquakeDetailCard(earthquake: earthquake)
                    .background(
                        GeometryReader { proxy in
                            Color.clear.preference(
                                key: DetailSheetHeightKey.self,
                                value: proxy.size.height
                            )
                        }
                    )
            } else {
                EmptyStateView(
                    title: "earthquake_not_found",
                    systemImage: "exclamationmark.triangle",
                    message: nil
                )
            }
        }
        .onPreferenceChange(DetailSheetHeightKey.self) {
            detailSheetHeight = $0
        }
        .navigationBarTitleDisplayMode(.inline)
        .toolbar(.hidden, for: .tabBar)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button(action: {}) {
                    Image(systemName: "square.and.arrow.up")
                }
                .accessibilityLabel(Text("share"))
            }
        }
    }
}

private struct EarthquakeDetailCard: View {
    let earthquake: EarthquakeDetail

    var body: some View {
        VStack(spacing: 0) {
            Text("magnitude")
                .font(.caption2.weight(.bold))
                .textCase(.uppercase)
                .tracking(1.5)
                .foregroundStyle(.secondary)
                .padding(.top, 12)

            Text(earthquake.magnitude)
                .font(.system(size: 52, weight: .heavy))
                .foregroundStyle(AppColors.magnitude(earthquake.magnitudeThreshold))

            Text(earthquake.place)
                .font(.headline)
                .multilineTextAlignment(.center)
                .padding(.top, 6)

            VStack(spacing: 0) {
                DetailRow(
                    systemImage: "calendar",
                    label: "date",
                    value: earthquake.date
                )
                Divider().padding(.leading, 48)
                DetailRow(
                    systemImage: "arrow.down.to.line",
                    label: "depth",
                    value: String(
                        format: NSLocalizedString("depth_value", comment: ""),
                        earthquake.depth
                    )
                )
                Divider().padding(.leading, 48)
                DetailRow(
                    systemImage: "globe",
                    label: "source",
                    value: "USGS"
                )
            }
            .background(.quaternary, in: RoundedRectangle(cornerRadius: 12))
            .padding(.top, 16)
        }
        .padding(.horizontal, 20)
        .padding(.bottom, 16)
        .frame(maxWidth: .infinity)
        .background(.regularMaterial)
        .clipShape(
            UnevenRoundedRectangle(
                topLeadingRadius: 28,
                topTrailingRadius: 28
            )
        )
    }
}

private struct DetailRow: View {
    let systemImage: String
    let label: LocalizedStringKey
    let value: String

    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: systemImage)
                .frame(width: 20)
                .foregroundStyle(AppColors.primary)
                .accessibilityHidden(true)
            Text(label)
                .foregroundStyle(.secondary)
            Spacer()
            Text(value)
                .fontWeight(.semibold)
        }
        .font(.subheadline)
        .frame(height: 52)
        .padding(.horizontal, 16)
    }
}

private struct DetailSheetHeightKey: PreferenceKey {
    static let defaultValue: CGFloat = 0

    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value = max(value, nextValue())
    }
}
