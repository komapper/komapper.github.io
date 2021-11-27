---
title: "UPDATE"
linkTitle: "UPDATE"
weight: 30
description: >
  UPDATE文
---

## 概要 {#overview}

UPDATEクエリは`QueryDsl`の`update`とそれに続く関数を呼び出して生成します。

クエリ実行時にキーが重複した場合、`org.komapper.core.UniqueConstraintException`がスローされます。

## single {#single}

エンティティ1件を更新するには`single`を呼び出します。

```kotlin
val address: Address = ..
val query: Query<Address> = QueryDsl.update(a).single(address)
/*
update ADDRESS set STREET = ?, VERSION = ? + 1 where ADDRESS_ID = ? and VERSION = ?
*/
```

このクエリを実行した場合の戻り値は追加されたデータを表す新しいエンティティです。

下記のマッピング定義に応じて、発行されるSQLにも新しいエンティティにも適切な値が反映されます。

- `@KomapperId`
- `@KomapperVersion`
- `@KomapperUpdatedAt`

クエリ実行時に楽観的排他制御が失敗した場合、`org.komapper.core.OptimisticLockException`がスローされます。

## batch {#batch}

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

このクエリを実行した場合の戻り値は追加されたデータを表す新しいエンティティのリストです。

下記のマッピング定義に応じて、発行されるSQLにも新しいエンティティにも適切な値が反映されます。

- `@KomapperVersion`
- `@KomapperUpdatedAt`

クエリ実行時に楽観的排他制御が失敗した場合、`org.komapper.core.OptimisticLockException`がスローされます。

## set {#set}

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

以下のマッピング定義を持つプロパティについて明示的に`set`を呼び出さない場合、発行されるSQLに自動で値が設定されます。
明示的に`set`した場合は明示した値が優先されます。

- `@KomapperVersion`
- `@KomapperUpdatedAt`

## where {#update-where}

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

## options

クエリの挙動をカスタマイズするには`options`を呼び出します。
ラムダ式のパラメータはデフォルトのオプションを表します。
変更したいプロパティを指定して`copy`メソッドを呼び出してください。

```kotlin
val address: Address = ..
val query: Query<Address> = QueryDsl.update(a).single(address).options {
    it.copty(
      queryTimeoutSeconds = 5
    )
}
```

指定可能なオプションには以下のものがあります。

allowEmptyWhereClause
: 空のWHERE句を認めるかどうかです。デフォルトは`false`です。

escapeSequence
: LIKE句に指定されるエスケープシーケンスです。デフォルトは`null`で`Dialect`の値を使うことを示します。

batchSize
: バッチサイズです。デフォルトは`null`です。

disableOptimisticLock
: 楽観的ロックを無効化するかどうかです。デフォルトは`false`です。この値が`true`のときWHERE句にバージョン番号が含まれません。

queryTimeoutSeconds
: クエリタイムアウトの秒数です。デフォルトは`null`でドライバの値を使うことを示します。

suppressLogging
: SQLのログ出力を抑制するかどうかです。デフォルトは`false`です。

suppressOptimisticLockException
: 楽観的ロックの取得に失敗した場合に`OptimisticLockException`のスローを抑制するかどうかです。デフォルトは`false`です。

[executionOptions]({{< relref "../../database-config/#executionoptions" >}})
の同名プロパティよりもこちらに明示的に設定した値が優先的に利用されます。
