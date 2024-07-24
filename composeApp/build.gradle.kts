import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
}

kotlin {
    jvm("desktop")

    // Room step6 part1 for adding ksp src directory to use AppDatabase::class.instantiateImpl() in iosMain:
    // Due to https://issuetracker.google.com/u/0/issues/342905180
    sourceSets.commonMain {
        kotlin.srcDir("build/generated/ksp/metadata")
    }

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
            implementation(libs.gson)
            implementation(libs.kotlin.coroutines.swing)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.room.compiler)
            implementation(libs.sqlite.bundled)
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
        mainClass = "MainKt"
        jvmArgs += listOf("-Xmx8G")

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.alekso.dtlstudio"
            packageVersion = "1.0.0"
        }
    }
}

task("testClasses")

//Room step3: path where we want to generate the schemas
room {
    schemaDirectory("$projectDir/schemas")
}

//Room step5  KSP For processing Room annotations , Otherwise we will get Is Room annotation processor correctly configured? error
dependencies {
    // Update: https://issuetracker.google.com/u/0/issues/342905180
    add("kspCommonMainMetadata", libs.androidx.room.compiler)
}

//Room step6 part 2 make all source sets to depend on kspCommonMainKotlinMetadata:  Update: https://issuetracker.google.com/u/0/issues/342905180
//tasks.withType<KotlinCompilationTask<*>>().configureEach {
//    if (name != "kspCommonMainKotlinMetadata") {
//        dependsOn("kspCommonMainKotlinMetadata")
//    }
//}