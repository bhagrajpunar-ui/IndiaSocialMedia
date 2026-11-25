# IndiaSocialMedia 🇮🇳

Instagram-inspired Android social networking app starter built with Kotlin and Jetpack Compose.

## Current starter features
- Compose home feed with sample posts and Stories row
- Bottom navigation: Home, Search, Reels, Create and Profile
- Demo like interaction
- Firebase SDK dependencies for Authentication, Firestore, Storage and FCM
- `AuthRepository`: email/password registration and sign-in, profile document creation
- `PostRepository`: publish a post document and listen to the newest feed
- `MediaUploadRepository`: upload selected image/video Uri to Firebase Storage
- Starter Firestore and Storage security rules

## App details
- Package: `com.indiasocialmedia.app`
- Minimum Android: API 24 (Android 7.0)
- Compile SDK: 35

## Connect Firebase
1. Open Firebase Console and create a Firebase project.
2. Add an Android app with package name `com.indiasocialmedia.app`.
3. Download `google-services.json` and place it inside the repository's `app/` folder.
4. In Firebase Authentication, enable **Email/Password** sign-in.
5. Create a Firestore database and a Storage bucket.
6. Publish the included `firestore.rules` in Firestore Rules and `storage.rules` in Storage Rules.
7. Open the project in Android Studio, sync Gradle, and run the `app` configuration.

The Google Services Gradle plugin is applied only when `app/google-services.json` exists, so the project can sync before Firebase config is added. The config file is ignored by Git in this starter; keep it local and do not commit service-account/private keys.

## Important
This is an early starter, not a finished or production-ready social network. Firebase helper classes and dependencies are added, but the current Compose screen still shows demo content; login UI, feed binding, upload flow, likes/comments, chat, Reels and Stories need further implementation. Review and test security rules before launch.
