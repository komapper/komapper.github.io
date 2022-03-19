---
title: "データベース"
weight: 10
description: >
---

## 概要 {#overview}

Komapperでデータベースにアクセスするためには`JdbcDatabase`もしくは`R2dbcDatabase`のインスタンスが必要です。
ここでは、これらを総称してDatabaseインスタンスと呼びます。

Databaseインスタンスはトランザクションの制御やクエリの実行を担当します。

## インスタンスの生成 {#instantiation}

Databaseインスタンスの生成方法はJDBCを使う場合とR2DBCを使う場合で異なります。

### JDBCを使う場合 {#instantiation-for-jdbc}

URLから生成する場合は次のように記述します。

```kotlin
val db: JdbcDatabase = JdbcDatabase("jdbc:h2:mem:example;DB_CLOSE_DELAY=-1")
```

URLに加えてユーザー名やパスワードを指定する場合は次のように記述します。

```kotlin
val db: JdbcDatabase = JdbcDatabase(
  url = "jdbc:h2:mem:example;DB_CLOSE_DELAY=-1", 
  user = "sa", 
  password = ""
)
```

`javax.sql.DataSource`を指定することもできます。
ただし、その場合は`dialect`の指定も必要です。

```kotlin
val dataSource: DataSource = ...
val db: JdbcDatabase = JdbcDatabase(
  dataSource = dataSource, 
  dialect = JdbcH2Dialect()
)
```

[ダイアレクト]({{< relref "Dialect" >}})も参照ください。

### R2DBCを使う場合 {#instantiation-for-r2dbc}

URLから生成する場合は次のように記述します。

```kotlin
val db: R2dbcDatabase = R2dbcDatabase("r2dbc:h2:mem:///example;DB_CLOSE_DELAY=-1")
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
val db: R2dbcDatabase = R2dbcDatabase(options)
```

`io.r2dbc.spi.ConnectionFactory`を指定することもできます。
ただし、その場合は`dialect`の指定も必要です。

```kotlin
val connectionFactory: ConnectionFactory = ...
val db: R2dbcDatabase = R2dbcDatabase(
  connectionFactory = connectionFactory, 
  dialect = R2dbcH2Dialect()
)
```

[ダイアレクト]({{< relref "dialect.md" >}})も参照ください。

## インスタンスの利用 {#usage}

### トランザクションの制御 {#transaction-control}

Databaseインスタンスの`withTransaction`関数でトランザクションを制御します。
`withTransaction`関数にはトランザクション内で処理したいロジックをラムダ式として渡します。

```kotlin
db.withTransaction {
    ...
}
```

詳細は [トランザクション]({{< relref "transaction.md" >}}) を参照ください。

### クエリの実行 {#query-execution}

Databaseインスタンスの`runQuery`関数を呼び出すことでクエリを実行できます。

```kotlin
val a = Meta.address
val query: Query<List<Address>> = QueryDsl.from(a)
val result: List<Address> = db.runQuery(query)
```

Databaseインスタンスが`R2dbcDatabase`の場合でクエリの型が`org.komapper.core.dsl.query.FlowQuery`のとき、`flowQuery`関数を実行できます。

```kotlin
val a = Meta.address
val query: FlowQuery<Address> = QueryDsl.from(a)
val flow: Flow<Address> = db.flowQuery(query)
```

データベースへのアクセスは`flowQuery`関数から返される`Flow`インスタンスを利用したときに初めて行われます。

クエリの構築については [クエリ]({{< relref "Query" >}}) を参照ください。

### 低レベルAPIの実行 {#low-level-api-execution}

[Query DSL]({{< relref "Query/QueryDSL" >}}) のAPIが要件に合わない場合、低レベルAPIを直接利用できます。

JDBCのAPIを直接利用するには、`JdbcDatabase`インスタンスからいくつかのプロパティと関数を呼び出して`java.sql.Connection`を取得します。

```kotlin
val db: JdbcDatabase = ...
db.config.session.useConnection { con: java.sql.Connection ->
    val sql = "select employee_name from employee where employee_id = ?"
    con.prepareStatement(sql).use { ps ->
        ps.setInt(1, 10)
        ps.executeQuery().use { rs ->
            if (rs.next()) {
                println(rs.getString(1))
            }
        }
    }
}
```

同様に、R2DBCのAPIを直接利用するには`R2dbcDatabase`インスタンスから
いくつかのプロパティと関数を呼び出して`io.r2dbc.spi.Connection`を取得します。

```kotlin
val db: R2dbcDatabase = ...
db.config.session.useConnection { con: io.r2dbc.spi.Connection ->
    val sql = "select employee_name from employee where employee_id = ?"
    val statement = con.createStatement(sql)
    statement.bind(0, 10)
    statement.execute().collect { result ->
        result.map { row -> row.get(0, String::class.java) }.collect {
            println(it)
        }
    }
}
```
