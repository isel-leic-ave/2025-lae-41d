plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    alias(libs.plugins.jvm)
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    id("me.champeau.jmh") version "0.7.3"

    // Apply the java-library plugin for API and implementation separation.
    `java-library`
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {

    implementation(kotlin("reflect"))
    // Use the Kotlin JUnit 5 integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    implementation(project(":week07:sample18-naivemapper-jmh"))
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(22))
    }
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}

/*tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "pt.isel.sample24.DynamicMapperBenchmark_jmhType"
    }
}*/