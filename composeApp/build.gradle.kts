import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation(project(":dltparser"))
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}


compose.desktop {
    application {
        mainClass = "MainKt"
        jvmArgs += listOf("-Xmx8G")

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.alekso.dtlstudio"
            packageVersion = "1.0.0"
        }
    }
}
