---
title: "Introduction"
linkTitle: "Introduction"
weight: 1
description: >
  クエリの紹介
---

## 概要 {#overview}

Komapperではクエリの構築と実行は分離されています。
クエリの構築は各種のDSLが担い、実行はJDBCやR2DBCを表す [Database]({{< relref "../database.md" >}}) インスタンスが担います。

```kotlin
// create a query
val query: Query<List<Address>> = EntityDsl.from(a)
// run the query
val result: List<Address> = db.runQuery { query }
```

このページではクエリとその構成要素である宣言について説明します。

クエリの構築を担うDSLについては専用のページを参照ください。

- [Entity DSL]({{< relref "entity-dsl.md" >}})
- [SQL DSL]({{< relref "entity-dsl.md" >}})
- [Template DSL]({{< relref "sql-dsl.md" >}})
- [Script DSL]({{< relref "script-dsl.md" >}})
- [Schema DSL]({{< relref "schema-dsl.md" >}})

宣言の構成要素である式については専用のページを参照ください。

- [Expression]({{< relref "expression.md" >}})

## クエリ {#query}

Komapperにおけるクエリは以下のクラスのいずれかもしくは両方で表現されます。

`org.komapper.core.dsl.query.Query<T>`
: `Database`インスタンスを介して実行するとデータベースにアクセスし`T`型の値を返すクエリ。

`org.komapper.core.dsl.query.FlowQuery<T>`
: `Database`インスタンスを介して実行すると`kotlinx.coroutines.flow.Flow<T>`型の値を返すクエリ。データベースアクセスは`Flow`が`collect`されたときに初めて行われます。

{{< alert title="Note" >}}
`FlowQuery<T>`を構築できるDSLはSQL DSLのみであり、`R2dbcDatabase`インスタンスによってのみ実行可能です。
{{< /alert >}}

## クエリの合成 {#query-composition}

クエリは合成できます。

### plus {#query-composition-plus}

`+`演算子を使うと、まとめて実行して最後の結果を返すクエリを構築できます。

```kotlin
val q1: Query<Address> = EntityDsl.insert(a).single(Address(16, "STREET 16", 0))
val q2: Query<Address> = EntityDsl.insert(a).single(Address(17, "STREET 17", 0))
val q3: Query<List<Address>> = EntityDsl.from(a).where { a.addressId inList listOf(16, 17) }
val query: Query<List<Address>> = q1 + q2 + q3
val list: List<Address> = db.runQuery { query }
/*
insert into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, ?, ?)
insert into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, ?, ?)
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID in (?, ?)
*/
```

### flatMap {#query-composition-flatmap}

`flatMap`関数を使うと、1番目のクエリの実行結果を受け取って2番目のクエリを実行し2番目の結果を返すクエリを構築できます。

```kotlin
val q1: Query<Address> = EntityDsl.insert(a).single(Address(16, "STREET 16", 0)) // 1st query
val query: Query<List<Employee>> = q1.flatMap { newAddress ->
    EntityDsl.from(e).where { e.addressId less newAddress.addressId } // 2nd query
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
val q1: Query<Address> = EntityDsl.insert(a).single(Address(16, "STREET 16", 0)) // 1st query
val query: Query<Pair<Address, List<Employee>>> = q1.flatZip { newAddress ->
    EntityDsl.from(e).where { e.addressId less newAddress.addressId } // 2nd query
}
val pair: Pair<Address, List<Employee>> = db.runQuery { query }
/*
insert into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, ?, ?)
select t0_.EMPLOYEE_ID, t0_.EMPLOYEE_NO, t0_.EMPLOYEE_NAME, t0_.MANAGER_ID, t0_.HIREDATE, t0_.SALARY, t0_.DEPARTMENT_ID, t0_.ADDRESS_ID, t0_.VERSION from EMPLOYEE as t0_ where t0_.ADDRESS_ID < ?
*/
```

## 宣言 {#declaration}

Entity DSLとSQL DSLでは、例えば`where`関数や`having`関数に検索条件を表すラムダ式を渡しますが、
Komapperではこれらのラムダ式のことを宣言と呼びます。

宣言には以下のものがあります。

- Having宣言 - `HavingDeclaration`
- On宣言 - `OnDeclaration`
- Set宣言 - `SetDeclaration`
- Values宣言 - `ValuesDeclaration`
- When宣言 - `WhenDeclaration`
- Where宣言 - `WhereDeclaration`

## 宣言の合成 {#declaration-composition}

宣言は合成できます。

### plus {#declaration-composition-plus}

`+`演算子を使うと、被演算子の宣言内部に持つ式を順番に実行するような新たな宣言を構築できます。

```kotlin
val w1: WhereDeclaration = {
    a.addressId eq 1
}
val w2: WhereDeclaration = {
    a.version eq 1
}
val w3: WhereDeclaration = w1 + w2 // +演算子の利用
val query: Query<List<Address>> = EntityDsl.from(a).where(w3)
val list: List<Address> = db.runQuery { query }
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID = ? and t0_.VERSION = ?
*/
```

`+`演算子はすべての宣言で利用できます。

### and {#declaration-composition-and}

`and`関数を使うと、宣言を`and`演算子で連結する新たな宣言を構築できます。

```kotlin
val w1: WhereDeclaration = {
    a.addressId eq 1
}
val w2: WhereDeclaration = {
    a.version eq 1
    or { a.version eq 2 }
}
val w3: WhereDeclaration = w1 and w2 // and関数の利用
val query: Query<List<Address>> = EntityDsl.from(a).where(w3)
val list: List<Address> = db.runQuery { query }
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID = ? and (t0_.VERSION = ? or (t0_.VERSION = ?))
*/
```

`and`関数は、Having宣言、When宣言、Where宣言に対して適用できます。

### or {#declaration-composition-or}

`or`関数を使うと、宣言を`or`演算子で連結する新たな宣言を構築できます。

```kotlin
val w1: WhereDeclaration = {
    a.addressId eq 1
}
val w2: WhereDeclaration = {
    a.version eq 1
    a.street eq "STREET 1"
}
val w3: WhereDeclaration = w1 or w2 // or関数の利用
val query: Query<List<Address>> = EntityDsl.from(a).where(w3)
val list: List<Address> = db.runQuery { query }
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID = ? or (t0_.VERSION = ? and t0_.STREET = ?)
*/
```
