---
title: "Quickstart"
linkTitle: "Quickstart"
weight: 2
description: >
  Try Komapper with minimum settings.
---

## Overview

We show you how to create an application that uses JDBC to access H2 Database Engine.

## Prerequisites

- JDK 8 or later
- Gradle 7.2

## Install

Install JDK and Gradle.

{{< alert title="Note" >}}
We recommend that you install JDK using [sdkman](https://sdkman.io/).
{{< /alert >}}

## Create Application
### Build Script

Write your build scripts using Gradle Kotlin DSL.

Include the following code in your build.gradle.kts:

```kotlin
plugins {
  application
  id("com.google.devtools.ksp") version "1.5.30-1.0.0"
  kotlin("jvm") version "1.5.30"
}

repositories {
  mavenCentral()
}

dependencies {
  val komapperVersion = "0.17.0"
  implementation("org.komapper:komapper-starter-jdbc:$komapperVersion")
  implementation("org.komapper:komapper-dialect-h2-jdbc:$komapperVersion")
  ksp("org.komapper:komapper-processor:$komapperVersion")
}

application {
  mainClass.set("org.komapper.quickstart.ApplicationKt")
}

kotlin {
  sourceSets.main {
    kotlin.srcDir("build/generated/ksp/main/kotlin")
  }
}
```

In the `dependencies` block, there are 3 modules.
Note they have same version number.

komapper-starter-jdbc module
: is necessary and useful module for JDBC

komapper-dialect-h2-jdbc module
: is required to access H2 Database Engine

komapper-processor module
: generates code at compile-time using [Kotlin Symbol Processing API](https://github.com/google/ksp)

### Source code

First, create an entity class and its mapping definition class:

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

### Build

To build your application, execute the following command:

```sh
$ gradle build
```

Check the `build/generated/ksp/main/kotlin` directory.
Kotlin Symbol Processing API outputs generated code to that directory.

### Run

To run your application, execute the following command:

```sh
$ gradle run
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

In the above repository, Gradle Wrapper is available.
So you can execute `./gradlew build` and `./gradlew run` instead of `gradle build` and `gradle run`.