plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.noljanolja.android"
    compileSdk = 33
    defaultConfig {
        applicationId = "com.noljanolja.android"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "com.noljanolja.android.InstrumentationTestRunner"
    }
    buildFeatures {
        compose = true
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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
        getByName("release") {
            isMinifyEnabled = false
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
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.0")
    implementation("androidx.navigation:navigation-compose:2.5.3")

}
