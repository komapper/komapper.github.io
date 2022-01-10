---
title: "Dialect"
linkTitle: "Dialect"
weight: 15
description: >
---

{{% pageinfo %}}
We are currently working on the translation from Japanese to English. We would appreciate your cooperation.
{{% /pageinfo %}}

## 概要 {#overview}

方言はデータベースやドライバの差異を吸収します。
方言の代表的な機能はKotlinとデータベースの型の対応関係を解決することです。

方言は個別のモジュール（Artifact）に含まれており、どのモジュールを利用するかは接続先データベースや接続タイプに合わせて選択する必要があります。

| Database           | Type  |            Artifact ID            |
|--------------------|:-----:|:---------------------------------:|
| H2 Database Engine | JDBC  |     komapper-dialect-h2-jdbc      |
| H2 Database Engine | R2DBC |     komapper-dialect-h2-r2dbc     |
| MariaDB            | JDBC  |   komapper-dialect-mariadb-jdbc   |
| MariaDB            | R2DBC |  komapper-dialect-mariadb-r2dbc   |
| MySQL              | JDBC  |    komapper-dialect-mysql-jdbc    |
| MySQL              | R2DBC |   komapper-dialect-mysql-r2dbc    |
| PostgreSQL         | JDBC  | komapper-dialect-postgresql-jdbc  |
| PostgreSQL         | R2DBC | komapper-dialect-postgresql-r2dbc |

## H2 - JDBC

利用するにはGradleの依存関係の宣言で次のように記述します。

```kotlin
val komapperVersion: String by project
dependencies {
    implementation("org.komapper:komapper-dialect-h2-jdbc:$komapperVersion")
}
```

プログラムの中で利用するには以下のようにインスタンス化します。

```kotlin
val dialect = H2JdbcDialect()
```

データ型の対応関係を示します。

| Kotlin Type              | Database Type            |
|--------------------------|--------------------------|
| java.math.BigDecimal     | BIGINT                   |
| java.math.BigInteger     | BIGINT                   |
| java.sql.Array           | ARRAY                    |
| java.sql.Blob            | BLOB                     |
| java.sql.Clob            | CLOB                     |
| java.sql.NClob           | CLOB                     |
| java.sql.SQLXML          | CLOB                     |
| java.time.LocalDate      | DATE                     |
| java.time.LocalDateTime  | TIMESTAMP                |
| java.time.LocalTime      | TIME                     |
| java.time.OffsetDateTime | TIMESTAMP WITH TIME ZONE |
| java.util.UUID           | UUID                     |
| kotlin.Any               | OTHER                    |
| kotlin.Boolean           | BOOLEAN                  |
| kotlin.Byte              | TINYINT                  |
| kotlin.ByteArray         | BINARY                   |
| kotlin.Double            | DOUBLE                   |
| kotlin.Float             | REAL                     |
| kotlin.Int               | INT                      |
| kotlin.Long              | BIGINT                   |
| kotlin.Short             | SMALLINT                 |
| kotlin.String            | VARCHAR                  |
| kotlin.UByte             | SMALLINT                 |
| kotlin.UInt              | BIGINT                   |
| kotlin.UShort            | INT                      |
| enum class               | VARCHAR                  |

## H2 - R2DBC

利用するにはGradleの依存関係の宣言で次のように記述します。

```kotlin
val komapperVersion: String by project
dependencies {
    implementation("org.komapper:komapper-dialect-h2-r2dbc:$komapperVersion")
}
```

プログラムの中で利用するには以下のようにインスタンス化します。

```kotlin
val dialect = H2R2dbcDialect()
```

データ型の対応関係を示します。

{{% pageinfo %}} Under Construction {{% /pageinfo %}}

## MariaDB - JDBC

利用するにはGradleの依存関係の宣言で次のように記述します。

```kotlin
val komapperVersion: String by project
dependencies {
    implementation("org.komapper:komapper-dialect-mariadb-jdbc:$komapperVersion")
}
```

プログラムの中で利用するには以下のようにインスタンス化します。

```kotlin
val dialect = MariaDbJdbcDialect()
```

データ型の対応関係を示します。

| Kotlin Type              | Database Type   |
|--------------------------|-----------------|
| java.math.BigDecimal     | DECIMAL         |
| java.math.BigInteger     | DECIMAL         |
| java.sql.Array           | N/A             |
| java.sql.Blob            | BLOB            |
| java.sql.Clob            | TEXT            |
| java.sql.NClob           | TEXT            |
| java.sql.SQLXML          | TEXT            |
| java.time.LocalDate      | DATE            |
| java.time.LocalDateTime  | TIMESTAMP(6)    |
| java.time.LocalTime      | TIME            |
| java.time.OffsetDateTime | TIMESTAMP       |
| java.util.UUID           | N/A             |
| kotlin.Any               | N/A             |
| kotlin.Boolean           | BIT(1), BOOLEAN |
| kotlin.Byte              | TINYINT         |
| kotlin.ByteArray         | VARBINARY       |
| kotlin.Double            | DOUBLE          |
| kotlin.Float             | FLOAT           |
| kotlin.Int               | INT             |
| kotlin.Long              | BIGINT          |
| kotlin.Short             | SMALLINT        |
| kotlin.String            | VARCHAR         |
| kotlin.UByte             | SMALLINT        |
| kotlin.UInt              | BIGINT          |
| kotlin.UShort            | INT             |
| enum class               | VARCHAR         |

