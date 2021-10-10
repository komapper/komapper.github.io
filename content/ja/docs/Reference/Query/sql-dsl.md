---
title: "SQL DSL"
linkTitle: "SQL DSL"
weight: 20
description: >
  柔軟にSQLを組み立てるためのDSL
---

## 概要 {#overview}

SQL DSLはエンティティのマッピング定義情報を用いてSQLを柔軟に組み立てます。
ただし、[Entity DSL]({{< relref "entity-dsl.md" >}}) とは異なり以下の機能は提供しません。

- エンティティのIDを用いた重複除去やエンティティの関連づけ
- IDやタイムスタンプの生成
  - ただしデータベースのAUTO INCREMENT機能で生成されたIDの取得はサポート
- バージョン番号を用いた楽観的排他制御

しかし、逆に、SQL DSLはEntity DSLでは組み立てられない次のSQLをサポートします。

- 射影
- HAVINGやGROUP BY
- UNIONやINTERSECT

## SELECT

SELECTクエリは`SqlDsl`の`from`を呼び出して生成します。これが基本の形となります。

次のクエリは`ADDRESS`テーブルを全件取得するSQLに対応します。

```kotlin
val query: Query<List<Address>> = SqlDsl.from(a)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_
*/
```

`from`を呼び出した後、以下に説明するような関数をいくつか呼び出すことでクエリを組み立てます。

### where {#select-where}

WHERE句を指定する場合は`where`を呼び出します。

```kotlin
val query: Query<List<Address>> = SqlDsl.from(a).where { a.addressId eq 1 }
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
val query: Query<List<Address>> = SqlDsl.from(a).innerJoin(e) { a.addressId eq e.addressId }
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ inner join EMPLOYEE as t1_ on (t0_.ADDRESS_ID = t1_.ADDRESS_ID)
*/
```

以下のドキュメントも参照ください。

- [比較演算子]({{< relref "expression.md#comparison-operator" >}})

### leftJoin {#select-leftjoin}

LEFT OUTER JOINを行う場合は`leftJoin`を呼び出します。

```kotlin
val query: Query<List<Address>> = SqlDsl.from(a).leftJoin(e) { a.addressId eq e.addressId }
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ left outer join EMPLOYEE as t1_ on (t0_.ADDRESS_ID = t1_.ADDRESS_ID)
*/
```

以下のドキュメントも参照ください。

- [比較演算子]({{< relref "expression.md#comparison-operator" >}})

### forUpdate {#select-forupdate}

FOR UPDATE句を指定する場合は`forUpdate`を呼び出します。

```kotlin
val query: Query<List<Address>> = SqlDsl.from(a).where { a.addressId eq 1 }.forUpdate()
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID = ? for update
*/
```

### orderBy {#select-orderby}

ORDER BY句を指定する場合は`orderBy`を呼び出します。

```kotlin
val query: Query<List<Adress>> = SqlDsl.from(a).orderBy(a.addressId)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ order by t0_.ADDRESS_ID asc
*/
```

デフォルトでは昇順ですが降順を指定する場合はカラムを`orderBy`に渡す前にカラムに対して`desc`を呼び出します。
また、昇順を表す`asc`を明示的に呼び出すことやカラムを複数指定することもできます。

```kotlin
val query: Query<List<Adress>> = SqlDsl.from(a).orderBy(a.addressId.desc(), a.street.asc())
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ order by t0_.ADDRESS_ID desc, t0_.STREET asc
*/
```

### offset, limit {#select-offset-limit}

指定した位置から一部の行を取り出すには`offset`や`limit`を呼び出します。

```kotlin
val query: Query<List<Adress>> = SqlDsl.from(a).orderBy(a.addressId).offset(10).limit(3)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ order by t0_.ADDRESS_ID asc offset ? rows fetch first ? rows only
*/
```

### distinct {#select-distinct}

DISTINCTキーワードを指定するには`distinct`を呼び出します。

```kotlin
val query: Query<List<Department>> = SqlDsl.from(d).distinct().innerJoin(e) { d.departmentId eq e.departmentId }
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
val query: Query<List<String?> = SqlDsl.from(a)
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
val query: Query<List<Pair<Int?, String?>>> = SqlDsl.from(a)
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
val query: Query<List<Triple<Int?, String?, Int?>>> = SqlDsl.from(a)
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
val query: Query<List<Columns>> = SqlDsl.from(a)
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
val query: Query<List<Pair<Int?, Long?>>> = SqlDsl.from(e)
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
val query: Query<List<Pair<Int?, Long?>>> = SqlDsl.from(e)
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
val q1: Query<List<Pair<Int?, String?>>> = SqlDsl.from(e).where { e.employeeId eq 1 }
    .select(e.employeeId alias "ID", e.employeeName alias "NAME")
val q2: Query<List<Pair<Int?, String?>>> = SqlDsl.from(a).where { a.addressId eq 2 }
  .select(a.addressId alias "ID", a.street alias "NAME")
val q3: Query<List<Pair<Int?, String?>>> = SqlDsl.from(d).where { d.departmentId eq 3 }
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
val query: Query<Address> = SqlDsl.from(a).where { a.addressId eq 1 }.first()
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID = ?
*/
```

