---
title: "Debugging"
linkTitle: "Debugging"
weight: 120
description: >
  クエリのデバッグ
---

## 概要 {#overview}

データベースへ接続することなくクエリで構築されるSQLを確認できます。

## dryRun

クエリに対して`dryRun`関数を呼び出すと、クエリによって構築されるSQLやクエリにバインドされた引数を確認できます。

```kotlin
val query: Query<List<Address>> = QueryDsl.from(a).where { a.addressId eq 1 }
val result: DryRunResult = query.dryRun()
println(result)
```

上記のコードの出力結果は以下の通りです（見やすさのため改行を入れています）。

```sh
DryRunResult(
  sql=select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID = ?, 
  sqlWithArgs=select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID = 1, 
  args=[Value(any=1, klass=class kotlin.Int)], 
  throwable=null, 
  description=This data was generated using DryRunDatabaseConfig. To get more correct information, specify the actual DatabaseConfig instance.
)
```

`DryRunResult`クラスのプロパティの意味は次の通りです。

sql
: クエリから構築されるSQL。バインド変数は`?`で表現される。例外が発生した場合はSQLではなく例外のメッセージを表す。

sqlWithArgs
: クエリから構築される引数付きのSQL。バインド変数は引数の文字列表現で置換されている。例外が発生した場合はSQLではなく例外のメッセージを表す。

args
: 引数の値と型のペア。

throwable
: クエリの構築にスローされた例外。例外が発生しなかった場合は`null`。

description
: `DryRunResult`のインスタンスに対する説明。

### Dialectの利用 {#dryrun-with-dialect}

引数なしの`dryRun`関数は、接続先データベースの [Dialect]({{< relref "../dialect.md" >}}) を考慮しない結果を返します。
Dialectを考慮した結果を取得したい場合は`DatabaseConfig`インスタンスを渡してください。

```kotlin
val database: JdbcDatabase = ...
val query: Query<List<Address>> = QueryDsl.from(a).where { a.addressId eq 1 }
val result: DryRunResult = query.dryRun(database.config)
println(result)
```

### クエリ構築途中のデバッグ {#dryrun-while-building-query}

`also`関数と組み合わせれば構築途中のクエリ情報を確認できます。

```kotlin
val query: Query<List<Address>> = QueryDsl.from(a)
  .also {
    println("1:" + it.dryRun().sql)
  }.where {
    a.addressId eq 1
  }.also {
    println("2:" + it.dryRun().sql)
  }.orderBy(a.addressId)
  .also {
    println("3:" + it.dryRun().sql)
  }
```

上記コードの実行結果は以下の通りです。

```sh
1:select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_
2:select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID = ?
3:select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID = ? order by t0_.ADDRESS_ID asc
```
