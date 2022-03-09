---
title: "UPDATE"
linkTitle: "UPDATE"
weight: 30
description: >
---

## Overview {#overview}

The UPDATE query is constructed by calling `QueryDsl.update` and subsequent functions.

If a duplicate key is detected during UPDATE query execution, 
the `org.komapper.core.UniqueConstraintException` is thrown.

## single {#single}

To update a single entity, call the `single` function:

```kotlin
val address: Address = ..
val query: Query<Address> = QueryDsl.update(a).single(address)
/*
update ADDRESS set STREET = ?, VERSION = ? + 1 where ADDRESS_ID = ? and VERSION = ?
*/
```

When the above query is executed, the return value is a new entity representing the updated data.

Depending on the mapping definitions shown below, both the SQL and the new entity will reflect the appropriate values.

- `@KomapperId`
- `@KomapperVersion`
- `@KomapperUpdatedAt`

If optimistic locking fails during query execution, 
the `org.komapper.core.OptimisticLockException` is thrown.

## batch {#batch}

To update multiple entities in a batch, call the `batch` function:

```kotlin
val address1: Address = ..
val address2: Address = ..
val address3: Address = ..
val query: Query<List<Address>> = QueryDsl.update(a).batch(address1, address2, address3)
/*
update ADDRESS set STREET = ?, VERSION = ? + 1 where ADDRESS_ID = ? and VERSION = ?
update ADDRESS set STREET = ?, VERSION = ? + 1 where ADDRESS_ID = ? and VERSION = ?
update ADDRESS set STREET = ?, VERSION = ? + 1 where ADDRESS_ID = ? and VERSION = ?
*/
```

When the above query is executed, the return value is a list of new entities representing the updated data.

Depending on the mapping definitions shown below, both the SQL and the new entities will reflect the appropriate values.

- `@KomapperVersion`
- `@KomapperUpdatedAt`

If optimistic locking fails during query execution,
the `org.komapper.core.OptimisticLockException` is thrown.

## set {#set}

To set a value to specific property, pass a lambda expression to the `set` function.
Within the lambda expression, values can be set to properties using the `eq` function:

```kotlin
val query: Query<Int> = QueryDsl.update(a).set {
  a.street eq "STREET 16"
}.where {
  a.addressId eq 1
}
/*
update ADDRESS as t0_ set STREET = ? where t0_.ADDRESS_ID = ?
*/
```

To set a value only if the value is not null, use the `eqIfNotNull` function:

```kotlin
val query: Query<Int> = QueryDsl.update(e).set {
  e.managerId eqIfNotNull managerId
  e.employeeName eq "test"
}.where {
  e.employeeId eq 1
}
```

When the above query is executed, the return value is the number of updated rows.

If you do not explicitly call the `eq` function for properties with the following mapping definitions
then the value is automatically set in the generated SQL:

- `@KomapperVersion`
- `@KomapperUpdatedAt`

If you explicitly call the `eq` function for those properties, the explicit value takes precedence.

## where {#update-where}

To update rows that match specific criteria, call the `where` function:

```kotlin
val query: Query<Int> = QueryDsl.update(a).set {
  a.street eq "STREET 16"
}.where {
  a.addressId eq 1
}
/*
update ADDRESS as t0_ set STREET = ? where t0_.ADDRESS_ID = ?
*/
```

By default, an exception is thrown if a WHERE clause is missing. 
To intentionally allow updating of all rows, call the `options` function and 
set the `allowEmptyWhereClause` property to true:

```kotlin
val query: Query<Int> = QueryDsl.update(e).set {
    e.employeeName eq "ABC"
}.options { 
    it.copy(allowEmptyWhereClause = true)
}
```

When the above query is executed, the return value is the number of updated rows.

## options

To customize the behavior of the query, call the `options` function.
The `options` function accept a lambda expression whose parameter represents default options.
Call the `copy` function on the parameter to change its properties:

```kotlin
val address: Address = ..
val query: Query<Address> = QueryDsl.update(a).single(address).options {
    it.copty(
      queryTimeoutSeconds = 5
    )
}
```

The options that can be specified are as follows:

allowEmptyWhereClause
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
