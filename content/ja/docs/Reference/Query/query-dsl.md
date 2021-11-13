---
title: "Query DSL"
linkTitle: "Query DSL"
weight: 10
description: >
  SQLのDMLを組み立てるためのDSL
---

## 概要 {#overview}

Query DSLはエンティティのマッピング定義情報を用いてSQLのDMLを柔軟に組み立てます。

## SELECT

SELECTクエリは`QueryDsl`の`from`を呼び出して生成します。これが基本の形となります。

次のクエリは`ADDRESS`テーブルを全件取得するSQLに対応します。

```kotlin
val query: Query<List<Address>> = QueryDsl.from(a)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_
*/
```

`from`を呼び出した後、以下に説明するような関数をいくつか呼び出すことでクエリを組み立てます。

### where {#select-where}

WHERE句を指定する場合は`where`を呼び出します。

```kotlin
val query: Query<List<Address>> = QueryDsl.from(a).where { a.addressId eq 1 }
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
val query: Query<List<Address>> = QueryDsl.from(a).innerJoin(e) { a.addressId eq e.addressId }
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ inner join EMPLOYEE as t1_ on (t0_.ADDRESS_ID = t1_.ADDRESS_ID)
*/
```

以下のドキュメントも参照ください。

- [比較演算子]({{< relref "expression.md#comparison-operator" >}})

### leftJoin {#select-leftjoin}

LEFT OUTER JOINを行う場合は`leftJoin`を呼び出します。

```kotlin
val query: Query<List<Address>> = QueryDsl.from(a).leftJoin(e) { a.addressId eq e.addressId }
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ left outer join EMPLOYEE as t1_ on (t0_.ADDRESS_ID = t1_.ADDRESS_ID)
*/
```

以下のドキュメントも参照ください。

- [比較演算子]({{< relref "expression.md#comparison-operator" >}})

### include {#select-include}

JOINしたテーブルのカラムをSELECT句に含める場合は`include`を呼び出します。
`include`は連続して何度も呼び出せます。

```kotlin
val a = Address.meta
val e = Employee.meta
val d = Department.meta
val query: Query<EntityContext<Address>> = QueryDsl.from(a)
  .innerJoin(e) {
    a.addressId eq e.addressId
  }.innerJoin(d) {
    e.departmentId eq d.departmentId
  }.include(e)
  .include(d)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION, t1_.EMPLOYEE_ID, t1_.EMPLOYEE_NO, t1_.EMPLOYEE_NAME, t1_.MANAGER_ID, t1_.HIREDATE, t1_.SALARY, t1_.DEPARTMENT_ID, t1_.ADDRESS_ID, t1_.VERSION, t2_.DEPARTMENT_ID, t2_.DEPARTMENT_NO, t2_.DEPARTMENT_NAME, t2_.LOCATION, t2_.VERSION from ADDRESS as t0_ inner join EMPLOYEE as t1_ on (t0_.ADDRESS_ID = t1_.ADDRESS_ID) inner join DEPARTMENT as t2_ on (t1_.DEPARTMENT_ID = t2_.DEPARTMENT_ID)
*/
```

全てのテーブルのカラムをSELECT句に含めたい場合は、複数回`include`を呼び出す代わりに`includeAll`を呼び出します。
下記のコードは上記のコードと同等です。

```kotlin
val a = Address.meta
val e = Employee.meta
val d = Department.meta
val query: Query<EntityContext<Address>> = QueryDsl.from(a)
  .innerJoin(e) {
    a.addressId eq e.addressId
  }.innerJoin(d) {
    e.departmentId eq d.departmentId
  }.includeAll()
/*
select t0_.EMPLOYEE_ID, t0_.EMPLOYEE_NO, t0_.EMPLOYEE_NAME, t0_.MANAGER_ID, t0_.HIREDATE, t0_.SALARY, t0_.DEPARTMENT_ID, t0_.ADDRESS_ID, t0_.VERSION, t1_.ADDRESS_ID, t1_.STREET, t1_.VERSION, t2_.DEPARTMENT_ID, t2_.DEPARTMENT_NO, t2_.DEPARTMENT_NAME, t2_.LOCATION, t2_.VERSION from EMPLOYEE as t0_ inner join ADDRESS as t1_ on (t0_.ADDRESS_ID = t1_.ADDRESS_ID) inner join DEPARTMENT as t2_ on (t0_.DEPARTMENT_ID = t2_.DEPARTMENT_ID)
*/
```

