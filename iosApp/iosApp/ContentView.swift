// import UIKit
// import SwiftUI
// import Shared
//
// struct ComposeView: UIViewControllerRepresentable {
//     func makeUIViewController(context: Context) -> UIViewController {
//         MainViewControllerKt.MainViewController()
//     }
//
//     func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
// }
//
// struct ContentView: View {
//     var body: some View {
//         ComposeView()
//             .ignoresSafeArea(.all) // Compose has own keyboard handler
//     }
// }

import UIKit
import SwiftUI
import Shared

private final class ComposeContainerViewController: UIViewController {
    private let composeViewController = MainViewControllerKt.MainViewController()

    override func viewDidLoad() {
        super.viewDidLoad()

        // iOS 26 can activate Compose's hidden text input while presenting a UIKit menu.
        // This app has no editable controls, so reject any unexpected keyboard presentation.
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(dismissUnexpectedKeyboard),
            name: UIResponder.keyboardWillShowNotification,
            object: nil
        )

        addChild(composeViewController)
        composeViewController.view.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(composeViewController.view)
        NSLayoutConstraint.activate([
            composeViewController.view.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            composeViewController.view.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            composeViewController.view.topAnchor.constraint(equalTo: view.topAnchor),
            composeViewController.view.bottomAnchor.constraint(equalTo: view.bottomAnchor),
        ])
        composeViewController.didMove(toParent: self)
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        dismissUnexpectedKeyboard()
    }

    @objc private func dismissUnexpectedKeyboard() {
        view.window?.endEditing(true)
    }

    deinit {
        NotificationCenter.default.removeObserver(self)
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        ComposeContainerViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
                .ignoresSafeArea(.all) // Compose has own keyboard handler
    }
}
