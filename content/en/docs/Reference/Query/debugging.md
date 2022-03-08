---
title: "Debugging"
linkTitle: "Debugging"
weight: 120
description: >
---

## Overview {#overview}

You can see the SQL generated from a `Query` without having to connect to the database.

## dryRun

Calling the `dryRun` function on a `Query` allows you 
to see the SQL and the arguments bound to the query:

```kotlin
val query: Query<List<Address>> = QueryDsl.from(a).where { a.addressId eq 1 }
val result: DryRunResult = query.dryRun()
println(result)
```

The output result of the above code is as follows (line breaks are inserted for readability):

```sh
DryRunResult(
  sql=select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID = ?, 
  sqlWithArgs=select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID = 1, 
  args=[Value(any=1, klass=class kotlin.Int)], 
  throwable=null, 
  description=This data was generated using DryRunDatabaseConfig. To get more correct information, specify the actual DatabaseConfig instance.
)
```

The meaning of the properties of the `DryRunResult` class is as follows:

sql
: The SQL generated from the query.
Bind variables are represented by `?`.
If an exception occurs, the exception message is expressed instead of SQL.

sqlWithArgs
: The SQL with arguments generated from the query.
Bind variables are replaced by string representations of the arguments.
If an exception occurs, the exception message is expressed instead of SQL.

args
: Argument value/type pairs.

throwable
: Exception thrown during SQL generation. If no exception was thrown, `null`.

description
: Description for the instance of `DryRunResult`.

### Using Dialect {#dryrun-with-dialect}

The `dryRun` function with no arguments returns a result without considering 
the [Dialect]({{< relref "../dialect.md" >}}) of the destination database.
If you want to get the result considering Dialect, pass a `DatabaseConfig` instance.

```kotlin
val database: JdbcDatabase = ...
val query: Query<List<Address>> = QueryDsl.from(a).where { a.addressId eq 1 }
val result: DryRunResult = query.dryRun(database.config)
println(result)
```

Or call the `dryRun` function of the `Database` instance.

```kotlin
val database: JdbcDatabase = ...
val query: Query<List<Address>> = QueryDsl.from(a).where { a.addressId eq 1 }
val result: DryRunResult = database.dryRun(query)
println(result)
```

### Debugging during query construction {#dryrun-during-query-construction}

Combined with the `also` function, you can check query information during the construction process.

```kotlin
val query: Query<List<Address>> = QueryDsl.from(a)
    .also {
        println("1:" + it.dryRun().sql)
    }.where {
        a.addressId eq 1
    }.also {
        println("2:" + it.dryRun().sql)
    }.orderBy(a.addressId)
    .also {
        println("3:" + it.dryRun().sql)
    }
```

The results of executing the above code are as follows:

```sh
1:select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_
2:select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID = ?
3:select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID = ? order by t0_.ADDRESS_ID asc
```
