plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            api(project(":modelContract"))
            implementation(project(":logger"))
            implementation(compose.runtime)
            implementation(compose.foundation)
        }
    }
}
