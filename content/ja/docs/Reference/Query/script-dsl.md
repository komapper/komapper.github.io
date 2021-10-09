---
title: "Script DSL"
linkTitle: "Script DSL"
weight: 40
description: >
  任意のSQLスクリプトを実行するためのDSL
---

## 概要 {#overview}

Script DSLは任意のSQLスクリプトを実行できます。

## execute

実行したいSQLスクリプトを`execute`に渡します。

クエリ実行時にキーが重複した場合、`org.komapper.core.UniqueConstraintException`がスローされます。

```kotlin
val query: Query<Unit> = ScriptDsl.execute("""
    drop table if exists example;
    create table example (id integer not null primary key, value varchar(20));
    insert into example (id, value) values(1, 'test');
""".trimIndent())
```