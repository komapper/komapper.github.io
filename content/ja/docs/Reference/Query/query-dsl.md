---
title: "Query DSL"
linkTitle: "Query DSL"
weight: 10
description: >
  SQLのDMLを組み立てるためのDSL
---

## 概要 {#overview}

Query DSLはエンティティのマッピング定義情報を用いてSQLのDMLを柔軟に組み立てます。
本ページは以下の節から成ります。

- [SELECT]({{< relref "#select" >}})
- [INSERT]({{< relref "#insert" >}})
- [UPDATE]({{< relref "#update" >}})
- [DELETE]({{< relref "#delete" >}})
- [比較演算子]({{< relref "#comparison-operator" >}})
- [論理演算子]({{< relref "#logical-operator" >}})
- [算術演算子]({{< relref "#arithmetic-operator" >}})
- [集約関数]({{< relref "#aggregate-function" >}})
- [文字列関数]({{< relref "#string-function" >}})
- [リテラル]({{< relref "#literal" >}})


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

### innerJoin {#select-innerjoin}

INNER JOINを行う場合は`innerJoin`を呼び出します。

```kotlin
val query: Query<List<Address>> = QueryDsl.from(a).innerJoin(e) { a.addressId eq e.addressId }
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ inner join EMPLOYEE as t1_ on (t0_.ADDRESS_ID = t1_.ADDRESS_ID)
*/
```

### leftJoin {#select-leftjoin}

LEFT OUTER JOINを行う場合は`leftJoin`を呼び出します。

```kotlin
val query: Query<List<Address>> = QueryDsl.from(a).leftJoin(e) { a.addressId eq e.addressId }
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ left outer join EMPLOYEE as t1_ on (t0_.ADDRESS_ID = t1_.ADDRESS_ID)
*/
```

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
ただし、データベースがサポートしていない場合、SQLを発行した時点で例外が発生します。
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
val aa = Meta.address.clone(table = "ADDRESS_ARCHIVE")
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
意図的に全件更新を認める場合は`options`を呼び出して`allowEmptyWhereClause`に`true`を設定します。

```kotlin
val query: Query<Int> = QueryDsl.update(e).set {
    e.employeeName set "ABC"
}.options { 
    it.copy(allowEmptyWhereClause = true)
}
```

このクエリを実行した場合の戻り値は更新された件数です。

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

`options`を呼び出して`allowEmptyWhereClause`に`true`を設定する必要があります。

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
意図的に全件削除を認めたい場合は`options`を呼び出して`allowEmptyWhereClause`に`true`を設定します。

```kotlin
val query: Query<Int> = QueryDsl.delete(e).where {}.options { it.copy(allowEmptyWhereClause = true) }
/*
delete from EMPLOYEE as t0_
*/
```

このクエリを実行した場合の戻り値は削除された件数です。

## 比較演算子 {#comparison-operator}

[宣言]({{< relref "composition#declaration" >}}) の中で利用できます。

演算子の引数に`null`を渡した場合その演算子は評価されません。つまりSQLに変換されません。

```kotlin
val nullable: Int? = null
val query = QueryDsl.from(a).where { a.addressId eq nullable }
```

したがって、上記の`query`が実行された場合は次のSQLが発行されます。

```sql
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_
```

### eq {#comparison-operator-eq}

```kotlin
QueryDsl.from(a).where { a.addressId eq 1 }
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID = ?
*/
```

### notEq {#comparison-operator-noteq}

```kotlin
QueryDsl.from(a).where { a.addressId notEq 1 }
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID <> ?
*/
```

### less {#comparison-operator-less}

```kotlin
QueryDsl.from(a).where { a.addressId less 1 }
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID < ?
*/

```

### lessEq {#comparison-operator-lesseq}

```kotlin
QueryDsl.from(a).where { a.addressId lessEq 1 }
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID <= ?
*/
```

### greater {#comparison-operator-greater}

```kotlin
QueryDsl.from(a).where { a.addressId greater 1 }
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID > ?
*/
```

### greaterEq {#comparison-operator-greatereq}

```kotlin
QueryDsl.from(a).where { a.addressId greaterEq 1 }
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID >= ?
*/
```

### isNull {#comparison-operator-isnull}

```kotlin
QueryDsl.from(e).where { e.managerId.isNull() }
/*
select t0_.EMPLOYEE_ID, t0_.EMPLOYEE_NO, t0_.EMPLOYEE_NAME, t0_.MANAGER_ID, t0_.HIREDATE, t0_.SALARY, t0_.DEPARTMENT_ID, t0_.ADDRESS_ID, t0_.VERSION from EMPLOYEE as t0_ where t0_.MANAGER_ID is null
*/
```
### isNotNull {#comparison-operator-isnotnull}

