---
title: "INSERTクエリ"
linkTitle: "追加"
weight: 20
description: 追加のためのクエリ
---

## 概要 {#overview}

INSERTクエリは`QueryDsl`の`insert`とそれに続く関数を呼び出して構築します。

クエリ実行時にキーが重複した場合、`org.komapper.core.UniqueConstraintException`がスローされます。

## single

エンティティ1件を追加するには`single`を呼び出します。

```kotlin
val address: Address = Address(16, "STREET 16", 0)
val query: Query<Address> = QueryDsl.insert(a).single(address)
/*
insert into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, ?, ?)
*/
```

このクエリを実行した場合の戻り値は追加されたデータを表す新しいエンティティです。

下記のマッピング定義に応じて、発行されるSQLにも新しいエンティティにも適切な値が反映されます。

- `@KomapperAutoIncrement`
- `@KomapperSequence`
- `@KomapperVersion`
- `@KomapperCreatedAt`
- `@KomapperUpdatedAt`

## multiple

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

下記のマッピング定義に応じて、発行されるSQLにも新しいエンティティにも適切な値が反映されます。

- `@KomapperAutoIncrement`
- `@KomapperSequence`
- `@KomapperVersion`
- `@KomapperCreatedAt`
- `@KomapperUpdatedAt`

## batch

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

下記のマッピング定義に応じて、発行されるSQLにも新しいエンティティにも適切な値が反映されます。

- `@KomapperAutoIncrement`
- `@KomapperSequence`
- `@KomapperVersion`
- `@KomapperCreatedAt`
- `@KomapperUpdatedAt`

## onDuplicateKeyIgnore

`onDuplicateKeyIgnore`を呼び出すことでキーが重複した場合のエラーを無視できます。
`onDuplicateKeyIgnore`には重複チェック対象のキーを指定できます。指定されない場合は主キーが使われます。

```kotlin
val address: Address = ..
val query: Query<Address?> = QueryDsl.insert(a).onDuplicateKeyIgnore().executeAndGet(address)
```

上記クエリに対応するSQLはどのDialectを使うかで異なります。
例えば、MariaDBのDialectを使う場合は次のようなSQLになります。

```sql
insert ignore into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, ?, ?)
```

PostgreSQLのDialectを使う場合は次のようなSQLになります。

```sql
insert into ADDRESS as t0_ (ADDRESS_ID, STREET, VERSION) values (?, ?, ?) on conflict (ADDRESS_ID) do nothing
```

{{< alert color="warning" title="Warning" >}}
auto increment カラムは重複しません。
{{< /alert >}}

{{< alert title="Support for the ON CONFLICT WHERE clause in PostgreSQL" >}}
PostgreSQLを利用する場合、`onDuplicateKeyIgnore`にラムダ式を指定することで
`ON CONFLICT WHERE`句を表現できます。

```kotlin
val address: Address = ..
val query: Query<Address?> = QueryDsl.insert(a).onDuplicateKeyIgnore() {
    a.street.isNull()
}.single(address)
```
{{< /alert >}}

### executeAndGet {#onduplicatekeyignore-executeandget}

`onDuplicateKeyIgnore`に続けて`executeAndGet`を呼び出した場合、戻り値は追加されたデータを表すエンティティです。
キーが重複していた場合は`null`が戻ります。

### single {#onduplicatekeyignore-single}

`onDuplicateKeyIgnore`に続けて`single`を呼び出した場合の戻り値はドライバの返す値です。

### multiple {#onduplicatekeyignore-multiple}

`onDuplicateKeyIgnore`に続けて`multiple`を呼び出した場合の戻り値はドライバの返す値です。

### batch {#onduplicatekeyignore-batch}

`onDuplicateKeyIgnore`に続けて`batch`を呼び出した場合の戻り値はドライバの返す値です。

## onDuplicateKeyUpdate

`onDuplicateKeyUpdate`を呼び出すことでキーが重複した場合に対象行を更新できます。
`onDuplicateKeyUpdate`には重複チェック対象のキーを指定できます。指定されない場合は主キーが使われます。

