# ZeroStorage

Android inventory and order-management application built for wholesale workflows, fast catalog operations, and scalable mobile-first business usage.

## Overview

ZeroStorage delivers core inventory and order operations in a clean mobile interface. The app is designed to simplify stock tracking and order flow management while maintaining strong responsiveness on Android devices.

This project highlights modern Android development with Jetpack Compose and a scalable app foundation for future backend integrations.

## Key Capabilities

- Mobile-friendly inventory browsing and management workflows.
- Product and order handling designed for wholesale operations.
- Compose-based UI architecture with reusable components.
- Fast, modern user experience using Kotlin and Material 3.
- Extensible app foundation for API and cloud integration.

## Technology Stack

- Language: Kotlin
- UI: Jetpack Compose + Material 3
- SDK: Android SDK 36 (min SDK 24)
- Build: Gradle Kotlin DSL
- Architecture: Compose-first Android structure
- Testing: JUnit + AndroidX test stack

## Architecture

The project uses a Compose-oriented Android architecture with strong UI/state separation to support:

- rapid iteration of product screens
- maintainable component design
- easier transition into larger multi-module patterns

## Project Structure

```
ZeroStorage/
|- app/
|  |- src/main/
|  |- build.gradle.kts
|- gradle/
|- build.gradle.kts
|- settings.gradle.kts
|- README.md
```

## Getting Started

### Prerequisites

- Android Studio (latest stable)
- JDK 11+

### Setup

1. Clone the repository.
2. Open the project in Android Studio.
3. Sync Gradle.
4. Build and run on emulator/device.

## Build and Run

```bash
./gradlew assembleDebug
./gradlew installDebug
```

On Windows PowerShell:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug
```

## Why This Project Stands Out

- Targets a practical business problem with a clean mobile UX.
- Uses modern Android Compose stack aligned with current industry direction.
- Serves as a solid base for scalable retail/wholesale app features.

## Author

Krishna Choudhary

## Repository

https://github.com/Byte-Maste/ZeroStorage
