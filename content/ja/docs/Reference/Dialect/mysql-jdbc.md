---
title: "MySQL - JDBC"
linkTitle: "MySQL - JDBC"
weight: 30
description: >
  MySQLにJDBCでアクセスするための方言
---

## 概要 {#overview}

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


## データ型の対応関係 {#data-type-mapping}

| Kotlin Type | Database Type |
|-------------|---------------|
| java.math.BigDecimal | DECIMAL |
| java.math.BigInteger | DECIMAL |
| java.sql.Array | N/A |
| java.sql.Blob | BLOB |
| java.sql.Clob | TEXT |
| java.sql.NClob | TEXT |
| java.sql.SQLXML | TEXT |
| java.time.LocalDate | DATE |
| java.time.LocalDateTime | TIMESTAMP(6) |
| java.time.LocalTime | TIME |
| java.time.OffsetDateTime | TIMESTAMP |
| java.util.UUID | N/A |
| kotlin.Any | N/A |
| kotlin.Boolean | BIT(1), BOOLEAN |
| kotlin.Byte | TINYINT |
| kotlin.ByteArray | VARBINARY |
| kotlin.Double | DOUBLE |
| kotlin.Float | FLOAT |
| kotlin.Int | INT |
| kotlin.Long | BIGINT |
| kotlin.Short | SMALLINT |
| kotlin.String | VARCHAR |
| kotlin.UByte | SMALLINT |
| kotlin.UInt | BIGINT |
| kotlin.UShort | INT |
