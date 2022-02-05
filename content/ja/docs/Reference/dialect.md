---
title: "Dialect"
linkTitle: "Dialect"
weight: 15
description: >
  方言
---

## 概要 {#overview}

方言はデータベースやドライバの差異を吸収します。
方言の代表的な機能はKotlinとデータベースの型の対応関係を解決することです。

方言は個別のモジュール（Artifact）に含まれており、どのモジュールを利用するかは接続先データベースや接続タイプに合わせて選択する必要があります。

| Database           | Type  |            Artifact ID            |
|--------------------|:-----:|:---------------------------------:|
| H2 Database Engine | JDBC  |     komapper-dialect-h2-jdbc      |
| H2 Database Engine | R2DBC |     komapper-dialect-h2-r2dbc     |
| MariaDB            | JDBC  |   komapper-dialect-mariadb-jdbc   |
| MySQL              | JDBC  |    komapper-dialect-mysql-jdbc    |
| Oracle Database    | JDBC  |   komapper-dialect-oracle-jdbc    |
| Oracle Database    | R2DBC |   komapper-dialect-oracle-r2dbc   |
| PostgreSQL         | JDBC  | komapper-dialect-postgresql-jdbc  |
| PostgreSQL         | R2DBC | komapper-dialect-postgresql-r2dbc |
| SQL Server         | JDBC  |  komapper-dialect-sqlserver-jdbc  |
| SQL Server         | R2DBC | komapper-dialect-sqlserver-r2dbc  |

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
val dialect = JdbcH2Dialect()
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
val dialect = R2dbcH2Dialect()
```

データ型の対応関係を示します。

| Kotlin Type              | Database Type            |
|--------------------------|--------------------------|
| io.r2dbc.spi.Blob        | Blob                     |
| io.r2dbc.spi.Clob        | Clob                     |
| java.math.BigDecimal     | BIGINT                   |
| java.math.BigDecimal     | BIGINT                   |
| java.math.BigInteger     | BIGINT                   |
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
val dialect = JdbcMariaDbDialect()
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
val dialect = JdbcMySqlDialect()
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

## Oracle - JDBC

利用するにはGradleの依存関係の宣言で次のように記述します。

```kotlin
val komapperVersion: String by project
dependencies {
    implementation("org.komapper:komapper-dialect-oracle-jdbc:$komapperVersion")
}
```

プログラムの中で利用するには以下のようにインスタンス化します。

```kotlin
val dialect = JdbcOracleDialect()
```

データ型の対応関係を示します。

| Kotlin Type              | Database Type            |
|--------------------------|--------------------------|
| java.math.BigDecimal     | NUMBER                   |
| java.math.BigInteger     | NUMBER                   |
| java.sql.Array           | N/A                      |
| java.sql.Blob            | BLOB                     |
| java.sql.Clob            | CLOB                     |
| java.sql.NClob           | N/A                      |
| java.sql.SQLXML          | N/A                      |
| java.time.LocalDate      | DATE                     |
| java.time.LocalDateTime  | DATE                     |
| java.time.LocalTime      | TIME                     |
| java.time.OffsetDateTime | TIMESTAMP WITH TIME ZONE |
| java.util.UUID           | N/A                      |
| kotlin.Any               | N/A                      |
| kotlin.Boolean           | NUMBER                   |
| kotlin.Byte              | NUMBER                   |
| kotlin.ByteArray         | RAW                      |
| kotlin.Double            | FLOAT                    |
| kotlin.Float             | FLOAT                    |
| kotlin.Int               | NUMBER                   |
| kotlin.Long              | NUMBER                   |
| kotlin.Short             | NUMBER                   |
| kotlin.String            | VARCHAR2                 |
| kotlin.UByte             | NUMBER                   |
| kotlin.UInt              | NUMBER                   |
| kotlin.UShort            | NUMBER                   |
| enum class               | VARCHAR2                 |

## Oracle - R2DBC

利用するにはGradleの依存関係の宣言で次のように記述します。

```kotlin
val komapperVersion: String by project
dependencies {
    implementation("org.komapper:komapper-dialect-oracle-r2dbc:$komapperVersion")
}
```

プログラムの中で利用するには以下のようにインスタンス化します。

```kotlin
val dialect = R2dbcOracleSqlDialect()
```

データ型の対応関係を示します。

| Kotlin Type              | Database Type            |
|--------------------------|--------------------------|
| io.r2dbc.spi.Blob        | BLOB                     |
| io.r2dbc.spi.Clob        | CLOB                     |
| java.math.BigDecimal     | NUMBER                   |
| java.math.BigInteger     | NUMBER                   |
| java.time.LocalDate      | DATE                     |
| java.time.LocalDateTime  | DATE                     |
| java.time.LocalTime      | TIME                     |
| java.time.OffsetDateTime | TIMESTAMP WITH TIME ZONE |
| java.util.UUID           | N/A                      |
| kotlin.Any               | N/A                      |
| kotlin.Boolean           | NUMBER                   |
| kotlin.Byte              | NUMBER                   |
| kotlin.ByteArray         | RAW                      |
| kotlin.Double            | FLOAT                    |
| kotlin.Float             | FLOAT                    |
| kotlin.Int               | NUMBER                   |
| kotlin.Long              | NUMBER                   |
| kotlin.Short             | NUMBER                   |
| kotlin.String            | VARCHAR2                 |
| kotlin.UByte             | NUMBER                   |
| kotlin.UInt              | NUMBER                   |
| kotlin.UShort            | NUMBER                   |
| enum class               | VARCHAR2                 |

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
val dialect = JdbcPostgreSqlDialect()
```