```kotlin
val department: Department = ..
val query: Query<Address> = QueryDsl.insert(d).onDuplicateKeyUpdate().executeAndGet(department)
```

上記クエリに対応するSQLはどのDialectを使うかで異なります。
例えば、MariaDBのDialectを使う場合は次のようなSQLになります。

```sql
insert into DEPARTMENT (DEPARTMENT_ID, DEPARTMENT_NO, DEPARTMENT_NAME, LOCATION, VERSION) values (?, ?, ?, ?, ?) on duplicate key update DEPARTMENT_NO = values(DEPARTMENT_NO), DEPARTMENT_NAME = values(DEPARTMENT_NAME), LOCATION = values(LOCATION), VERSION = values(VERSION)
```

PostgreSQLのDialectを使う場合は次のようなSQLになります。

```sql
insert into DEPARTMENT as t0_ (DEPARTMENT_ID, DEPARTMENT_NO, DEPARTMENT_NAME, LOCATION, VERSION) values (?, ?, ?, ?, ?) on conflict (DEPARTMENT_ID) do update set DEPARTMENT_NO = excluded.DEPARTMENT_NO, DEPARTMENT_NAME = excluded.DEPARTMENT_NAME, LOCATION = excluded.LOCATION, VERSION = excluded.VERSION
```

{{< alert color="warning" title="Warning" >}}
更新される行に楽観的ロックは適用されません。
{{< /alert >}}

{{< alert color="warning" title="Warning" >}}
auto increment カラムは重複しません。
{{< /alert >}}

{{< alert title="Support for the ON CONFLICT WHERE clause in PostgreSQL" >}}
PostgreSQLを利用する場合、`onDuplicateKeyUpdate`にラムダ式を指定することで
`ON CONFLICT WHERE`句を表現できます。

```kotlin
val address: Address = ..
val query: Query<Address?> = QueryDsl.insert(a).onDuplicateKeyUpdate() {
    a.street.isNull()
}.single(address)
```
{{< /alert >}}


### executeAndGet {#onduplicatekeyupdate-executeandget}

`onDuplicateKeyUpdate`に続けて`executeAndGet`を呼び出した場合、戻り値は追加もしくは更新されたデータを表すエンティティです。

### single {#onduplicatekeyupdate-single}

`onDuplicateKeyUpdate`に続けて`single`を呼び出した場合の戻り値はドライバの返す値です。

### multiple {#onduplicatekeyupdate-multiple}

`onDuplicateKeyUpdate`に続けて`multiple`を呼び出した場合の戻り値はドライバの返す値です。

### batch {#onduplicatekeyupdate-batch}

`onDuplicateKeyUpdate`に続けて`batch`を呼び出した場合の戻り値はドライバの返す値です。

### set {#onduplicatekeyupdate-set}

`onDuplicateKeyUpdate`の呼び出し後、`set`を使って更新対象のカラムに特定の値を設定できます。

```kotlin
val department: Department = ..
val query = QueryDsl.insert(d).onDuplicateKeyUpdate().set { excluded ->
    d.departmentName eq "PLANNING2"
    d.location eq concat(d.location, concat("_", excluded.location))
}.single(department)
```

`set`関数に渡すラムダ式には`excluded`パラメータがあります。
`excluded`は、追加しようとしているエンティティのメタモデルを表します。
したがって、`excluded`の利用により追加しようとしているデータに基づいた更新を実現できます。

PostgreSQLのDialectを使う場合、上記のクエリは次のようなSQLとして発行されます。

```sql
insert into DEPARTMENT as t0_ (DEPARTMENT_ID, DEPARTMENT_NO, DEPARTMENT_NAME, LOCATION, VERSION) values (?, ?, ?, ?, ?) on conflict (DEPARTMENT_ID) do update set DEPARTMENT_NAME = ?, LOCATION = (concat(t0_.LOCATION, (concat(?, excluded.LOCATION))))
```

MySQLのDialectを使う場合、上記のクエリは次のようなSQLとして発行されます。