### firstOrNull {#select-firstornull}

1件もしくは0件の場合に`null`を返却するクエリであることを示すには最後に`firstOrNull`を呼び出します。

```kotlin
val query: Query<Address?> = SqlDsl.from(a).where { a.addressId eq 1 }.firstOrNull()
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID = ?
*/
```

### collect {#select-collect}

結果セットを`kotlinx.coroutines.flow.Flow`として処理するには最後に`collect`を呼び出します。

```kotlin
val query: Query<Int> = SqlDsl.from(a).collect { flow: Flow<Address> -> flow.count() }
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_
*/
```

{{< alert title="Note" >}}
`collect`を使うと、結果セットを全てメモリに読み込んだ後に処理するのではなく結果セットを1件ずつ読み込みながら処理することになります。
したがって、メモリの使用効率を向上させることができます。
{{< /alert >}}

## INSERT

INSERTクエリは`SqlDsl`の`insert`とそれに続く関数を呼び出して生成します。

クエリを実行した場合の戻り値は追加された件数と生成されるIDの`Pair`です。
IDはエンティティクラスのマッピング定義に`@KomapperAutoIncrement`が注釈されている場合にのみ返されます。

クエリ実行時にキーが重複した場合、`org.komapper.core.UniqueConstraintException`がスローされます。

### values {#insert-values}

1件を追加するには`values`を呼び出します。

```kotlin
val query: Query<Pair<Int, Int?>> = SqlDsl.insert(a).values {
  a.addressId set 19
  a.street set "STREET 16"
  a.version set 0
}
/*
insert into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, ?, ?)
*/
```

### select {#insert-select}

検索結果を追加するには`select`を呼び出します。

```kotlin
val aa = Address.newMeta(table = "ADDRESS_ARCHIVE")
val query: Query<Int, Int?> = SqlDsl.insert(aa).select {
  SqlDsl.from(a).where { a.addressId between 1..5 }
}
/*
insert into ADDRESS_ARCHIVE (ADDRESS_ID, STREET, VERSION) select t1_.ADDRESS_ID, t1_.STREET, t1_.VERSION from ADDRESS as t1_ where t1_.ADDRESS_ID between ? and ?
*/
```

## UPDATE

UPDATEクエリは`SqlDsl`の`update`とそれに続く関数を呼び出して生成します。

クエリを実行した場合の戻り値は更新された件数です。

クエリ実行時にキーが重複した場合、`org.komapper.core.UniqueConstraintException`がスローされます。

### set {#update-set}

更新データをセットするには`set`を呼び出します。

```kotlin
val query: Query<Int> = SqlDsl.update(a).set {
  a.street set "STREET 16"
}.where {
  a.addressId eq 1
}
/*
update ADDRESS as t0_ set STREET = ? where t0_.ADDRESS_ID = ?
*/
```

### where {#update-where}

WHERE句を指定する場合は`where`を呼び出します。

デフォルトではWHERE句の指定は必須でありWHERE句が指定されない場合は例外が発生します。
意図的にWHERE句を指定したくない場合は`option`を呼び出して`allowEmptyWhereClause`に`true`を設定します。

```kotlin
val query: Query<Int> = SqlDsl.update(e).set {
    e.employeeName set "ABC"
}.options { 
    it.copy(allowEmptyWhereClause = true)
}
```

## DELETE

DELETEクエリは`SqlDsl`の`delete`とそれに続く関数を呼び出して生成します。

クエリを実行した場合の戻り値は削除された件数です。

### where {#delete-where}

WHERE句を指定するには`where`を呼び出します。

```kotlin
val query: Query<Int> = SqlDsl.delete(a).where { a.addressId eq 15 }
/*
delete from ADDRESS as t0_ where t0_.ADDRESS_ID = ?
*/
```

デフォルトではWHERE句の指定は必須でありWHERE句が指定されない場合は例外が発生します。
意図的にWHERE句を指定したくない場合は`option`を呼び出して`allowEmptyWhereClause`に`true`を設定します。

```kotlin
val query: Query<Int> = SqlDsl.delete(e).options { it.copy(allowEmptyWhereClause = true) }
/*
delete from EMPLOYEE as t0_
*/
```