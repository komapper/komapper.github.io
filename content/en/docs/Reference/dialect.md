---
title: "Dialects"
weight: 15
description: >
---

## Overview {#overview}

Dialects absorb differences in databases and drivers.
A typical function of dialects is to resolve the type mappings between Kotlin and databases.

The classes representing dialects are contained in separate modules (artifacts), 
and the choice of which module to use must be based on the destination database and connectivity type.

| Database           | Type  |            Artifact ID            |     Dialect Class      |
|--------------------|:-----:|:---------------------------------:|:----------------------:|
| H2 Database Engine | JDBC  |     komapper-dialect-h2-jdbc      |    JdbcH2SqlDialect    |
| H2 Database Engine | R2DBC |     komapper-dialect-h2-r2dbc     |   R2dbcH2SqlDialect    |
| MariaDB            | JDBC  |   komapper-dialect-mariadb-jdbc   |   JdbcMariadbDialect   |
| MySQL              | JDBC  |    komapper-dialect-mysql-jdbc    |    JdbcMySqlDialect    |
| Oracle Database    | JDBC  |   komapper-dialect-oracle-jdbc    |   JdbcOracleDialect    |
| Oracle Database    | R2DBC |   komapper-dialect-oracle-r2dbc   |   R2dbcOracleDialect   |
| PostgreSQL         | JDBC  | komapper-dialect-postgresql-jdbc  | JdbcPostgreSqlDialect  |
| PostgreSQL         | R2DBC | komapper-dialect-postgresql-r2dbc | R2dbcPostgreSqlDialect |
| SQL Server         | JDBC  |  komapper-dialect-sqlserver-jdbc  |  JdbcSqlServerDialect  |
| SQL Server         | R2DBC | komapper-dialect-sqlserver-r2dbc  | R2dbcSqlServerDialect  |

To use a dialect, specify the Artifact ID above in the Gradle dependencies declaration.

```kotlin
val komapperVersion: String by project
dependencies {
    implementation("org.komapper:komapper-dialect-h2-jdbc:$komapperVersion")
}
```

## Support for kotlinx-datetime {#kotlinx-datetime-support}

Komapper supports following data types of [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime):

- kotlinx.datetime.Instant
- kotlinx.datetime.LocalDate
- kotlinx.datetime.LocalDateTime

