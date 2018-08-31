import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.2.61"
}

group = "com.github.igorperikov"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/spekframework/spek-dev")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    testImplementation("com.natpryce", "hamkrest", "1.6.0.0")
    testImplementation("org.spekframework.spek2", "spek-dsl-jvm", "2.0.0-alpha.1")
        .exclude("org.jetbrains.kotlin")
    testRuntimeOnly("org.spekframework.spek2", "spek-runner-junit5", "2.0.0-alpha.1")
        .exclude("org.jetbrains.kotlin")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines("spek2")
    }
}
