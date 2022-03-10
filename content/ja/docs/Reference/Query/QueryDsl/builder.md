---
title: "クエリビルダー"
linkTitle: "ビルダー"
weight: 300
description: >
---

## 概要 {#overview}

`QueryDsl`で構築可能なクエリ全体の一部を部品として定義し、それらを組み立てることで1つのクエリを構築できます。
部品を定義する関数をビルダーと呼びます。
ビルダー関数はすべて`org.komapper.core.dsl.query`に定義されます。

## where

Where宣言を組み立てるビルダーです。

```kotlin
val salaryWhere = where {
  e.salary greater BigDecimal(1_000)
}
val query: Query<List<Employee>> = QueryDsl.from(e).where(salaryWhere)
```

## on

On宣言を組み立てるビルダーです。

```kotlin
val departmentIdOn = on {
    e.departmentId eq d.departmentId
}
val query: Query<List<Employee>> = QueryDsl.from(e).innerJoin(d, departmentIdOn)
```

## having

Having宣言を組み立てるビルダーです。

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

Assignment宣言を組み立てるビルダーです。

```kotlin
val addressAssignment = set(a) {
    a.street eq "STREET 16"
}
val query: Query<Int> = QueryDsl.update(a).set(addressAssignment).where {
    a.addressId eq 1
}
```

## values

Assignment宣言を組み立てるビルダーです。

```kotlin
val addressAssignment = set(a) {
    a.street eq "STREET 16"
}
val query: Query<Pair<Int, Int?>> = QueryDsl.insert(a).values(addressAssignment)
```

## join

Join要素を組み立てるビルダーです。

`join`関数により得られた値は、クエリの`innerJoin`や`leftJoin`の関数に渡せます。

```kotlin
val departmentJoin = join(d) {
    e.departmentId eq d.departmentId
}

val query1: Query<List<Employee>> = QueryDsl.from(e).innerJoin(departmentJoin)
val query2: Query<List<Employee>> = QueryDsl.from(e).leftJoin(departmentJoin)
```

## groupBy

GroupBy要素を組み立てるビルダーです。

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

OrderBy要素を組み立てるビルダーです。

```kotlin
val orderBySalaryAndNo = orderBy( e.salary, e.employeeNo)
val query: Query<List<Employee>> = QueryDsl.from(e).orderBy(orderBySalaryAndNo)
```

