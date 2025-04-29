import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("com.android.library")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.vanniktech.maven.publish")
}

android {
    namespace = "io.john6.base.compose.jwheelpicker"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    implementation("androidx.compose.material:material:1.8.0")
    implementation("androidx.fragment:fragment-ktx:1.8.6")
    implementation("com.google.android.material:material:1.12.0")
}


mavenPublishing {
    coordinates("io.github.oojohn6oo", "jwheelpicker", "1.3.5")
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    pom {
        name.set("JWheelPicker")
        description.set("iOS-style wheel picker supports all Android versions starting from Android lollipop(5.0)")
        url.set("https://github.com/oOJohn6Oo/JWheelPicker")
        licenses {
            license {
                name = "DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE"
                url = "http://www.wtfpl.net/txt/copying/"
            }
        }
        developers {
            developer {
                id = "oOJohn6Oo"
                name = "John6"
                email = "john6.lq@gmail.com"
            }
        }
        scm {
            connection.set("scm:git:git://github.com/oOJohn6Oo/JWheelPicker.git")
            developerConnection.set("scm:git:ssh://github.com/oOJohn6Oo/JWheelPicker.git")
            url.set("https://github.com/oOJohn6Oo/JWheelPicker")
        }
    }
}