import SwiftUI

struct EmptyStateView<Actions: View>: View {
    let title: LocalizedStringKey
    let systemImage: String
    private let message: Text?
    private let actions: Actions

    init(
        title: LocalizedStringKey,
        systemImage: String,
        message: LocalizedStringKey?,
        @ViewBuilder actions: () -> Actions
    ) {
        self.title = title
        self.systemImage = systemImage
        self.message = message.map { Text($0) }
        self.actions = actions()
    }

    /// For a message already formatted with arguments, which must not be looked up as a key again.
    init(
        title: LocalizedStringKey,
        systemImage: String,
        verbatimMessage: String,
        @ViewBuilder actions: () -> Actions
    ) {
        self.title = title
        self.systemImage = systemImage
        self.message = Text(verbatim: verbatimMessage)
        self.actions = actions()
    }

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
                message
                    .appFont(.subheadline)
                    .foregroundStyle(.secondary)
                    .multilineTextAlignment(.center)
            }
            // An empty actions stack would still add its spacing and top padding.
            if Actions.self != EmptyView.self {
                VStack(spacing: AppSpacing.space12) {
                    actions
                }
                .padding(.top, AppSpacing.space12)
            }
        }
        .padding(AppSpacing.space32)
    }
}

extension EmptyStateView where Actions == EmptyView {
    init(title: LocalizedStringKey, systemImage: String, message: LocalizedStringKey? = nil) {
        self.init(title: title, systemImage: systemImage, message: message) { EmptyView() }
    }
}
