---
title: "Quickstart"
linkTitle: "Quickstart"
weight: 2
description: >
  最小の構成でKomapperを動かす
---

## 必要要件

- JDK 8、もしくはそれ以降のバージョン
- Kotlin 15.21、もしくはそれ以降のバージョン
- Gradle 7、もしくはそれ以降のバージョン

## インストール

JDKのインストールには [sdkman](https://sdkman.io/) の利用をお勧めします。

## セットアップ

GradleのビルドスクリプトをKotlin DSLを使って書きます。

最初に、以下のコードをsettings.gradle.ktsに記述します。

```kotlin
pluginManagement {
  val kotlinVersion: String by settings
  val kspVersion: String by settings
  repositories {
    gradlePluginPortal()
    google()
  }
  plugins {
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    id("com.google.devtools.ksp") version kspVersion
  }
}

rootProject.name = "komapper-quickstart"
```

pluginManagementブロックではKotlinと [Kotlin Symbol Processing API](https://github.com/google/ksp) のプラグインのバージョンを指定します。

次に、以下のコードをbuild.gradle.ktsに記述します。

```kotlin
plugins {
  application
  idea
  kotlin("jvm")
  id("com.google.devtools.ksp")
}

repositories {
  mavenCentral()
  google()
}

dependencies {
  val komapperVersion: String by project
  implementation("org.komapper:komapper-starter:$komapperVersion")
  ksp("org.komapper:komapper-processor:$komapperVersion")
}
```
`komapper-starter`モジュールと`komapper-processor`モジュールのバージョンは同一でなければいけません。
また`komapper-processor`モジュールは「ksp」というキーワードを使って定義されていることに注意してください。
「ksp」はKotlin Symbol Processing APIのプラグインが提供する機能で、コンパイル時のコード生成するために必要です。

## アプリケーションの作成

ここではH2データベースに接続するアプリケーションを作成します。

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

ビルドをするには次のコマンドを実行します。

```sh
$ ./grdlew build
```

コマンド実行後、`build/generated/ksp/main/kotlin`ディレクトリを確認してください。
Kotlin Symbol Processing APIによって生成されたコードが存在することがわかります。

### アプリケーションの実行

アプリケーションを動かすには次のコマンドを実行します。

```sh
$ ./grdlew run
```

アプリケーションの実行によりコンソール上に次のような出力が現われます。

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
