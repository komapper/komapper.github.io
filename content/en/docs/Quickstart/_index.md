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
  val kotlinVersion: String by settings
  val kspVersion: String by settings
  repositories {
    gradlePluginPortal()
    google()
  }
  plugins {
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    id("com.google.devtools.ksp") version kspVersion
  }
}

rootProject.name = "komapper-quickstart"
```

Next, include the following code in your build.gradle.kts:

```kotlin
plugins {
  application
  idea
  kotlin("jvm")
  id("com.google.devtools.ksp")
}

repositories {
  mavenCentral()
  google()
}

dependencies {
  val komapperVersion: String by project
  implementation("org.komapper:komapper-starter:$komapperVersion")
  ksp("org.komapper:komapper-processor:$komapperVersion")
}
```

The version number of `komapper-starter` and `komapper-processor` must be same.
Note that komapper-processor must be defined with `ksp` keyword.

## Try it out!

We create the application that connects to H2 Database.

### Source code

First, create an entity class and its mapping definition:

```kotlin
data class Employee(
  val id: Int = 0,
  val name: String,
  val version: Int = 0,
  val createdAt: LocalDateTime = LocalDateTime.MIN,
  val updatedAt: LocalDateTime = LocalDateTime.MIN,
)

@KomapperEntityDef(Employee::class)
data class EmployeeDef(
  @KomapperId @KomapperAutoIncrement val id: Nothing,
  @KomapperVersion val version: Nothing,
  @KomapperCreatedAt val createdAt: Nothing,
  @KomapperUpdatedAt val updatedAt: Nothing,
) {
  companion object
}
```

Next, create a main logic:

```kotlin
fun main() {
  // (1) create a database instance
  val database = JdbcDatabase.create("jdbc:h2:mem:quickstart;DB_CLOSE_DELAY=-1")

  // (2) start transaction
  database.withTransaction {

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

## Get complete code

To get complete code,
see https://github.com/komapper/komapper-quickstart