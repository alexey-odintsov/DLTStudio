rootProject.name = "DLTStudio"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

include(":desktop-app")
include(":data-utils")
include(":dlt-message")
include(":dlt-parser")
include(":model-contract")
include(":logger")
include(":ui-components")
include(":plugins:contract")
include(":plugins:manager")
include(":plugins:predefined-plugins")
include(":plugins:device-analyze")
include(":plugins:files-viewer")
include(":plugins:test-plugin")
