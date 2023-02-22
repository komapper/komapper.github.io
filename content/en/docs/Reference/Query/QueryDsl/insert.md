---
title: "INSERT Queries"
linkTitle: "INSERT"
weight: 20
description: >
---

## Overview {#overview}

The INSERT query is constructed by calling `QueryDsl.insert` and subsequent functions.

If a duplicate key is detected during INSERT query execution, 
the `org.komapper.core.UniqueConstraintException` is thrown.

## single

To add a single entity, call the `single` function:

```kotlin
val address: Address = Address(16, "STREET 16", 0)
val query: Query<Address> = QueryDsl.insert(a).single(address)
/*
insert into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, ?, ?)
*/
```

When the above query is executed, the return value is a new entity representing the added data.

Depending on the mapping definitions shown below, both the SQL and the new entity will reflect the appropriate values.

- `@KomapperAutoIncrement`
- `@KomapperSequence`
- `@KomapperVersion`
- `@KomapperCreatedAt`
- `@KomapperUpdatedAt`

## multiple

To add multiple entities in one statement, call the `multiple` function:

```kotlin
val query: Query<List<Address>> = QueryDsl.insert(a).multiple(
    Address(16, "STREET 16", 0),
    Address(17, "STREET 17", 0),
    Address(18, "STREET 18", 0)
)
/*
insert into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, ?, ?), (?, ?, ?), (?, ?, ?)
*/
```

When the above query is executed, the return value is a list of new entities representing the added data.

Depending on the mapping definitions shown below, both the SQL and the new entity will reflect the appropriate values.

- `@KomapperAutoIncrement`
- `@KomapperSequence`
- `@KomapperVersion`
- `@KomapperCreatedAt`
- `@KomapperUpdatedAt`

## batch

To add multiple entities in a batch, call the `batch` function.

```kotlin
val query: Query<List<Address>> = QueryDsl.insert(a).batch(
    Address(16, "STREET 16", 0),
    Address(17, "STREET 17", 0),
    Address(18, "STREET 18", 0)
)
/*
insert into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, ?, ?)
insert into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, ?, ?)
insert into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, ?, ?)
*/
```

When the above query is executed, the return value is a list of new entities representing the added data.

Depending on the mapping definitions shown below, both the SQL and the new entity will reflect the appropriate values.


- `@KomapperAutoIncrement`
- `@KomapperSequence`
- `@KomapperVersion`
- `@KomapperCreatedAt`
- `@KomapperUpdatedAt`

## onDuplicateKeyIgnore

Call the `onDuplicateKeyIgnore` function to ignore errors in case of duplicate keys.
The key to be checked for duplicates can be specified in the `onDuplicateKeyIgnore` function. 
If not specified, the primary key is used.

```kotlin
val address: Address = ..
val query: Query<Address?> = QueryDsl.insert(a).onDuplicateKeyIgnore().executeAndGet(address)
```

The SQL corresponding to the above query depends on which Dialect is used. 
For example, if you use MariaDB's Dialect, the SQL would be as follows:

```sql
insert ignore into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, ?, ?)
```

When using PostgreSQL's Dialect, the SQL would be as follows:

```sql
insert into ADDRESS as t0_ (ADDRESS_ID, STREET, VERSION) values (?, ?, ?) on conflict (ADDRESS_ID) do nothing
```

### executeAndGet {#onduplicatekeyignore-executeandget}

If the `executeAndGet` function is called following the `onDuplicateKeyIgnore` function, 
the return value is an entity representing the added data. 
If the key is a duplicate, `null` is returned.

### single {#onduplicatekeyignore-single}

If the `single` function is called following the `onDuplicateKeyIgnore` function,
the return value is driver specific.

### multiple {#onduplicatekeyignore-multiple}

If the `multiple` function is called following the `onDuplicateKeyIgnore` function,
the return value is driver specific.

### batch {#onduplicatekeyignore-batch}

If the `batch` function is called following the `onDuplicateKeyIgnore` function,
the return value is driver specific.

## onDuplicateKeyUpdate

Call the `onDuplicateKeyUpdate` function to update the target row when a key is duplicated.
The key to be checked for duplicates can be specified in the `onDuplicateKeyUpdate` function.
If not specified, the primary key is used.

```kotlin
val department: Department = ..
val query: Query<Address> = QueryDsl.insert(d).onDuplicateKeyUpdate().executeAndGet(department)
```

The SQL corresponding to the above query depends on which Dialect is used.
For example, if you use MariaDB's Dialect, the SQL would be as follows:

```sql
insert into DEPARTMENT (DEPARTMENT_ID, DEPARTMENT_NO, DEPARTMENT_NAME, LOCATION, VERSION) values (?, ?, ?, ?, ?) on duplicate key update DEPARTMENT_NO = values(DEPARTMENT_NO), DEPARTMENT_NAME = values(DEPARTMENT_NAME), LOCATION = values(LOCATION), VERSION = values(VERSION)
```

When using PostgreSQL's Dialect, the SQL would be as follows:

```sql
insert into DEPARTMENT as t0_ (DEPARTMENT_ID, DEPARTMENT_NO, DEPARTMENT_NAME, LOCATION, VERSION) values (?, ?, ?, ?, ?) on conflict (DEPARTMENT_ID) do update set DEPARTMENT_NO = excluded.DEPARTMENT_NO, DEPARTMENT_NAME = excluded.DEPARTMENT_NAME, LOCATION = excluded.LOCATION, VERSION = excluded.VERSION
```

{{< alert color="warning" title="Warning" >}}
Optimistic locking is not applied to the rows to be updated.
{{< /alert >}}

### executeAndGet {#onduplicatekeyupdate-executeandget}

