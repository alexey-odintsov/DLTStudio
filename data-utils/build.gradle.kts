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
            implementation(libs.alexey.odintsov.logger)
        }
    }
}

task("testClasses")

tasks.withType<Test> {
    maxHeapSize = "8g"
}