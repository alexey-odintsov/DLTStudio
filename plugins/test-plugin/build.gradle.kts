plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}


group = "com.alekso.dltstudio.plugins.testplugin"
version = "1.0.0"

kotlin {
    jvm("desktop") {
        withSourcesJar(publish = true)
    }

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
            implementation(project(":ui-components"))
            implementation(project(":plugins:contract"))
            implementation(project(":charts"))
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(libs.kotlin.datetime)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }

    }
}
