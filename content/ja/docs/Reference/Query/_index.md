---
title: "Query"
linkTitle: "Query"
weight: 30
description: >
  クエリ
---

## 概要

Komapperではクエリの生成と実行は分離されています。
クエリの生成は各種のDSLが担い、実行はJDBCやR2DBCを表すDatabaseインスタンスが担います。

```kotlin
// create a query
val query = EntityDsl.from(a)
// run the query
val result = db.runQuery { query }
```

{{< alert title="Note" >}}
本ページの子ページでは下記のようにクエリの生成例を示します。
どのようなクエリが生成されるかわかりやすくするために型を明示的に記載しますが実際は省略可能です。

また、対応するSQLはコメント内に記述しますが、特に断りがなければH2 Database Engine向けのDialectを使った場合に生成されるSQLを示します。
利用するDialectによっては異なるSQLが生成されることがあります。

```kotlin
val query: Query<List<Address>> = EntityDsl.from(a)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_
*/
```
{{< /alert >}}

