# Android Jetpack Compose Authentication System Boilerplate

This project is a production-ready **Authentication System** module built with **Jetpack Compose** and **Clean Architecture**. It provides a fully functional and scalable authentication flow using MVVM, Dependency Injection (Hilt), and the Repository Pattern, mirroring the structure and logic of its iOS counterpart.

## Features

- **Authentication Flows**: Email/Password Login, Signup, and Logout.
- **Firebase Authentication**: Integrated with Firebase for real-world usability and production security.
- **Firestore Integration**: Automatically persists and manages user profile data in Cloud Firestore upon successful authentication.
- **Google Sign-In**: Fully implemented Google authentication using the standard `GoogleSignInClient` for a seamless account selection experience.
- **Premium UI/UX**: Modern, responsive design with gradients, shadows, and smooth transitions, optimized for a high-quality user experience on Android.
- **Clean Architecture + MVVM**: Strict separation of concerns across Data, Domain, and Presentation layers ensuring code testability and modularity.

## Architecture & Tech Stack

- **UI Framework**: Jetpack Compose
- **Language**: Kotlin
- **Architecture**: Clean Architecture (Presentation, Domain, Data) + MVVM
- **Dependency Management**: Gradle Kotlin DSL with Version Catalogs (`libs.versions.toml`)
- **Dependency Injection**: Hilt (Dagger)
- **Asynchronous Flow**: Kotlin Coroutines & Flow
- **Backend/Services**: Firebase SDK (Auth & Firestore)

## Folder Structure

```text
app/src/main/java/com/ck/events/app/
├── data/
│   ├── model/            # Data Transfer Objects (DTOs) and Mappers
│   ├── repository/       # Concrete repositories implementing Domain protocols
│   └── source/           # Remote data sources (Firebase Auth, Firestore)
├── di/                   # Hilt Dependency Injection Modules
├── domain/
│   ├── model/            # Domain Entities (User, AuthProvider)
│   ├── repository/       # Repository interfaces
│   └── usecase/          # Login, Signup, Logout, SocialLogin UseCases
├── presentation/
│   ├── components/       # Reusable UI (CustomTextField, PrimaryButton, etc.)
│   ├── navigation/       # Compose Navigation Graph and Routes
│   ├── screens/          # LoginScreen, SignupScreen, HomeScreen
│   └── viewmodels/       # ViewModels backing the UI
└── ui/theme/             # App theme, colors, and typography
```

## Setup Instructions

### 1. Configure Firebase

To utilize the Firebase authentication and Firestore layers:

1. Create a project in the [Firebase Console](https://console.firebase.google.com/).
2. Add an **Android App** with the package name `com.ck.events.app`.
3. Enable **Email/Password** and **Google** authentication in the Auth section.
4. Download the `google-services.json` file.
5. Place `google-services.json` into the `app/` directory of this project.

### 2. Enable Google Login

For Google Sign-In to function correctly on your local environment:

1. **SHA-1 Fingerprint**: You must add your local debug SHA-1 to your Firebase project.
   - Run `./gradlew signingReport` in your terminal.
   - Copy the SHA-1 value from the `debug` variant.
   - Add this fingerprint to your app settings in the Firebase Console.
2. **Web Client ID**: 
   - Navigate to **Authentication** > **Sign-in method** > **Google** in Firebase.
   - Copy the **Web client ID** found under the "Web SDK configuration" section.
   - For this project, put your client ID in `LoginScreen.kt`:
     `val serverClientId = "YOUR_CLIENT_ID_HERE"`

### 3. Build and Run

- Open the project in **Android Studio**.
- Sync Gradle to install all dependencies.
- Select your target device or emulator and press **Run** (`Shift + F10`).

## Custom Backend Integration

The project is structured to easily swap Firebase with a custom backend.
To integrate a custom API:
1. Navigate to `data/source/AuthRemoteDataSource.kt`.
2. Implement your API logic (e.g., using Retrofit) inside the `AuthDataSource` interface methods.
3. The rest of the app (Domain and Presentation) will remain untouched, maintaining full feature parity!

---

## Contribution

Feel free to fork and improve this project! Pull requests are welcome.

---

## License

This project is licensed under the Apache-2.0 License.

---
*Note: Do not commit your real API key to public repositories.*