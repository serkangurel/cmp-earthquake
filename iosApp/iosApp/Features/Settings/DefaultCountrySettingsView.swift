import Shared
import SwiftUI

struct DefaultCountrySettingsView: View {
    @EnvironmentObject private var store: SettingsStore
    @EnvironmentObject private var navigation: NavigationStore
    @State private var searchText = ""

    private var filteredCountries: [CountryOption] {
        store.filterCountries(query: searchText)
    }

    var body: some View {
        List(filteredCountries, id: \.code) { country in
            let isSelected = country.code == store.state.preferences.defaultCountryCode
            Button {
                store.select(defaultCountry: country)
                navigation.goBack()
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
                    if isSelected {
                        Image(systemName: "checkmark")
                            .foregroundStyle(AppColors.primary)
                            .accessibilityHidden(true)
                    }
                }
                .frame(minHeight: 44)
                .contentShape(Rectangle())
            }
            .buttonStyle(.plain)
            .accessibilityAddTraits(isSelected ? .isSelected : [])
        }
        .navigationTitle("select_country")
        .navigationBarTitleDisplayMode(.inline)
        .searchable(
            text: $searchText,
            placement: .navigationBarDrawer(displayMode: .always),
            prompt: "search_countries"
        )
        .overlay {
            if filteredCountries.isEmpty && !store.state.countries.isEmpty {
                EmptyStateView(
                    title: "no_countries_found",
                    systemImage: "magnifyingglass",
                    message: "no_countries_found_description"
                )
                .allowsHitTesting(false)
            }
        }
    }
}