このクエリを実行した場合の戻り値は、SQLの結果セットから生成された複数のエンティティを保持する
`org.komapper.core.dsl.query.EntityContext<ENTITY>`のインスタンスです。

`EntityContext<ENTITY>`のインスタンスからは、エンティティの関連を`Map`として取得できます。

```kotlin
val entityContext: EntityContext<Address> = db.runQuery { query }

val departmentEmployees: Map<Department, Set<Employee>> = entityContext.associate(d to e)
val employeeAddress: Map<Employee, Address?> = entityContext.associate(e to a).asOneToOne()

val departmentIdEmployees: Map<Int, Set<Employee>> = entityContext.associateById(d to e)
val employeeIdAddress: Map<Int, Address?> = entityContext.associateById(e to a).asOneToOne()
```

### forUpdate {#select-forupdate}

FOR UPDATE句を指定する場合は`forUpdate`を呼び出します。

```kotlin
val query: Query<List<Address>> = QueryDsl.from(a).where { a.addressId eq 1 }.forUpdate()
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID = ? for update
*/
```

### orderBy {#select-orderby}

ORDER BY句を指定する場合は`orderBy`を呼び出します。

```kotlin
val query: Query<List<Adress>> = QueryDsl.from(a).orderBy(a.addressId)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ order by t0_.ADDRESS_ID asc
*/
```

デフォルトでは昇順ですが降順を指定する場合はカラムを`orderBy`に渡す前にカラムに対して`desc`を呼び出します。
また、昇順を表す`asc`を明示的に呼び出すことやカラムを複数指定することもできます。

```kotlin
val query: Query<List<Adress>> = QueryDsl.from(a).orderBy(a.addressId.desc(), a.street.asc())
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ order by t0_.ADDRESS_ID desc, t0_.STREET asc
*/
```

NULLのソート順序を制御するために、カラムに対して`ascNullsFirst`、`ascNullsLast`、`descNullsFirst`、`descNullsLast`を呼び出すこともできます。

```kotlin
val query: Query<List<Employee>> = QueryDsl.from(e).orderBy(e.managerId.ascNullsFirst())
/*
select t0_.EMPLOYEE_ID, t0_.EMPLOYEE_NO, t0_.EMPLOYEE_NAME, t0_.MANAGER_ID, t0_.HIREDATE, t0_.SALARY, t0_.DEPARTMENT_ID, t0_.ADDRESS_ID, t0_.VERSION from EMPLOYEE as t0_ order by t0_.MANAGER_ID asc nulls first
 */
```

### offset, limit {#select-offset-limit}

指定した位置から一部の行を取り出すには`offset`や`limit`を呼び出します。

```kotlin
val query: Query<List<Adress>> = QueryDsl.from(a).orderBy(a.addressId).offset(10).limit(3)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ order by t0_.ADDRESS_ID asc offset ? rows fetch first ? rows only
*/
```

### distinct {#select-distinct}

DISTINCTキーワードを指定するには`distinct`を呼び出します。

```kotlin
val query: Query<List<Department>> = QueryDsl.from(d).distinct().innerJoin(e) { d.departmentId eq e.departmentId }
/*
select distinct t0_.DEPARTMENT_ID, t0_.DEPARTMENT_NO, t0_.DEPARTMENT_NAME, t0_.LOCATION, t0_.VERSION from DEPARTMENT as t0_ inner join EMPLOYEE as t1_ on (t0_.DEPARTMENT_ID = t1_.DEPARTMENT_ID)
*/
```

### select {#select-select}

射影を行うには`select`を呼び出します。

以下のドキュメントも参照ください。

- [集約関数]({{< relref "expression.md#aggregate-function" >}})

1つのカラムを射影する例です。

```kotlin
val query: Query<List<String?> = QueryDsl.from(a)
    .where {
        a.addressId inList listOf(1, 2)
    }
    .orderBy(a.addressId)
    .select(a.street)
/*
select t0_.STREET from ADDRESS as t0_ where t0_.ADDRESS_ID in (?, ?) order by t0_.ADDRESS_ID asc
*/
```

2つのカラムを射影する例です。

```kotlin
val query: Query<List<Pair<Int?, String?>>> = QueryDsl.from(a)
    .where {
        a.addressId inList listOf(1, 2)
    }
    .orderBy(a.addressId)
    .select(a.addressId, a.street)
/*
select t0_.ADDRESS_ID, t0_.STREET from ADDRESS as t0_ where t0_.ADDRESS_ID in (?, ?) order by t0_.ADDRESS_ID asc
*/
```

