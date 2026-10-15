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
                EqualWidthVStack(spacing: AppSpacing.space12) {
                    actions
                }
                .padding(.top, AppSpacing.space12)
            }
        }
        .padding(AppSpacing.space32)
    }
}

/// Stacks views at the widest one's ideal width, capped to the offered width, so actions whose
/// labels fill the width share one size.
private struct EqualWidthVStack: Layout {
    let spacing: CGFloat

    func sizeThatFits(proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) -> CGSize {
        let width = sharedWidth(proposal: proposal, subviews: subviews)
        let heights = subviews.map { $0.sizeThatFits(ProposedViewSize(width: width, height: nil)).height }
        let totalSpacing = spacing * CGFloat(max(subviews.count - 1, 0))
        return CGSize(width: width, height: heights.reduce(0, +) + totalSpacing)
    }

    func placeSubviews(in bounds: CGRect, proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) {
        let childProposal = ProposedViewSize(width: bounds.width, height: nil)
        var y = bounds.minY
        for subview in subviews {
            subview.place(at: CGPoint(x: bounds.minX, y: y), proposal: childProposal)
            y += subview.sizeThatFits(childProposal).height + spacing
        }
    }

    private func sharedWidth(proposal: ProposedViewSize, subviews: Subviews) -> CGFloat {
        let widest = subviews.map { $0.sizeThatFits(.unspecified).width }.max() ?? 0
        return proposal.width.map { min(widest, $0) } ?? widest
    }
}

extension EmptyStateView where Actions == EmptyView {
    init(title: LocalizedStringKey, systemImage: String, message: LocalizedStringKey? = nil) {
        self.init(title: title, systemImage: systemImage, message: message) { EmptyView() }
    }
}
