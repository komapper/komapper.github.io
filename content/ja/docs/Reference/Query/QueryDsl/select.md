---
title: "SELECT"
linkTitle: "SELECT"
weight: 10
description: >
  SELECTクエリ
---

## 概要 {#overview}

SELECTクエリは`QueryDsl`の`from`を呼び出して構築します。これが基本の形となります。

次のクエリは`ADDRESS`テーブルを全件取得するSQLに対応します。

```kotlin
val query: Query<List<Address>> = QueryDsl.from(a)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_
*/
```

`from`を呼び出した後、以下に説明するような関数をいくつか呼び出すことでクエリを組み立てます。

## where

WHERE句を指定する場合は`where`を呼び出します。

```kotlin
val query: Query<List<Address>> = QueryDsl.from(a).where { a.addressId eq 1 }
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID = ?
*/
```

## innerJoin

INNER JOINを行う場合は`innerJoin`を呼び出します。

```kotlin
val query: Query<List<Address>> = QueryDsl.from(a).innerJoin(e) { a.addressId eq e.addressId }
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ inner join EMPLOYEE as t1_ on (t0_.ADDRESS_ID = t1_.ADDRESS_ID)
*/
```

## leftJoin

LEFT OUTER JOINを行う場合は`leftJoin`を呼び出します。

```kotlin
val query: Query<List<Address>> = QueryDsl.from(a).leftJoin(e) { a.addressId eq e.addressId }
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ left outer join EMPLOYEE as t1_ on (t0_.ADDRESS_ID = t1_.ADDRESS_ID)
*/
```

## forUpdate

FOR UPDATE句を指定する場合は`forUpdate`を呼び出します。

```kotlin
val query: Query<List<Address>> = QueryDsl.from(a).where { a.addressId eq 1 }.forUpdate()
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID = ? for update
*/
```

## orderBy

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

## offset, limit {#offset-limit}

指定した位置から一部の行を取り出すには`offset`や`limit`を呼び出します。

```kotlin
val query: Query<List<Adress>> = QueryDsl.from(a).orderBy(a.addressId).offset(10).limit(3)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ order by t0_.ADDRESS_ID asc offset ? rows fetch first ? rows only
*/
```

## distinct

DISTINCTキーワードを指定するには`distinct`を呼び出します。

```kotlin
val query: Query<List<Department>> = QueryDsl.from(d).distinct().innerJoin(e) { d.departmentId eq e.departmentId }
/*
select distinct t0_.DEPARTMENT_ID, t0_.DEPARTMENT_NO, t0_.DEPARTMENT_NAME, t0_.LOCATION, t0_.VERSION from DEPARTMENT as t0_ inner join EMPLOYEE as t1_ on (t0_.DEPARTMENT_ID = t1_.DEPARTMENT_ID)
*/
```

## select

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

## selectColumns

4つ未満のカラムの射影で結果を`Columns`として受け取りたい場合は`select`の代わりに`selectColumns`を呼び出します。

```kotlin
val query: Query<List<Columns> = QueryDsl.from(a)
    .where {
        a.addressId inList listOf(1, 2)
    }
    .orderBy(a.addressId)
    .selectColumn(a.street)
/*
select t0_.STREET from ADDRESS as t0_ where t0_.ADDRESS_ID in (?, ?) order by t0_.ADDRESS_ID asc
*/
```

## having

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

## groupBy

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

## union

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

## first

1件を返却するクエリであることを示すには最後に`first`を呼び出します。

```kotlin
val query: Query<Address> = QueryDsl.from(a).where { a.addressId eq 1 }.first()
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID = ?
*/
```

## firstOrNull

1件もしくは0件の場合に`null`を返却するクエリであることを示すには最後に`firstOrNull`を呼び出します。

```kotlin
val query: Query<Address?> = QueryDsl.from(a).where { a.addressId eq 1 }.firstOrNull()
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID = ?
*/
```

## collect

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

## include

JOINしたテーブルのカラムをSELECT句に含める場合は`include`を呼び出します。

