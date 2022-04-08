---
title: "ダイアレクト"
weight: 15
description: >
---

## 概要 {#overview}

ダイアレクトはデータベースやドライバの差異を吸収します。
ダイアレクトの代表的な機能はKotlinとデータベースの型の対応関係を解決することです。

ダイアレクトを表すクラスは個別のモジュール（Artifact）に含まれており、
どのモジュールを利用するかは接続先データベースや接続タイプに合わせて選択する必要があります。

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

ダイアレクトを利用するにはGradleの依存関係の宣言の中で上述のArtifact IDを記述します。

```kotlin
val komapperVersion: String by project
dependencies {
    implementation("org.komapper:komapper-dialect-h2-jdbc:$komapperVersion")
}
```

## kotlinx-datetimeのサポート {#kotlinx-datetime-support}

Komapperは [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime) の以下のデータ型をサポートします。

- kotlinx.datetime.Instant
- kotlinx.datetime.LocalDate
- kotlinx.datetime.LocalDateTime

これらの型を利用するには、Gradleの依存関係の宣言で次のように宣言してください。

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
}
```

また、`komapper-datetime-jdbc`もしくは`komapper-datetime-r2dbc`を追加してください。

```kotlin
val komapperVersion: String by project
dependencies {
    runtimeOnly("org.komapper:komapper-datetime-jdbc:$komapperVersion")
}
```

{{< alert title="Note" >}}
[スターター]({{< relref "starter.md" >}}) を利用する場合、
`komapper-datetime-jdbc`や`komapper-datetime-r2dbc`の宣言は不要です。
{{< /alert >}}

## H2 - JDBC

[データ型の対応関係]({{< relref path="dialect#h2---jdbc" lang="en" >}})

## H2 - R2DBC

[データ型の対応関係]({{< relref path="dialect#h2---r2dbc" lang="en" >}})

## MariaDB - JDBC

[データ型の対応関係]({{< relref path="dialect#mariadb---jdbc" lang="en" >}})

## MySQL - JDBC

[データ型の対応関係]({{< relref path="dialect#mysql---jdbc" lang="en" >}})

## Oracle - JDBC

[データ型の対応関係]({{< relref path="dialect#oracle---jdbc" lang="en" >}})

## Oracle - R2DBC

[データ型の対応関係]({{< relref path="dialect#oracle---r2dbc" lang="en" >}})

## PostgreSQL - JDBC

[データ型の対応関係]({{< relref path="dialect#postgresql---jdbc" lang="en" >}})

## PostgreSQL - R2DBC

[データ型の対応関係]({{< relref path="dialect#postgresql---r2dbc" lang="en" >}})

## SQL Server - JDBC

[データ型の対応関係]({{< relref path="dialect#sql-server---jdbc" lang="en" >}})

## SQL Server - R2DBC

[データ型の対応関係]({{< relref path="dialect#sql-server---r2dbc" lang="en" >}})
