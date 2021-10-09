---
title: "Entity DSL"
linkTitle: "Entity DSL"
weight: 10
description: >
  エンティティ操作を行うためのDSL
---

## 概要 {#overview}

Entity DSLはエンティティのマッピング定義情報を基に以下のことを行います。

- SELECT実行時に検索結果を返す前にエンティティのIDを用いた重複除去やエンティティの関連づけ
- INSERT実行時にIDやタイムスタンプの生成
- UPDATEやDELETEの実行時にバージョン番号を用いた楽観的排他制御

## SELECT

SELECTクエリは`EntityDsl`の`from`を呼び出して生成します。これが基本の形となります。

次のクエリは`ADDRESS`テーブルを全件取得するSQLに対応します。

```kotlin
val query: Query<List<Address>> = EntityDsl.from(a)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_
*/
```

`from`を呼び出した後、以下に説明するような関数をいくつか呼び出すことでクエリを組み立てます。

### where {#select-where}

WHERE句を指定する場合は`where`を呼び出します。

```kotlin
val query: Query<List<Address>> = EntityDsl.from(a).where { a.addressId eq 1 }
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID = ?
*/
```

以下のドキュメントも参照ください。

- [比較演算子]({{< relref "expression.md#comparison-operator" >}})
- [論理演算子]({{< relref "expression.md#logical-operator" >}})

### innerJoin {#select-innerjoin}

INNER JOINを行う場合は`innerJoin`を呼び出します。

```kotlin
val query: Query<List<Address>> = EntityDsl.from(a).innerJoin(e) { a.addressId eq e.addressId }
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ inner join EMPLOYEE as t1_ on (t0_.ADDRESS_ID = t1_.ADDRESS_ID)
*/
```

以下のドキュメントも参照ください。

- [比較演算子]({{< relref "expression.md#comparison-operator" >}})

### leftJoin {#select-leftjoin}

LEFT OUTER JOINを行う場合は`leftJoin`を呼び出します。

```kotlin
val query: Query<List<Address>> = EntityDsl.from(a).leftJoin(e) { a.addressId eq e.addressId }
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ left outer join EMPLOYEE as t1_ on (t0_.ADDRESS_ID = t1_.ADDRESS_ID)
*/
```

以下のドキュメントも参照ください。

- [比較演算子]({{< relref "expression.md#comparison-operator" >}})

### associate {#select-associate}

エンティティ間の関連づけを行うには、`innerJoin`もしくは`leftJoin`を呼び出した後、同一のマッピング定義に対して`associate`を呼び出します。

```kotlin
val query: Query<List<Employee>> = EntityDsl.from(e).innerJoin(a) {
    e.addressId eq a.addressId
}.associate(e, a) { employee, address ->
    employee.copy(address = address)
}
/*
select t0_.EMPLOYEE_ID, t0_.EMPLOYEE_NO, t0_.EMPLOYEE_NAME, t0_.MANAGER_ID, t0_.HIREDATE, t0_.SALARY, t0_.DEPARTMENT_ID, t0_.ADDRESS_ID, t0_.VERSION, t1_.ADDRESS_ID, t1_.STREET, t1_.VERSION from EMPLOYEE as t0_ inner join ADDRESS as t1_ on (t0_.ADDRESS_ID = t1_.ADDRESS_ID)
*/
```

### forUpdate {#select-forupdate}

FOR UPDATE句を指定する場合は`forUpdate`を呼び出します。

```kotlin
val query: Query<List<Address>> = EntityDsl.from(a).where { a.addressId eq 1 }.forUpdate()
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID = ? for update
 */
```

### orderBy {#select-orderby}

ORDER BY句を指定する場合は`orderBy`を呼び出します。

```kotlin
val query: Query<List<Adress>> = EntityDsl.from(a).orderBy(a.addressId)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ order by t0_.ADDRESS_ID asc
*/
```

デフォルトでは昇順ですが降順を指定する場合はカラムを`orderBy`に渡す前にカラムに対して`desc`を呼び出します。
また、昇順を表す`asc`を明示的に呼び出すことやカラムを複数指定することもできます。

```kotlin
val query: Query<List<Adress>> = EntityDsl.from(a).orderBy(a.addressId.desc(), a.street.asc())
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ order by t0_.ADDRESS_ID desc, t0_.STREET asc
*/
```

### offset, limit {#select-offset-limit}

指定した位置から一部の行を取り出すには`offset`や`limit`を呼び出します。

```kotlin
val query: Query<List<Adress>> = EntityDsl.from(a).orderBy(a.addressId).offset(10).limit(3)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ order by t0_.ADDRESS_ID asc offset ? rows fetch first ? rows only
*/
```

### first {#select-first}

1件を返却するクエリであることを示すには最後に`first`を呼び出します。

```kotlin
val query: Query<Address> = EntityDsl.from(a).where { a.addressId eq 1 }.first()
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID = ?
*/
```

### firstOrNull {#select-firstornull}

1件もしくは0件の場合に`null`を返却するクエリであることを示すには最後に`firstOrNull`を呼び出します。

```kotlin
val query: Query<Address?> = EntityDsl.from(a).where { a.addressId eq 1 }.firstOrNull()
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID = ?
*/
```

## INSERT

INSERTクエリは`EntityDsl`の`insert`とそれに続く関数を呼び出して生成します。

クエリ実行時にキーが重複した場合、`org.komapper.core.UniqueConstraintException`がスローされます。

### single {#insert-single}

1件を追加するには`single`を呼び出します。

```kotlin
val address: Address = ..
val query: Query<Address> = EntityDsl.insert(a).single(address)
/*
insert into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, ?, ?)
*/
```

