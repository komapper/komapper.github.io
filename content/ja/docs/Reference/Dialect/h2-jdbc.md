---
title: "H2 - JDBC"
linkTitle: "H2 - JDBC"
weight: 10
description: >
  H2 Database EngineにJDBCでアクセスするための方言
---

## 概要 {#overview}

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

## データ型の対応関係 {#data-type-mapping}

| Kotlin Type | Database Type |
|-------------|---------------|
| java.math.BigDecimal | BIGINT |
| java.math.BigInteger | BIGINT |
| java.sql.Array | ARRAY |
| java.sql.Blob | BLOB |
| java.sql.Clob | CLOB |
| java.sql.NClob | CLOB |
| java.sql.SQLXML | CLOB |
| java.time.LocalDate | DATE |
| java.time.LocalDateTime | TIMESTAMP |
| java.time.LocalTime | TIME |
| java.time.OffsetDateTime | TIMESTAMP WITH TIME ZONE |
| java.util.UUID | UUID |
| kotlin.Any | OTHER |
| kotlin.Boolean | BOOLEAN |
| kotlin.Byte | TINYINT |
| kotlin.ByteArray | BINARY |
| kotlin.Double | DOUBLE |
| kotlin.Float | REAL |
| kotlin.Int | INT |
| kotlin.Long | BIGINT |
| kotlin.Short | SMALLINT |
| kotlin.String | VARCHAR |
| kotlin.UByte | SMALLINT |
| kotlin.UInt | BIGINT |
| kotlin.UShort | INT |
