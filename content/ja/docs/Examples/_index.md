---
title: "Examples"
linkTitle: "Examples"
weight: 3
description: >
  サンプルアプリケーション
---

## 概要 {#overview}

JDBC接続、R2DBC接続、
[Spring Boot](https://spring.io/projects/spring-boot) 連携、
[Ktor](https://ktor.io/) 連携など、実行可能なサンプルアプリケーションをいくつか提供します。

## 必要要件 {#prerequisites}

- JDK 8、もしくはそれ以降のバージョン

## リポジトリの取得 {#clone}

[komapper/komapper-examples](https://github.com/komapper/komapper-examples)
をクローンしてください。

```sh
$ git clone https://github.com/komapper/komapper-examples.git
```

```sh
$ cd komapper-examples
```

リポジトリはGradleのマルチプロジェクト構成です。
サンプルアプリケーションはGradleのサブプロジェクトとして実装されています。

## サンプルアプリケーションの説明 {#applications}

### console-jdbc

このプロジェクトは、コンソールアプリケーションからJDBCの接続を行います。

動かすには以下のコマンドを実行します。

```sh
$ ./gradlew :console-jdbc:run
```

### console-r2dbc

このプロジェクトは、コンソールアプリケーションからR2DBCの接続を行います。

動かすには以下のコマンドを実行します。

```sh
$ ./gradlew :console-r2dbc:run
```

### spring-boot-jdbc

このプロジェクトは、Spring Bootを使ったWebアプリケーションからJDBCの接続を行います。

動かすには次のコマンドを実行します。

```sh
$ ./gradlew :spring-boot-jdbc:bootRun
```

アプリケーションが実行されたら、ブラウザで `http://localhost:8080` を開いてください。
JSONで返されたデータがブラウザ上に表示されます。

データを追加するには `http://localhost:8080/?text=Hi` のようにクエリパラメーターでデータを渡します。
再度 `http://localhost:8080` を開くと追加されたデータと合わせて一覧されます。

### spring-boot-r2dbc

このプロジェクトは、Spring Bootを使ったWebアプリケーションからR2DBCの接続を行います。

動かすには次のコマンドを実行します。

```sh
$ ./gradlew :spring-boot-r2dbc:bootRun
```

アプリケーションが実行されたら、ブラウザで `http://localhost:8080` を開いてください。
JSONで返されたデータがブラウザ上に表示されます。

データを追加するには `http://localhost:8080/?text=Hi` のようにクエリパラメーターでデータを渡します。
再度 `http://localhost:8080` を開くと追加されたデータと合わせて一覧されます。

### repository-pattern-jdbc

このプロジェクトは、リポジトリパターンの実装例を含みます。

リポジトリパターンを呼び出すテストを実行するには次のコマンドを実行します。

```sh
$ ./gradlew :repository-pattern-jdbc:check
```

### codegen

このプロジェクトは、[Gradleプラグイン]({{< relref "../Reference/gradle-plugin" >}})
を使用してデータベースのメタデータからエンティティクラスのソースコードを生成します。

{{< alert title="Note" >}}
このプロジェクトは、[Testcontainers](https://www.testcontainers.org/) を使っているため
実行にあたってはDockerコンテナ内が必要です。
{{< /alert >}}

KomapperのGradleプラグインの設定は、build.gradle.ktsファイル内のkomapperブロックに記述されています。
この例ではMySQLとPostgreSQLからコード生成をします。

MySQLからコード生成するには次のコマンドを実行します。

```sh
$ ./gradlew :codegen:komapperMysqlGenerator
```

PostgreSQLからコード生成するには次のコマンドを実行します。

```sh
$ ./gradlew :codegen:komapperPostgresqlGenerator
```

MySQLからもPostgreSQLからも生成する場合は次のコマンドを実行します。

```sh
$ ./gradlew :codegen:komapperGenerator
```

生成コードは `codgen/src/main/kotlin` の下に出力されます。

### comparison-with-exposed

このプロジェクトは、[JetBrains Exposedのサンプルコード](https://github.com/JetBrains/Exposed#sql-dsl)
をKomapper用に書き換えたものです。

動かすには次のコマンドを実行します。

```sh
$ ./gradlew :comparison-with-exposed:run
```

### jpetstore

このプロジェクトは、Spring Bootを使ったWebアプリケーションからJDBCの接続を行います。
MyBatisの [jpetstore-6](https://github.com/mybatis/jpetstore-6) をベースにしたアプリケーションです。

動かすには次のコマンドを実行します。

```sh
$ ./gradlew :jpetstore:bootRun
```

アプリケーションが実行されたら、ブラウザで `http://localhost:8080` を開いてください。
サインインが求められる箇所では以下のusernameとpasswordが利用できます。

- username: jpetstore
- password: jpetstore

### kweet

このプロジェクトは、Ktorを使ったWebアプリケーションからR2DBCの接続を行います。
Ktorの [Kweet](https://github.com/ktorio/ktor-samples/tree/main/kweet) をベースにしたアプリケーションです。

動かすには次のコマンドを実行します。

```sh
$ ./gradlew :kweet:run
```

アプリケーションが実行されたら、ブラウザで `http://localhost:8080` を開いてください。