```sql
insert into DEPARTMENT (DEPARTMENT_ID, DEPARTMENT_NO, DEPARTMENT_NAME, LOCATION, VERSION) values (?, ?, ?, ?, ?) as excluded on duplicate key update DEPARTMENT_NAME = ?, LOCATION = (concat(DEPARTMENT.LOCATION, (concat(?, excluded.LOCATION))))
```

### where {#onduplicatekeyupdate-where}

`onDuplicateKeyUpdate`の呼び出し後、`where`を使って更新条件を設定できます。

```kotlin
val department: Department = ..
val query = QueryDsl.insert(d).onDuplicateKeyUpdate().where {
    d.departmentName eq "PLANNING"
}.single(department)
```

{{< alert color="warning" title="Warning" >}}
MariaDBやMySQLのDialectではサポートされていません。
{{< /alert >}}

## dangerouslyOnDuplicateKeyIgnore

この関数は、任意のconflict_targetを指定できることを除いて、[onDuplicateKeyIgnore]({{< relref "#onduplicatekeyignore" >}})と同等です。

{{< alert color="warning" title="Warning" >}}
この関数にSQLインジェクションの可能性がある文字列を渡さないでください。
{{< /alert >}}

{{< alert color="warning" title="Warning" >}}
PostgreSQLのDialectでのみサポートされます。
conflict_targetについてはPostgreSQLのドキュメントを参照ください。
https://www.postgresql.org/docs/current/sql-insert.html
{{< /alert >}}

## dangerouslyOnDuplicateKeyUpdate

この関数は、任意のconflict_targetを指定できることを除いて、[onDuplicateKeyUpdate]({{< relref "#onduplicatekeyupdate" >}})と同等です。

{{< alert color="warning" title="Warning" >}}
この関数にSQLインジェクションの可能性がある文字列を渡さないでください。
{{< /alert >}}

{{< alert color="warning" title="Warning" >}}
PostgreSQLのDialectでのみサポートされます。
conflict_targetについてはPostgreSQLのドキュメントを参照ください。
https://www.postgresql.org/docs/current/sql-insert.html
{{< /alert >}}

## values

プロパティごとの値を設定して1件を追加するには`values`関数にラムダ式を渡します。

ラムダ式の中では`eq`関数を使って値を設定できます。

```kotlin
val query: Query<Pair<Long, Int?>> = QueryDsl.insert(a).values {
  a.addressId eq 19
  a.street eq "STREET 16"
  a.version eq 0
}
/*
insert into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, ?, ?)
*/
```

`eqIfNotNull`を使って値が`null`でない場合にのみ値を設定することもできます。

```kotlin
val query: Query<Pair<Long, Int?>> = QueryDsl.insert(a).values {
    a.addressId eq 19
    a.street eqIfNotNull street
    a.version eq 0
}
```

クエリを実行した場合の戻り値は追加された件数と生成されたIDの`Pair`です。
IDはマッピング定義に`@KomapperAutoIncrement`や`@KomapperSequence`が注釈されている場合にのみ返されます。

以下のマッピング定義を持つプロパティについて明示的に`eq`を呼び出さない場合、発行されるSQLに自動で値が設定されます。
明示的に`eq`を呼び出した場合は明示した値が優先されます。

- `@KomapperSequence`
- `@KomapperVersion`
- `@KomapperCreatedAt`
- `@KomapperUpdatedAt`

`@KomapperAutoIncrement`の定義は常に有効です。明示的に値を`eq`した場合、無視されます。

## select

検索結果を追加するには`select`を呼び出します。

```kotlin
val aa = Meta.address.clone(table = "ADDRESS_ARCHIVE")
val query: Query<Long, List<Int>> = QueryDsl.insert(aa).select {
  QueryDsl.from(a).where { a.addressId between 1..5 }
}
/*
insert into ADDRESS_ARCHIVE (ADDRESS_ID, STREET, VERSION) select t1_.ADDRESS_ID, t1_.STREET, t1_.VERSION from ADDRESS as t1_ where t1_.ADDRESS_ID between ? and ?
*/
```

