---
title: "Overview"
linkTitle: "Overview"
weight: 1
description: >
  サーバーサイドKotlinのためのシンプルでパワフルなSQLマッパー
---

## Komapperとは？

KomapperはKotlin 1.5以上に対応したサーバーサイド向けのSQLマッピングライブラリーです。

Komapperにはいくつかの強みがあります。

- JDBCとR2DBCのサポート
- コンパイル時のコード生成
- イミュータブルなクエリ
- Value Classのサポート
- Spring Bootのサポート

### JDBCとR2DBCのサポート

Komapperは [JDBC](https://jcp.org/en/jsr/detail?id=221) もしくは
[R2DBC](https://r2dbc.io/) を用いてデータベースにアクセスできます。

KomapperはKotlinコルーチンの機能を活用することでJDBCとR2DBCのどちらを使ったとしてもほとんど同じプログラミングモデルを可能にしています。

例えば、JDBCを用いるコードは次のように書けます。

```kotlin
fun main() {
    // create a Database instance
    val db = JdbcDatabase.create("jdbc:h2:mem:example;DB_CLOSE_DELAY=-1")

    // get a metamodel
    val a = AddressDef.meta

    // execute simple CRUD operations in a transaction
    db.withTransaction {
        // INSERT
        val newAddress = db.runQuery {
            EntityDsl.insert(a).single(Address(street = "street A"))
        }

        // SELECT
        val address1 = db.runQuery {
            EntityDsl.from(a).where { a.id eq newAddress.id }.first()
        }
    }
}
```

一方でR2DBCを使うコードは次のように書けます。（上述のJDBC版との違いがわかるでしょうか？）

```kotlin
fun main() = runBlocking {
    // create a Database instance
    val db = R2dbcDatabase.create("r2dbc:h2:mem:///example;DB_CLOSE_DELAY=-1")

    // get a metamodel
    val a = AddressDef.meta

    // execute simple CRUD operations in a transaction
    db.withTransaction {
        // INSERT
        val newAddress = db.runQuery {
            EntityDsl.insert(a).single(Address(street = "street A"))
        }

        // SELECT
        val address1 = db.runQuery {
            EntityDsl.from(a).where { a.id eq newAddress.id }.first()
        }
    }
}
```

動作する完全なコードについては [komapper-examples](https://github.com/komapper/komapper-examples)
リポジトリ直下のconsole-jdbcとconsole-r2dbcのサブプロジェクトを参照ください。

### コンパイル時のコード生成

Komapperは [Kotlin Symbol Processing API](https://github.com/google/ksp)
を使ってコンパイル時にデータベースアクセスに必要なメタモデル（テーブルやカラムの情報）をKotlinのソースコードとして生成します。

この仕組みによりKomapperは実行時にリフレクションを用いたりデータベースからメタデータを読み取ったりする必要がありません。
そのため実行時の信頼性とパフォーマンスが向上します。

コード生成はアノテーションの読み取りによって行われます。
例えば、`Address`クラスを`ADDRESS`テーブルにマッピングさせる場合次のように記述できます。

```kotlin
data class Address(
    val id: Int,
    val street: String,
    val version: Int
)

@KomapperEntityDef(Address::class)
data class AddressDef(
    @KomapperId val id: Nothing,
    @KomapperVersion val version: Nothing,
) {
    companion object
}
```

また、生成されたメタモデルを使ってタイプセーフにクエリを組み立てられます。

```kotlin
// get a generated metamodel
val a = AddressDef.meta

// define a query
val query = EntityDsl.from(e).where { a.street eq "STREET 101" }.orderBy(a.id)
```

### イミュータブルなクエリ

Komapperのクエリは実質的にイミュータブルです。
状態の共有に伴う不具合を心配することなく安全に再利用できます。

```kotlin
// get a generated metamodel
val a = AddressDef.meta

// define queries
val query1 = EntityDsl.from(a)
val query2 = query1.where { a.id eq 1 }
val query3 = query2.where { or { a.id eq 2 } }.orderBy(a.street)

// issue "select * from address"
db.runQuery { query1 }
// issue "select * from address where id = 1"
db.runQuery { query2 }
// issue "select * from address where id = 1 or id = 2 order by street"
db.runQuery { query3 }
```

### Value Classのサポート

Kotlin 1.5から導入されたValue Classをエンティティクラスのプロパティとして利用できます。 利用に当たって特別な設定は不要です。

```kotlin
@JvmInline
value class Age(val value: Int)

data class Employee(val id: Int = 0, val name: String, val age: Age)

@KomapperEntityDef(Employee::class)
data class EmployeeDef(@KomapperId @KomapperAutoIncrement val id: Nothing) {
    companion object
}
```

### Spring Bootのサポート

KomapperはSpring Bootとの組み合わせを容易にするstarterを提供します。

例えば、JDBCを使ったデータアクセスをSpring Bootと組み合わせて行いたい場合、
Gradleのdependenciesブロックに次のような設定をするだけで必要なライブラリの依存性が解決されSpring Boot管理のデータソースやトランザクションと連動します。

```kotlin
val komapperVersion: String by project

dependencies {
    implementation("org.komapper:komapper-spring-boot-starter-jdbc:$komapperVersion")
}
```

動作する完全なコードについては [komapper-examples](https://github.com/komapper/komapper-examples)
リポジトリ直下のspring-boot-jdbcとspring-boot-r2dbcのサブプロジェクトを参照ください。

## サポートするデータベース

現在、下記の4つのデータベースをサポートしています。

| データベース         | バージョン | JDBC | R2DBC |
|--------------------|:--------:|:----:|:-----:|
| H2 Database Engine |  1.4.200 |  v   |   v   |
| MariaDB            |     10.6 |  v   |   v   |
| MySQL              |      8.0 |  v   |   v   |
| PostgreSQL         |     13.0 |  v   |   v   |

バージョンはサポートする最小バージョンを表していますが、より小さいバージョンでも動作することがあります。

## 次に役立ちそうなドキュメント

* [Quickstart](/ja/docs/quickstart/): Komapperを始める
* [Examples](/ja/docs/examples/): サンプルコードを見る

