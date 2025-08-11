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

include(":resources")
include(":extraction")
include(":desktop-app")
include(":app-theme")
include(":data-utils")
include(":dlt-message")
include(":dlt-parser")
include(":model-contract")
include(":logger")
include(":ui-components")
include(":charts")
include(":plugins:contract")
include(":plugins:manager")
include(":plugins:predefined-plugins")
include(":plugins:device-analyze")
include(":plugins:diagram-timeline")
include(":plugins:files-viewer")
include(":plugins:virtual-device")
include(":plugins:dlt-detailed-view")
include(":plugins:log-info-view")
include(":plugins:log-insights")
include(":plugins:test-plugin")
