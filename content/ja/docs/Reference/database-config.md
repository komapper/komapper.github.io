---
title: "データベースの設定"
linkTitle: "設定"
weight: 12
description: >
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
val db = JdbcDatabase(config)
```

`R2dbcDatabase`インスタンスを生成する例です。

```kotlin
val connectionFactory: ConnectionFactory = ..
val dialect: R2dbcDialect = ..
val config: R2dbcDatabaseConfig = object: DefaultR2dbcDatabaseConfig(connectionFactory, dialect) {
  // you can override properties here
}
val db = R2dbcDatabase(config)
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
: INSERT、UPDATE、DELETEでバッチ更新を行う際のバッチサイズです。デフォルトは`null`です。クエリオプションでも指定されない場合`10`が使われます。

fetchSize
: SELECT文発行時のフェッチサイズです。デフォルトは`null`でドライバの値を使うことを示します。

maxRows
: SELECT文発行時の最大行数です。デフォルトは`null`でドライバの値を使うことを示します。

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

[ロギング]({{< relref "logging.md" >}})も参照ください。

### loggerFacade

ロガーのファサードです。

実行されるSQLやトランザクションに関するログ出力指示を受け付け、ログメッセージをフォーマットし、ロガーへ送ります。
Komapperから出力されるログはすべてこのファサードを経由します。

ログメッセージやログレベルを変更するには、ファサードの実装を切り替えてください。

[ロギング]({{< relref "logging.md" >}})も参照ください。

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

[Templateクエリ]({{< relref "Query/QueryDsl/template.md" >}})も参照ください。

### statisticManager

`statisticManager` は、SQL 実行に関連する統計情報を管理します。  
各 SQL ステートメントに対して以下の情報を保持します：

- 実行回数
- 実行時間の最大値（ミリ秒単位）
- 実行時間の最小値（ミリ秒単位）
- 実行時間の合計（ミリ秒単位）
- 実行時間の平均値（ミリ秒単位）

`statisticManager` を有効化するには、以下のように `enableStatistics = true` を設定してください：

```kotlin
val config: JdbcDatabaseConfig = DefaultJdbcDatabaseConfig(dataSource, dialect, enableStatistics = true)
```

```kotlin
val config: R2dbcDatabaseConfig = DefaultR2dbcDatabaseConfig(connectionFactory, dialect, enableStatistics = true)
```

デフォルトでは、このプロパティはサービスローダーによって解決されます。
サービスローダーが statisticManager を解決できない場合、
このプロパティはデフォルトの statisticManager を返します。

デフォルトの statisticManager は、有効化されている間、統計情報を無期限に収集します。
メモリ不足を防ぐために、定期的に statisticManager の `clear` メソッドを呼び出すか、
`org.komapper.core.StatisticManager` の適切な実装クラスを作成してください。