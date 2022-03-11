---
title: "DELETE Queries"
linkTitle: "DELETE"
weight: 40
description: >
---

## Overview {#overview}

The DELETE query is constructed by calling `QueryDsl.delete` and subsequent functions.

## single

To delete a single entity, call the `single` function:

```kotlin
val address: Address = ..
val query: Query<Unit> = QueryDsl.delete(a).single(address)
/*
delete from ADDRESS as t0_ where t0_.ADDRESS_ID = ? and t0_.VERSION = ?
*/
```

Depending on the mapping definitions shown below, the SQL will reflect the appropriate values.

- `@KomapperId`
- `@KomapperVersion`

If optimistic locking fails during query execution,
the `org.komapper.core.OptimisticLockException` is thrown.

## batch

To delete multiple entities in a batch, call the `batch` function:

```kotlin
val address1: Address = ..
val address2: Address = ..
val address3: Address = ..
val query: Query<Unit> = QueryDsl.delete(a).batch(address1, address2, address3)
/*
delete from ADDRESS as t0_ where t0_.ADDRESS_ID = ? and t0_.VERSION = ?
delete from ADDRESS as t0_ where t0_.ADDRESS_ID = ? and t0_.VERSION = ?
delete from ADDRESS as t0_ where t0_.ADDRESS_ID = ? and t0_.VERSION = ?
*/
```

Depending on the mapping definitions shown below, the SQL will reflect the appropriate values.

- `@KomapperId`
- `@KomapperVersion`

If optimistic locking fails during query execution,
the `org.komapper.core.OptimisticLockException` is thrown.

## all

To delete all rows, call the `all` function:

```kotlin
val query: Query<Int> = QueryDsl.delete(e).all().options { it.copy(allowMissingWhereClause = true) }
/*
delete from EMPLOYEE as t0_
*/
```

To allow deleting of all rows, you have to call the `options` function and
set the `allowMissingWhereClause` property to true.

When the above query is executed, the return value is the number of deleted rows.

## where

To delete rows that match specific criteria, call the `where` function:

```kotlin
val query: Query<Int> = QueryDsl.delete(a).where { a.addressId eq 15 }
/*
delete from ADDRESS as t0_ where t0_.ADDRESS_ID = ?
*/
```

By default, an exception is thrown if a WHERE clause is missing.
To intentionally allow deleting of all rows, call the `options` function and
set the `allowMissingWhereClause` property to true:

```kotlin
val query: Query<Int> = QueryDsl.delete(e).where {}.options { it.copy(allowMissingWhereClause = true) }
/*
delete from EMPLOYEE as t0_
*/
```

When the above query is executed, the return value is the number of deleted rows.

## options

To customize the behavior of the query, call the `options` function.
The `options` function accept a lambda expression whose parameter represents default options.
Call the `copy` function on the parameter to change its properties:

```kotlin
val address: Address = ..
val query: Query<Unit> = QueryDsl.delete(a).single(address).options {
    it.copty(
      queryTimeoutSeconds = 5
    )
}
```

The options that can be specified are as follows:

allowMissingWhereClause
: Whether empty WHERE clauses are allowed or not. Default is `false`.

escapeSequence
: Escape sequence specified for the LIKE predicate. The default is `null` to indicate the use of Dialect values.

batchSize
: Default is `null`.

disableOptimisticLock
: Whether to disable optimistic locking.
Default is `false`.
When this value is `true`, the version number is not included in the WHERE clause.

queryTimeoutSeconds
: Query timeout in seconds. Default is `null` to indicate that the driver value should be used.

suppressLogging
: Whether to suppress SQL log output. Default is `false`.

suppressOptimisticLockException
: Whether to suppress the throwing of `OptimisticLockException` if an attempt to acquire an optimistic lock fails.
Default is `false`.

Properties explicitly set here will be used in preference to properties with the same name that exist
in [executionOptions]({{< relref "../../database-config/#executionoptions" >}}).
