---
title: "SCRIPTクエリ"
linkTitle: "SCRIPT"
weight: 60
description: >
  任意のSQLスクリプトを表すクエリ
---

## 概要 {#overview}

SCRIPTクエリは任意のSQLスクリプトを表します。

## executeScript

実行したいSQLスクリプトを`executeScript`に渡します。

クエリ実行時にキーが重複した場合、`org.komapper.core.UniqueConstraintException`がスローされます。

```kotlin
val query: Query<Unit> = QueryDsl.executeScript("""
    drop table if exists example;
    create table example (id integer not null primary key, value varchar(20));
    insert into example (id, value) values(1, 'test');
""".trimIndent())
```

## options

クエリの挙動をカスタマイズするには`options`を呼び出します。
ラムダ式のパラメータはデフォルトのオプションを表します。
変更したいプロパティを指定して`copy`メソッドを呼び出してください。

```kotlin
val query: Query<Unit> = QueryDsl.executeScript("""
    drop table if exists example;
    create table example (id integer not null primary key, value varchar(20));
    insert into example (id, value) values(1, 'test');
""".trimIndent()).options {
    it.copty(
      queryTimeoutSeconds = 5
    )
}
```

指定可能なオプションには以下のものがあります。

queryTimeoutSeconds
: クエリタイムアウトの秒数です。デフォルトは`null`でドライバの値を使うことを示します。

suppressLogging
: SQLのログ出力を抑制するかどうかです。デフォルトは`false`です。

separator
: SQLステートメントの区切り文字です。デフォルトは`;`です。

[executionOptions]({{< relref "../../database-config/#executionoptions" >}})
の同名プロパティよりもこちらに明示的に設定した値が優先的に利用されます。
