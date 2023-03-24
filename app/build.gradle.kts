import com.google.firebase.appdistribution.gradle.firebaseAppDistribution

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.appdistribution")
    id("kotlinx-serialization")
    id("kotlin-parcelize")
}

android {
    namespace = "com.noljanolja.android"
    compileSdk = 33
    defaultConfig {
        applicationId = "com.noljanolja.app.android"
        minSdk = 21
        targetSdk = 33
        versionCode = 11
        versionName = "1.0.4"
        testInstrumentationRunner = "com.noljanolja.android.InstrumentationTestRunner"
        multiDexEnabled = true
    }

    signingConfigs {
        named("debug") {
            keyAlias = "signin_debug"
            keyPassword = "3131994no1"
            storeFile = file("../key/signin_debug.jks")
            storePassword = "3131994no1"
        }
    }

    buildFeatures {
        compose = true
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.0"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            firebaseAppDistribution {
                artifactType = "APK"
                serviceCredentialsFile = "key/key_distributor.json"
                releaseNotes = "Version release test app distributor and full flow auth"
                testers = "doduchieu.kstn@gmail.com"
            }
        }
        debug {
            firebaseAppDistribution {
                artifactType = "APK"
                serviceCredentialsFile = "key/key_distributor.json"
                releaseNotes = ""
                testers = "doduchieu.kstn@gmail.com"
            }
        }
    }
    applicationVariants.all {
        kotlin.sourceSets {
            getByName(name) {
                kotlin.srcDir("build/generated/ksp/$name/kotlin")
            }
        }
    }
}
dependencies {
    implementation(project(":core"))

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.activity:activity-compose:1.6.1")
    implementation(platform("androidx.compose:compose-bom:2023.01.00"))
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
    implementation(project(mapOf("path" to ":firebase_auth")))
    implementation("androidx.lifecycle:lifecycle-process:2.6.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.0")
    implementation("androidx.navigation:navigation-compose:2.5.3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.0-rc01")
    implementation("androidx.compose.material:material-icons-extended:1.4.0-beta02")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.0")
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:31.2.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")

    // Splash
    implementation("androidx.core:core-splashscreen:1.0.0")
    // Coil
    implementation("io.coil-kt:coil-compose:2.2.2")
    implementation("com.github.yalantis:ucrop:2.2.7")
    implementation("io.coil-kt:coil-compose:2.2.2")
    implementation("io.coil-kt:coil-gif:2.2.2")
    implementation("io.coil-kt:coil-video:2.2.2")
    implementation("com.github.penfeizhou.android.animation:awebp:2.17.0")

    implementation("com.google.accompanist:accompanist-pager:0.22.0-rc")

    // ktor
    implementation("io.ktor:ktor-client-android:2.1.1")

    // lottie
    implementation("com.airbnb.android:lottie-compose:6.0.0")

    implementation("com.google.accompanist:accompanist-permissions:0.29.1-alpha")
    implementation("com.google.accompanist:accompanist-insets:0.29.1-alpha")
    implementation("com.googlecode.libphonenumber:libphonenumber:8.12.39")

    // koin
    implementation("io.insert-koin:koin-android:3.3.3")
    implementation("io.insert-koin:koin-androidx-compose:3.4.1")
}

kapt {
    correctErrorTypes = true
}
