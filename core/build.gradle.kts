plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("com.squareup.sqldelight")
    id("kotlinx-serialization")
}

android {
    namespace = "com.noljanolja.core"
    compileSdk = 33
    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }
}

kotlin {
    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        framework {
            baseName = "core"
        }
    }

    sourceSets {
        val sqlDelightVersion = "1.5.5"
        val commonMain by getting {
            dependencies {
                implementation("com.squareup.sqldelight:runtime:1.5.4")
                implementation("com.squareup.sqldelight:coroutines-extensions:1.5.4")

                api("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

                api("io.ktor:ktor-client-core:2.1.1")
                api("io.ktor:ktor-client-logging:2.1.1")
                api("io.ktor:ktor-client-auth:2.1.1")
                api("io.ktor:ktor-client-content-negotiation:2.1.1")
                api("io.ktor:ktor-client-serialization:2.1.1")
                api("io.ktor:ktor-serialization-kotlinx-json:2.1.1")
                api("io.rsocket.kotlin:rsocket-ktor-client:0.15.4")
                api("io.ktor:ktor-client-okhttp:2.1.1")

                api("co.touchlab:kermit:1.1.3")
                implementation("com.squareup.sqldelight:runtime:$sqlDelightVersion")
                implementation("com.squareup.sqldelight:coroutines-extensions:$sqlDelightVersion")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:2.1.1")
                implementation("com.squareup.sqldelight:android-driver:$sqlDelightVersion")
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation("io.ktor:ktor-client-ios:2.1.1")
            }
        }
    }
}

sqldelight {
    database("Noljanolja") {
        packageName = "com.noljanolja.core.db"
    }
}
