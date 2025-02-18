plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":dlt-message"))
            implementation(libs.kotlin.datetime)
        }
    }
}

task("testClasses")
