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
    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"http://34.64.110.104\"")
        }
        release {
            buildConfigField("String", "BASE_URL", "\"http://34.64.110.104\"")
        }
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
    ios()
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
                api(project(":socket"))
                implementation("com.squareup.sqldelight:runtime:1.5.4")
                implementation("com.squareup.sqldelight:coroutines-extensions:1.5.4")

                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
                api("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
                api("com.squareup.okio:okio:3.0.0")
                api("io.ktor:ktor-client-core:2.1.1")
                api("io.ktor:ktor-client-logging:2.1.1")
                api("io.ktor:ktor-client-auth:2.1.1")
                api("io.ktor:ktor-client-content-negotiation:2.1.1")
                api("io.ktor:ktor-client-serialization:2.1.1")
                api("io.ktor:ktor-serialization-kotlinx-json:2.1.1")
                api("io.rsocket.kotlin:rsocket-ktor-client:0.15.4")
//                api("io.ktor:ktor-client-okhttp:2.1.1")

                api("co.touchlab:kermit:1.1.3")
                implementation("com.squareup.sqldelight:runtime:$sqlDelightVersion")
                implementation("com.squareup.sqldelight:coroutines-extensions:$sqlDelightVersion")
                // koin
                implementation("io.insert-koin:koin-core:3.3.3")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:2.1.1")
                implementation("com.squareup.sqldelight:android-driver:$sqlDelightVersion")
            }
        }
        val iosMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-darwin:2.1.1")
            }
        }
        val iosTest by getting
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosSimulatorArm64Test by getting {
            dependsOn(iosTest)
        }
    }
}

sqldelight {
    database("Noljanolja") {
        packageName = "com.noljanolja.core.db"
        version = 2
    }
}
