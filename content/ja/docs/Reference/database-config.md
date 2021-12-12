---
title: "Database Config"
linkTitle: "Database Config"
weight: 12
description: >
  設定
---

## 概要 {#overview}

Databaseインスタンスの生成時にDatabaseConfigインスタンスを与えることで挙動をカスタマイズできます。

`JdbcDatabase`インスタンスを生成する例です。

```kotlin
val dataSource: DataSource = ..
val dialect: JdbcDialect = ..
val config: JdbcDatabaseConfig = object: DefaultJdbcDatabaseConfig(dataSource, dialect) {
    // you can override properties here
}
val db = JdbcDatabase.create(config)
```

`R2dbcDatabase`インスタンスを生成する例です。

```kotlin
val connectionFactory: ConnectionFactory = ..
val dialect: R2dbcDialect = ..
val config: R2dbcDatabaseConfig = object: DefaultR2dbcDatabaseConfig(connectionFactory, dialect) {
  // you can override properties here
}
val db = R2dbcDatabase.create(config)
```

## プロパティ {#properties}

下記に説明する`JdbcDatabaseConfig`や`R2dbcDatabaseConfig`のプロパティをオーバーライドしたりサービスローダーの仕組みを使うことで挙動をカスタマイズできます。

### clockProvider

`Clock`のプロバイダーです。

プロバイダによって提供された`Clock`は`@KomapperCreatedAt`や`@KomapperUpdatedAt`が付与されたエンティティクラスのプロパティにタイムスタンプを設定する際に利用されます。

デフォルトでは、システムデフォルトのゾーンIDを使って現在時刻を生成するプロバイダーを返します。

### executionOptions

JDBCやR2DBCのデフォルトの実行時オプションです。
下記の設定ができます。

batchSize
: バッチサイズです。デフォルトは`null`です。クエリオプションでも指定されない場合`10`が使われます。

fetchSize
: フェッチサイズです。デフォルトは`null`でドライバの値を使うことを示します。

maxRows
: 最大行数です。デフォルトは`null`でドライバの値を使うことを示します。

queryTimeoutSeconds
: クエリタイムアウトの秒数です。デフォルトは`null`でドライバの値を使うことを示します。

suppressLogging
: SQLのログ出力を抑制するかどうかです。デフォルトは`false`です。

これらは全てクエリのオプションで上書きできます。

### logger

ロガーです。

デフォルトでは、サービスローダーでファクトリを取得しファクトリから利用すべきロガーを生成します。
サービスローダーでファクトリを取得できない場合、出力先を標準出力とするロガーを返します。

以下のモジュールがサービスローダー対応のファクトリを持ちます。

- komapper-slf4j

以下のドキュメントも参照ください。

- [Logging]({{< relref "logging.md" >}})

### loggerFacade

ロガーのファサードです。

実行されるSQLやトランザクションに関するログ出力指示を受け付け、ログメッセージをフォーマットし、ロガーへ送ります。
Komapperから出力されるログはすべてこのファサードを経由します。

ログメッセージやログレベルを変更するには、ファサードの実装を切り替えてください。

以下のドキュメントも参照ください。

- [Logging]({{< relref "logging.md" >}})

### statementInspector

`org.komapper.core.Statement`のインスペクターです。
実行直前にSQLを変換します。

デフォルトでは、サービスローダーでファクトリを取得しファクトリから利用すべきインスペクターを生成します。
サービスローダーでファクトリを取得できない場合、何もしないインスペクターを返します。

以下のモジュールがサービスローダー対応のファクトリを持ちます。

- komapper-sqlcommenter

### templateStatementBuilder

SQLテンプレートから`org.komapper.core.Statement`を生成するビルダーです。

デフォルトでは、サービスローダーでファクトリを取得しファクトリから利用すべきビルダーを生成します。
サービスローダーでファクトリを取得できない場合、例外をスローします。

以下のモジュールがサービスローダー対応のファクトリを持ちます。

- komapper-template

以下のドキュメントも参照ください。

- [Template DSL]({{< relref "Query/template-dsl.md" >}})
