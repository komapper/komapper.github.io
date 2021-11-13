---
title: "Overview"
linkTitle: "Overview"
weight: 1
description: >
  Komapper is a simple and powerful SQL mapper for Kotlin.
---

## What is it?

Komapper is SQL mapping library for Kotlin 1.5.31 or later.

Komapper has several strengths as follows:

- Support for both JDBC and R2DBC
- Code generation at compile-time
- Immutable and composable queries
- Support for Kotlin value classes
- Easy Spring Boot integration

### Support for both JDBC and R2DBC

Komapper provides almost the same programming model for both JDBC and R2DBC.

JDBC sample code:

```kotlin
fun main() {
    // create a Database instance
    val db = JdbcDatabase.create("jdbc:h2:mem:example;DB_CLOSE_DELAY=-1")

    // get a metamodel
    val a = AddressDef.meta

    // execute simple CRUD operations in a transaction
    db.withTransaction {
        // create a schema
        db.runQuery {
            SchemaDsl.create(a)
        }

        // INSERT
        val newAddress = db.runQuery {
            QueryDsl.insert(a).single(Address(street = "street A"))
        }

        // SELECT
        val address1 = db.runQuery {
            QueryDsl.from(a).where { a.id eq newAddress.id }.first()
        }
    }
}
```

R2DBC sample code:

```kotlin
fun main() = runBlocking {
    // create a Database instance
    val db = R2dbcDatabase.create("r2dbc:h2:mem:///example;DB_CLOSE_DELAY=-1")

    // get a metamodel
    val a = AddressDef.meta

    // execute simple CRUD operations in a transaction
    db.withTransaction {
        // create a schema
        db.runQuery {
            SchemaDsl.create(a)
        }

        // INSERT
        val newAddress = db.runQuery {
            QueryDsl.insert(a).single(Address(street = "street A"))
        }

        // SELECT
        val address1 = db.runQuery {
            QueryDsl.from(a).where { a.id eq newAddress.id }.first()
        }
    }
}
```

### Code generation at compile-time

Komapper uses [Kotlin Symbol Processing API](https://github.com/google/ksp) to generate code at compile-time.

You can define an entity class and its mapping definition class as follows:

```kotlin
data class Address(
    val id: Int,
    val street: String,
    val version: Int
)

@KomapperEntityDef(Address::class)
data class AddressDef(
    @KomapperId val id: Nothing,
    @KomapperVersion val version: Nothing,
) {
    companion object
}
```

Kotlin Symbol Processing API generates metamodel code from the above code.
Using the generated code, you can build type-safe queries as follows:

```kotlin
// get a generated metamodel
val a = AddressDef.meta

// define a query
val query = QueryDsl.from(e).where { a.street eq "STREET 101" }.orderBy(a.id)
```

### Immutable and composable queries

Komapper query objects are immutable.
So you can compose them safely:

```kotlin
// get a generated metamodel
val a = AddressDef.meta

// define queries
val query1 = QueryDsl.from(a)
val query2 = query1.where { a.id eq 1 }
val query3 = query2.where { or { a.id eq 2 } }.orderBy(a.street)

// issue "select * from address"
db.runQuery { query1 }
// issue "select * from address where id = 1"
db.runQuery { query2 }
// issue "select * from address where id = 1 or id = 2 order by street"
db.runQuery { query3 }
```

### Support for Kotlin value classes

You can use a value class as a property of your entity class as follows:

```kotlin
@JvmInline
value class Age(val value: Int)

data class Employee(val id: Int = 0, val name: String, val age: Age)

@KomapperEntityDef(Employee::class)
data class EmployeeDef(@KomapperId @KomapperAutoIncrement val id: Nothing) {
    companion object
}
```

### Easy Spring Boot integration

We provide starter modules to make Spring Boot integration easy.
You can write your Gradle build script to access H2 Database Engine using JDBC as follows:

```kotlin
val komapperVersion: String by project

dependencies {
    implementation("org.komapper:komapper-spring-boot-starter-jdbc:$komapperVersion")
    implementation("org.komapper:komapper-dialect-h2-jdbc:$komapperVersion")
}
```

## Supported database

We support following databases:

| Database         | Version | JDBC | R2DBC |
|--------------------|:--------:|:----:|:-----:|
| H2 Database Engine |  1.4.200 |  v   |   v   |
| MariaDB            |     10.6 |  v   |   v   |
| MySQL              |      8.0 |  v   |   v   |
| PostgreSQL         |     13.0 |  v   |   v   |

## Where should I go next?

* [Quickstart]({{< relref "../Quickstart" >}}): Get started with Komapper
* [Examples]({{< relref "../Examples" >}}): Check out some example code!

