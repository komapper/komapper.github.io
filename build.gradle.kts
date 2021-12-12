plugins {
    base
}
val kotlinVersion: String by project
val kspVersion: String by project
val komapperVersion: String by project
val encoding: String by project
val branchName: String = komapperVersion.split(".").take(2).joinToString(".", prefix="v")

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

fun archive(key :String, old: String, new: String) {
    ant.withGroovyBuilder {
        "replaceregexp"("match" to "^$key = $old$",
            "replace" to "$key = $new",
            "encoding" to encoding,
            "flags" to "gm") {
            "fileset"("dir" to ".") {
                "include"("name" to "config.toml")
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

    register("archive") {
        doLast {
            archive("archived_version", "false", "true")
            archive("version", "\"main\"", "\"$branchName\"")
            archive("github_branch", "\"main\"", "\"$branchName\"")
            archive("algolia_docsearch", "true", "false")
            archive("offlineSearch", "false", "true")
        }
    }

    register("debug") {
        doLast {
            println("branchName: $branchName")
        }
    }
}
