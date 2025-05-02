plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "2025-lae-41d"
include("week01:sample03-reflect")
include("week02:sample04-logger")
include("week02:sample05-naivemapper")
include("jsonoy")
include("week03:sample06-naivemapper-annotations")
include("week04:sample07-naivemapper-recursive-and-generics")
include("week06:sample17-simple-bench-and-jmh")
include("week07:sample18-naivemapper-jmh")
include("week09:sample22-class-file")
include("week09:sample23-dynamic-mapper-metaprogramming")
include("week10:sample24-dynamic-mapper-metaprogramming-lists")
