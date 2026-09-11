# Hallyu Android App

This project is a native Android application rewritten from a Kotlin Multiplatform specification.
It uses Jetpack Compose and Koin for dependency injection.

## Features Preserved
- UI and layouts from the original multiplatform design.
- Core business logic including the `HallyuStore` data layer.
- All screens: Home, Explore, Drama, Post Detail, Profile, Notifications, etc.

## Architecture
- Single-module Android app (`:app`).
- Uses modern Jetpack Compose.
- Gradle Kotlin DSL build scripts.

## Limitations
- Stubbed data / mockup backend via `Seed` and `HallyuStore`.
- No actual backend server configured in this preview.
