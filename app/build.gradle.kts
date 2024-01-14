import com.google.firebase.appdistribution.gradle.*

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
        applicationId = "com.ppnyy.nolgobulja"
        minSdk = 21
        targetSdk = 33
        versionCode = 62
        versionName = "1.1.11"//"1.1.45"
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
        create("release") {
            keyAlias = "alias_nolgo"
            keyPassword = "3131994no1"
            storeFile = file("../key/signin_release.jks")
            storePassword = "3131994no1"
        }
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
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
        val notes = file("release_notes.txt").readText()
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            firebaseAppDistribution {
                artifactType = "APK"
                serviceCredentialsFile = "key/key_distributor.json"
                releaseNotes = notes
                testers =
                    "doduchieu.kstn@gmail.com, itanchi.dev@gmail.com, sangjin.d.han@gmail.com, sangjin.han@ppnyy.com, tiaddeeps@gmail.com, taduydoan123.dng@gmail.com"
            }
            proguardFiles("proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
//            isDebuggable = true
        }
        debug {
            firebaseAppDistribution {
                artifactType = "APK"
                serviceCredentialsFile = "key/key_distributor.json"
                applicationIdSuffix = ".dev"
                releaseNotes = notes
                testers =
                    "doduchieu.kstn@gmail.com, itanchi.dev@gmail.com, sangjin.d.han@gmail.com, sangjin.han@ppnyy.com, tiaddeeps@gmail.com, taduydoan123.dng@gmail.com"
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

    implementation("com.google.code.gson:gson:2.10")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.activity:activity-compose:1.6.1")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
    implementation(project(mapOf("path" to ":firebase_auth")))
    implementation("androidx.lifecycle:lifecycle-process:2.6.0")
    implementation("androidx.cardview:cardview:1.0.0")
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
    implementation("com.google.firebase:firebase-config")

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
//    implementation("com.google.accompanist:accompanist-insets:0.29.1-alpha")
    implementation("com.googlecode.libphonenumber:libphonenumber:8.12.39")

    // koin
    implementation("io.insert-koin:koin-android:3.3.3")
    implementation("io.insert-koin:koin-androidx-compose:3.4.1")

    // Youtube
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")
    // chart
    implementation("com.patrykandpatrick.vico:core:1.6.5")
    implementation("com.patrykandpatrick.vico:compose-m3:1.6.5")

    // Qr
    implementation("com.google.zxing:core:3.4.1")
    implementation("com.google.mlkit:barcode-scanning:17.1.0")
    // CameraX
    implementation("androidx.camera:camera-camera2:1.2.3")
    implementation("androidx.camera:camera-lifecycle:1.2.3")
    implementation("androidx.camera:camera-view:1.3.0-alpha07")

    implementation("com.google.android.gms:play-services-ads:22.0.0")
    implementation("io.github.farimarwat:admobnative-compose:1.2")

    // Animation
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
}

kapt {
    correctErrorTypes = true
}
