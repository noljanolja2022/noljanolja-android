pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        jcenter()
    }
}

dependencyResolutionManagement {
//    versionCatalogs {
//        create("libs") {
//            from(files("./libs.versions.toml"))
//        }
//    }
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven { url = uri("https://devrepo.kakao.com/nexus/content/groups/public/") }
    }
}
rootProject.name = "Noljanolja"
include(":app")
include(":firebase_auth")
include(":core")
