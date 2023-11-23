---
title: "Query Composition"
linkTitle: "Composition"
weight: 110
description: >
---

## Overview {#overview}

Queries support composition.

## Composition functions {#composition-functions}

In Komapper, a query is represented by one or both of the following classes.

`org.komapper.core.dsl.query.Query<T>`
: A query that returns the value of type `T`

`org.komapper.core.dsl.query.FlowQuery<T>`
: A query that returns the value of type `kotlinx.coroutines.flow.Flow<T>`

Of these, only `Query<T>` supports composition.
The following sections describe the composite functions that can be performed on `Query<T>`.

### andThen {#query-composition-andthen}

The `andThen` functions construct a query that runs together and returns the last result:

```kotlin
val q1: Query<Address> = QueryDsl.insert(a).single(Address(16, "STREET 16", 0))
val q2: Query<Address> = QueryDsl.insert(a).single(Address(17, "STREET 17", 0))
val q3: Query<List<Address>> = QueryDsl.from(a).where { a.addressId inList listOf(16, 17) }
val query: Query<List<Address>> = q1.andThen(q2).andThen(q3)
val list: List<Address> = db.runQuery { query }
/*
insert into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, ?, ?)
insert into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, ?, ?)
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID in (?, ?)
*/
```

### map {#query-composition-map}

The `map` function constructs a query that makes changes to the query results:

```kotlin
val query: Query<List<Address>> = QueryDsl.from(a).map { 
  it.map { address -> address.copy(version = 100) }
}
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ 
*/
```

### zip {#query-composition-zip}

The `zip` function constructs a query that returns two query results as `Pair`.

```kotlin
val q1 = QueryDsl.insert(a).single(Address(16, "STREET 16", 0))
val q2 = QueryDsl.from(a)
val query: Query<Pair<Address, List<Address>>> = q1.zip(q2)
/*
insert into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, ?, ?)
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_
*/
```

### flatMap {#query-composition-flatmap}

The `flatMap` function constructs a query that executes the second query using the result of the first query 
and returns the result of the second query.

```kotlin
val q1: Query<Address> = QueryDsl.insert(a).single(Address(16, "STREET 16", 0)) // 1st query
val query: Query<List<Employee>> = q1.flatMap { newAddress ->
    QueryDsl.from(e).where { e.addressId less newAddress.addressId } // 2nd query
}
val list: List<Employee> = db.runQuery { query }
/*
insert into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, ?, ?)
select t0_.EMPLOYEE_ID, t0_.EMPLOYEE_NO, t0_.EMPLOYEE_NAME, t0_.MANAGER_ID, t0_.HIREDATE, t0_.SALARY, t0_.DEPARTMENT_ID, t0_.ADDRESS_ID, t0_.VERSION from EMPLOYEE as t0_ where t0_.ADDRESS_ID < ?
*/
```

### flatZip {#query-composition-flatzip}

The `flatZip` function constructs a query that executes the second query using the result of first query and 
returns the two query results as `Pair`.

```kotlin
val q1: Query<Address> = QueryDsl.insert(a).single(Address(16, "STREET 16", 0)) // 1st query
val query: Query<Pair<Address, List<Employee>>> = q1.flatZip { newAddress ->
    QueryDsl.from(e).where { e.addressId less newAddress.addressId } // 2nd query
}
val pair: Pair<Address, List<Employee>> = db.runQuery { query }
/*
insert into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, ?, ?)
select t0_.EMPLOYEE_ID, t0_.EMPLOYEE_NO, t0_.EMPLOYEE_NAME, t0_.MANAGER_ID, t0_.HIREDATE, t0_.SALARY, t0_.DEPARTMENT_ID, t0_.ADDRESS_ID, t0_.VERSION from EMPLOYEE as t0_ where t0_.ADDRESS_ID < ?
*/
```