```kotlin
QueryDsl.from(e).where { e.managerId.isNotNull() }
/*
select t0_.EMPLOYEE_ID, t0_.EMPLOYEE_NO, t0_.EMPLOYEE_NAME, t0_.MANAGER_ID, t0_.HIREDATE, t0_.SALARY, t0_.DEPARTMENT_ID, t0_.ADDRESS_ID, t0_.VERSION from EMPLOYEE as t0_ where t0_.MANAGER_ID is not null
*/
```

### like {#comparison-operator-like}

```kotlin
QueryDsl.from(a).where { a.street like "STREET 1_" }.orderBy(a.addressId)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.STREET like ? escape ? order by t0_.ADDRESS_ID asc
*/
```

### notLike {#comparison-operator-notlike}

```kotlin
QueryDsl.from(a).where { a.street notLike "STREET 1_" }.orderBy(a.addressId)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.STREET not like ? escape ? order by t0_.ADDRESS_ID asc
*/
```

### startsWith {#comparison-operator-startswith}

```kotlin
QueryDsl.from(a).where { a.street startsWith "STREET 1" }.orderBy(a.addressId)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.STREET like ? escape ? order by t0_.ADDRESS_ID asc
*/
```

### notStartsWith {#comparison-operator-notstartswith}

```kotlin
QueryDsl.from(a).where { a.street notStartsWith "STREET 1" }.orderBy(a.addressId)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.STREET not like ? escape ? order by t0_.ADDRESS_ID asc
*/
```

### contains {#comparison-operator-contains}

```kotlin
QueryDsl.from(a).where { a.street contains "T 1" }.orderBy(a.addressId)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.STREET like ? escape ? order by t0_.ADDRESS_ID asc
*/
```

### notContains {#comparison-operator-notcontains}

```kotlin
QueryDsl.from(a).where { a.street notContains "T 1" }.orderBy(a.addressId)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.STREET not like ? escape ? order by t0_.ADDRESS_ID asc
*/
```

### endsWith {#comparison-operator-endswith}

```kotlin
QueryDsl.from(a).where { a.street endsWith "1" }.orderBy(a.addressId)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.STREET like ? escape ? order by t0_.ADDRESS_ID asc
*/
```

### notEndsWith {#comparison-operator-notendswith}

```kotlin
QueryDsl.from(a).where { a.street notEndsWith "1" }.orderBy(a.addressId)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.STREET not like ? escape ? order by t0_.ADDRESS_ID asc
*/
```

### between {#comparison-operator-between}

```kotlin
QueryDsl.from(a).where { a.addressId between 5..10 }.orderBy(a.addressId)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID between ? and ? order by t0_.ADDRESS_ID asc
*/
```

### notBetween {#comparison-operator-notbetween}

```kotlin
QueryDsl.from(a).where { a.addressId notBetween 5..10 }.orderBy(a.addressId)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID not between ? and ? order by t0_.ADDRESS_ID asc
*/
```

### inList {#comparison-operator-inlist}

```kotlin
QueryDsl.from(a).where { a.addressId inList listOf(9, 10) }.orderBy(a.addressId.desc())
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID in (?, ?) order by t0_.ADDRESS_ID desc
*/
```

サブクエリも使えます。

```kotlin
QueryDsl.from(e).where {
    e.addressId inList {
        QueryDsl.from(a)
            .where {
                e.addressId eq a.addressId
                e.employeeName like "%S%"
            }.select(a.addressId)
    }
}
/*
select t0_.EMPLOYEE_ID, t0_.EMPLOYEE_NO, t0_.EMPLOYEE_NAME, t0_.MANAGER_ID, t0_.HIREDATE, t0_.SALARY, t0_.DEPARTMENT_ID, t0_.ADDRESS_ID, t0_.VERSION from EMPLOYEE as t0_ where t0_.ADDRESS_ID in (select t1_.ADDRESS_ID from ADDRESS as t1_ where t0_.ADDRESS_ID = t1_.ADDRESS_ID and t0_.EMPLOYEE_NAME like ? escape ?)
*/
```

### notInList {#comparison-operator-notinlist}

```kotlin
QueryDsl.from(a).where { a.addressId notInList (1..9).toList() }.orderBy(a.addressId)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID not in (?, ?, ?, ?, ?, ?, ?, ?, ?) order by t0_.ADDRESS_ID asc
*/
```

サブクエリも使えます。