## MariaDB - R2DBC

利用するにはGradleの依存関係の宣言で次のように記述します。

```kotlin
val komapperVersion: String by project
dependencies {
    implementation("org.komapper:komapper-dialect-mariadb-r2dbc:$komapperVersion")
}
```

プログラムの中で利用するには以下のようにインスタンス化します。

```kotlin
val dialect = MariaDbR2dbcDialect()
```

{{% pageinfo %}} Under Construction {{% /pageinfo %}}

## MySQL - JDBC

利用するにはGradleの依存関係の宣言で次のように記述します。

```kotlin
val komapperVersion: String by project
dependencies {
    implementation("org.komapper:komapper-dialect-mysql-jdbc:$komapperVersion")
}
```

プログラムの中で利用するには以下のようにインスタンス化します。

```kotlin
val dialect = MySqlJdbcDialect()
```

| Kotlin Type              | Database Type   |
|--------------------------|-----------------|
| java.math.BigDecimal     | DECIMAL         |
| java.math.BigInteger     | DECIMAL         |
| java.sql.Array           | N/A             |
| java.sql.Blob            | BLOB            |
| java.sql.Clob            | TEXT            |
| java.sql.NClob           | TEXT            |
| java.sql.SQLXML          | TEXT            |
| java.time.LocalDate      | DATE            |
| java.time.LocalDateTime  | TIMESTAMP(6)    |
| java.time.LocalTime      | TIME            |
| java.time.OffsetDateTime | TIMESTAMP       |
| java.util.UUID           | N/A             |
| kotlin.Any               | N/A             |
| kotlin.Boolean           | BIT(1), BOOLEAN |
| kotlin.Byte              | TINYINT         |
| kotlin.ByteArray         | VARBINARY       |
| kotlin.Double            | DOUBLE          |
| kotlin.Float             | FLOAT           |
| kotlin.Int               | INT             |
| kotlin.Long              | BIGINT          |
| kotlin.Short             | SMALLINT        |
| kotlin.String            | VARCHAR         |
| kotlin.UByte             | SMALLINT        |
| kotlin.UInt              | BIGINT          |
| kotlin.UShort            | INT             |
| enum class               | VARCHAR         |

## MySQL - R2DBC

利用するにはGradleの依存関係の宣言で次のように記述します。

```kotlin
val komapperVersion: String by project
dependencies {
    implementation("org.komapper:komapper-dialect-mysql-r2dbc:$komapperVersion")
}
```

プログラムの中で利用するには以下のようにインスタンス化します。

```kotlin
val dialect = MySqlR2dbcDialect()
```

{{% pageinfo %}} Under Construction {{% /pageinfo %}}

## PostgreSQL - JDBC

利用するにはGradleの依存関係の宣言で次のように記述します。

```kotlin
val komapperVersion: String by project
dependencies {
    implementation("org.komapper:komapper-dialect-postgresql-jdbc:$komapperVersion")
}
```

プログラムの中で利用するには以下のようにインスタンス化します。

```kotlin
val dialect = PostgreSqlJdbcDialect()
```

| Kotlin Type              | Database Type                        |
|--------------------------|--------------------------------------|
| java.math.BigDecimal     | numeric, decimal                     |
| java.math.BigInteger     | numeric, decimal                     |
| java.sql.Array           | array of data type                   |
| java.sql.Blob            | N/A                                  |
| java.sql.Clob            | N/A                                  |
| java.sql.NClob           | N/A                                  |
| java.sql.SQLXML          | xml                                  |
| java.time.LocalDate      | date                                 |
| java.time.LocalDateTime  | timestamp                            |
| java.time.LocalTime      | time                                 |
| java.time.OffsetDateTime | timestamp with time zone             |
| java.util.UUID           | uuid                                 |
| kotlin.Any               | N/A                                  |
| kotlin.Boolean           | boolean, bool                        |
| kotlin.Byte              | smallint                             |
| kotlin.ByteArray         | bytea                                |
| kotlin.Double            | double precision, float8             |
| kotlin.Float             | real                                 |
| kotlin.Int               | integer, int, int4, serial           |
| kotlin.Long              | bigint, int8, bigserial, serial8     |
| kotlin.Short             | smallint, int2, smallserial, serial2 |
| kotlin.String            | character varying, varchar, text     |
| kotlin.UByte             | smallint, int2, smallserial, serial2 |
| kotlin.UInt              | bigint, int8, bigserial, serial8     |
| kotlin.UShort            | integer, int, int4, serial           |
| enum class               | character varying, varchar, text     |

## PostgreSQL - R2DBC

利用するにはGradleの依存関係の宣言で次のように記述します。

```kotlin
val komapperVersion: String by project
dependencies {
    implementation("org.komapper:komapper-dialect-postgresql-r2dbc:$komapperVersion")
}
```

プログラムの中で利用するには以下のようにインスタンス化します。

```kotlin
val dialect = PostgreSqlR2dbcDialect()
```

{{% pageinfo %}} Under Construction {{% /pageinfo %}}
