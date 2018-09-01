import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.2.61"
    jacoco
}

group = "com.github.igorperikov"
version = "0.0.1"

repositories {
    mavenCentral()
}

val junit5Version by extra { "5.1.1" }
val hamkrestVersion by extra { "1.6.0.0" }

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    testImplementation("com.natpryce", "hamkrest", hamkrestVersion)
    testImplementation("org.junit.jupiter", "junit-jupiter-api", junit5Version)
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", junit5Version)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines("junit-jupiter")
    }
}

tasks.withType<JacocoReport> {
    reports {
        xml.isEnabled = true
        html.isEnabled = false
    }
}
