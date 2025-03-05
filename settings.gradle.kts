plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

rootProject.name = "2025-lae-41d"
include("week01:sample03-reflect")
include("week02:sample04-logger")
include("week02:sample05-naivemapper")
include("jsonoy")
include("week03:sample06-naivemapper-annotations")
include("week04:sample07-naivemapper-recursive-and-generics")