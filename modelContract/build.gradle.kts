plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":dltMessage"))
            implementation(project(":dltparser"))
            implementation(libs.kotlin.datetime)
        }
    }
}

task("testClasses")
