import CoreLocation
import GoogleMaps
import QuartzCore
import Shared
import SwiftUI
import UIKit

struct EarthquakeMapView: UIViewRepresentable {
    let earthquake: EarthquakeDetail
    let recenterRequest: Int
    let bottomInset: CGFloat

    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @Environment(\.scenePhase) private var scenePhase

    func makeCoordinator() -> Coordinator {
        Coordinator()
    }

    func makeUIView(context: Context) -> GMSMapView {
        let coordinate = CLLocationCoordinate2D(
            latitude: earthquake.latitude,
            longitude: earthquake.longitude
        )
        let camera = GMSCameraPosition(
            latitude: coordinate.latitude,
            longitude: coordinate.longitude,
            zoom: 8
        )
        let options = GMSMapViewOptions()
        options.camera = camera
        let mapView = GMSMapView(options: options)
        mapView.paddingAdjustmentBehavior = .never
        mapView.settings.compassButton = false
        mapView.settings.indoorPicker = false
        mapView.settings.myLocationButton = false
        mapView.padding = UIEdgeInsets(
            top: 0,
            left: 0,
            bottom: bottomInset,
            right: 0
        )

        updateAppearance(of: mapView)
        let marker = GMSMarker(position: coordinate)
        marker.title = earthquake.place
        marker.groundAnchor = CGPoint(x: 0.5, y: 0.5)
        marker.infoWindowAnchor = CGPoint(x: 0.5, y: 0)
        marker.zIndex = 1
        marker.map = mapView

        context.coordinator.configure(
            mapView: mapView,
            marker: marker,
            coordinate: coordinate,
            color: markerColor,
            recenterRequest: recenterRequest
        )
        context.coordinator.setAnimationEnabled(scenePhase == .active && !reduceMotion)
        return mapView
    }

    func updateUIView(_ mapView: GMSMapView, context: Context) {
        mapView.padding = UIEdgeInsets(
            top: 0,
            left: 0,
            bottom: bottomInset,
            right: 0
        )
        updateAppearance(of: mapView)
        context.coordinator.update(earthquake: earthquake, color: markerColor)
        context.coordinator.setAnimationEnabled(scenePhase == .active && !reduceMotion)
        context.coordinator.recenterIfNeeded(recenterRequest)
    }

    static func dismantleUIView(_ mapView: GMSMapView, coordinator: Coordinator) {
        coordinator.stop()
        mapView.clear()
    }

    private func updateAppearance(of mapView: GMSMapView) {
        mapView.overrideUserInterfaceStyle = colorScheme == .dark ? .dark : .light
    }

    private var markerColor: UIColor {
        let traits = UITraitCollection(userInterfaceStyle: colorScheme == .dark ? .dark : .light)
        return UIColor(AppColors.magnitudeText(earthquake.magnitudeThreshold))
            .resolvedColor(with: traits)
    }

    final class Coordinator: NSObject {
        private weak var mapView: GMSMapView?
        private var coordinate = CLLocationCoordinate2D()
        private var color = UIColor.label
        private var marker: GMSMarker?
        private var waves: [GMSCircle] = []
        private var displayLink: CADisplayLink?
        private var startTime = CACurrentMediaTime()
        private var lastRecenterRequest = 0

        func configure(
            mapView: GMSMapView,
            marker: GMSMarker,
            coordinate: CLLocationCoordinate2D,
            color: UIColor,
            recenterRequest: Int
        ) {
            self.mapView = mapView
            self.marker = marker
            self.coordinate = coordinate
            self.color = color
            lastRecenterRequest = recenterRequest

            marker.icon = Self.markerImage(color: color)
            waves = (0..<2).map { _ in
                let wave = GMSCircle(position: coordinate, radius: 1)
                wave.strokeWidth = 1.5
                wave.strokeColor = .clear
                wave.fillColor = .clear
                wave.isTappable = false
                return wave
            }
        }

        func update(earthquake: EarthquakeDetail, color: UIColor) {
            let updatedCoordinate = CLLocationCoordinate2D(
                latitude: earthquake.latitude,
                longitude: earthquake.longitude
            )
            if coordinate.latitude != updatedCoordinate.latitude ||
                coordinate.longitude != updatedCoordinate.longitude {
                coordinate = updatedCoordinate
                marker?.position = coordinate
                waves.forEach { $0.position = coordinate }
            }
            marker?.title = earthquake.place
            if !self.color.isEqual(color) {
                self.color = color
                marker?.icon = Self.markerImage(color: color)
            }
        }

        func setAnimationEnabled(_ enabled: Bool) {
            guard let mapView else { return }
            waves.forEach { $0.map = enabled ? mapView : nil }
            guard enabled else {
                stop()
                return
            }
            guard displayLink == nil else { return }

            startTime = CACurrentMediaTime()
            let displayLink = CADisplayLink(target: self, selector: #selector(updatePulse))
            displayLink.preferredFrameRateRange = CAFrameRateRange(
                minimum: 30,
                maximum: 60,
                preferred: 60
            )
            displayLink.add(to: .main, forMode: .common)
            self.displayLink = displayLink
        }

        func recenterIfNeeded(_ request: Int) {
            guard request != lastRecenterRequest, let mapView else { return }
            lastRecenterRequest = request
            mapView.animate(
                to: GMSCameraPosition(
                    latitude: coordinate.latitude,
                    longitude: coordinate.longitude,
                    zoom: 8
                )
            )
        }

        func stop() {
            displayLink?.invalidate()
            displayLink = nil
        }

        @objc private func updatePulse() {
            guard let mapView else { return }
            let pointsPerMeter = mapView.projection.points(forMeters: 1, at: coordinate)
            guard pointsPerMeter.isFinite, pointsPerMeter > 0 else { return }
            let elapsed = CACurrentMediaTime() - startTime

            // Decorative waves have a consistent visual size, not a geographic impact radius.
            for (index, wave) in waves.enumerated() {
                let progress = (elapsed / 3 + Double(index) / Double(waves.count))
                    .truncatingRemainder(dividingBy: 1)
                let easedProgress = 1 - pow(1 - progress, 2)
                let opacity = pow(sin(.pi * progress), 2)
                wave.radius = (14 + 42 * easedProgress) / Double(pointsPerMeter)
                wave.strokeColor = color.withAlphaComponent(0.4 * opacity)
                wave.fillColor = color.withAlphaComponent(0.035 * opacity)
            }
        }

        private static func markerImage(color: UIColor) -> UIImage {
            UIGraphicsImageRenderer(size: CGSize(width: 40, height: 40)).image { context in
                let body = UIBezierPath(ovalIn: CGRect(x: 6, y: 6, width: 28, height: 28))
                context.cgContext.saveGState()
                context.cgContext.setShadow(
                    offset: CGSize(width: 0, height: 2),
                    blur: 4,
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
                UIBezierPath(ovalIn: CGRect(x: 15, y: 15, width: 10, height: 10)).fill()
                color.setFill()
                UIBezierPath(ovalIn: CGRect(x: 18, y: 18, width: 4, height: 4)).fill()
            }
        }
    }
}
