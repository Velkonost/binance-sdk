import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import util.libs
import com.vanniktech.maven.publish.SonatypeHost


plugins {
    id("serialization-convention")
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kmm.publish)
    alias(libs.plugins.dokka)
}

kotlin {
    jvm()
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
        publishLibraryVariants("release", "debug")
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "binance-sdk"
            isStatic = true
        }
    }

    js {
        browser()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.bundles.ktor)
            implementation(libs.serialization.json)
            implementation(libs.slf4j.simple)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.concurrent.collections)
            implementation(libs.kotlinx.datetime)
            implementation(libs.cryptography.core)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.ios)
            implementation(libs.cryptography.ios)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.cryptography.jvm)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.cryptography.jvm)
        }
        jsMain.dependencies {
            implementation(libs.ktor.client.js)
            implementation(libs.cryptography.js)
        }
        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)
            implementation(libs.cryptography.js)
        }
    }
}

android {
    namespace = "com.velkonost.binance.sdk"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
    }
}

group = "com.velkonost"
version = libs.versions.binance.sdk.get()
description = "Unofficial wrapper for binance api in Kotlin"

mavenPublishing {
    // Define coordinates for the published artifact

    configure(
        KotlinMultiplatform(
            sourcesJar = true,
            javadocJar = JavadocJar.Dokka("dokkaHtml"),
            androidVariantsToPublish = listOf("debug", "release"),
        )
    )

    coordinates(
        groupId = project.group.toString(),
        artifactId = "binance-sdk",
        version = libs.versions.binance.sdk.get()
    )

    // Configure POM metadata for the published artifact
    pom {
        name.set("Binance SDK KMP Library")
        description.set(project.description)
        inceptionYear.set("2025")
        url.set("https://github.com/Velkonost/binance-sdk")

        licenses {
            license {
                name.set("The Apache Software License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }

        developers {
            developer {
                id.set("velkonost")
                name.set("Artem Klimenko")
                email.set("velkonost@gmail.com")
                url.set("t.me/velkonost")
            }
        }

        scm {
            url.set("https://github.com/Velkonost/binance-sdk")
        }
    }

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()
}
//publishing {
//
//    publications {
//        create<MavenPublication>("release") {
//            from(components["java"])
//
//            groupId = "io.github.velkonost"
//            artifactId = "binance-sdk"
//
//            pom {
//                name.set(project.properties["POM_NAME"].toString())
//                description.set(project.description)
//                url.set("https://github.com/Velkonost/binance-sdk")
//                issueManagement {
//                    url.set("https://github.com/Velkonost/binance-sdk/issues")
//                }
//
//                scm {
//                    url.set("https://github.com/Velkonost/binance-sdk")
//                    connection.set("scm:git://github.com/Velkonost/binance-sdk.git")
//                    developerConnection.set("scm:git://github.com/Velkonost/binance-sdk.git")
//                }
//
//                licenses {
//                    license {
//                        name.set("The Apache Software License, Version 2.0")
//                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
//                        distribution.set("repo")
//                    }
//                }
//
//                developers {
//                    developer {
//                        id.set("velkonost")
//                        name.set("Artem Klimenko")
//                        email.set("velkonost@gmail.com")
//                        url.set("t.me/velkonost")
//                    }
//                }
//            }
//
//        }
//    }
//
//    repositories {
//        maven {
//            setUrl(layout.buildDirectory.dir("staging-deploy"))
//        }
//    }
//}
//
//jreleaser {
//    project {
//        inceptionYear = "2024"
//        author("@velkonost")
//    }
//    gitRootSearch = true
//    release {
//        github {
//            skipRelease = true
//            skipTag = true
//            sign = true
//            branch = "main"
//            branchPush = "main"
//            overwrite = true
//        }
//    }
//    signing {
//        active = Active.ALWAYS
//        armored = true
//        verify = true
//    }
//    deploy {
//        maven {
//            mavenCentral.create("sonatype") {
//                active = Active.ALWAYS
//                url = "https://central.sonatype.com/api/v1/publisher"
//                stagingRepository(layout.buildDirectory.dir("staging-deploy").get().toString())
//                setAuthorization("Basic")
//                retryDelay = 60
//            }
//        }
//    }
//}