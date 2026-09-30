import CoreLocation
import GoogleMaps
import QuartzCore
import Shared
import SwiftUI

struct EarthquakeMapView: UIViewRepresentable {
    let earthquake: EarthquakeDetail
    let recenterRequest: Int
    let bottomInset: CGFloat

    @Environment(\.colorScheme) private var colorScheme

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
        mapView.settings.compassButton = false
        mapView.settings.indoorPicker = false
        mapView.settings.myLocationButton = false
        mapView.padding = UIEdgeInsets(
            top: 0,
            left: 0,
            bottom: bottomInset,
            right: 0
        )

        let marker = GMSMarker(position: coordinate)
        marker.title = earthquake.place
        marker.map = mapView

        context.coordinator.configure(
            mapView: mapView,
            coordinate: coordinate,
            color: UIColor(AppColors.magnitude(earthquake.magnitudeThreshold)),
            recenterRequest: recenterRequest
        )
        updateAppearance(of: mapView)
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
        context.coordinator.recenterIfNeeded(recenterRequest)
    }

    static func dismantleUIView(_ mapView: GMSMapView, coordinator: Coordinator) {
        coordinator.stop()
        mapView.clear()
    }

    private func updateAppearance(of mapView: GMSMapView) {
        mapView.overrideUserInterfaceStyle = colorScheme == .dark ? .dark : .light
    }

    final class Coordinator {
        private weak var mapView: GMSMapView?
        private var coordinate = CLLocationCoordinate2D()
        private var color = UIColor.label
        private var circle: GMSCircle?
        private var timer: Timer?
        private var startTime = CACurrentMediaTime()
        private var lastRecenterRequest = 0

        func configure(
            mapView: GMSMapView,
            coordinate: CLLocationCoordinate2D,
            color: UIColor,
            recenterRequest: Int
        ) {
            self.mapView = mapView
            self.coordinate = coordinate
            self.color = color
            lastRecenterRequest = recenterRequest

            let circle = GMSCircle(position: coordinate, radius: 0)
            circle.strokeWidth = 4
            circle.map = mapView
            self.circle = circle

            let timer = Timer(timeInterval: 1 / 30, repeats: true) { [weak self] _ in
                self?.updatePulse()
            }
            RunLoop.main.add(timer, forMode: .common)
            self.timer = timer
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
            timer?.invalidate()
            timer = nil
        }

        private func updatePulse() {
            guard let mapView, let circle else { return }
            let progress = (CACurrentMediaTime() - startTime)
                .truncatingRemainder(dividingBy: 2) / 2
            let alpha = 1 - progress
            let latitude = min(max(coordinate.latitude, -85.05112878), 85.05112878)
            let metersPerScreenUnit = 40_075_016.686
                * cos(latitude * .pi / 180)
                / (256 * pow(2, Double(mapView.camera.zoom)))

            circle.radius = 40 * progress * metersPerScreenUnit
            circle.strokeColor = color.withAlphaComponent(alpha)
            circle.fillColor = color.withAlphaComponent(0.12 * alpha)
        }
    }
}
