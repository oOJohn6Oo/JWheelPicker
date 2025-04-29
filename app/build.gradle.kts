plugins {
    id("com.android.application")
    id("kotlin-android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "io.john6.demo.wheelpicker"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.john6.demo.wheelpicker"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        androidResources.localeFilters.addAll(setOf("zh", "en"))
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs["debug"]
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":JWheelPicker"))
//    implementation("io.github.oojohn6oo:jwheelpicker:1.3.4")
    implementation("androidx.compose.material:material:1.8.0")
    implementation("androidx.fragment:fragment-ktx:1.8.6")
}