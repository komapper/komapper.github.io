---
title: "Gradle Plugin"
linkTitle: "Gradle Plugin"
weight: 200
description: >
  Gradleプラグイン
---

## 概要 {#overview}

Komapperが提供するGradleプラグインは、
データベースのメタデータから次の2種類のKotlinソースコードを生成します。

- エンティティクラス
- エンティティクラスのマッピング定義

エンティティクラスやマッピング定義については [Entity Class]({{< relref "entity-class" >}}) を参照ください。

{{< alert title="Note" >}}
Gradleプラグインの利用は必須ではありません。
データベース上にテーブル定義がすでに存在する場合に利用を検討ください。
{{< /alert >}}

## 利用方法 {#usage}

プラグインの最新版はGradleプラグインのポータルのサイトで確認できます。

- https://plugins.gradle.org/plugin/org.komapper.gradle

下記のコードはプラグインを利用したgradle.ktsファイルの例（抜粋）です。

```kotlin
// Komapperプラグインの利用を宣言する
plugins {
  id("org.komapper.gradle") version "0.27.0"
}

// Komapperプラグインに関する設定を行う
komapper {
    // Komapperプラグインの中でもコード生成に関するプラグインの利用を示す
    generators {
        // 利用するデータベースごとにregisterブロックに適当な名前をつけてブロック内に設定を記述する
        register("postgresql") {
            val initScript = file("src/main/resources/init_postgresql.sql")
            // databaseパラメータの設定
            database.set(
                // Testcontainers上のPostgreSQLを利用する
                JdbcDatabase.create(
                    url = "jdbc:tc:postgresql:13.3:///test?TC_INITSCRIPT=file:${initScript.absolutePath}",
                    user = "test",
                    password = "test"
                )
            )
            // packageNameパラメータの設定
            packageName.set("org.komapper.example.postgresql")
            // overwriteEntitiesパラメータの設定
            overwriteEntities.set(true)
            // overwriteDefinitionsパラメータの設定
            overwriteDefinitions.set(true)
        }
    }
}
```

上記の設定をした上で下記のコマンドを実行するとコードを生成できます。

```sh
$ ./gradlew komapperGenerator
```

`register`に指定した名前を明示しても同じ結果が得られます。

```sh
$ ./gradlew komapperPostgresqlGenerator
```

[Examples]({{< relref "../Examples#codegen" >}}) も参考にしてください。

## パラメータ一覧 {#parameter-list}

`register`ブロック内で設定可能なパラメータを示します。

### database

接続先データベースを表します。

`org.komapper.jdbc.JdbcDatabase`のインスタンスを設定してください。

設定必須です。

### catalog

接続先データベースのカタログです。

設定は必須ではありません。

`java.sql.DatabaseMetaData#getTables`メソッドの同名のパラメータに渡されます。

### schemaPattern

接続先データベースの読み込み対象スキーマです。

`SALES%`のようにLIKE述語と同様の記述ができます。

設定は必須ではありません。

`java.sql.DatabaseMetaData#getTables`メソッドの同名のパラメータに渡されます。

### tableNamePattern

接続先データベースの読み込み対象テーブルです。

`JOB%`のようにLIKE述語と同様の記述ができます。

設定は必須ではありません。

`java.sql.DatabaseMetaData#getTables`メソッドの同名のパラメータに渡されます。

### tableTypes

接続先のテーブルのタイプです。

設定は必須ではありません。

デフォルトの値は`TABLE`のみを含んだ`List`です。
`List`は以下のような値を含むことができます。

- TABLE
- VIEW
- SYSTEM TABLE
- GLOBAL TEMPORARY
- LOCAL TEMPORARY
- ALIAS
- SYNONYM

`java.sql.DatabaseMetaData#getTables`メソッドの同名のパラメータに渡されます。

### destinationDir

生成されるKotlinソースコードの出力先です。

エンティティクラスのソースコードは`entities.kt`、マッピング定義のソースコードは`entityDefinitions.kt`というファイル名で出力されます。

設定は必須ではありません。

デフォルトの値は`src/main/kotlin`です。

### packageName

生成されるエンティティクラスやマッピング定義クラスのパッケージ名です。

設定は必須ではありません。

### prefix

生成されるエンティティクラス名のプレフィックスです。

設定は必須ではありません。

デフォルトの値は空文字です。

### suffix

生成されるエンティティクラス名のサフィックスです。

設定は必須ではありません。

デフォルトの値は空文字です。

### overwriteEntities

生成されるエンティティクラスのソースコードを上書きするかどうかを表します。

設定は必須ではありません。

デフォルトの値は`false`です。

### overwriteDefinitions

生成されるマッピング定義のソースコードを上書きするかどうかを表します。

設定は必須ではありません。

デフォルトの値は`false`です。

### declareAsNullable

生成されるエンティティクラスの全プロパティをNULL許容型として宣言するかどうかを表します。

設定は必須ではありません。

デフォルトの値は`false`です。
この値が`false`の場合、 NULL許容型として宣言するかどうかはプロパティごとにデータベースのメタデータから判定します。

### useCatalog

生成されるマッピング定義でカタログ名を明示するかどうかを表します。

設定は必須ではありません。

デフォルトの値は`false`です。

### useSchema

生成されるマッピング定義でスキーマ名を明示するかどうかを表します。

設定は必須ではありません。

デフォルトの値は`false`です。

### classResolver

生成されるエンティティクラスのプロパティの型を決定するリゾルバです。

`org.komapper.codegen.ClassResolver`のインスタンスを設定してください。

デフォルトは`org.komapper.codegen.DefaultClassResolver`のインスタンスです。
このデフォルトの実装クラスは、接続先データベースのDialectに登録された情報を使って型を解決します。

リゾルバで型を解決できない場合、`String`型として生成されます。