3つのカラムを射影する例です。

```kotlin
val query: Query<List<Triple<Int?, String?, Int?>>> = QueryDsl.from(a)
    .where {
        a.addressId inList listOf(1, 2)
    }
    .orderBy(a.addressId)
    .select(a.addressId, a.street, a.version)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID in (?, ?) order by t0_.ADDRESS_ID asc
*/
```

4つ以上のカラムを射影する例です。

```kotlin
val query: Query<List<Columns>> = QueryDsl.from(a)
    .where {
        a.addressId inList listOf(1, 2)
    }
    .orderBy(a.addressId)
    .select(a.addressId, a.street, a.version, concat(a.street, " test"))
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION, (concat(t0_.STREET, ?)) from ADDRESS as t0_ where t0_.ADDRESS_ID in (?, ?) order by t0_.ADDRESS_ID asc
*/

val list: List<Columns> = db.runQuery { query }
for (row: Columns in list) {
  println(row[a.addressId])
  println(row[a.street])
  println(row[a.version])
  println(row[concat(a.street, " test")])
}
```

4つ以上のカラムを射影した場合、結果の値は`Columns`に含まれます。
クエリの`select`に指定したカラムをkeyにして値を取得できます。

### having {#select-having}

HAVING句を指定するには`having`を呼び出します。

```kotlin
val query: Query<List<Pair<Int?, Long?>>> = QueryDsl.from(e)
    .having {
        count(e.employeeId) greaterEq 4L
    }
    .orderBy(e.departmentId)
    .select(e.departmentId, count(e.employeeId))
/*
select t0_.DEPARTMENT_ID, count(t0_.EMPLOYEE_ID) from EMPLOYEE as t0_ group by t0_.DEPARTMENT_ID having count(t0_.EMPLOYEE_ID) >= ? order by t0_.DEPARTMENT_ID asc
*/
```

{{< alert title="Note" >}}
`groupBy`の呼び出しがない場合、GROUP BY句は`select`関数に渡された引数から推測されて生成されます。
{{< /alert >}}

以下のドキュメントも参照ください。

- [比較演算子]({{< relref "expression.md#comparison-operator" >}})
- [論理演算子]({{< relref "expression.md#logical-operator" >}})
- [集約関数]({{< relref "expression.md#aggregate-function" >}})

### groupBy {#select-groupby}

GROUP BY句を指定するには`groupBy`を呼び出します。

```kotlin
val query: Query<List<Pair<Int?, Long?>>> = QueryDsl.from(e)
    .groupBy(e.departmentId)
    .having {
        count(e.employeeId) greaterEq 4L
    }
    .orderBy(e.departmentId)
    .select(e.departmentId, count(e.employeeId))
/*
select t0_.DEPARTMENT_ID, count(t0_.EMPLOYEE_ID) from EMPLOYEE as t0_ group by t0_.DEPARTMENT_ID having count(t0_.EMPLOYEE_ID) >= ? order by t0_.DEPARTMENT_ID asc
*/
```

### union {#select-union}

UNION演算を行うにはクエリを`union`で連携します。

```kotlin
val q1: Query<List<Pair<Int?, String?>>> = QueryDsl.from(e).where { e.employeeId eq 1 }
    .select(e.employeeId alias "ID", e.employeeName alias "NAME")
val q2: Query<List<Pair<Int?, String?>>> = QueryDsl.from(a).where { a.addressId eq 2 }
  .select(a.addressId alias "ID", a.street alias "NAME")
val q3: Query<List<Pair<Int?, String?>>> = QueryDsl.from(d).where { d.departmentId eq 3 }
  .select(d.departmentId alias "ID", d.departmentName alias "NAME")
val query: Query<List<Pair<Int?, String?>>> = (q1 union q2 union q3).orderBy("ID", desc("NAME"))
/*
(select t0_.EMPLOYEE_ID as "ID", t0_.EMPLOYEE_NAME as "NAME" from EMPLOYEE as t0_ where t0_.EMPLOYEE_ID = ?) union (select t1_.ADDRESS_ID as "ID", t1_.STREET as "NAME" from ADDRESS as t1_ where t1_.ADDRESS_ID = ?) union (select t2_.DEPARTMENT_ID as "ID", t2_.DEPARTMENT_NAME as "NAME" from DEPARTMENT as t2_ where t2_.DEPARTMENT_ID = ?) order by "ID" asc, "NAME" desc
*/
```

