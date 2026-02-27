plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlin.coroutines.swing)
            }
        }
        commonMain.dependencies {
            implementation(project(":logger"))
            implementation(project(":app-theme"))
            implementation(project(":dlt-message"))
            implementation(project(":extraction"))
            implementation(project(":model-contract"))
            implementation(project(":ui-components"))
            implementation(project(":plugins:contract"))
            implementation(project(":resources"))
            implementation(libs.compose.components.resources)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.splitpane)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }

    }
}

task("testClasses")
