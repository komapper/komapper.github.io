---
title: "Composition"
linkTitle: "Composition"
weight: 110
description: >
  クエリの合成
---

## 概要 {#overview}

クエリとその構成要素である宣言は合成をサポートします。

## クエリ {#query}

Komapperにおいて、クエリは以下のクラスのいずれかもしくは両方で表現されます。

org.komapper.core.dsl.query.Query<T>
: `JdbcDatabase`もしくは`R2dbcDatabase`インスタンスを介して実行するとデータベースにアクセスし`T`型の値を返すクエリ。

org.komapper.core.dsl.query.FlowQuery<T>
: `R2dbcDatabase`インスタンスを介して実行すると`kotlinx.coroutines.flow.Flow<T>`型の値を返すクエリ。データベースアクセスは`Flow`が`collect`されたときに初めて行われます。

これらのうち、合成をサポートしているのは`Query<T>`のみです。
下記では、`Query<T>`に対して実行できる合成関数を説明します。

### andThen {#query-composition-andthen}

`andThen`関数を使うと、まとめて実行して最後の結果を返すクエリを構築できます。

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

`map`関数を使うと、クエリ結果に変更を加えるクエリを構築できます。

```kotlin
val query: Query<List<Address>> = QueryDsl.from(a).map { 
  it.map { address -> address.copy(version = 100) }
}
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ 
*/
```

### zip {#query-composition-zip}

`map`関数を使うと、2つのクエリ結果を`Pair`型で返すクエリを構築できます。

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

`flatMap`関数を使うと、1番目のクエリの実行結果を受け取って2番目のクエリを実行し2番目の結果を返すクエリを構築できます。

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

`flatZip`関数を使うと、1番目のクエリの実行結果を受け取って2番目のクエリを実行し1番目と2番目の結果を`Pair`型で返すクエリを構築できます。

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

