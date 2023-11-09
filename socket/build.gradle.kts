plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("kotlinx-serialization")
}

android {
    namespace = "com.noljanolja.socket"
    compileSdk = 33
    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }
    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"ws://34.64.110.104\"")
        }
        release {
            buildConfigField("String", "BASE_URL", "\"ws://consumer-service.ppnyy.com\"")
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
    // Note: iosSimulatorArm64 target requires that all dependencies have M1 support
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        framework {
            baseName = "socket"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("io.rsocket.kotlin:rsocket-ktor-client:0.15.4")
//                api("io.ktor:ktor-client-okhttp:2.1.1")
                api("io.ktor:ktor-client-core:2.1.1")
                api("io.ktor:ktor-client-logging:2.1.1")
                // koin
                implementation("io.insert-koin:koin-core:3.3.3")
                api("co.touchlab:kermit:1.1.3")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:2.1.1")
            }
        }
        val androidUnitTest by getting
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