データ型の対応関係を示します。

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
val dialect = R2dbcPostgreSqlDialect()
```

データ型の対応関係を示します。

| Kotlin Type                        | Database Type                        |
|------------------------------------|--------------------------------------|
| io.r2dbc.postgresql.codec.Interval | interval                             |
| io.r2dbc.postgresql.codec.Json     | json, jsonb                          |
| io.r2dbc.spi.Blob                  | bytea                                |
| io.r2dbc.spi.Clob                  | text                                 |
| java.math.BigDecimal               | numeric, decimal                     |
| java.math.BigInteger               | numeric, decimal                     |
| java.time.LocalDate                | date                                 |
| java.time.LocalDateTime            | timestamp                            |
| java.time.LocalTime                | time                                 |
| java.time.OffsetDateTime           | timestamp with time zone             |
| java.util.UUID                     | uuid                                 |
| kotlin.Any                         | N/A                                  |
| kotlin.Array                       | array of data type                   |
| kotlin.Boolean                     | boolean, bool                        |
| kotlin.Byte                        | smallint                             |
| kotlin.ByteArray                   | bytea                                |
| kotlin.Double                      | double precision, float8             |
| kotlin.Float                       | real                                 |
| kotlin.Int                         | integer, int, int4, serial           |
| kotlin.Long                        | bigint, int8, bigserial, serial8     |
| kotlin.Short                       | smallint, int2, smallserial, serial2 |
| kotlin.String                      | character varying, varchar, text     |
| kotlin.UByte                       | smallint, int2, smallserial, serial2 |
| kotlin.UInt                        | bigint, int8, bigserial, serial8     |
| kotlin.UShort                      | integer, int, int4, serial           |
| enum class                         | character varying, varchar, text     |

## SQL Server - JDBC

利用するにはGradleの依存関係の宣言で次のように記述します。

```kotlin
val komapperVersion: String by project
dependencies {
    implementation("org.komapper:komapper-dialect-sqlserver-jdbc:$komapperVersion")
}
```

プログラムの中で利用するには以下のようにインスタンス化します。

```kotlin
val dialect = JdbcSqlServerDialect()
```

データ型の対応関係を示します。

| Kotlin Type              | Database Type     |
|--------------------------|-------------------|
| java.math.BigDecimal     | decimal           |
| java.math.BigInteger     | decimal           |
| java.sql.Array           | N/A               |
| java.sql.Blob            | varbinary(max)    |
| java.sql.Clob            | text              |
| java.sql.NClob           | N/A               |
| java.sql.SQLXML          | xml               |
| java.time.LocalDate      | date              |
| java.time.LocalDateTime  | datetime          |
| java.time.LocalTime      | time              |
| java.time.OffsetDateTime | N/A               |
| java.util.UUID           | N/A               |
| kotlin.Any               | N/A               |
| kotlin.Boolean           | bit               |
| kotlin.Byte              | smallint, tinyint |
| kotlin.ByteArray         | varbinary         |
| kotlin.Double            | float             |
| kotlin.Float             | real              |
| kotlin.Int               | int               |
| kotlin.Long              | bigint            |
| kotlin.Short             | smallint          |
| kotlin.String            | varchar, nvarchar |
| kotlin.UByte             | smallint          |
| kotlin.UInt              | bigint            |
| kotlin.UShort            | int               |
| enum class               | varchar, nvarchar |

## SQL Server - R2DBC

利用するにはGradleの依存関係の宣言で次のように記述します。

```kotlin
val komapperVersion: String by project
dependencies {
    implementation("org.komapper:komapper-dialect-sqlserver-r2dbc:$komapperVersion")
}
```

プログラムの中で利用するには以下のようにインスタンス化します。

```kotlin
val dialect = R2dbcSqlServerDialect()
```

データ型の対応関係を示します。

| Kotlin Type              | Database Type     |
|--------------------------|-------------------|
| io.r2dbc.spi.Blob        | varbinary(max)    |
| io.r2dbc.spi.Clob        | text              |
| java.math.BigDecimal     | decimal           |
| java.math.BigInteger     | decimal           |
| java.time.LocalDate      | date              |
| java.time.LocalDateTime  | datetime          |
| java.time.LocalTime      | time              |
| java.time.OffsetDateTime | N/A               |
| java.util.UUID           | N/A               |
| kotlin.Any               | N/A               |
| kotlin.Boolean           | bit               |
| kotlin.Byte              | smallint, tinyint |
| kotlin.ByteArray         | varbinary         |
| kotlin.Double            | float             |
| kotlin.Float             | real              |
| kotlin.Int               | int               |
| kotlin.Long              | bigint            |
| kotlin.Short             | smallint          |
| kotlin.String            | varchar, nvarchar |
| kotlin.UByte             | smallint          |
| kotlin.UInt              | bigint            |
| kotlin.UShort            | int               |
| enum class               | varchar, nvarchar |