```kotlin
val a = Meta.address
val e = Meta.employee
val d = Meta.department

val query: Query<EntityStore> = QueryDsl.from(a)
  .innerJoin(e) {
    a.addressId eq e.addressId
  }.innerJoin(d) {
    e.departmentId eq d.departmentId
  }.include(e, d)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION, t1_.EMPLOYEE_ID, t1_.EMPLOYEE_NO, t1_.EMPLOYEE_NAME, t1_.MANAGER_ID, t1_.HIREDATE, t1_.SALARY, t1_.DEPARTMENT_ID, t1_.ADDRESS_ID, t1_.VERSION, t2_.DEPARTMENT_ID, t2_.DEPARTMENT_NO, t2_.DEPARTMENT_NAME, t2_.LOCATION, t2_.VERSION from ADDRESS as t0_ inner join EMPLOYEE as t1_ on (t0_.ADDRESS_ID = t1_.ADDRESS_ID) inner join DEPARTMENT as t2_ on (t1_.DEPARTMENT_ID = t2_.DEPARTMENT_ID)
*/
```

このクエリを実行した場合の戻り値は、SQLの結果セットから生成された複数のエンティティを保持する
`org.komapper.core.dsl.query.EntityStore`インスタンスです。

`EntityStore`からエンティティの一覧を`List`として取得したり、エンティティの関連を`Map`として取得したりが可能です。
上述の`query`を実行して`EntityStore`から一覧や関連を取り出す例を以下に示します。

```kotlin
val store: EntityStore = db.runQuery { query }

val addresses: List<Address> = store.list(a)
val employees: List<Employee> = store.list(e)
val departments: List<Department> = store.list(d)

val departmentEmployees: Map<Department, Set<Employee>> = store.oneToMany(d, e)
val employeeDepartment: Map<Employee, Department?> = store.oneToOne(e, d)
val employeeAddress: Map<Employee, Address?> = store.oneToOne(e, a)
```

関連を表す`Map`のキーをエンティティのIDに変換して取得することもできます。

```kotlin
val departmentIdEmployees: Map<Int, Set<Employee>> = store.oneToManyById(d, e)
```

## includeAll

JOINしたテーブル全てのカラムをSELECT句に含めたい場合は、`includeAll`を呼び出します。

```kotlin
val a = Meta.address
val e = Meta.employee
val d = Meta.department

val query: Query<EntityStore> = QueryDsl.from(a)
  .innerJoin(e) {
    a.addressId eq e.addressId
  }.innerJoin(d) {
    e.departmentId eq d.departmentId
  }.includeAll()
/*
select t0_.EMPLOYEE_ID, t0_.EMPLOYEE_NO, t0_.EMPLOYEE_NAME, t0_.MANAGER_ID, t0_.HIREDATE, t0_.SALARY, t0_.DEPARTMENT_ID, t0_.ADDRESS_ID, t0_.VERSION, t1_.ADDRESS_ID, t1_.STREET, t1_.VERSION, t2_.DEPARTMENT_ID, t2_.DEPARTMENT_NO, t2_.DEPARTMENT_NAME, t2_.LOCATION, t2_.VERSION from EMPLOYEE as t0_ inner join ADDRESS as t1_ on (t0_.ADDRESS_ID = t1_.ADDRESS_ID) inner join DEPARTMENT as t2_ on (t0_.DEPARTMENT_ID = t2_.DEPARTMENT_ID)
*/
```

これは [include](#include) で示した例と同等です。

## options

クエリの挙動をカスタマイズするには`options`を呼び出します。
ラムダ式のパラメータはデフォルトのオプションを表します。
変更したいプロパティを指定して`copy`メソッドを呼び出してください。

```kotlin
val query: Query<List<Address>> = QueryDsl.from(a).options {
    it.copty(
      fetchSize = 100,
      queryTimeoutSeconds = 5
    )
}
```

指定可能なオプションには以下のものがあります。

allowEmptyWhereClause
: 空のWHERE句を認めるかどうかです。デフォルトは`true`です。

escapeSequence
: LIKE句に指定されるエスケープシーケンスです。デフォルトは`null`で`Dialect`の値を使うことを示します。

fetchSize
: フェッチサイズです。デフォルトは`null`でドライバの値を使うことを示します。

maxRows
: 最大行数です。デフォルトは`null`でドライバの値を使うことを示します。

queryTimeoutSeconds
: クエリタイムアウトの秒数です。デフォルトは`null`でドライバの値を使うことを示します。

suppressLogging
: SQLのログ出力を抑制するかどうかです。デフォルトは`false`です。

[executionOptions]({{< relref "../../database-config/#executionoptions" >}})
の同名プロパティよりもこちらに明示的に設定した値が優先的に利用されます。
