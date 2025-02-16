plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvm()

    sourceSets {
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlin.coroutines.swing)
            }
        }
        commonMain.dependencies {
            api(project(":modelContract"))
            implementation(project(":logger"))
            implementation(compose.runtime)
            implementation(compose.foundation)
        }
    }
}

task("testClasses")

tasks.withType<Test> {
    maxHeapSize = "8g"
}