# IndiaSocialMedia 🇮🇳

Instagram-inspired Android social networking app starter built with Kotlin and Jetpack Compose.

## Included in this first version
- Android app module and Gradle configuration
- Compose home feed with sample posts
- Stories row UI
- Bottom navigation: Home, Search, Reels, Create and Profile
- Like button state for demo posts
- Placeholder pages for features that need backend integration

## App details
- Package: `com.indiasocialmedia.app`
- Minimum Android: API 24 (Android 7.0)
- Compile SDK: 35

## Build
Open the repository in Android Studio, allow Gradle sync, then run the `app` configuration on an Android device or emulator.

## Planned next steps
Firebase Authentication, Firestore-backed posts and comments, image/video uploads with Firebase Storage, follow system, real Reels, Stories expiry, direct messages, push notifications, moderation and admin tools.

## Firebase setup
Create a Firebase project and register an Android app with package `com.indiasocialmedia.app`. Download `google-services.json` into the `app/` folder. Firebase SDK integration and security rules will be added in a subsequent step. Never commit service-account keys or private secrets.

**Note:** This repository currently contains a UI starter/demo, not a production-ready Instagram clone. Backend features are not connected yet.
