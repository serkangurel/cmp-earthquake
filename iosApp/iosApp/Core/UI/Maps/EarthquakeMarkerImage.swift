import UIKit

enum EarthquakeMarkerImage {
    static func make(color: UIColor, selected: Bool = false) -> UIImage {
        let size: CGFloat = selected ? 48 : 40
        let inset: CGFloat = selected ? 10 : 6
        return UIGraphicsImageRenderer(size: CGSize(width: size, height: size)).image { context in
            if selected {
                color.withAlphaComponent(0.25).setFill()
                UIBezierPath(ovalIn: CGRect(x: 2, y: 2, width: 44, height: 44)).fill()
                color.setStroke()
                let ring = UIBezierPath(ovalIn: CGRect(x: 3, y: 3, width: 42, height: 42))
                ring.lineWidth = 2
                ring.stroke()
            }
            let body = UIBezierPath(ovalIn: CGRect(x: inset, y: inset, width: 28, height: 28))
            context.cgContext.saveGState()
            context.cgContext.setShadow(
                offset: CGSize(width: 0, height: 2), blur: 4,
                color: UIColor.black.withAlphaComponent(0.25).cgColor
            )
            color.setFill()
            body.fill()
            context.cgContext.restoreGState()
            UIColor.white.setStroke()
            body.lineWidth = 3
            body.stroke()
            var red: CGFloat = 0
            var green: CGFloat = 0
            var blue: CGFloat = 0
            color.getRed(&red, green: &green, blue: &blue, alpha: nil)
            let brightness = 0.299 * red + 0.587 * green + 0.114 * blue
            let centerColor: UIColor = brightness > 0.6 ? .black : .white
            centerColor.setFill()
            UIBezierPath(ovalIn: CGRect(x: inset + 9, y: inset + 9, width: 10, height: 10)).fill()
            color.setFill()
            UIBezierPath(ovalIn: CGRect(x: inset + 12, y: inset + 12, width: 4, height: 4)).fill()
        }
    }
}
