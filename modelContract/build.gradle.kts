plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":dltMessage"))
            implementation(libs.kotlin.datetime)
        }
    }
}

task("testClasses")
