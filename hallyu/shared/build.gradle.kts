plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.androidLibrary)
}

val isMac = System.getProperty("os.name").startsWith("Mac", ignoreCase = true)

kotlin {
    androidTarget()
    if (isMac) {
        iosArm64()
        iosSimulatorArm64()
    }

    sourceSets {
        commonMain.dependencies {
            api(compose.runtime)
            api(compose.foundation)
            api(compose.material3)
            api(compose.ui)
            api(compose.components.resources)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.koin.core)
        }
    }
}

android {
    namespace = "app.hallyu.shared"
    compileSdk = 36
    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