```kotlin
QueryDsl.from(e).where {
    e.addressId notInList {
        QueryDsl.from(a).where {
            e.addressId eq a.addressId
            e.employeeName like "%S%"
        }.select(a.addressId)
    }
}
/*
select t0_.EMPLOYEE_ID, t0_.EMPLOYEE_NO, t0_.EMPLOYEE_NAME, t0_.MANAGER_ID, t0_.HIREDATE, t0_.SALARY, t0_.DEPARTMENT_ID, t0_.ADDRESS_ID, t0_.VERSION from EMPLOYEE as t0_ where t0_.ADDRESS_ID not in (select t1_.ADDRESS_ID from ADDRESS as t1_ where t0_.ADDRESS_ID = t1_.ADDRESS_ID and t0_.EMPLOYEE_NAME like ? escape ?)
*/
```

### inList2 {#comparison-operator-inlist2}

```kotlin
QueryDsl.from(a).where { a.addressId to a.version inList2 listOf(9 to 1, 10 to 1) }.orderBy(a.addressId.desc())
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where (t0_.ADDRESS_ID, t0_.VERSION) in ((?, ?), (?, ?)) order by t0_.ADDRESS_ID desc
*/
```

サブクエリも使えます。

```kotlin
QueryDsl.from(e).where {
    e.addressId to e.version inList2 {
        QueryDsl.from(a)
            .where {
                e.addressId eq a.addressId
                e.employeeName like "%S%"
            }.select(a.addressId, a.version)
    }
}
/*
select t0_.EMPLOYEE_ID, t0_.EMPLOYEE_NO, t0_.EMPLOYEE_NAME, t0_.MANAGER_ID, t0_.HIREDATE, t0_.SALARY, t0_.DEPARTMENT_ID, t0_.ADDRESS_ID, t0_.VERSION from EMPLOYEE as t0_ where (t0_.ADDRESS_ID, t0_.VERSION) in (select t1_.ADDRESS_ID, t1_.VERSION from ADDRESS as t1_ where t0_.ADDRESS_ID = t1_.ADDRESS_ID and t0_.EMPLOYEE_NAME like ? escape ?)
*/
```

### notInList2 {#comparison-operator-notinlist2}

```kotlin
QueryDsl.from(a).where { a.addressId to a.version notInList2 listOf(9 to 1, 10 to 1) }.orderBy(a.addressId)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where (t0_.ADDRESS_ID, t0_.VERSION) not in ((?, ?), (?, ?)) order by t0_.ADDRESS_ID asc
*/
```

サブクエリも使えます。

```kotlin
QueryDsl.from(e).where {
    e.addressId to e.version notInList2 {
        QueryDsl.from(a).where {
            e.addressId eq a.addressId
            e.employeeName like "%S%"
        }.select(a.addressId, a.version)
    }
}
/*
select t0_.EMPLOYEE_ID, t0_.EMPLOYEE_NO, t0_.EMPLOYEE_NAME, t0_.MANAGER_ID, t0_.HIREDATE, t0_.SALARY, t0_.DEPARTMENT_ID, t0_.ADDRESS_ID, t0_.VERSION from EMPLOYEE as t0_ where (t0_.ADDRESS_ID, t0_.VERSION) not in (select t1_.ADDRESS_ID, t1_.VERSION from ADDRESS as t1_ where t0_.ADDRESS_ID = t1_.ADDRESS_ID and t0_.EMPLOYEE_NAME like ? escape ?)
*/
```

### exists {#comparison-operator-exists}

```kotlin
QueryDsl.from(e).where {
    exists {
        QueryDsl.from(a).where {
            e.addressId eq a.addressId
            e.employeeName like "%S%"
        }
    }
}
/*
select t0_.EMPLOYEE_ID, t0_.EMPLOYEE_NO, t0_.EMPLOYEE_NAME, t0_.MANAGER_ID, t0_.HIREDATE, t0_.SALARY, t0_.DEPARTMENT_ID, t0_.ADDRESS_ID, t0_.VERSION from EMPLOYEE as t0_ where exists (select t1_.ADDRESS_ID, t1_.STREET, t1_.VERSION from ADDRESS as t1_ where t0_.ADDRESS_ID = t1_.ADDRESS_ID and t0_.EMPLOYEE_NAME like ? escape ?)
*/
```

### notExists {#comparison-operator-notexists}

```kotlin
QueryDsl.from(e).where {
    notExists {
        QueryDsl.from(a).where {
            e.addressId eq a.addressId
            e.employeeName like "%S%"
        }
    }
}
/*
select t0_.EMPLOYEE_ID, t0_.EMPLOYEE_NO, t0_.EMPLOYEE_NAME, t0_.MANAGER_ID, t0_.HIREDATE, t0_.SALARY, t0_.DEPARTMENT_ID, t0_.ADDRESS_ID, t0_.VERSION from EMPLOYEE as t0_ where not exists (select t1_.ADDRESS_ID, t1_.STREET, t1_.VERSION from ADDRESS as t1_ where t0_.ADDRESS_ID = t1_.ADDRESS_ID and t0_.EMPLOYEE_NAME like ? escape ?)
*/
```

