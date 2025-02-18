plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm()

    sourceSets {
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        commonMain.dependencies {
            implementation(project(":logger"))
        }
    }
}

task("testClasses")

tasks.withType<Test> {
    maxHeapSize = "8g"
}