{{< alert title="Note" >}}
`union`以外のセット演算子では、`unionAll`、`except`、`intersect`が利用できます。
ただし、データベースがサポートしていない場合をSQLを発行した時点で例外が発生します。
{{< /alert >}}

### first {#select-first}

1件を返却するクエリであることを示すには最後に`first`を呼び出します。

```kotlin
val query: Query<Address> = QueryDsl.from(a).where { a.addressId eq 1 }.first()
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID = ?
*/
```

### firstOrNull {#select-firstornull}

1件もしくは0件の場合に`null`を返却するクエリであることを示すには最後に`firstOrNull`を呼び出します。

```kotlin
val query: Query<Address?> = QueryDsl.from(a).where { a.addressId eq 1 }.firstOrNull()
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID = ?
*/
```

### collect {#select-collect}

結果セットを`kotlinx.coroutines.flow.Flow`として処理するには最後に`collect`を呼び出します。

```kotlin
val query: Query<Unit> = QueryDsl.from(a).collect { flow: Flow<Address> -> flow.collect { println(it) } }
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_
*/
```

{{< alert title="Note" >}}
`collect`を使うと、結果セットを全てメモリに読み込んだ後に処理するのではなく結果セットを1件ずつ読み込みながら処理することになります。
したがって、メモリの使用効率を向上させられます。
{{< /alert >}}

## INSERT

INSERTクエリは`QueryDsl`の`insert`とそれに続く関数を呼び出して生成します。

クエリ実行時にキーが重複した場合かつ`onDuplicateKeyIgnore`や`onDuplicateKeyUpdate`を呼び出していない場合、
`org.komapper.core.UniqueConstraintException`がスローされます。

### single {#insert-single}

エンティティ1件を追加するには`single`を呼び出します。

```kotlin
val address: Address = ..
val query: Query<Address> = QueryDsl.insert(a).single(address)
/*
insert into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, ?, ?)
*/
```

このクエリを実行した場合の戻り値は追加されたデータを表す新しいエンティティです。
設定に応じてIDやタイムスタンプが新しいエンティティに反映されます。

### multiple {#insert-multiple}

1文でエンティティ複数件を追加するには`multiple`を呼び出します。

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

このクエリを実行した場合の戻り値は追加されたデータを表す新しいエンティティのリストです。
設定に応じてIDやタイムスタンプが新しいエンティティに反映されます。

### batch {#insert-batch}

バッチでエンティティ複数件を追加するには`batch`を呼び出します。

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

このクエリを実行した場合の戻り値は追加されたデータを表す新しいエンティティのリストです。
設定に応じてIDやタイムスタンプが新しいエンティティに反映されます。

### onDuplicateKeyIgnore {#insert-onduplicatekeyignore}

`onDuplicateKeyIgnore`を呼び出すことでキーが重複した場合のエラーを無視できます。

