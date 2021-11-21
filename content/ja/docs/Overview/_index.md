---
title: "Overview"
linkTitle: "Overview"
weight: 1
description: >
  サーバーサイドKotlinのためのシンプルでパワフルなSQLマッパー
---

## Komapperとは？ {#what-is-it}

KomapperはKotlin 1.5.31 以上に対応したサーバーサイド向けのSQLマッピングライブラリーです。

Komapperにはいくつかの強みがあります。

- JDBCとR2DBCのサポート
- コンパイル時のコード生成
- 不変で合成可能なクエリ
- Value Classのサポート
- Spring Bootのサポート

### JDBCとR2DBCのサポート {#support-for-both-jdbc-and-r2dbc}

Komapperは [JDBC](https://jcp.org/en/jsr/detail?id=221) もしくは
[R2DBC](https://r2dbc.io/) を用いてデータベースにアクセスできます。

KomapperはKotlinコルーチンの機能を活用することでJDBCとR2DBCの違いの大部分を吸収しています。

例えば、JDBCを用いるコードは次のように書けます。

```kotlin
fun main() {
    // create a Database instance
    val db = JdbcDatabase.create("jdbc:h2:mem:example;DB_CLOSE_DELAY=-1")

    // get a metamodel
    val a = Meta.address

    // execute simple CRUD operations in a transaction
    db.withTransaction {
        // create a schema
        db.runQuery {
            SchemaDsl.create(a)
        }

        // INSERT
        val newAddress = db.runQuery {
            QueryDsl.insert(a).single(Address(street = "street A"))
        }

        // SELECT
        val address1 = db.runQuery {
            QueryDsl.from(a).where { a.id eq newAddress.id }.first()
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
    val a = Meta.address

    // execute simple CRUD operations in a transaction
    db.withTransaction {
        // create a schema
        db.runQuery {
            SchemaDsl.create(a)
        }

        // INSERT
        val newAddress = db.runQuery {
            QueryDsl.insert(a).single(Address(street = "street A"))
        }

        // SELECT
        val address1 = db.runQuery {
            QueryDsl.from(a).where { a.id eq newAddress.id }.first()
        }
    }
}
```

見た目上の違いは以下の2点のみです。

1. R2DBC版では`main`関数の処理全体を`runBlocking`で囲んでいる
2. `db`インスタンスの生成方法が異なる

動作する完全なコードについては [komapper-examples](https://github.com/komapper/komapper-examples)
リポジトリ直下のconsole-jdbcとconsole-r2dbcのプロジェクトを参照ください。

### コンパイル時のコード生成 {#code-generation-at-compile-time}

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
)
```

上述のコードから次のようなコードがコンパイル時に生成されます。

```kotlin
@org.komapper.core.dsl.metamodel.EntityMetamodelImplementor
class _AddressDef private constructor(table: String = "address", catalog: String = "", schema: String = "", alwaysQuote: Boolean = false, disableSequenceAssignment: Boolean = false, declarations: List<org.komapper.core.dsl.metamodel.MetamodelDeclaration<Address, kotlin.Int, _AddressDef>> = emptyList()) : org.komapper.core.dsl.metamodel.EntityMetamodel<Address, kotlin.Int, _AddressDef> {
    private val __tableName = table
    private val __catalogName = catalog
    private val __schemaName = schema
    private val __alwaysQuote = alwaysQuote
    private val __disableSequenceAssignment = disableSequenceAssignment
    private val __declarations = declarations
    private object __EntityDescriptor {
        val id = org.komapper.core.dsl.metamodel.PropertyDescriptor<Address, kotlin.Int, kotlin.Int>(kotlin.Int::class, kotlin.Int::class, "id", "id", false, { it.id }, { e, v -> e.copy(id = v) }, { it }, { it }, false)
        val street = org.komapper.core.dsl.metamodel.PropertyDescriptor<Address, kotlin.String, kotlin.String>(kotlin.String::class, kotlin.String::class, "street", "street", false, { it.street }, { e, v -> e.copy(street = v) }, { it }, { it }, false)
        val version = org.komapper.core.dsl.metamodel.PropertyDescriptor<Address, kotlin.Int, kotlin.Int>(kotlin.Int::class, kotlin.Int::class, "version", "version", false, { it.version }, { e, v -> e.copy(version = v) }, { it }, { it }, false)
    }
    val id: org.komapper.core.dsl.metamodel.PropertyMetamodel<Address, kotlin.Int, kotlin.Int> by lazy { org.komapper.core.dsl.metamodel.PropertyMetamodelImpl(this, __EntityDescriptor.id) }
    val street: org.komapper.core.dsl.metamodel.PropertyMetamodel<Address, kotlin.String, kotlin.String> by lazy { org.komapper.core.dsl.metamodel.PropertyMetamodelImpl(this, __EntityDescriptor.street) }
    val version: org.komapper.core.dsl.metamodel.PropertyMetamodel<Address, kotlin.Int, kotlin.Int> by lazy { org.komapper.core.dsl.metamodel.PropertyMetamodelImpl(this, __EntityDescriptor.version) }
    override fun klass() = Address::class
    override fun tableName() = __tableName
    override fun catalogName() = __catalogName
    override fun schemaName() = __schemaName
    override fun alwaysQuote() = __alwaysQuote
    override fun declarations() = __declarations
    override fun idAssignment(): org.komapper.core.dsl.metamodel.IdAssignment<Address>? = null
    override fun idProperties(): List<org.komapper.core.dsl.metamodel.PropertyMetamodel<Address, *, *>> = listOf(id)
    override fun versionProperty(): org.komapper.core.dsl.metamodel.PropertyMetamodel<Address, *, *>? = version
    override fun createdAtProperty(): org.komapper.core.dsl.metamodel.PropertyMetamodel<Address, *, *>? = null
    override fun updatedAtProperty(): org.komapper.core.dsl.metamodel.PropertyMetamodel<Address, *, *>? = null
    override fun properties(): List<org.komapper.core.dsl.metamodel.PropertyMetamodel<Address, *, *>> = listOf(
        id,
        street,
        version)
    override fun toId(generatedKey: Long): kotlin.Int? = this.id.wrap(generatedKey.toInt())
    override fun getId(e: Address): kotlin.Int = e.id
    override fun preInsert(e: Address, c: java.time.Clock): Address = e.copy(version = e.version)
    override fun preUpdate(e: Address, c: java.time.Clock): Address = e
    override fun postUpdate(e: Address): Address = e.copy(version = e.version.inc())
    override fun newEntity(m: Map<org.komapper.core.dsl.metamodel.PropertyMetamodel<*, *, *>, Any?>) = Address(
        id = m[this.id] as kotlin.Int,
        street = m[this.street] as kotlin.String,
        version = m[this.version] as kotlin.Int)
    override fun newMetamodel(table: String, catalog: String, schema: String, alwaysQuote: Boolean, disableSequenceAssignment: Boolean, declarations: List<org.komapper.core.dsl.metamodel.MetamodelDeclaration<Address, kotlin.Int, _AddressDef>>) = _AddressDef(table, catalog, schema, alwaysQuote, disableSequenceAssignment, declarations)
    fun clone(table: String = "address", catalog: String = "", schema: String = "", alwaysQuote: Boolean = false, disableSequenceAssignment: Boolean = false, declarations: List<org.komapper.core.dsl.metamodel.MetamodelDeclaration<Address, kotlin.Int, _AddressDef>> = emptyList()) = _AddressDef(table, catalog, schema, alwaysQuote, disableSequenceAssignment, declarations)
    companion object {
        val address = _AddressDef()
    }
}

val org.komapper.core.dsl.Meta.address get() = _AddressDef.address
```

アプリーケーションでは、生成されたメタモデルを使ってタイプセーフにクエリを組み立てられます。

```kotlin
// get a generated metamodel
val a = Meta.address

// define a query
val query = QueryDsl.from(e).where { a.street eq "STREET 101" }.orderBy(a.id)
```

アノテーションを使ったマッピングに関する詳細は
[Entity Class]({{< relref "../Reference/entity-class" >}}) を、
コンパイル時のアノテーション処理に関する詳細は
[Annotation Processing]({{< relref "../Reference/annotation-processing" >}}) を参照ください。

### 不変で合成可能なクエリ {#immutable-and-composable-queries}

Komapperのクエリは実質的に不変（immutable）です。
従って、状態の共有に伴う不具合を心配することなく安全に合成可能（composable）です。

```kotlin
// get a generated metamodel
val a = Meta.address

// define queries
val query1 = QueryDsl.from(a)
val query2 = query1.where { a.id eq 1 }
val query3 = query2.where { or { a.id eq 2 } }.orderBy(a.street)
val query4 = query1.zip(query2)
    
// issue "select * from address"
val list1 = db.runQuery { query1 }
// issue "select * from address where id = 1"
val list2 = db.runQuery { query2 }
// issue "select * from address where id = 1 or id = 2 order by street"
val list3 = db.runQuery { query3 }
// issue "select * from address" and "select * from address where id = 1"
val (list4, list5) = db.runQuery { query4 }
```

`where`関数などを使って既存のクエリを基に他のクエリを作るだけでなく、
`zip`関数などを使って複数のクエリを単一のクエリに合成することもできます。

クエリの合成に関する詳細は [Composition]({{< relref "../Reference/Query/composition" >}}) を参照ください。

### Value Classのサポート {#support-for-kotlin-value-classes}

Value Classをエンティティクラスのプロパティとして利用できます。 利用に当たって特別な設定は不要です。

```kotlin
@JvmInline
value class Age(val value: Int)

data class Employee(val id: Int = 0, val name: String, val age: Age)

@KomapperEntityDef(Employee::class)
data class EmployeeDef(@KomapperId @KomapperAutoIncrement val id: Nothing)
```

クエリでの利用例です。

```kotlin
val e = Meta.employee
val query = QueryDsl.from(e).where { e.age greaterEq Age(40) }
val seniorEmployeeList = db.runQuery { query }
```

### Spring Bootのサポート {#easy-spring-boot-integration}

KomapperはSpring Bootとの組み合わせを容易にするstarterを提供します。

例えば、JDBCを使ったデータアクセスをSpring Bootと組み合わせて行いたい場合、
Gradleのdependenciesブロックに次のような設定をするだけで必要なライブラリの依存性が解決されSpring Boot管理のデータソースやトランザクションと連動します。

```kotlin
val komapperVersion: String by project

dependencies {
    implementation("org.komapper:komapper-spring-boot-starter-jdbc:$komapperVersion")
    implementation("org.komapper:komapper-dialect-h2-jdbc:$komapperVersion")
}
```

動作する完全なコードについては [komapper-examples](https://github.com/komapper/komapper-examples)
リポジトリ直下のspring-boot-jdbcとspring-boot-r2dbcのプロジェクトを参照ください。

関連情報として [Starter]({{< relref "../Reference/Starter" >}}) も参照ください。

## サポートするデータベース {#supported-database}

現在、下記の4つのデータベースをサポートしています。

| データベース         | バージョン | JDBC | R2DBC |
|--------------------|:--------:|:----:|:-----:|
| H2 Database Engine |  1.4.200 |  v   |   v   |
| MariaDB            |     10.6 |  v   |   v   |
| MySQL              |      8.0 |  v   |   v   |
| PostgreSQL         |     13.0 |  v   |   v   |

バージョンはサポートする最小バージョンを表していますが、より小さいバージョンでも動作することがあります。

## 次に見るべきドキュメント {#where-should-i-go-next}

* [Quickstart]({{< relref "../Quickstart" >}})
* [Examples]({{< relref "../Examples" >}})

