import com.google.firebase.appdistribution.gradle.firebaseAppDistribution

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.appdistribution")
}

android {
    namespace = "com.noljanolja.android"
    compileSdk = 33
    defaultConfig {
        applicationId = "com.noljanolja.app.android"
        minSdk = 21
        targetSdk = 33
        versionCode = 3
        versionName = "0.0.3"
        testInstrumentationRunner = "com.noljanolja.android.InstrumentationTestRunner"
        multiDexEnabled = true
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

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.activity:activity-compose:1.6.1")
    implementation(platform("androidx.compose:compose-bom:2023.01.00"))
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0-alpha01")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.0")
    implementation("androidx.navigation:navigation-compose:2.5.3")
    // Hilt
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-android-compiler:2.44")
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:31.2.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.android.gms:play-services-auth:20.4.1")
    implementation("com.google.firebase:firebase-functions-ktx")
    // Splash
    implementation("androidx.core:core-splashscreen:1.0.0")

    // Kakao
    implementation("com.kakao.sdk:v2-user:2.12.1")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")

    // Naver
    implementation("com.navercorp.nid:oauth:5.4.0")
}

kapt {
    correctErrorTypes = true
}
