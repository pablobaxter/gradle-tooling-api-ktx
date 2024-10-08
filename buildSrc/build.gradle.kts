plugins {
    `kotlin-dsl`
    kotlin("jvm") version "2.0.20"
}

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.dokka.plugin)
    implementation(libs.ktlint.plugin)
    implementation(libs.dependency.analysis)
    implementation(libs.vanniktech)
}

gradlePlugin {
    plugins {
        create("frybitsLibraryPlugin") {
            id = "frybits-library"
            implementationClass = "com.frybits.gradle.FrybitsLibraryPlugin"
        }
    }
}
