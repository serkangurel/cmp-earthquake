import SwiftUI

struct EmptyStateView: View {
    let title: LocalizedStringKey
    let systemImage: String
    var message: LocalizedStringKey?

    var body: some View {
        VStack(spacing: AppSpacing.space12) {
            Image(systemName: systemImage)
                .font(.system(size: 56))
                .foregroundStyle(AppColors.primary)
                .accessibilityHidden(true)
            Text(title)
                .appFont(.title3, weight: .semibold)
                .multilineTextAlignment(.center)
            if let message {
                Text(message)
                    .appFont(.subheadline)
                    .foregroundStyle(.secondary)
                    .multilineTextAlignment(.center)
            }
        }
        .padding(AppSpacing.space32)
    }
}
