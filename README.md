# Mapbox Navigation SDK Integration for Android

## Overview
This project integrates Mapbox Navigation SDK into an Android application, enabling real-time navigation with turn-by-turn guidance.

## Prerequisites
- Android Studio installed
- A Mapbox account
- Android device or emulator running API level 21 or higher

## Step 1: Create a Secret Token
To access the SDK dependencies, you need a secret token.

1. Go to your Mapbox account's [tokens page](https://account.mapbox.com/).
2. Click **Create a token**.
3. Name your token (e.g., `InstallTokenAndroid`).
4. In the **Secret Scope** section, check `Downloads:Read`.
5. Click **Create token**, enter your password, and save the token securely.

### Store the Secret Token
Add your token to the global `gradle.properties` file located at `~/.gradle/gradle.properties`:
```properties
MAPBOX_DOWNLOADS_TOKEN=YOUR_SECRET_MAPBOX_ACCESS_TOKEN
```

## Step 2: Configure Public Token
1. Navigate to `app/res/values/` in your Android Studio project.
2. Create a new XML file named `mapbox_access_token.xml`.
3. Add the following content:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources xmlns:tools="http://schemas.android.com/tools">
    <string name="mapbox_access_token" translatable="false" tools:ignore="UnusedResources">YOUR_MAPBOX_ACCESS_TOKEN</string>
</resources>
```

## Step 3: Add Required Permissions
Ensure the following permissions are present in `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_LOCATION" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## Step 4: Add Mapbox Maven Repository
In `settings.gradle.kts`, add the following under `dependencyResolutionManagement.repositories`:
```kotlin
maven {
    url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
    authentication {
        create<BasicAuthentication>("basic")
    }
    credentials {
        username = "mapbox"
        password = providers.gradleProperty("MAPBOX_DOWNLOADS_TOKEN").get()
    }
}
```

## Step 5: Add Navigation SDK Dependencies
In `build.gradle.kts` (module-level):
```kotlin
dependencies {
    implementation("com.mapbox.navigationcore:android:3.7.1")
    implementation("com.mapbox.navigationcore:navigation:3.7.1")
    implementation("com.mapbox.navigationcore:copilot:3.7.1")
    implementation("com.mapbox.navigationcore:ui-maps:3.7.1")
    implementation("com.mapbox.navigationcore:voice:3.7.1")
    implementation("com.mapbox.navigationcore:tripdata:3.7.1")
    implementation("com.mapbox.navigationcore:ui-components:3.7.1")
}
```
Ensure `minSdk` is set to at least 21:
```kotlin
android {
    defaultConfig {
        minSdk = 21
    }
}
```

## Step 6: Implement Navigation UI
Use the following files:
- **activity_main.xml**: Define the UI layout.
- **MainActivity.java**: Implement the navigation logic.

Place the appropriate contents from:
- `C:\Users\hp\StudioProjects\map\app\src\main\res\layout\activity_main.xml`
- `C:\Users\hp\StudioProjects\map\app\src\main\java\com\example\map\MainActivity.java`

## Step 7: Run the Application
1. Sync Gradle and build the project.
2. Connect a device or run an emulator.
3. Launch the application and start navigation.

---
### Notes
- Keep your secret tokens private.
- Ensure location permissions are granted at runtime.
- Use **PermissionsManager** to handle runtime permissions dynamically.

This README provides a step-by-step guide to integrate Mapbox Navigation SDK into an Android app successfully. 🚀

