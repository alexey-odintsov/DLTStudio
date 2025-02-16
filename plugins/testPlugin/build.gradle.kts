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
            implementation(project(":uiComponents"))
            implementation(project(":plugins:contract"))
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }

    }
}
