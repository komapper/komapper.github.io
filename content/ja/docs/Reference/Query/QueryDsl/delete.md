---
title: "DELETEクエリ"
linkTitle: "DELETE"
weight: 40
description: >
---

## 概要 {#overview}

DELETEクエリは`QueryDsl`の`delete`とそれに続く関数を呼び出して構築します。

## single

エンティティ1件を削除するには`single`を呼び出します。

```kotlin
val address: Address = ..
val query: Query<Unit> = QueryDsl.delete(a).single(address)
/*
delete from ADDRESS as t0_ where t0_.ADDRESS_ID = ? and t0_.VERSION = ?
*/
```

下記のマッピング定義に応じて、発行されるSQLに適切な値が反映されます。

- `@KomapperId`
- `@KomapperVersion`

クエリ実行時に楽観的排他制御が失敗した場合、`org.komapper.core.OptimisticLockException`がスローされます。

## batch

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

下記のマッピング定義に応じて、発行されるSQLに適切な値が反映されます。

- `@KomapperId`
- `@KomapperVersion`

クエリ実行時に楽観的排他制御が失敗した場合、`org.komapper.core.OptimisticLockException`がスローされます。

## all

全件を削除するには`all`を呼び出します。

```kotlin
val query: Query<Int> = QueryDsl.delete(e).all().options { it.copy(allowMissingWhereClause = true) }
/*
delete from EMPLOYEE as t0_
*/
```

`options`を呼び出して`allowMissingWhereClause`に`true`を設定する必要があります。

このクエリを実行した場合の戻り値は削除された件数です。

## where

任意の条件にマッチする行を削除するには`where`を呼び出します。

```kotlin
val query: Query<Int> = QueryDsl.delete(a).where { a.addressId eq 15 }
/*
delete from ADDRESS as t0_ where t0_.ADDRESS_ID = ?
*/
```

デフォルトではWHERE句の指定は必須です。もし`where`のブロック内で条件が指定されない場合は例外が発生します。
意図的に全件削除を認めたい場合は`options`を呼び出して`allowMissingWhereClause`に`true`を設定します。

```kotlin
val query: Query<Int> = QueryDsl.delete(e).where {}.options { it.copy(allowMissingWhereClause = true) }
/*
delete from EMPLOYEE as t0_
*/
```

このクエリを実行した場合の戻り値は削除された件数です。

## options

クエリの挙動をカスタマイズするには`options`を呼び出します。
ラムダ式のパラメータはデフォルトのオプションを表します。
変更したいプロパティを指定して`copy`メソッドを呼び出してください。

```kotlin
val address: Address = ..
val query: Query<Unit> = QueryDsl.delete(a).single(address).options {
    it.copty(
      queryTimeoutSeconds = 5
    )
}
```

指定可能なオプションには以下のものがあります。

allowMissingWhereClause
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
