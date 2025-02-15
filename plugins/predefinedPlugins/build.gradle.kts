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
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(project(":plugins:filesViewer"))
            implementation(project(":plugins:deviceAnalyze"))
        }
    }
}