If the `executeAndGet` function is called following onDuplicateKeyUpdate, 
the return value is an entity representing the data that was added or updated.

### single {#onduplicatekeyupdate-single}

If the `single` function is called following the `onDuplicateKeyUpdate` function,
the return value is driver specific.

### multiple {#onduplicatekeyupdate-multiple}

If the `single` function is called following the `multiple` function,
the return value is driver specific.

### batch {#onduplicatekeyupdate-batch}

If the `single` function is called following the `batch` function,
the return value is driver specific.

### set {#onduplicatekeyupdate-set}

After calling the `onDuplicateKeyUpdate` function,
you can call the `set` function to set specific values to the columns to be updated:

```kotlin
val department: Department = ..
val query = QueryDsl.insert(d).onDuplicateKeyUpdate().set { excluded ->
    d.departmentName eq "PLANNING2"
    d.location eq concat(d.location, concat("_", excluded.location))
}.single(department)
```

The `set` function accepts the lambda expression whose parameter is `excluded`.
The `excluded` represents the metamodel of the entity being added.
Therefore, the use of excluded allows for updates based on the data being added.

When using PostgreSQL's Dialect, the above query generates the following SQL:

```sql
insert into DEPARTMENT as t0_ (DEPARTMENT_ID, DEPARTMENT_NO, DEPARTMENT_NAME, LOCATION, VERSION) values (?, ?, ?, ?, ?) on conflict (DEPARTMENT_ID) do update set DEPARTMENT_NAME = ?, LOCATION = (concat(t0_.LOCATION, (concat(?, excluded.LOCATION))))
```

When using MySQL's Dialect, the above query generates the following SQL:

```sql
insert into DEPARTMENT (DEPARTMENT_ID, DEPARTMENT_NO, DEPARTMENT_NAME, LOCATION, VERSION) values (?, ?, ?, ?, ?) as excluded on duplicate key update DEPARTMENT_NAME = ?, LOCATION = (concat(DEPARTMENT.LOCATION, (concat(?, excluded.LOCATION))))
```

{{< alert color="warning" title="Warning" >}}
Not supported by MariaDB Dialect.
{{< /alert >}}

### where {#onduplicatekeyupdate-where}

After calling the `onDuplicateKeyUpdate` function,
you can call the `where` function to set search conditions:

```kotlin
val department: Department = ..
val query = QueryDsl.insert(d).onDuplicateKeyUpdate().where {
    d.departmentName eq "PLANNING"
}.single(department)
```

{{< alert color="warning" title="Warning" >}}
Not supported by MariaDB and MySQL Dialects.
{{< /alert >}}

## values

To set a value for each property and add one row, pass a lambda expression to the `values` function.
Within the lambda expression, values can be set to properties using the `eq` function:

```kotlin
val query: Query<Pair<Long, Int?>> = QueryDsl.insert(a).values {
  a.addressId eq 19
  a.street eq "STREET 16"
  a.version eq 0
}
/*
insert into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, ?, ?)
*/
```

To set a value only if the value is not null, use the `eqIfNotNull` function:

```kotlin
val query: Query<Pair<Long, Int?>> = QueryDsl.insert(a).values {
    a.addressId eq 19
    a.street eqIfNotNull street
    a.version eq 0
}
```

When the above query is executed, the return value is a `Pair` of the number of rows added and IDs generated.
IDs are returned only if `@KomapperAutoIncrement` or `@KomapperSequence` is annotated in the mapping definition.

If you do not explicitly call the `eq` function for properties with the following mapping definitions
then the value is automatically set in the generated SQL:

- `@KomapperSequence`
- `@KomapperVersion`
- `@KomapperCreatedAt`
- `@KomapperUpdatedAt`

If you explicitly call the `eq` function for those properties, the explicit value takes precedence.

The definition of `KomapperAutoIncrement` cannot be disabled with an explicit value.

## select

To add search results, call the `select` function.

```kotlin
val aa = Meta.address.clone(table = "ADDRESS_ARCHIVE")
val query: Query<Long, List<Int>> = QueryDsl.insert(aa).select {
  QueryDsl.from(a).where { a.addressId between 1..5 }
}
/*
insert into ADDRESS_ARCHIVE (ADDRESS_ID, STREET, VERSION) select t1_.ADDRESS_ID, t1_.STREET, t1_.VERSION from ADDRESS as t1_ where t1_.ADDRESS_ID between ? and ?
*/
```

When the above query is executed, the return value is a Pair of the number of rows added and a list of IDs generated.
IDs are generated only if `@KomapperAutoIncrement` is annotated in the entity class mapping definition.

The following mapping definitions are not considered:

- `@KomapperSequence`
- `@KomapperVersion`
- `@KomapperCreatedAt`
- `@KomapperUpdatedAt`

## options

To customize the behavior of the query, call the `options` function.
The `options` function accept a lambda expression whose parameter represents default options.
Call the `copy` function on the parameter to change its properties:

```kotlin
val address: Address = Address(16, "STREET 16", 0)
val query: Query<Address> = QueryDsl.insert(a).single(address).options {
    it.copy(
      queryTimeoutSeconds = 5
    )
}
```

The options that can be specified are as follows:

batchSize
: Default is `null`.

disableSequenceAssignment
: Whether to disable the assignment of sequence-generated values to IDs. Default is `false`.

queryTimeoutSeconds
: Query timeout in seconds. Default is `null` to indicate that the driver value should be used.

returnGeneratedKeys
: Whether to return the auto-incremented ID value. Default is `true`.

suppressLogging
: Whether to suppress SQL log output. Default is `false`.

Properties explicitly set here will be used in preference to properties with the same name that exist
in [executionOptions]({{< relref "../../database-config/#executionoptions" >}}).