このクエリを実行した場合の戻り値は追加されたデータを表す新しいエンティティです。
つまり、IDやタイムスタンプが自動生成される設定をしている場合、生成されたIDやタイムスタンプがセットされたエンティティが返されます。

### multiple {#insert-multiple}

1文で複数件を追加するには`multiple`を呼び出します。

```kotlin
val query: Query<List<Address>> = EntityDsl.insert(a).multiple(
    Address(16, "STREET 16", 0),
    Address(17, "STREET 17", 0),
    Address(18, "STREET 18", 0)
)
/*
insert into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, ?, ?), (?, ?, ?), (?, ?, ?)
*/
```

このクエリを実行した場合の戻り値は追加されたデータを表す新しいエンティティです。
つまり、IDやタイムスタンプが自動生成される設定をしている場合、生成されたIDやタイムスタンプがセットされたエンティティが返されます。

### batch {#insert-batch}

バッチで複数件を追加するには`batch`を呼び出します。

```kotlin
val query: Query<List<Address>> = EntityDsl.insert(a).batch(
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

このクエリを実行した場合の戻り値は追加されたデータを表す新しいエンティティです。
つまり、IDやタイムスタンプが自動生成される設定をしている場合、生成されたIDやタイムスタンプがセットされたエンティティが返されます。

### onDuplicateKeyIgnore {#insert-onduplicatekeyignore}

`onDuplicateKeyIgnore`を呼び出すことでキーが重複した場合のエラーを無視できます。

```kotlin
val address: Address = ..
val query: Query<Int> = EntityDsl.insert(a).onDuplicateKeyIgnore().single(address)
```

このクエリを実行した場合の戻り値はドライバの返す値です。

上記クエリに対応するSQLはどのDialectを使うかで異なります。
例えば、MariaDBのDialectを使う場合は次のようなSQLになります。

```sql
insert ignore into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, ?, ?)
```

PostgreSQLのDialectを使う場合は次のようなSQLになります。

```sql
insert into ADDRESS as t0_ (ADDRESS_ID, STREET, VERSION) values (?, ?, ?) on conflict (ADDRESS_ID) do nothing
```

### onDuplicateKeyUpdate {#insert-onduplicatekeyupdate}

`onDuplicateKeyIgnore`を呼び出すことでキーが重複した場合にUPDATEを実行できます。

```kotlin
val department: Department = ..
val query: Query<Int> = EntityDsl.insert(d).onDuplicateKeyUpdate().single(department)
```

このクエリを実行した場合の戻り値はドライバの返す値です。

上記クエリに対応するSQLはどのDialectを使うかで異なります。
例えば、MariaDBのDialectを使う場合は次のようなSQLになります。

```sql
insert into DEPARTMENT (DEPARTMENT_ID, DEPARTMENT_NO, DEPARTMENT_NAME, LOCATION, VERSION) values (?, ?, ?, ?, ?) on duplicate key update DEPARTMENT_NO = values(DEPARTMENT_NO), DEPARTMENT_NAME = values(DEPARTMENT_NAME), LOCATION = values(LOCATION), VERSION = values(VERSION)
```

PostgreSQLのDialectを使う場合は次のようなSQLになります。

```sql
insert into DEPARTMENT as t0_ (DEPARTMENT_ID, DEPARTMENT_NO, DEPARTMENT_NAME, LOCATION, VERSION) values (?, ?, ?, ?, ?) on conflict (DEPARTMENT_ID) do update set DEPARTMENT_NO = excluded.DEPARTMENT_NO, DEPARTMENT_NAME = excluded.DEPARTMENT_NAME, LOCATION = excluded.LOCATION, VERSION = excluded.VERSION
```

## UPDATE

UPDATEクエリは`EntityDsl`の`update`とそれに続く関数を呼び出して生成します。

クエリ実行時にキーが重複した場合、`org.komapper.core.UniqueConstraintException`がスローされます。

### single {#update-single}

1件を更新するには`single`を呼び出します。

```kotlin
val address: Address = ..
val query: Query<Address> = EntityDsl.update(a).single(address)
/*
update ADDRESS set STREET = ?, VERSION = ? + 1 where ADDRESS_ID = ? and VERSION = ?
*/
```

### batch {#update-batch}

バッチで複数件を更新するには`batch`を呼び出します。

```kotlin
val address1: Address = ..
val address2: Address = ..
val address3: Address = ..
val query: Query<List<Address>> = EntityDsl.update(a).batch(address1, address2, address3)
/*
update ADDRESS set STREET = ?, VERSION = ? + 1 where ADDRESS_ID = ? and VERSION = ?
update ADDRESS set STREET = ?, VERSION = ? + 1 where ADDRESS_ID = ? and VERSION = ?
update ADDRESS set STREET = ?, VERSION = ? + 1 where ADDRESS_ID = ? and VERSION = ?
*/
```

## DELETE

DELETEクエリは`EntityDsl`の`delete`とそれに続く関数を呼び出して生成します。

### single {#delete-single}

1件を削除するには`single`を呼び出します。

```kotlin
val address: Address = ..
val query: Query<Unit> = EntityDsl.delete(a).single(address)
/*
delete from ADDRESS as t0_ where t0_.ADDRESS_ID = ? and t0_.VERSION = ?
*/
```

### batch {#delete-batch}

バッチで複数件を削除するには`batch`を呼び出します。

```kotlin
val address1: Address = ..
val address2: Address = ..
val address3: Address = ..
val query: Query<Unit> = EntityDsl.delete(a).batch(address1, address2, address3)
/*
delete from ADDRESS as t0_ where t0_.ADDRESS_ID = ? and t0_.VERSION = ?
delete from ADDRESS as t0_ where t0_.ADDRESS_ID = ? and t0_.VERSION = ?
delete from ADDRESS as t0_ where t0_.ADDRESS_ID = ? and t0_.VERSION = ?
*/
```