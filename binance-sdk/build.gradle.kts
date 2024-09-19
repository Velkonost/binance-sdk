import org.jreleaser.model.Active

plugins {
    id("default-convention")
    id("serialization-convention")
    id("maven-publish")
    alias(libs.plugins.jreleaser)
}

dependencies {
    implementation(libs.bundles.ktor)
    implementation(libs.serialization.json)
}

java {
    withJavadocJar()
    withSourcesJar()
}

group = "io.github.velkonost"
version = libs.versions.binance.sdk.get()
description = "Unofficial wrapper for binance api in Kotlin"

publishing {

    publications {
        create<MavenPublication>("release") {
            from(components["java"])

            groupId = "io.github.velkonost"
            artifactId = "binance-sdk"

            pom {
                name.set(project.properties["POM_NAME"].toString())
                description.set(project.description)
                url.set("https://github.com/Velkonost/binance-sdk")
                issueManagement {
                    url.set("https://github.com/Velkonost/binance-sdk/issues")
                }

                scm {
                    url.set("https://github.com/Velkonost/binance-sdk")
                    connection.set("scm:git://github.com/Velkonost/binance-sdk.git")
                    developerConnection.set("scm:git://github.com/Velkonost/binance-sdk.git")
                }

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
            }

        }
    }

    repositories {
        maven {
            setUrl(layout.buildDirectory.dir("staging-deploy"))
        }
    }
}

jreleaser {
    project {
        inceptionYear = "2024"
        author("@velkonost")
    }
    gitRootSearch = true
    release {
        github {
            skipRelease = true
            skipTag = true
            sign = true
            branch = "main"
            branchPush = "main"
            overwrite = true
        }
    }
    signing {
        active = Active.ALWAYS
        armored = true
        verify = true
    }
    deploy {
        maven {
            mavenCentral.create("sonatype") {
                active = Active.ALWAYS
                url = "https://central.sonatype.com/api/v1/publisher"
                stagingRepository(layout.buildDirectory.dir("staging-deploy").get().toString())
                setAuthorization("Basic")
                retryDelay = 60
            }
        }
    }
}