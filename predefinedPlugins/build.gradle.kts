@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            implementation(project(":plugins"))
            implementation(project(":pluginFilesViewer"))
            implementation(project(":pluginDeviceAnalyze"))
        }
    }
}