## 論理演算子 {#logical-operator}

[宣言]({{< relref "composition#declaration" >}}) の中で利用できます。

### and {#logical-operator-and}

宣言の中で式を並べるとAND演算子で連結されます。

```kotlin
QueryDsl.from(a).where {
    a.addressId greater 1
    a.street startsWith "S"
    a.version less 100
}
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID > ? and t0_.STREET like ? escape ? and t0_.VERSION < ?
*/
```

明示的にAND演算子を使いたい場合は`and`関数にラムダ式を渡します。

```kotlin
QueryDsl.from(a).where {
  a.addressId greater 1
  and {
    a.street startsWith "S"
    a.version less 100
  }
}
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID > ? and (t0_.STREET like ? escape ? and t0_.VERSION < ?)
*/
```

### or {#logical-operator-or}

OR演算子で連結したい場合は`or`関数にラムダ式を渡します。

```kotlin
QueryDsl.from(a).where {
  a.addressId greater 1
  or {
    a.street startsWith "S"
    a.version less 100
  }
}
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID > ? or (t0_.STREET like ? escape ? and t0_.VERSION < ?)
*/
```

### not {#logical-operator-not}

NOT演算子を使うには`not`関数にラムダ式を渡します。

```kotlin
QueryDsl.from(a).where {
    a.addressId greater 5
    not {
        a.addressId greaterEq 10
    }
}.orderBy(a.addressId)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID > ? and not (t0_.ADDRESS_ID >= ?) order by t0_.ADDRESS_ID asc
*/
```

## 算術演算子 {#arithmetic-operator}

以下の演算子が使えます。

- `+`
- `-`
- `*`
- `/`
- `%`

これらの演算子は`org.komapper.core.dsl.operator`に定義されています。

`+`演算子を使った例を次に示します。

```kotlin
QueryDsl.update(a).set {
    a.version set (a.version + 10)
}.where {
    a.addressId eq 1
}
/*
update ADDRESS as t0_ set VERSION = (t0_.VERSION + ?) where t0_.ADDRESS_ID = ?
*/
```

## 文字列関数 {#string-function}

次の関数が使えます。

- concat
- substring
- lower
- upper
- trim
- ltrim
- rtrim

これらの関数は`org.komapper.core.dsl.operator`に定義されています。

`concat`関数を使った例を次に示します。

```kotlin
QueryDsl.update(a).set {
  a.street set (concat(concat("[", a.street), "]"))
}.where {
  a.addressId eq 1
}
/*
update ADDRESS as t0_ set STREET = (concat((concat(?, t0_.STREET)), ?)) where t0_.ADDRESS_ID = ?
*/
```

## 集約関数 {#aggregate-function}

次の関数が使えます。

- avg
- count
- sum
- max
- min

これらの関数は`org.komapper.core.dsl.operator`に定義されています。

呼び出して得られる式は`having`や`select`で使われることを想定しています。

```kotlin
QueryDsl.from(e)
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

### avg {#aggregate-function-avg}

```kotlin
QueryDsl.from(a).select(avg(a.addressId))
/*
select avg(t0_.ADDRESS_ID) from ADDRESS as t0_
*/
```

### count {#aggregate-function-count}

SQLの`count(*)`に変換するには`count`関数を引数なしで呼び出します。

```kotlin
QueryDsl.from(a).select(count()).first()
/*
select count(*) from ADDRESS as t0_
*/
```

`count`関数をカラムを指定して呼び出すこともできます。

```kotlin
QueryDsl.from(a).select(count(a.street)).first()
/*
select count(t0_.STREET) from ADDRESS as t0_
*/
```

### sum {#aggregate-function-sum}

```kotlin
QueryDsl.from(a).select(sum(a.addressId)).first()
/*
select sum(t0_.ADDRESS_ID) from ADDRESS as t0_
*/
```

### max {#aggregate-function-max}

```kotlin
QueryDsl.from(a).select(max(a.addressId))
/*
select max(t0_.ADDRESS_ID) from ADDRESS as t0_
*/
```

### min {#aggregate-function-min}

```kotlin
QueryDsl.from(a).select(min(a.addressId))
/*
select min(t0_.ADDRESS_ID) from ADDRESS as t0_
*/
```

## リテラル {#literal}

バインド変数を介さず直接値をリテラルとしてSQLに埋め込みたい場合は`literal`を呼び出します。

`literal`は`org.komapper.core.dsl.operator`に定義されています。

`literal`に渡せるKotlinの型は以下のものです。

- Boolean
- Int
- Long
- String

使用例です。

```kotlin
QueryDsl.insert(a).values {
  a.addressId set 100
  a.street set literal("STREET 100")
  a.version set literal(100)
}
/*
insert into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, 'STREET 100', 100)
*/
```