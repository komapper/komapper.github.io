---
title: "Quickstart"
linkTitle: "Quickstart"
weight: 2
description: >
  Try Komapper with minimum settings.
---

## Prerequisites

- JDK 8 or later
- Kotlin 15 or later
- Gradle 7 or later

## Install

We recommend that you install JDK using [sdkman](https://sdkman.io/).

## Setup

Write your Gradle build scripts using Kotlin DSL.

Komapper uses [Kotlin Symbol Processing (KSP)](https://github.com/google/ksp) to generate source code at compile-time.
For more details about KSP settings, see https://github.com/google/ksp/blob/master/docs/quickstart.md.

First, include the following code in your settings.gradle.kts:

```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }
}

rootProject.name = "komapper-quickstart"
```
The `pluginManagement` section is required to use KSP.

Next, include the following code in your build.gradle.kts:

```kotlin
plugins {
  application
  idea
  kotlin("jvm") version "1.5.0"
  id("com.google.devtools.ksp") version "1.5.0-1.0.0-alpha10"
}

val generatedSourcePath = "build/generated/ksp/main/kotlin"

sourceSets {
  main {
    java {
      srcDir(generatedSourcePath)
    }
  }
}

idea.module {
  generatedSourceDirs.add(file(generatedSourcePath))
}

repositories {
  mavenCentral()
  google()
}

dependencies {
  implementation("org.komapper:komapper-starter:0.10.0")
  ksp("org.komapper:komapper-processor:0.10.0")
}

application {
  mainClass.set("org.komapper.quickstart.ApplicationKt")
}
```

The version number of `komapper-starter` and `komapper-processor` must be same.
Note that komapper-processor must be defined with `ksp` keyword instead of `implementation` keyword.

## Try it out!

We create the application that connects to H2 Database.

### Source code

First, create an Entity class and its mapping definition:

```kotlin
data class Employee(
  val id: Int = 0,
  val name: String,
  val version: Int = 0,
  val createdAt: LocalDateTime = LocalDateTime.MIN,
  val updatedAt: LocalDateTime = LocalDateTime.MIN,
)

@KmEntityDef(Employee::class)
data class EmployeeDef(
  @KmId @KmAutoIncrement val id: Nothing,
  @KmVersion val version: Nothing,
  @KmCreatedAt val createdAt: Nothing,
  @KmUpdatedAt val updatedAt: Nothing,
) {
  companion object
}
```

Next, create a main logic:

```kotlin
fun main() {
  // (1) create a database instance
  val database: Database = Database.create("jdbc:h2:mem:quickstart;DB_CLOSE_DELAY=-1")

  // (2) start transaction
  database.transaction {

    // (3) get an entity metamodel
    val e = EmployeeDef.meta

    // (4) create schema
    database.runQuery {
      SchemaDsl.create(e)
    }

    // (5) insert multiple employees at once
    database.runQuery {
      EntityDsl.insert(e).multiple(Employee(name = "AAA"), Employee(name = "BBB"))
    }

    // (6) select all
    val employees = database.runQuery {
      EntityDsl.from(e).orderBy(e.id)
    }

    // (7) print all results
    for ((i, employee) in employees.withIndex()) {
      println("RESULT $i: $employee")
    }
  }
}
```

### Build the application

Run the following command:

```sh
$ ./grdlew build
```

Check the `build/generated/ksp/main/kotlin` directory.
KSP outputs generated code to that directory.

### Run the application

Run the following command:

```sh
$ ./grdlew run
```

You can see the following outputs in your console:

```
21:00:53.099 [main] DEBUG org.komapper.SQL - create table if not exists employee (id integer not null auto_increment, name varchar(500) not null, version integer not null, created_at timestamp not null, updated_at timestamp not null, constraint pk_employee primary key(id));
21:00:53.117 [main] DEBUG org.komapper.SQL - insert into employee (name, version, created_at, updated_at) values (?, ?, ?, ?), (?, ?, ?, ?)
21:00:53.140 [main] DEBUG org.komapper.SQL - select t0_.id, t0_.name, t0_.version, t0_.created_at, t0_.updated_at from employee as t0_ order by t0_.id asc
RESULT 0: Employee(id=1, name=AAA, version=0, createdAt=2021-05-05T21:00:53.115127, updatedAt=2021-05-05T21:00:53.115127)
RESULT 1: Employee(id=2, name=BBB, version=0, createdAt=2021-05-05T21:00:53.115250, updatedAt=2021-05-05T21:00:53.115250)
```

Notice that the ID and timestamp values are set automatically.

## Complete source code

To get complete source code,
see https://github.com/komapper/komapper-quickstart