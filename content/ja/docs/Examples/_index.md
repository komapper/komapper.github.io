---
title: "サンプルアプリケーション"
linkTitle: "サンプルアプリ"
weight: 3
description: >
  いくつかのサンプルアプリケーション
---

## 概要 {#overview}

JDBC接続、R2DBC接続、
[Spring Boot](https://spring.io/projects/spring-boot) 連携、
[Ktor](https://ktor.io/) 連携など、実行可能なサンプルアプリケーションをいくつか提供します。

## 必要要件 {#prerequisites}

サンプルアプリケーションの動作に必要な要件です。

- JDK 11、もしくはそれ以降のバージョン
- Docker（spring-native-jdbc など一部のサンプルが利用）

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

### quarkus-jdbc

このプロジェクトは、JDBCを使ってPostgreSQLデータベースにアクセスするQuarkusのウェブアプリケーションです。

詳細は [README](https://github.com/komapper/komapper-examples/blob/main/quarkus-jdbc/README.md) をご覧ください。

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

### spring-native-jdbc

このプロジェクトは [Spring Native](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/) に対応したアプリケーションです。
データベースの接続にJDBCを用います。

以下のコマンドでネイティブアプリケーションをビルドできます。

```sh
$ ./gradlew :spring-native-jdbc:bootBuildImage
```

アプリケーションを動かすには次のようにDockerを起動します。

```sh
$ docker run --rm -p 8080:8080 docker.io/library/spring-native-jdbc:0.0.1
```

アプリケーションが実行されたら、ブラウザで `http://localhost:8080` を開いてください。
JSONで返されたデータがブラウザ上に表示されます。

データを追加するには `http://localhost:8080/?text=Hi` のようにクエリパラメーターでデータを渡します。
再度 `http://localhost:8080` を開くと追加されたデータと合わせて一覧されます。

### spring-native-r2dbc

このプロジェクトは [Spring Native](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/) に対応したアプリケーションです。
データベースの接続にR2DBCを用います。

現在のバージョンでは、Kotlin コルーチンに関する制約を受けます。
 
- https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/#limitations

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