クエリを実行した場合の戻り値は追加された件数と生成されるIDのリストの`Pair`です。
IDはエンティティクラスのマッピング定義に`@KomapperAutoIncrement`が注釈されている場合にのみ生成されます。

以下のマッピング定義は考慮されません。

- `@KomapperSequence`
- `@KomapperVersion`
- `@KomapperCreatedAt`
- `@KomapperUpdatedAt`

## returning

以下の関数の後続で`returning`関数を呼び出すことで、 追加された値や更新された値を取得できます。

- single
- multiple
- values

`single`関数の後続で`returning`関数を呼び出す例です。

```kotlin
val address: Address = Address(16, "STREET 16", 0)
val query: Query<Address> = QueryDsl.insert(a).single(address).returning()
/*
insert into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, ?, ?) returning ADDRESS_ID, STREET, VERSION
*/
```

`returning`関数にプロパティを指定することで取得対象のカラムを限定できます。

```kotlin
val query: Query<Int?> = QueryDsl.insert(a).single(address).returning(a.addressId)
/*
insert into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, ?, ?) returning ADDRESS_ID
*/
```

```kotlin
val query: Query<Pair<Int?, String?>> = QueryDsl.insert(a).single(address).returning(a.addressId, a.street)
/*
insert into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, ?, ?) returning ADDRESS_ID, STREET
*/
```

```kotlin
val query: Query<Triple<Int?, String?, Int?>> = QueryDsl.insert(a).single(address).returning(a.addressId, a.street, a.version)
/*
insert into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, ?, ?) returning ADDRESS_ID, STREET, VERSION
*/
```

`returning`関数は、`onDuplicateKeyIgnore`関数や`onDuplicateKeyUpdate`関数と組み合わせて使用することもできます。

```kotlin
val departments = listOf(
    Department(5, 50, "PLANNING", 1),
    Department(1, 60, "DEVELOPMENT", 1),
)
val query: Query<List<Department>> = QueryDsl.insert(d).onDuplicateKeyUpdate().multiple(departments).returning()
/*
insert into department as t0_ (department_id, department_no, department_name, version) 
values (?, ?, ?, ?), (?, ?, ?, ?) on conflict (department_id)
do update set department_no = excluded.department_no, department_name = excluded.department_name, version = excluded.version 
returning department_id, department_no, department_name, version
*/
```

{{< alert color="warning" title="Warning" >}}
`returning`関数は次のDialectでのみサポートされています。
- H2
- MariaDB
- Oracle Database
- PostgreSQL
- SQL Server

ただし、Oracle DatabaseのDialectで`returning`関数を使う場合、次のような制限があります。
- R2DBCはサポートされない
- `multiple`関数との組み合わせはサポートされない
- `onDuplicateKeyIgnore`関数や`onDuplicateKeyUpdate`関数との組み合わせはサポートされない
{{< /alert >}}

## options

クエリの挙動をカスタマイズするには`options`を呼び出します。
ラムダ式のパラメータはデフォルトのオプションを表します。
変更したいプロパティを指定して`copy`メソッドを呼び出してください。

```kotlin
val address: Address = Address(16, "STREET 16", 0)
val query: Query<Address> = QueryDsl.insert(a).single(address).options {
    it.copy(
      queryTimeoutSeconds = 5
    )
}
```

指定可能なオプションには以下のものがあります。

batchSize
: バッチサイズです。デフォルトは`null`です。

disableSequenceAssignment
: IDにシーケンスで生成した値をアサインすることを無効化するかどうかです。デフォルトは`false`です。

queryTimeoutSeconds
: クエリタイムアウトの秒数です。デフォルトは`null`でドライバの値を使うことを示します。

returnGeneratedKeys
: AUTO INCREMENTされたIDの値を返すかどうかです。デフォルトは`true`です。

suppressLogging
: SQLのログ出力を抑制するかどうかです。デフォルトは`false`です。

[executionOptions]({{< relref "../../database-config/#executionoptions" >}})
の同名プロパティよりもこちらに明示的に設定した値が優先的に利用されます。
