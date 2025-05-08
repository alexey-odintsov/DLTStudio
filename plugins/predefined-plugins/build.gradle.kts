plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(project(":plugins:contract"))
            implementation(project(":plugins:files-viewer"))
            implementation(project(":plugins:device-analyze"))
            implementation(project(":plugins:diagram-timeline"))
            implementation(project(":plugins:virtual-device"))
            implementation(project(":plugins:dlt-detailed-view"))
            implementation(project(":plugins:log-info-view"))
            implementation(project(":plugins:log-insights"))
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

