---
title: "Overview"
linkTitle: "Overview"
weight: 1
description: >
  Komapper is a simple and powerful SQL mapper for Kotlin.
---

## What is it?

Komapper is SQL mapping library for Kotlin 1.5 or later.

Komapper has several strengths as follows:

- compile-time code generation
- annotation-free data models
- value class support
- immutable and composable queries
- upsert (insert-or-update) query support

### Compile-time code generation

Thanks to compile-time code generation using [Kotlin Symbol Processing API](https://github.com/google/ksp),
Komapper works without reflection.

### Annotation-free data models

Kotlin Symbol Processing API requires annotations to process source code.
However, you can make your data model annotation-free as follows:

```kotlin
// data model: any annotations are not required
data class Employee(val id: Int = 0, val name: String, val age: Int, val job: String)

// mapping definition: some annotation are required
@KmEntityDef(Employee::class)
data class EmployeeDef(@KmId @KmAutoIncrement val id: Nothing) {
    companion object
}
```

### Value class support

You can use a value class as a property of your data model as follows:

```kotlin
@JvmInline
value class Job(val value: String)

data class Employee(val id: Int = 0, val name: String, val age: Int, val job: Job)

@KmEntityDef(Employee::class)
data class EmployeeDef(@KmId @KmAutoIncrement val id: Nothing) {
    companion object
}
```


### Immutable and composable queries

You can compose queries as follows:

```kotlin
val e = EmployeeDef.meta
val selectAll = EntityDsl.from(e)
val selectByAge30 = selectAll.where { e.age eq 30 }
val selectByAge30AndSalesman = selectByAge30.where { e.job eq "SALESMAN" }
```

### Upsert query support

You can issue upsert query as follows:

```kotlin
val e = EmployeeDef.meta

EntityDsl.insert(e)
  .onDuplicateKeyUpdate(e.name)
  .single(Employee(name = "ABC", age = 20, job = "SALESMAN"))
```

For example, if you use PostgreSQL, the above query is translated as follows:

```sql
insert into EMPLOYEE as t0_ (NAME, AGE, JOB) values (?, ?, ?) 
on conflict (NAME) do update set 
NAME = excluded.NAME, AGE = excluded.AGE, JOB = excluded.JOB
```

## Supported database

- PostgreSQL 11 and higher
- MySQL 8.0 and higher
- H2 1.4.200 and higher

## Where should I go next?

* [Quickstart](/docs/quickstart/): Get started with Komapper
* [Examples](/docs/examples/): Check out some example code!

