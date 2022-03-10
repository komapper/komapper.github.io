---
title: "Query Builders"
linkTitle: "Builders"
weight: 300
description: >
---

## Overview {#overview}

A function that builds part of a query is called a builder.

All builder functions are defined in `org.komapper.core.dsl.query`.

## where

The `where` function builds a Where declaration.

```kotlin
val salaryWhere = where {
  e.salary greater BigDecimal(1_000)
}
val query: Query<List<Employee>> = QueryDsl.from(e).where(salaryWhere)
```

## on

The `on` function builds a On declaration.

```kotlin
val departmentIdOn = on {
    e.departmentId eq d.departmentId
}
val query: Query<List<Employee>> = QueryDsl.from(e).innerJoin(d, departmentIdOn)
```

## having

The `having` function builds a Having declaration.

```kotlin
val countHaving = having {
    count() greater 3
}
val query: Query<List<Int?>> = QueryDsl.from(e)
    .groupBy(e.departmentId)
    .having(countHaving)
    .select(e.departmentId)
```

## set

The `set` function builds an Assignment declaration.

```kotlin
val addressAssignment = set(a) {
    a.street eq "STREET 16"
}
val query: Query<Int> = QueryDsl.update(a).set(addressAssignment).where {
    a.addressId eq 1
}
```

## values

The `values` function builds an Assignment declaration.

```kotlin
val addressAssignment = values(a) {
    a.street eq "STREET 16"
}
val query: Query<Pair<Int, Int?>> = QueryDsl.insert(a).values(addressAssignment)
```

## join

The `join` function builds a Join element.

The Join element can be passed to the `innerJoin` or `leftJoin` functions:

```kotlin
val departmentJoin = join(d) {
    e.departmentId eq d.departmentId
}

val query1: Query<List<Employee>> = QueryDsl.from(e).innerJoin(departmentJoin)
val query2: Query<List<Employee>> = QueryDsl.from(e).leftJoin(departmentJoin)
```

## groupBy

The `groupBy` function builds a list of column expressions.

```kotlin
val groupByDepartmentId = groupBy(e.departmentId)
val query: Query<List<Int?>> = QueryDsl.from(e)
    .groupBy(groupByDepartmentId)
    .having {
        count() greater 3
    }
    .select(e.departmentId)
```

## orderBy

The `orderBy` function builds a list of sort expressions.

```kotlin
val orderBySalaryAndNo = orderBy(e.salary, e.employeeNo)
val query: Query<List<Employee>> = QueryDsl.from(e).orderBy(orderBySalaryAndNo)
```

