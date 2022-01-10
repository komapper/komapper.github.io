---
title: "Database"
linkTitle: "Database"
weight: 10
description: >
---

{{% pageinfo %}}
We are currently working on the translation from Japanese to English. We would appreciate your cooperation.
{{% /pageinfo %}}

## 概要 {#overview}

Komapperでデータベースにアクセスするためには`JdbcDatabase`もしくは`R2dbcDatabase`のインスタンスが必要です。
ここでは、これらを総称してDatabaseインスタンスと呼びます。

Databaseインスタンスはトランザクションの制御やクエリの実行を担当します。

## Databaseインスタンスの生成 {#instantiation}

Databaseインスタンスの生成方法はJDBCを使う場合とR2DBCを使う場合で異なります。

### JDBCを使う場合 {#instantiation-for-jdbc}

URLから生成する場合は次のように記述します。

```kotlin
val db: JdbcDatabase = JdbcDatabase.create("jdbc:h2:mem:example;DB_CLOSE_DELAY=-1")
```

URLに加えてユーザー名やパスワードを指定する場合は次のように記述します。

```kotlin
val db: JdbcDatabase = JdbcDatabase.create(
  url = "jdbc:h2:mem:example;DB_CLOSE_DELAY=-1", 
  user = "sa", 
  password = ""
)
```

`javax.sql.DataSource`を指定することもできます。
ただし、その場合は`dialect`の指定も必要です。

```kotlin
val dataSource: DataSource = ...
val db: JdbcDatabase = JdbcDatabase.create(
  dataSource = dataSource, 
  dialect = H2JdbcDialect()
)
```

以下のドキュメントも参照ください。

- [Dialect]({{< relref "Dialect" >}})

### R2DBCを使う場合 {#instantiation-for-r2dbc}

URLから生成する場合は次のように記述します。

```kotlin
val db: R2dbcDatabase = R2dbcDatabase.create("r2dbc:h2:mem:///example;DB_CLOSE_DELAY=-1")
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
val db: R2dbcDatabase = R2dbcDatabase.create(options)
```

`io.r2dbc.spi.ConnectionFactory`を指定することもできます。
ただし、その場合は`dialect`の指定も必要です。

```kotlin
val connectionFactory: ConnectionFactory = ...
val db: R2dbcDatabase = R2dbcDatabase.create(
  connectionFactory = connectionFactory, 
  dialect = H2R2dbcDialect()
)
```

以下のドキュメントも参照ください。

- [Dialect]({{< relref "dialect.md" >}})

## Databaseインスタンスの利用 {#usage}

### トランザクションの制御 {#transaction-control}

Databaseインスタンスの`withTransaction`拡張関数でトランザクションを制御します。
`withTransaction`拡張関数にはトランザクション内で処理したいロジックをラムダ式として渡します。

```kotlin
db.withTransaction {
    ...
}
```

詳細は [Transaction]({{< relref "transaction.md" >}}) を参照ください。

### クエリの実行 {#query-execution}

Databaseインスタンスの`runQuery`関数を呼び出すことでクエリを実行できます。

```kotlin
val a = Meta.address
val query: Query<List<Address>> = QueryDsl.from(a)
val result: List<Address> = db.runQuery(query)
```

Databaseインスタンスが`R2dbcDatabase`の場合でクエリの型が`org.komapper.core.dsl.query.FlowQuery`のとき、`flow`関数を実行できます。

```kotlin
val a = Meta.address
val query: FlowQuery<Address> = QueryDsl.from(a)
val flow: Flow<Address> = db.flow(query)
```

データベースへのアクセスは`flow`関数から返される`Flow`インスタンスを利用したときに初めて行われます。

クエリの生成については [Query]({{< relref "query.md" >}}) を参照ください。

### 低レベルAPIの実行 {#low-level-api-execution}

Komapperの提供する高レベルAPI（[Query]({{< relref "query.md" >}}) ）が要件に合わない場合、
低レベルAPI（JDBCやR2DBCのAPI）を利用できます。

JDBCのAPIを直接利用するには、`JdbcDatabase`インスタンスからいくつかのプロパティを呼び出して`java.sql.Connection`を取得します。

```kotlin
db.config.session.connection.use { con ->
    val sql = "select employee_name from employee where employee_id = ?"
    con.prepareStatement(sql).use { ps ->
        ps.setInt(1,10)
        ps.executeQuery().use { rs ->
            if (rs.next()) {
                println(rs.getString(1))
            }
        }
    }
}
```

同様に、R2DBCのAPIを直接利用するには`R2dbcDatabase`インスタンスから
いくつかのプロパティを呼び出して`io.r2dbc.spi.Connection`の`Publisher`を取得します。

```kotlin
val con: Publisher<out Connection> = db.config.session.connection
```

{{< alert color="warning" title="Warning" >}}
高レベルAPIと低レベルAPIの混在は可能です。
ただし、Komapperのトランザクション制御下にある場合、低レベルAPIを使ってトランザクションの設定を変更することは推奨されません。
{{< /alert >}}
