plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    api(libs.gradle.kotlin)
    api(libs.gradle.kotlinx.serialization)

    implementation(libs.gradle.plugin.ksp)
    implementation(libs.gradle.plugin.ktlint)
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
