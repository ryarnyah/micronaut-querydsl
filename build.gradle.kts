plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.10"
    id("org.jetbrains.kotlin.kapt") version "1.4.10"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.4.10"
    id("io.micronaut.library") version "1.2.0"
    id("io.codearte.nexus-staging") version "0.22.0"
    id("maven-publish")
    id("signing")
}

version = getGitVersion()
group = "com.github.ryarnyah"

val kotlinVersion = project.properties["kotlinVersion"]
val queryDslVersion = "4.4.0"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    kapt("io.micronaut:micronaut-inject-java")
    kapt("io.micronaut:micronaut-graal")

    api("io.micronaut:micronaut-management")
    api("io.micronaut.data:micronaut-data-tx")
    api("com.querydsl:querydsl-sql:${queryDslVersion}")

    testImplementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    testImplementation("io.micronaut:micronaut-management")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("io.micronaut.test:micronaut-test-junit5:2.3.1")
    testImplementation("io.micronaut.data:micronaut-data-jdbc")
    testImplementation("io.micronaut.sql:micronaut-jdbc-hikari")
    testRuntimeOnly("ch.qos.logback:logback-classic")
    testRuntimeOnly("com.h2database:h2:1.4.200")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

signing {
    isRequired = !isSnapshot

    val signingKey = System.getenv("GPG_SIGNING_KEY")
    val signingPassword = System.getenv("GPG_SIGNING_PASSWORD")
    useInMemoryPgpKeys(signingKey, signingPassword)

    sign(publishing.publications)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set("Micronaut support for Requery")
                url.set("https://github.com/ryarnyah/${project.name}")
                description.set("Micronaut support for Requery")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("ryarnyah")
                        name.set("Ryar Nyah")
                        email.set("ryarnyah@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git@github.com:ryarnyah/${project.name}.git")
                    developerConnection.set("scm:git:git@github.com:ryarnyah/${project.name}.git")
                    url.set("https://github.com/ryarnyah/${project.name}")
                }
            }
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots")
            url = if (isSnapshot) snapshotsRepoUrl else releasesRepoUrl
            if (!isSnapshot) {
                credentials {
                    username = System.getenv("SONATYPE_USERNAME")
                    password = System.getenv("SONATYPE_PASSWORD")
                }
            }
        }
    }
}

nexusStaging {
    username = System.getenv("SONATYPE_USERNAME")
    password = System.getenv("SONATYPE_PASSWORD")
}

java {
    sourceCompatibility = JavaVersion.toVersion("11")

    withJavadocJar()
    withSourcesJar()
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}


// Tooling
fun getGitVersion(defaultVersion: String = "0.0.1"): String {
    var gitLastTag = "git describe --abbrev=0 --tags".runCommand()
    if (gitLastTag.isEmpty()) {
        gitLastTag = defaultVersion
    }
    val gitCurrentTag = "git describe --exact-match --tags HEAD".runCommand()
    return gitLastTag + (if (gitCurrentTag != gitLastTag) "-SNAPSHOT" else "")
}

fun String.runCommand(workingDir: File = file("./")): String {
    val parts = this.split("\\s".toRegex())
    val proc = ProcessBuilder(*parts.toTypedArray())
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()

    proc.waitFor(1, TimeUnit.MINUTES)
    return proc.inputStream.bufferedReader().readText().trim()
}

inline val Project.isSnapshot
    get() = version.toString().endsWith("-SNAPSHOT")