val kotlinVersion = "1.5.31"
val kspVersion = "1.5.31-1.0.0"
val komapperVersion = "0.19.0"
val encoding = "UTF-8"

fun replaceVersion(version: String, prefix :String, suffix: String = "\"") {
    ant.withGroovyBuilder {
        "replaceregexp"("match" to """($prefix)[^"]*($suffix)""",
            "replace" to "\\1${version}\\2",
            "encoding" to encoding,
            "flags" to "g") {
            "fileset"("dir" to "content") {
                "include"("name" to "en/docs/Quickstart/_index.md")
                "include"("name" to "ja/docs/Quickstart/_index.md")
                "include"("name" to "ja/docs/Reference/gradle-plugin.md")
            }
        }
    }
}

tasks {
    register("updateVersion") {
        doLast {
            replaceVersion(kotlinVersion, """kotlin\("jvm"\) version """")
            replaceVersion(kspVersion, """id\("com.google.devtools.ksp"\) version """")
            replaceVersion(komapperVersion, """val komapperVersion = """")
            replaceVersion(komapperVersion, """id\("org.komapper.gradle"\) version """")
        }
    }
}
