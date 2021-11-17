plugins {
    kotlin("jvm") version "1.5.31"
    id("org.jetbrains.dokka") version "1.5.0"
    `maven-publish`
    signing
}

group = "io.github.monull"
version = "2.0.1"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(16))
    }
}

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("net.dv8tion:JDA:4.3.0_277")
}

tasks {
    test {
        useJUnitPlatform()
    }
    create<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }

    create<Jar>("dokkaJar") {
        archiveClassifier.set("javadoc")
        dependsOn("dokkaHtml")

        from("$buildDir/dokka/html/") {
            include("**")
        }
    }
}

publishing {
    repositories {
        mavenLocal()
        maven {
            name = "central"

            credentials.runCatching {
                val nexusUsername: String by project
                val nexusPassword: String by project
                username = nexusUsername
                password = nexusPassword
            }.onFailure {
                logger.warn("Failed to load nexus credentials, Check the gradle.properties")
            }

            url = uri(
                if ("SNAPSHOT" in version as String) {
                    "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                } else {
                    "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                }
            )
        }
    }

    publications {
        create<MavenPublication>("command") {
            artifactId = "command"
            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["dokkaJar"])

            pom {
                name.set("command")
                description.set("command library for jda")
                url.set("https://github.com/monull/command")

                licenses {
                    license {
                        name.set("GNU General Public License version 3")
                        url.set("https://opensource.org/licenses/GPL-3.0")
                    }
                }

                developers {
                    developer {
                        id.set("monull")
                        name.set("monull2452")
                        email.set("monull2452@gmail.com")
                        url.set("https://github.com/monull")
                        roles.addAll("developer")
                        timezone.set("Asia/Daejeon")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/monull/command.git")
                    developerConnection.set("scm:git:ssh://github.com:monull/command.git")
                    url.set("https://github.com/monull/command")
                }
            }
        }
    }
}

signing {
    isRequired = true
    sign(tasks.jar.get(), tasks["sourcesJar"], tasks["dokkaJar"])
    sign(publishing.publications["command"])
}