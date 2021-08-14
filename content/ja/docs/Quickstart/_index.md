---
title: "Quickstart"
linkTitle: "Quickstart"
weight: 2
description: >
  Komapperの設定、ビルド、実行について最小の説明
---

## 必要要件

- JDK 8、もしくはそれ以降のバージョン
- Gradle 7.1.1

## インストール

JDKとGradleをインストールしてください。

{{< alert title="Note" >}}
[sdkman](https://sdkman.io/) を使ってインストールすることをお勧めします。
{{< /alert >}}

## セットアップ

GradleのビルドスクリプトをKotlin DSLを使って書きます。

以下のコードをbuild.gradle.ktsに記述します。

```kotlin
plugins {
  application
  id("com.google.devtools.ksp") version "1.5.21-1.0.0-beta07"
  kotlin("jvm") version "1.5.21"
}

repositories {
  mavenCentral()
}

dependencies {
  val komapperVersion = "0.15.2"
  implementation("org.komapper:komapper-starter-jdbc:$komapperVersion")
  implementation("org.komapper:komapper-dialect-h2-jdbc:$komapperVersion")
  ksp("org.komapper:komapper-processor:$komapperVersion")
}

application {
  mainClass.set("org.komapper.quickstart.ApplicationKt")
}
```

IDEで動作を確認するには追加のコードが必要ですがここでは省略します。
詳細は本ページの最後に示すリンク先のリポジトリを確認ください。

`komapper-starter`モジュール、`komapper-dialect-h2-jdbc`モジュール、`komapper-processor`モジュールのバージョンは同一でなければいけません。
また`komapper-processor`モジュールは「ksp」というキーワードを使って定義されていることに注意してください。
「ksp」は  [Kotlin Symbol Processing API](https://github.com/google/ksp) のプラグインが提供する機能で、コンパイル時のコード生成に必要です。

## アプリケーションの作成

ここではH2データベースにJDBCで接続するアプリケーションを作成します。

### ソースコード 

最初に、データベースのテーブルに対応するエンティティクラス（`Employee`）と対応関係を定義するクラス（`EmployeeDef`）を作ります。

```kotlin
data class Employee(
  val id: Int = 0,
  val name: String,
  val version: Int = 0,
  val createdAt: LocalDateTime = LocalDateTime.MIN,
  val updatedAt: LocalDateTime = LocalDateTime.MIN,
)

@KomapperEntityDef(Employee::class)
data class EmployeeDef(
  @KomapperId @KomapperAutoIncrement val id: Nothing,
  @KomapperVersion val version: Nothing,
  @KomapperCreatedAt val createdAt: Nothing,
  @KomapperUpdatedAt val updatedAt: Nothing,
) {
  companion object
}
```

次に、main関数を書きます。

```kotlin
fun main() {
  // (1) create a database instance
  val database = JdbcDatabase.create("jdbc:h2:mem:quickstart;DB_CLOSE_DELAY=-1")

  // (2) start transaction
  database.withTransaction {

    // (3) get an entity metamodel
    val e = EmployeeDef.meta

    // (4) create schema
    database.runQuery {
      SchemaDsl.create(e)
    }

    // (5) insert multiple employees at once
    database.runQuery {
      EntityDsl.insert(e).multiple(Employee(name = "AAA"), Employee(name = "BBB"))
    }

    // (6) select all
    val employees = database.runQuery {
      EntityDsl.from(e).orderBy(e.id)
    }

    // (7) print all results
    for ((i, employee) in employees.withIndex()) {
      println("RESULT $i: $employee")
    }
  }
}
```

### ビルド

ビルドをするには次のGradleコマンドを実行します。

```sh
$ gradle build
```

コマンド実行後、`build/generated/ksp/main/kotlin`ディレクトリを確認してください。
Kotlin Symbol Processing APIによって生成されたコードが存在することがわかります。

### アプリケーションの実行

アプリケーションを動かすには次のGradleコマンドを実行します。

```sh
$ gradle run
```

アプリケーションを実行するとコンソール上に次のような出力が表示されます。

```
21:00:53.099 [main] DEBUG org.komapper.SQL - create table if not exists employee (id integer not null auto_increment, name varchar(500) not null, version integer not null, created_at timestamp not null, updated_at timestamp not null, constraint pk_employee primary key(id));
21:00:53.117 [main] DEBUG org.komapper.SQL - insert into employee (name, version, created_at, updated_at) values (?, ?, ?, ?), (?, ?, ?, ?)
21:00:53.140 [main] DEBUG org.komapper.SQL - select t0_.id, t0_.name, t0_.version, t0_.created_at, t0_.updated_at from employee as t0_ order by t0_.id asc
RESULT 0: Employee(id=1, name=AAA, version=0, createdAt=2021-05-05T21:00:53.115127, updatedAt=2021-05-05T21:00:53.115127)
RESULT 1: Employee(id=2, name=BBB, version=0, createdAt=2021-05-05T21:00:53.115250, updatedAt=2021-05-05T21:00:53.115250)
```

EmployeeインスタンスにIDやタイムスタンプが設定されていることがわかります。
これらはKomapperにより自動的に設定されました。

## 完全なコードの取得

完全なコードを得るには以下のリポジトリを確認ください。

- https://github.com/komapper/komapper-quickstart

上記リンク先のリポジトリではGradle Wrapperを使っています。
したがって、Gradleをインストールしなくてもアプリケーションを動かすことができます。
本ページではビルドと実行で2つのGradleコマンドを示しましたが、Gradle Wrapperを使うにはそれぞれ次のコマンドを使ってください。

```shell
$ ./gradlew build
```

```shell
$ ./gradlew run
```