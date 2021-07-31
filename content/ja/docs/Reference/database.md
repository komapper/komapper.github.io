---
title: "Database"
linkTitle: "Database"
weight: 10
description: >
  データベース
---

## 概要

KomapperでデータベースにアクセスするためにはDatabaseインスタンスが必要です。
Databaseインスタンスはトランザクションの制御やクエリの実行を担当します。

## Databaseインスタンスの生成

Databaseインスタンスの生成方法はJDBCを使う場合とR2DBCを使う場合で異なります。

### JDBCを使う場合

URLから生成する場合は次のように記述します。

```kotlin
val db = JdbcDatabase.create("jdbc:h2:mem:example;DB_CLOSE_DELAY=-1")
```

URLに加えてユーザー名やパスワードを指定する場合は次のように記述します。

```kotlin
val db = JdbcDatabase.create(
  url = "jdbc:h2:mem:example;DB_CLOSE_DELAY=-1", 
  user = "sa", 
  password = ""
)
```

`javax.sql.DataSource`を指定することもできます。
ただし、その場合は`dialect`の指定も必要です。

```kotlin
val dataSource: DataSource = ...
val db = JdbcDatabase.create(
  dataSource = dataSource, 
  dialect = H2JdbcDialect()
)
```

### R2DBCを使う場合

URLから生成する場合は次のように記述します。

```kotlin
val db = R2dbcDatabase.create("r2dbc:h2:mem:///example;DB_CLOSE_DELAY=-1")
```

`io.r2dbc.spi.ConnectionFactoryOptions`から生成する場合は次のように記述します。
`options`には`ConnectionFactoryOptions.DRIVER`をキーとする値を含めなければいけません。

```kotlin
val options = ConnectionFactoryOptions.builder()
  .option(ConnectionFactoryOptions.DRIVER, "h2")
  .option(ConnectionFactoryOptions.PROTOCOL, "mem")
  .option(ConnectionFactoryOptions.DATABASE, "example")
  .option(Option.valueOf("DB_CLOSE_DELAY"), "-1")
  .build()
val db = R2dbcDatabase.create(options)
```

`io.r2dbc.spi.ConnectionFactory`を指定することもできます。
ただし、その場合は`dialect`の指定も必要です。

```kotlin
val connectionFactory: ConnectionFactory = ...
val db = R2dbcDatabase.create(
  connectionFactory = connectionFactory, 
  dialect = H2R2dbcDialect()
)
```

## Databaseインスタンスの利用

### トランザクションの制御

Databaseインスタンスの`withTransaction`拡張関数でトランザクションを制御します。
`withTransaction`拡張関数にはトランザクション内で処理したいロジックをラムダ式として渡します。

```kotlin
db.withTransaction {
    ...
}
```

詳細は [Transaction](../transaction/) を参照ください。

### クエリの実行

Databaseインスタンスの`runQuery`関数を呼び出すことでクエリを実行できます。

```kotlin
val query = ...
val result = db.runQuery { query }
```

R2DBCを使っている場合でクエリの型が`org.komapper.core.dsl.query.FlowableQuery`のとき、`runFlowableQuery`関数を実行できます。

```kotlin
val query: FlowableQuery = ...
val flow = db.runFlowableQuery { query }
```

クエリの生成については [Query](../query/) を参照ください。