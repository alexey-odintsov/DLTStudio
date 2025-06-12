plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
    kotlin("plugin.serialization") version libs.versions.kotlin
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
            implementation(project(":extraction"))
            implementation(project(":ui-components"))
            implementation(project(":plugins:contract"))
            implementation(project(":data-utils"))
            implementation(project(":dlt-message"))
            implementation(project(":resources"))
            implementation(project(":charts"))
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(libs.kotlin.datetime)
            implementation(libs.kotlin.serializaion)
            implementation(compose.components.resources)
            implementation(libs.androidx.room.runtime)
            implementation(libs.sqlite.bundled)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }

    }
}

task("testClasses")

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("ksp", libs.androidx.room.compiler) // Fixes AppDatabase_Impl not found
}