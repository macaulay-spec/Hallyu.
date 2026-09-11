# iosApp (Kotlin Multiplatform — Compose Multiplatform)

The shared UI and all app logic live in `:shared`. The iOS target compiles on a
macOS host (Xcode 16+). On non-Mac hosts the iOS targets are disabled automatically
in `shared/build.gradle.kts`.

## Setup (on your Mac)

1. Open `hallyu/` in Android Studio (or IntelliJ + the Kotlin Multiplatform plugin).
2. Sync — Gradle will build `shared` for `iosArm64` / `iosSimulatorArm64`.
3. In Xcode: File → New → Project → iOS App ("Hallyu"), Swift.
4. Drag the `shared.framework` (built by `./gradlew :shared:embedAndSignAppleFrameworkForXcode`)
   into the Xcode project, or add it via an SPM local package pointing at `shared/`.
5. Replace the generated `ContentView.swift` with a `ComposeView` that hosts
   `HallyuApp()` from the shared framework.

## Platform edges (expect/actual in :shared)

Push (APNs vs FCM), deep links (Universal Links vs App Links), haptics, and
biometrics are the only native seams. None exist yet in this preview — they
appear when the backend phase lands.

## Backend

The frontend is repository-shaped for a future Firebase backend (owner: product).
`HallyuStore` in commonMain is the preview data source; the swap is an
implementation change behind the same API — no UI changes.