To use these types, declare `kotlinx-datetime` in the Gradle dependencies declaration as follows:

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
}
```

Also, declare `komapper-datetime-jdbc` or `komapper-datetime-r2dbc`:

```kotlin
val komapperVersion: String by project
dependencies {
    runtimeOnly("org.komapper:komapper-datetime-jdbc:$komapperVersion")
}
```

{{< alert title="Note" >}}
If you use one of the [Starters]({{< relref "starter.md" >}}) ,
you do not need to declare `komapper-datetime-jdbc` and `komapper-datetime-r2dbc`.
{{< /alert >}}

## H2 - JDBC

The following table shows the data type mapping:

| Kotlin Type                    | Database Type            |
|--------------------------------|--------------------------|
| java.math.BigDecimal           | BIGINT                   |
| java.math.BigInteger           | BIGINT                   |
| java.sql.Array                 | ARRAY                    |
| java.sql.Blob                  | BLOB                     |
| java.sql.Clob                  | CLOB                     |
| java.sql.NClob                 | CLOB                     |
| java.sql.SQLXML                | CLOB                     |
| java.time.Instant              | TIMESTAMP                |
| java.time.LocalDate            | DATE                     |
| java.time.LocalDateTime        | TIMESTAMP                |
| java.time.LocalTime            | TIME                     |
| java.time.OffsetDateTime       | TIMESTAMP WITH TIME ZONE |
| java.util.UUID                 | UUID                     |
| kotlin.Any                     | OTHER                    |
| kotlin.Boolean                 | BOOLEAN                  |
| kotlin.Byte                    | TINYINT                  |
| kotlin.ByteArray               | BINARY                   |
| kotlin.Double                  | DOUBLE                   |
| kotlin.Float                   | REAL                     |
| kotlin.Int                     | INT                      |
| kotlin.Long                    | BIGINT                   |
| kotlin.Short                   | SMALLINT                 |
| kotlin.String                  | VARCHAR                  |
| kotlin.UByte                   | SMALLINT                 |
| kotlin.UInt                    | BIGINT                   |
| kotlin.UShort                  | INT                      |
| kotlinx.datetime.Instant       | TIMESTAMP                |
| kotlinx.datetime.LocalDate     | DATE                     |
| kotlinx.datetime.LocalDateTime | TIMESTAMP                |
| enum class                     | VARCHAR                  |

## H2 - R2DBC

The following table shows the data type mapping:

| Kotlin Type                    | Database Type            |
|--------------------------------|--------------------------|
| io.r2dbc.spi.Blob              | Blob                     |
| io.r2dbc.spi.Clob              | Clob                     |
| java.math.BigDecimal           | BIGINT                   |
| java.math.BigDecimal           | BIGINT                   |
| java.math.BigInteger           | BIGINT                   |
| java.time.Instant              | TIMESTAMP                |
| java.time.LocalDate            | DATE                     |
| java.time.LocalDateTime        | TIMESTAMP                |
| java.time.LocalTime            | TIME                     |
| java.time.OffsetDateTime       | TIMESTAMP WITH TIME ZONE |
| java.util.UUID                 | UUID                     |
| kotlin.Any                     | OTHER                    |
| kotlin.Boolean                 | BOOLEAN                  |
| kotlin.Byte                    | TINYINT                  |
| kotlin.ByteArray               | BINARY                   |
| kotlin.Double                  | DOUBLE                   |
| kotlin.Float                   | REAL                     |
| kotlin.Int                     | INT                      |
| kotlin.Long                    | BIGINT                   |
| kotlin.Short                   | SMALLINT                 |
| kotlin.String                  | VARCHAR                  |
| kotlin.UByte                   | SMALLINT                 |
| kotlin.UInt                    | BIGINT                   |
| kotlin.UShort                  | INT                      |
| kotlinx.datetime.Instant       | TIMESTAMP                |
| kotlinx.datetime.LocalDate     | DATE                     |
| kotlinx.datetime.LocalDateTime | TIMESTAMP                |
| enum class                     | VARCHAR                  |

## MariaDB - JDBC

The following table shows the data type mapping:

| Kotlin Type                    | Database Type   |
|--------------------------------|-----------------|
| java.math.BigDecimal           | DECIMAL         |
| java.math.BigInteger           | DECIMAL         |
| java.sql.Array                 | N/A             |
| java.sql.Blob                  | BLOB            |
| java.sql.Clob                  | TEXT            |
| java.sql.NClob                 | TEXT            |
| java.sql.SQLXML                | TEXT            |
| java.time.Instant              | TIMESTAMP       |
| java.time.LocalDate            | DATE            |
| java.time.LocalDateTime        | TIMESTAMP       |
| java.time.LocalTime            | TIME            |
| java.time.OffsetDateTime       | TIMESTAMP       |
| java.util.UUID                 | N/A             |
| kotlin.Any                     | N/A             |
| kotlin.Boolean                 | BIT(1), BOOLEAN |
| kotlin.Byte                    | TINYINT         |
| kotlin.ByteArray               | VARBINARY       |
| kotlin.Double                  | DOUBLE          |
| kotlin.Float                   | FLOAT           |
| kotlin.Int                     | INT             |
| kotlin.Long                    | BIGINT          |
| kotlin.Short                   | SMALLINT        |
| kotlin.String                  | VARCHAR         |
| kotlin.UByte                   | SMALLINT        |
| kotlin.UInt                    | BIGINT          |
| kotlin.UShort                  | INT             |
| kotlinx.datetime.Instant       | TIMESTAMP       |
| kotlinx.datetime.LocalDate     | DATE            |
| kotlinx.datetime.LocalDateTime | TIMESTAMP       |
| enum class                     | VARCHAR         |

## MySQL - JDBC

The following table shows the data type mapping:

| Kotlin Type                    | Database Type   |
|--------------------------------|-----------------|
| java.math.BigDecimal           | DECIMAL         |
| java.math.BigInteger           | DECIMAL         |
| java.sql.Array                 | N/A             |
| java.sql.Blob                  | BLOB            |
| java.sql.Clob                  | TEXT            |
| java.sql.NClob                 | TEXT            |
| java.sql.SQLXML                | TEXT            |
| java.time.Instant              | TIMESTAMP       |
| java.time.LocalDate            | DATE            |
| java.time.LocalDateTime        | TIMESTAMP(6)    |
| java.time.LocalTime            | TIME            |
| java.time.OffsetDateTime       | TIMESTAMP       |
| java.util.UUID                 | N/A             |
| kotlin.Any                     | N/A             |
| kotlin.Boolean                 | BIT(1), BOOLEAN |
| kotlin.Byte                    | TINYINT         |
| kotlin.ByteArray               | VARBINARY       |
| kotlin.Double                  | DOUBLE          |
| kotlin.Float                   | FLOAT           |
| kotlin.Int                     | INT             |
| kotlin.Long                    | BIGINT          |
| kotlin.Short                   | SMALLINT        |
| kotlin.String                  | VARCHAR         |
| kotlin.UByte                   | SMALLINT        |
| kotlin.UInt                    | BIGINT          |
| kotlin.UShort                  | INT             |
| kotlinx.datetime.Instant       | TIMESTAMP       |
| kotlinx.datetime.LocalDate     | DATE            |
| kotlinx.datetime.LocalDateTime | TIMESTAMP       |
| enum class                     | VARCHAR         |

## Oracle - JDBC

The following table shows the data type mapping:

| Kotlin Type                    | Database Type            |
|--------------------------------|--------------------------|
| java.math.BigDecimal           | NUMBER                   |
| java.math.BigInteger           | NUMBER                   |
| java.sql.Array                 | N/A                      |
| java.sql.Blob                  | BLOB                     |
| java.sql.Clob                  | CLOB                     |
| java.sql.NClob                 | N/A                      |
| java.sql.SQLXML                | N/A                      |
| java.time.Instant              | TIMESTAMP                |
| java.time.LocalDate            | DATE                     |
| java.time.LocalDateTime        | TIMESTAMP                |
| java.time.LocalTime            | TIME                     |
| java.time.OffsetDateTime       | TIMESTAMP WITH TIME ZONE |
| java.util.UUID                 | N/A                      |
| kotlin.Any                     | N/A                      |
| kotlin.Boolean                 | NUMBER                   |
| kotlin.Byte                    | NUMBER                   |
| kotlin.ByteArray               | RAW                      |
| kotlin.Double                  | FLOAT                    |
| kotlin.Float                   | FLOAT                    |
| kotlin.Int                     | NUMBER                   |
| kotlin.Long                    | NUMBER                   |
| kotlin.Short                   | NUMBER                   |
| kotlin.String                  | VARCHAR2                 |
| kotlin.UByte                   | NUMBER                   |
| kotlin.UInt                    | NUMBER                   |
| kotlin.UShort                  | NUMBER                   |
| kotlinx.datetime.Instant       | TIMESTAMP                |
| kotlinx.datetime.LocalDate     | DATE                     |
| kotlinx.datetime.LocalDateTime | TIMESTAMP                |
| enum class                     | VARCHAR2                 |

## Oracle - R2DBC

The following table shows the data type mapping:

| Kotlin Type                    | Database Type            |
|--------------------------------|--------------------------|
| io.r2dbc.spi.Blob              | BLOB                     |
| io.r2dbc.spi.Clob              | CLOB                     |
| java.math.BigDecimal           | NUMBER                   |
| java.math.BigInteger           | NUMBER                   |
| java.time.Duration             | INTERVAL DAY TO SECOND   |
| java.time.Instant              | TIMESTAMP                |
| java.time.LocalDate            | DATE                     |
| java.time.LocalDateTime        | TIMESTAMP                |
| java.time.LocalTime            | TIME                     |
| java.time.Period               | INTERVAL YEAR TO MONTH   |
| java.time.OffsetDateTime       | TIMESTAMP WITH TIME ZONE |
| java.util.UUID                 | N/A                      |
| kotlin.Any                     | N/A                      |
| kotlin.Boolean                 | NUMBER                   |
| kotlin.Byte                    | NUMBER                   |
| kotlin.ByteArray               | RAW                      |
| kotlin.Double                  | FLOAT                    |
| kotlin.Float                   | FLOAT                    |
| kotlin.Int                     | NUMBER                   |
| kotlin.Long                    | NUMBER                   |
| kotlin.Short                   | NUMBER                   |
| kotlin.String                  | VARCHAR2                 |
| kotlin.UByte                   | NUMBER                   |
| kotlin.UInt                    | NUMBER                   |
| kotlin.UShort                  | NUMBER                   |
| kotlinx.datetime.Instant       | TIMESTAMP                |
| kotlinx.datetime.LocalDate     | DATE                     |
| kotlinx.datetime.LocalDateTime | TIMESTAMP                |
| enum class                     | VARCHAR2                 |

## PostgreSQL - JDBC

The following table shows the data type mapping:

| Kotlin Type                    | Database Type                        |
|--------------------------------|--------------------------------------|
| java.math.BigDecimal           | numeric, decimal                     |
| java.math.BigInteger           | numeric, decimal                     |
| java.sql.Array                 | array of data type                   |
| java.sql.Blob                  | N/A                                  |
| java.sql.Clob                  | N/A                                  |
| java.sql.NClob                 | N/A                                  |
| java.sql.SQLXML                | xml                                  |
| java.time.Instant              | timestamp                            |
| java.time.LocalDate            | date                                 |
| java.time.LocalDateTime        | timestamp                            |
| java.time.LocalTime            | time                                 |
| java.time.OffsetDateTime       | timestamp with time zone             |
| java.util.UUID                 | uuid                                 |
| kotlin.Any                     | N/A                                  |
| kotlin.Boolean                 | boolean, bool                        |
| kotlin.Byte                    | smallint                             |
| kotlin.ByteArray               | bytea                                |
| kotlin.Double                  | double precision, float8             |
| kotlin.Float                   | real                                 |
| kotlin.Int                     | integer, int, int4, serial           |
| kotlin.Long                    | bigint, int8, bigserial, serial8     |
| kotlin.Short                   | smallint, int2, smallserial, serial2 |
| kotlin.String                  | character varying, varchar, text     |
| kotlin.UByte                   | smallint, int2, smallserial, serial2 |
| kotlin.UInt                    | bigint, int8, bigserial, serial8     |
| kotlin.UShort                  | integer, int, int4, serial           |
| kotlinx.datetime.Instant       | timestamp                            |
| kotlinx.datetime.LocalDate     | date                                 |
| kotlinx.datetime.LocalDateTime | timestamp                            |
| enum class                     | character varying, varchar, text     |

## PostgreSQL - R2DBC

The following table shows the data type mapping:

| Kotlin Type                        | Database Type                        |
|------------------------------------|--------------------------------------|
| io.r2dbc.postgresql.codec.Interval | interval                             |
| io.r2dbc.postgresql.codec.Json     | json, jsonb                          |
| io.r2dbc.spi.Blob                  | bytea                                |
| io.r2dbc.spi.Clob                  | text                                 |
| java.math.BigDecimal               | numeric, decimal                     |
| java.math.BigInteger               | numeric, decimal                     |
| java.time.Instant                  | timestamp                            |
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
| kotlinx.datetime.Instant           | timestamp                            |
| kotlinx.datetime.LocalDate         | date                                 |
| kotlinx.datetime.LocalDateTime     | timestamp                            |
| enum class                         | character varying, varchar, text     |

## SQL Server - JDBC

The following table shows the data type mapping:

| Kotlin Type                    | Database Type     |
|--------------------------------|-------------------|
| java.math.BigDecimal           | decimal           |
| java.math.BigInteger           | decimal           |
| java.sql.Array                 | N/A               |
| java.sql.Blob                  | varbinary(max)    |
| java.sql.Clob                  | text              |
| java.sql.NClob                 | N/A               |
| java.sql.SQLXML                | xml               |
| java.time.Instant              | datetime          |
| java.time.LocalDate            | date              |
| java.time.LocalDateTime        | datetime          |
| java.time.LocalTime            | time              |
| java.time.OffsetDateTime       | N/A               |
| java.util.UUID                 | N/A               |
| kotlin.Any                     | N/A               |
| kotlin.Boolean                 | bit               |
| kotlin.Byte                    | smallint, tinyint |
| kotlin.ByteArray               | varbinary         |
| kotlin.Double                  | float             |
| kotlin.Float                   | real              |
| kotlin.Int                     | int               |
| kotlin.Long                    | bigint            |
| kotlin.Short                   | smallint          |
| kotlin.String                  | varchar, nvarchar |
| kotlin.UByte                   | smallint          |
| kotlin.UInt                    | bigint            |
| kotlin.UShort                  | int               |
| kotlinx.datetime.Instant       | datetime          |
| kotlinx.datetime.LocalDate     | date              |
| kotlinx.datetime.LocalDateTime | datetime          |
| enum class                     | varchar, nvarchar |

## SQL Server - R2DBC

The following table shows the data type mapping:

| Kotlin Type                    | Database Type     |
|--------------------------------|-------------------|
| io.r2dbc.spi.Blob              | varbinary(max)    |
| io.r2dbc.spi.Clob              | text              |
| java.math.BigDecimal           | decimal           |
| java.math.BigInteger           | decimal           |
| java.time.Instant              | datetime          |
| java.time.LocalDate            | date              |
| java.time.LocalDateTime        | datetime          |
| java.time.LocalTime            | time              |
| java.time.OffsetDateTime       | N/A               |
| java.util.UUID                 | N/A               |
| kotlin.Any                     | N/A               |
| kotlin.Boolean                 | bit               |
| kotlin.Byte                    | smallint, tinyint |
| kotlin.ByteArray               | varbinary         |
| kotlin.Double                  | float             |
| kotlin.Float                   | real              |
| kotlin.Int                     | int               |
| kotlin.Long                    | bigint            |
| kotlin.Short                   | smallint          |
| kotlin.String                  | varchar, nvarchar |
| kotlin.UByte                   | smallint          |
| kotlin.UInt                    | bigint            |
| kotlin.UShort                  | int               |
| kotlinx.datetime.Instant       | datetime          |
| kotlinx.datetime.LocalDate     | date              |
| kotlinx.datetime.LocalDateTime | datetime          |
| enum class                     | varchar, nvarchar |
