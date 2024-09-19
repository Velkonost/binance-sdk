plugins {
    id("default-convention")
    id("serialization-convention")
}

dependencies {
    implementation(libs.bundles.ktor)
    implementation(libs.serialization.json)
}