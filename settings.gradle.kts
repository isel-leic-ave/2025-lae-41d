plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

rootProject.name = "2025-lae-41d"
include("week01:sample03-reflect")
include("week02:sample04-logger")
