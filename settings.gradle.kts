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

include(":composeApp")
include(":dataUtils")
include(":dltMessage")
include(":dltparser")
include(":modelContract")
include(":logger")
include(":uicomponents")
include(":plugins:contract")
include(":plugins:manager")
include(":plugins:predefinedPlugins")
include(":plugins:deviceAnalyze")
include(":plugins:filesViewer")
include(":plugins:testPlugin")
