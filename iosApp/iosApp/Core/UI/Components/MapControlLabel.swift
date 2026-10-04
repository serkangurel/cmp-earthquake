import SwiftUI

struct MapControlLabel: View {
    let systemImage: String

    var body: some View {
        Image(systemName: systemImage)
            .font(.system(size: 22, weight: .medium))
            .frame(width: 48, height: 48)
            .foregroundStyle(AppColors.primary)
            .background(Color(uiColor: .systemBackground), in: Circle())
            .shadow(color: .black.opacity(0.12), radius: 6, y: 2)
    }
}