```kotlin
val address: Address = ..
val query: Query<Int> = QueryDsl.insert(a).onDuplicateKeyIgnore().single(address)
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
val query: Query<Int> = QueryDsl.insert(d).onDuplicateKeyUpdate().single(department)
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

### values {#insert-values}

プロパティごとの値を設定して1件を追加するには`values`を呼び出します。

```kotlin
val query: Query<Pair<Int, Int?>> = QueryDsl.insert(a).values {
  a.addressId set 19
  a.street set "STREET 16"
  a.version set 0
}
/*
insert into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, ?, ?)
*/
```

クエリを実行した場合の戻り値は追加された件数と生成されるIDの`Pair`です。
IDはエンティティクラスのマッピング定義に`@KomapperAutoIncrement`が注釈されている場合にのみ返されます。

### select {#insert-select}

検索結果を追加するには`select`を呼び出します。

```kotlin
val aa = Address.newMeta(table = "ADDRESS_ARCHIVE")
val query: Query<Int, Int?> = QueryDsl.insert(aa).select {
  QueryDsl.from(a).where { a.addressId between 1..5 }
}
/*
insert into ADDRESS_ARCHIVE (ADDRESS_ID, STREET, VERSION) select t1_.ADDRESS_ID, t1_.STREET, t1_.VERSION from ADDRESS as t1_ where t1_.ADDRESS_ID between ? and ?
*/
```

クエリを実行した場合の戻り値は追加された件数と生成されるIDの`Pair`です。
IDはエンティティクラスのマッピング定義に`@KomapperAutoIncrement`が注釈されている場合にのみ返されます。

## UPDATE

UPDATEクエリは`QueryDsl`の`update`とそれに続く関数を呼び出して生成します。

クエリ実行時にキーが重複した場合、`org.komapper.core.UniqueConstraintException`がスローされます。

### single {#update-single}

エンティティ1件を更新するには`single`を呼び出します。

```kotlin
val address: Address = ..
val query: Query<Address> = QueryDsl.update(a).single(address)
/*
update ADDRESS set STREET = ?, VERSION = ? + 1 where ADDRESS_ID = ? and VERSION = ?
*/
```

クエリ実行時に楽観的排他制御が失敗した場合、`org.komapper.core.OptimisticLockException`がスローされます。

このクエリを実行した場合の戻り値は追加されたデータを表す新しいエンティティです。
設定に応じてバージョン番号やタイムスタンプが新しいエンティティに反映されます。

### batch {#update-batch}

バッチでエンティティ複数件を更新するには`batch`を呼び出します。

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

クエリ実行時に楽観的排他制御が失敗した場合、`org.komapper.core.OptimisticLockException`がスローされます。

このクエリを実行した場合の戻り値は追加されたデータを表す新しいエンティティのリストです。
設定に応じてバージョン番号やタイムスタンプが新しいエンティティに反映されます。

### set {#update-set}

任意のプロパティに更新データをセットするには`set`を呼び出します。

```kotlin
val query: Query<Int> = QueryDsl.update(a).set {
  a.street set "STREET 16"
}.where {
  a.addressId eq 1
}
/*
update ADDRESS as t0_ set STREET = ? where t0_.ADDRESS_ID = ?
*/
```

このクエリを実行した場合の戻り値は更新された件数です。

### where {#update-where}

任意の条件にマッチする行を更新するには`where`を呼び出します。

デフォルトではWHERE句の指定は必須でありWHERE句が指定されない場合は例外が発生します。
意図的に全件更新を認める場合は`option`を呼び出して`allowEmptyWhereClause`に`true`を設定します。

```kotlin
val query: Query<Int> = QueryDsl.update(e).set {
    e.employeeName set "ABC"
}.options { 
    it.copy(allowEmptyWhereClause = true)
}
```

このクエリを実行した場合の戻り値は更新された件数です。

以下のドキュメントも参照ください。

- [比較演算子]({{< relref "expression.md#comparison-operator" >}})
- [論理演算子]({{< relref "expression.md#logical-operator" >}})

## DELETE

DELETEクエリは`QueryDsl`の`delete`とそれに続く関数を呼び出して生成します。

### single {#delete-single}

エンティティ1件を削除するには`single`を呼び出します。

```kotlin
val address: Address = ..
val query: Query<Unit> = QueryDsl.delete(a).single(address)
/*
delete from ADDRESS as t0_ where t0_.ADDRESS_ID = ? and t0_.VERSION = ?
*/
```

クエリ実行時に楽観的排他制御が失敗した場合、`org.komapper.core.OptimisticLockException`がスローされます。

### batch {#delete-batch}

バッチでエンティティ複数件を削除するには`batch`を呼び出します。

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

クエリ実行時に楽観的排他制御が失敗した場合、`org.komapper.core.OptimisticLockException`がスローされます。

### all {#delete-where}

全件を削除するには`all`を呼び出します。

```kotlin
val query: Query<Int> = QueryDsl.delete(e).all().options { it.copy(allowEmptyWhereClause = true) }
/*
delete from EMPLOYEE as t0_
*/
```

`option`を呼び出して`allowEmptyWhereClause`に`true`を設定する必要があります。

このクエリを実行した場合の戻り値は削除された件数です。

### where {#delete-where}

任意の条件にマッチする行を削除するには`where`を呼び出します。

```kotlin
val query: Query<Int> = QueryDsl.delete(a).where { a.addressId eq 15 }
/*
delete from ADDRESS as t0_ where t0_.ADDRESS_ID = ?
*/
```

デフォルトではWHERE句の指定は必須です。もし`where`のブロック内で条件が指定されない場合は例外が発生します。
意図的に全件削除を認めたい場合は`option`を呼び出して`allowEmptyWhereClause`に`true`を設定します。

```kotlin
val query: Query<Int> = QueryDsl.delete(e).where {}.options { it.copy(allowEmptyWhereClause = true) }
/*
delete from EMPLOYEE as t0_
*/
```

このクエリを実行した場合の戻り値は削除された件数です。

以下のドキュメントも参照ください。

- [比較演算子]({{< relref "expression.md#comparison-operator" >}})
- [論理演算子]({{< relref "expression.md#logical-operator" >}})
