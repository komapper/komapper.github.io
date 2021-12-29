---
title: "INSERT"
linkTitle: "INSERT"
weight: 20
description: >
  INSERTクエリ
---

## 概要 {#overview}

INSERTクエリは`QueryDsl`の`insert`とそれに続く関数を呼び出して構築します。

クエリ実行時にキーが重複した場合かつ`onDuplicateKeyIgnore`や`onDuplicateKeyUpdate`を適切に呼び出していない場合、
`org.komapper.core.UniqueConstraintException`がスローされます。

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

### executeAndGet {#onduplicatekeyignore-executeandget}

`onDuplicateKeyIgnore`に続けて`executeAndGet`を呼び出した場合、戻り値は追加されたデータを表すエンティティです。
キーが重複していた場合は`null`が戻ります。

### single {#onduplicatekeyignore-single}

`onDuplicateKeyIgnore`に続けて`single`を呼び出した場合の戻り値はドライバの返す値です。

### multiple {#onduplicatekeyignore-multiple}

`onDuplicateKeyIgnore`に続けて`multiple`を呼び出した場合の戻り値はドライバの返す値です。

### batch {#onduplicatekeyignore-batch}

`onDuplicateKeyIgnore`に続けて`batch`を呼び出した場合の戻り値はドライバの返す値です。

{{< alert color="warning" title="Warning" >}}
R2DBCではサポートされていません。
{{< /alert >}}

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

### executeAndGet {#onduplicatekeyupdate-executeandget}

`onDuplicateKeyUpdate`に続けて`executeAndGet`を呼び出した場合、戻り値は追加もしくは更新されたデータを表すエンティティです。

{{< alert title="Note" >}}
戻り値を返すために、指定されたキーを使った検索クエリを追加で発行します。
{{< /alert >}}

### single {#onduplicatekeyupdate-single}

`onDuplicateKeyUpdate`に続けて`single`を呼び出した場合の戻り値はドライバの返す値です。

### multiple {#onduplicatekeyupdate-multiple}

`onDuplicateKeyUpdate`に続けて`multiple`を呼び出した場合の戻り値はドライバの返す値です。

### batch {#onduplicatekeyupdate-batch}

`onDuplicateKeyUpdate`に続けて`batch`を呼び出した場合の戻り値はドライバの返す値です。

{{< alert color="warning" title="Warning" >}}
R2DBCではサポートされていません。
{{< /alert >}}

### set {#onduplicatekeyupdate-set}

`onDuplicateKeyUpdate`の呼び出し後、他の関数を呼ぶ前に`set`を使って更新対象のカラムに特定の値を設定できます。

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

{{< alert color="warning" title="Warning" >}}
MariaDBのDialectではサポートされていません。
{{< /alert >}}

## values

プロパティごとの値を設定して1件を追加するには`values`関数にラムダ式を渡します。

ラムダ式の中では`eq`関数を使って値を設定できます。

```kotlin
val query: Query<Pair<Int, Int?>> = QueryDsl.insert(a).values {
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
val query: Query<Pair<Int, Int?>> = QueryDsl.insert(a).values {
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
val query: Query<Int, List<Int>> = QueryDsl.insert(aa).select {
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

## options

クエリの挙動をカスタマイズするには`options`を呼び出します。
ラムダ式のパラメータはデフォルトのオプションを表します。
変更したいプロパティを指定して`copy`メソッドを呼び出してください。

```kotlin
val address: Address = Address(16, "STREET 16", 0)
val query: Query<Address> = QueryDsl.insert(a).single(address).options {
    it.copty(
      queryTimeoutSeconds = 5
    )
}
```

指定可能なオプションには以下のものがあります。

batchSize
: バッチサイズです。デフォルトは`null`です。

disableSequenceAssignment
: IDにシーケンスで生成した値をアサインすることを無効化かどうかです。デフォルトは`false`です。

queryTimeoutSeconds
: クエリタイムアウトの秒数です。デフォルトは`null`でドライバの値を使うことを示します。

suppressLogging
: SQLのログ出力を抑制するかどうかです。デフォルトは`false`です。

[executionOptions]({{< relref "../../database-config/#executionoptions" >}})
の同名プロパティよりもこちらに明示的に設定した値が優先的に利用されます。
