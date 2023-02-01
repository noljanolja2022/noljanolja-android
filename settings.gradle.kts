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
    }
}
rootProject.name = "Noljanolja"
include(":app")
