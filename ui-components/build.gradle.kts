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
            implementation(project(":app-theme"))
            implementation(project(":logger"))
            implementation(project(":resources"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.material.icons)
        }

        desktopMain.dependencies {
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.extended)
            implementation(compose.desktop.currentOs)
        }

    }
}

task("testClasses")
