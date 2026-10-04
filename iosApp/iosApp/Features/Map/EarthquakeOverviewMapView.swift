import CoreLocation
import GoogleMaps
import Shared
import SwiftUI
import UIKit

@MainActor
final class EarthquakeMapCameraMemory: ObservableObject {
    // Camera changes do not publish SwiftUI updates; restore only when the native view is recreated.
    var position: GMSCameraPosition?
    var lastFramedCountryCode: String?
}

struct EarthquakeOverviewMapView: UIViewRepresentable {
    let state: EarthquakeMapState
    let cameraMemory: EarthquakeMapCameraMemory
    let recenterRequest: Int
    let topInset: CGFloat
    let bottomInset: CGFloat
    let onSelect: (String) -> Void
    let onDismiss: () -> Void

    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.accessibilityReduceMotion) private var reduceMotion

    func makeCoordinator() -> Coordinator { Coordinator(parent: self) }

    func makeUIView(context: Context) -> LayoutAwareMapView {
        let options = GMSMapViewOptions()
        options.camera = cameraMemory.position ?? GMSCameraPosition(latitude: 0, longitude: 0, zoom: 1)
        let map = LayoutAwareMapView(options: options)
        map.accessibilityElementsHidden = false
        map.paddingAdjustmentBehavior = .never
        map.settings.compassButton = false
        map.settings.indoorPicker = false
        map.settings.myLocationButton = false
        map.delegate = context.coordinator
        context.coordinator.mapView = map
        map.onLayout = { [weak coordinator = context.coordinator] in
            coordinator?.fitCountryIfNeeded()
        }
        context.coordinator.lastRecenterRequest = recenterRequest
        context.coordinator.update(parent: self)
        return map
    }

    func updateUIView(_ map: LayoutAwareMapView, context: Context) {
        context.coordinator.update(parent: self)
    }

    static func dismantleUIView(_ map: LayoutAwareMapView, coordinator: Coordinator) {
        coordinator.parent.cameraMemory.position = map.camera
        map.onLayout = nil
        map.delegate = nil
        map.clear()
    }

    final class LayoutAwareMapView: GMSMapView {
        var onLayout: (() -> Void)?

        override func layoutSubviews() {
            super.layoutSubviews()
            onLayout?()
        }
    }

    // SDK callbacks run on the UI thread, but its Objective-C delegate has no actor annotation.
    @MainActor
    final class Coordinator: NSObject, @preconcurrency GMSMapViewDelegate {
        var parent: EarthquakeOverviewMapView
        weak var mapView: GMSMapView?
        var lastRecenterRequest = 0
        private var markers: [String: GMSMarker] = [:]
        private var icons: [String: UIImage] = [:]
        private var interfaceStyle: UIUserInterfaceStyle?

        init(parent: EarthquakeOverviewMapView) { self.parent = parent }

        func update(parent: EarthquakeOverviewMapView) {
            self.parent = parent
            guard let mapView else { return }
            let style: UIUserInterfaceStyle = parent.colorScheme == .dark ? .dark : .light
            if interfaceStyle != style {
                interfaceStyle = style
                icons.removeAll()
                mapView.overrideUserInterfaceStyle = style
            }
            let padding = UIEdgeInsets(top: parent.topInset, left: 0, bottom: parent.bottomInset, right: 0)
            if mapView.padding != padding { mapView.padding = padding }
            updateMarkers()
            fitCountryIfNeeded()
            if lastRecenterRequest != parent.recenterRequest && isReady {
                lastRecenterRequest = parent.recenterRequest
                frameCountry(animated: !parent.reduceMotion)
            }
        }

        private var isReady: Bool {
            guard let mapView else { return false }
            return mapView.bounds.width > 48 &&
                mapView.bounds.height - mapView.padding.top - mapView.padding.bottom > 48
        }

        func fitCountryIfNeeded() {
            guard isReady, let country = parent.state.snapshot.country,
                parent.cameraMemory.lastFramedCountryCode != country.code else { return }
            parent.cameraMemory.lastFramedCountryCode = country.code
            frameCountry(animated: false)
        }

        private func frameCountry(animated: Bool) {
            guard let mapView, let country = parent.state.snapshot.country, isReady else { return }
            let bounds = country.bounds
            let south = max(-85.05112878, min(85.05112878, bounds.minLatitude))
            let north = max(-85.05112878, min(85.05112878, bounds.maxLatitude))
            let extent = bounds.maxLongitude - bounds.minLongitude
            let update: GMSCameraUpdate
            if country.code == "GLOBAL" || !bounds.minLatitude.isFinite || !bounds.maxLatitude.isFinite ||
                !bounds.minLongitude.isFinite || !bounds.maxLongitude.isFinite || south >= north ||
                !(-180...180).contains(bounds.minLongitude) || !(-180...180).contains(bounds.maxLongitude) ||
                extent <= 0 || extent >= 360 {
                update = .setCamera(GMSCameraPosition(latitude: 0, longitude: 0, zoom: 1))
            } else {
                update = .fit(
                    GMSCoordinateBounds(
                        coordinate: CLLocationCoordinate2D(latitude: south, longitude: bounds.minLongitude),
                        coordinate: CLLocationCoordinate2D(latitude: north, longitude: bounds.maxLongitude)
                    ),
                    withPadding: 24
                )
            }
            if animated { mapView.animate(with: update) } else { mapView.moveCamera(update) }
            parent.cameraMemory.position = mapView.camera
        }

        private func updateMarkers() {
            guard let mapView else { return }
            let pins = parent.state.snapshot.pins
            let ids = Set(pins.map { $0.earthquake.id })
            for id in markers.keys.filter({ !ids.contains($0) }) {
                markers.removeValue(forKey: id)?.map = nil
            }
            let traits = UITraitCollection(userInterfaceStyle: interfaceStyle ?? .light)
            for pin in pins {
                let id = pin.earthquake.id
                let selected = id == parent.state.selectedEarthquakeId
                let marker = markers[id] ?? GMSMarker()
                marker.position = CLLocationCoordinate2D(latitude: pin.latitude, longitude: pin.longitude)
                marker.userData = id
                marker.title = String(
                    format: NSLocalizedString(selected ? "map_selected_earthquake_pin" : "map_earthquake_pin", comment: ""),
                    pin.earthquake.place, pin.earthquake.magnitude
                )
                let iconKey = "\(pin.earthquake.magnitudeThreshold.name):\(selected)"
                let icon: UIImage
                if let cached = icons[iconKey] {
                    icon = cached
                } else {
                    let color = UIColor(AppColors.magnitudeText(pin.earthquake.magnitudeThreshold))
                        .resolvedColor(with: traits)
                    icon = EarthquakeMarkerImage.make(color: color, selected: selected)
                    icons[iconKey] = icon
                }
                if marker.icon !== icon { marker.icon = icon }
                marker.groundAnchor = CGPoint(x: 0.5, y: 0.5)
                marker.zIndex = selected ? 2 : 1
                marker.map = mapView
                markers[id] = marker
            }
        }

        func mapView(_ mapView: GMSMapView, didTap marker: GMSMarker) -> Bool {
            guard let id = marker.userData as? String else { return false }
            parent.onSelect(id)
            return true
        }

        func mapView(_ mapView: GMSMapView, didTapAt coordinate: CLLocationCoordinate2D) {
            parent.onDismiss()
        }

        func mapView(_ mapView: GMSMapView, didChange position: GMSCameraPosition) {
            parent.cameraMemory.position = position
        }
    }
}
