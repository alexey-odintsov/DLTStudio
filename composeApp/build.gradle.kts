import org.jetbrains.compose.desktop.application.dsl.TargetFormat

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

        commonMain.dependencies {
            implementation(project(":logger"))
            implementation(project(":dltparser"))
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(libs.compose.splitpane)
            implementation(compose.components.resources)
            implementation(libs.kotlin.coroutines.swing)
            implementation(libs.androidx.room.runtime)
            implementation(libs.sqlite.bundled)
            implementation(libs.kotlin.serializaion)
            implementation(libs.kotlin.datetime)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val desktopTest by getting {
            dependencies {
                implementation(compose.desktop.uiTestJUnit4)
                implementation(compose.desktop.currentOs)
            }
        }

    }
}


compose.desktop {
    application {
        mainClass = "com.alekso.dltstudio.MainKt"
        jvmArgs += listOf("-Xmx8G")

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.alekso.dltstudio"
            packageVersion = "1.0.0"
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
