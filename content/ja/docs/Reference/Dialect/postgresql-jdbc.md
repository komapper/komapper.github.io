---
title: "PostgreSQL - JDBC"
linkTitle: "PostgreSQL - JDBC"
weight: 40
description: >
  PostgreSQLにJDBCでアクセスするための方言
---

## 概要 {#overview}

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

## データ型の対応関係 {#data-type-mapping}

| Kotlin Type | Database Type |
|-------------|---------------|
| java.math.BigDecimal | numeric, decimal |
| java.math.BigInteger | numeric, decimal |
| java.sql.Array | array |
| java.sql.Blob | N/A |
| java.sql.Clob | N/A |
| java.sql.NClob | N/A |
| java.sql.SQLXML | text |
| java.time.LocalDate | date |
| java.time.LocalDateTime | timestamp |
| java.time.LocalTime | time |
| java.time.OffsetDateTime | timestamp with time zone |
| java.util.UUID | uuid |
| kotlin.Any | N/A |
| kotlin.Boolean | boolean, bool |
| kotlin.Byte | smallint |
| kotlin.ByteArray | bytea |
| kotlin.Double | double precision, float8 |
| kotlin.Float | real |
| kotlin.Int | integer, int, int4, serial |
| kotlin.Long | bigint, int8, bigserial, serial8 |
| kotlin.Short | smallint, int2, smallserial, serial2 |
| kotlin.String | character varying, varchar |
| kotlin.UByte | smallint, int2, smallserial, serial2 |
| kotlin.UInt | bigint, int8, bigserial, serial8 |
| kotlin.UShort | integer, int, int4, serial |