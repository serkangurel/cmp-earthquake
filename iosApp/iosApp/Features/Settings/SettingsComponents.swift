import Shared
import SwiftUI

/// Pushes a settings screen through the shared navigation path.
struct SettingsNavigationRow: View {
    let destination: NavigationDestination
    let title: LocalizedStringKey
    var value: Text?

    var body: some View {
        NavigationLink(value: NavigationRoute(key: destination.key)) {
            LabeledContent {
                if let value {
                    value
                }
            } label: {
                Text(title)
            }
        }
    }
}

/// One choice in a single-selection list.
struct SettingsOptionRow: View {
    let title: Text
    let isSelected: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack {
                title
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
}
