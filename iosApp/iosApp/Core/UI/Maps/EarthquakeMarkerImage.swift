import Shared
import UIKit

/// Earthquake map marker drawn from the shared `DesignTokens.marker` geometry, matching Android's
/// `EarthquakePin`. `selected` adds the halo and the solid ring behind the marker.
enum EarthquakeMarkerImage {
    static func make(color: UIColor, selected: Bool = false) -> UIImage {
        let marker = DesignTokens.shared.marker
        let size = CGFloat(marker.canvasSize)
        let center = CGPoint(x: size / 2, y: size / 2)
        return UIGraphicsImageRenderer(size: CGSize(width: size, height: size)).image { _ in
            func disc(_ radius: Int32, _ fill: UIColor, offsetY: CGFloat = 0) {
                let radius = CGFloat(radius)
                fill.setFill()
                UIBezierPath(ovalIn: CGRect(
                    x: center.x - radius,
                    y: center.y - radius + offsetY,
                    width: radius * 2,
                    height: radius * 2
                )).fill()
            }
            if selected {
                disc(marker.selectedHaloRadius, color.withAlphaComponent(CGFloat(marker.selectedHaloAlpha)))
                disc(marker.selectedRingRadius, color)
            }
            disc(
                marker.shadowRadius,
                UIColor.black.withAlphaComponent(CGFloat(marker.shadowAlpha)),
                offsetY: CGFloat(marker.shadowOffsetY)
            )
            disc(marker.ringRadius, .white)
            disc(marker.bodyRadius, color)
            let darkCenter = relativeLuminance(of: color) > CGFloat(marker.darkCenterLuminance)
            disc(marker.centerRadius, darkCenter ? .black : .white)
            disc(marker.coreRadius, color)
        }
    }

    /// WCAG relative luminance of an sRGB color, the same measure as Compose's `Color.luminance()`.
    private static func relativeLuminance(of color: UIColor) -> CGFloat {
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        color.getRed(&red, green: &green, blue: &blue, alpha: nil)
        func linear(_ component: CGFloat) -> CGFloat {
            component <= 0.04045 ? component / 12.92 : pow((component + 0.055) / 1.055, 2.4)
        }
        return 0.2126 * linear(red) + 0.7152 * linear(green) + 0.0722 * linear(blue)
    }
}
