plugins {
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "app.hallyu.android"
    compileSdk = 36

    defaultConfig {
        applicationId = "app.hallyu.android"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation(project(":shared"))
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation(libs.koin.android)
}
