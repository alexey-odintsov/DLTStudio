plugins {
    alias(libs.plugins.kotlinMultiplatform)
    kotlin("plugin.serialization") version libs.versions.kotlin
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":dlt-message"))
            implementation(libs.kotlin.datetime)
            implementation(libs.kotlin.serializaion)
        }
    }
}

task("testClasses")
