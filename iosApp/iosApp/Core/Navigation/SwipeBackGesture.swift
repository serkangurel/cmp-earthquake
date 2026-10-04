import SwiftUI
import UIKit

/// Restores UIKit's interactive edge-pop gesture for screens with a custom back button.
struct SwipeBackGesture: UIViewControllerRepresentable {
    func makeCoordinator() -> Coordinator {
        Coordinator()
    }

    func makeUIViewController(context: Context) -> UIViewController {
        GestureController(coordinator: context.coordinator)
    }

    func updateUIViewController(_ controller: UIViewController, context: Context) {
        (controller as? GestureController)?.configureWhenAttached()
    }

    static func dismantleUIViewController(_ controller: UIViewController, coordinator: Coordinator) {
        coordinator.restore()
    }

    final class Coordinator: NSObject, UIGestureRecognizerDelegate {
        private weak var navigationController: UINavigationController?
        private weak var recognizer: UIGestureRecognizer?
        private weak var previousDelegate: UIGestureRecognizerDelegate?
        private var wasEnabled = false

        func install(on controller: UINavigationController) {
            guard controller.viewControllers.count > 1,
                  let gesture = controller.interactivePopGestureRecognizer,
                  gesture.state == .possible else { return }

            if recognizer === gesture, gesture.delegate === self {
                if controller.transitionCoordinator == nil {
                    gesture.isEnabled = true
                }
                return
            }

            restore()
            navigationController = controller
            recognizer = gesture
            previousDelegate = gesture.delegate
            wasEnabled = gesture.isEnabled
            gesture.delegate = self
            gesture.isEnabled = true
        }

        func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
            guard gestureRecognizer === recognizer,
                  let controller = navigationController else { return false }
            return controller.viewControllers.count > 1 && controller.transitionCoordinator == nil
        }

        func gestureRecognizer(
            _ gestureRecognizer: UIGestureRecognizer,
            shouldBeRequiredToFailBy otherGestureRecognizer: UIGestureRecognizer
        ) -> Bool {
            // Map SDKs also install custom recognizers, so prioritize back over all content gestures.
            guard gestureRecognizer === recognizer,
                  let view = otherGestureRecognizer.view,
                  let navigationView = navigationController?.view else { return false }
            return view !== navigationView && view.isDescendant(of: navigationView)
        }

        func restore() {
            if let gesture = recognizer, gesture.delegate === self {
                gesture.delegate = previousDelegate
                gesture.isEnabled = wasEnabled
            }
            recognizer = nil
            previousDelegate = nil
            navigationController = nil
        }
    }
}

private final class GestureController: UIViewController {
    private let coordinator: SwipeBackGesture.Coordinator

    init(coordinator: SwipeBackGesture.Coordinator) {
        self.coordinator = coordinator
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) {
        fatalError("GestureController is created programmatically")
    }

    override func loadView() {
        view = UIView()
        view.backgroundColor = .clear
        view.isUserInteractionEnabled = false
    }

    override func didMove(toParent parent: UIViewController?) {
        super.didMove(toParent: parent)
        if parent == nil {
            coordinator.restore()
        } else {
            configureWhenAttached()
        }
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        configureWhenAttached()
    }

    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        coordinator.restore()
    }

    func configureWhenAttached() {
        // SwiftUI attaches the representable before its navigation-controller ancestry is ready.
        DispatchQueue.main.async { [weak self] in
            guard let self, self.parent != nil, self.viewIfLoaded?.window != nil,
                  let controller = self.navigationController else { return }
            self.coordinator.install(on: controller)
        }
    }
}
