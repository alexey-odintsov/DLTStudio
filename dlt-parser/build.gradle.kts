plugins {
    alias(libs.plugins.kotlinMultiplatform)
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
            implementation(project(":logger"))
            implementation(project(":data-utils"))
            implementation(project(":dlt-message"))
        }
    }
}

task("testClasses")

tasks.withType<Test> {
    maxHeapSize = "8g"
}