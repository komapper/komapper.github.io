---
title: "Examples"
linkTitle: "Examples"
weight: 3
date: 2017-01-05
description: >
  JDBC接続、R2DBC接続、Spring Boot連携、コード生成など様々なコード例
---

## 必要要件

- JDK 8、もしくはそれ以降のバージョン

## リポジトリの取得

[komapper/komapper-examples](https://github.com/komapper/komapper-examples)
をクローンしてください。

```sh
$ git clone https://github.com/komapper/komapper-examples.git
```

```sh
$ cd komapper-examples
```

## サンプルコードの動かし方

### コンソールアプリケーション

JDBC版のアプリケーションを動かすには次のコマンドを実行します。

```sh
$ ./gradlew :console-jdbc:run
```

R2DBC版のアプリケーションを動かすには次のコマンドを実行します。

```sh
$ ./gradlew :console-r2dbc:run
```

### Spring Bootを使ったWebアプリケーション

JDBC版のアプリケーションを動かすには次のコマンドを実行します。

```sh
$ ./gradlew :spring-boot-jdbc:bootRun
```

R2DBC版のアプリケーションを動かすには次のコマンドを実行します。

```sh
$ ./gradlew :spring-boot-r2dbc:bootRun
```

アプリケーションが実行されたら、ブラウザで `http://localhost:8080` を開いてください。
JSONで返されたデータがブラウザ上に表示されます。

データを追加するには `http://localhost:8080/?text=Hi` のようにクエリパラメーターでデータを渡します。
再度 `http://localhost:8080` を開くと追加されたデータと合わせて一覧されます。

### データベースを使ったコード生成

Komapperはデータベースのメタデータからエンティティクラスのソースコードを生成するGradleプラグインを提供します。

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

生成コードは `codgen/src` の下に出力されます。
