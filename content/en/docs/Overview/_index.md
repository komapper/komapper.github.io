---
title: "Overview"
linkTitle: "Overview"
weight: 1
description: >
  Komapper is a simple and powerful SQL mapper for Kotlin 1.5 and later.
---

## What is it?

Komapper is SQL mapping library for Kotlin 1.5 or later.

Komapper has several strengths as follows:

- compile-time code generation
- annotation-free data models
- immutable and composable queries

## Compile-time code generation

Thanks to compile-time code generation, Komapper works without reflection.

Komapper uses [Kotlin Symbol Processing API](https://github.com/google/ksp)
to generate code at compile-time.

## Annotation-free data models

Kotlin Symbol Processing API requires annotations to generate code at compile-time. However, you may avoid specifying
annotations on your data models.

Komapper allows you to make your data model annotation-free as follows:

```kotlin
data class Employee(val id: Int, val name: String)

@KmEntityDef(Employee::class)
data class EmployeeDef(@KmId @KmAutoIncrement val id: Nothing) {
    companion object
}
```

In above example, `Employee` is not annotated.
Instead, EmployeeDef is annotated.

## Immutable and composable queries

You can write queries as follows:

```kotlin
val e = Employee.meta
val selectAll = EntityDsl.from(e)
val selectByAge30 = selectAll.where { e.age eq 30 }
val selectByJobSalesman = selectAll.where { e.job eq "SALESMAN" }
```

## Supported database

- PostgreSQL 11 and higher
- MySQL 8.0 and higher
- H2 1.4.200 and higher

## Where should I go next?

* [Quickstart](/docs/quickstart/): Get started with Komapper
* [Examples](/docs/examples/): Check out some example code!

