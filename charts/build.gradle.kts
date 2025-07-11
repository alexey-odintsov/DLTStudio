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
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(libs.kotlin.datetime)
        }

        desktopMain.dependencies {
            implementation(compose.material3)
            implementation(compose.desktop.currentOs)
        }

    }
}

task("testClasses")
