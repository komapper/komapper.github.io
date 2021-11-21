---
title: "Entity Class"
linkTitle: "Entity Class"
weight: 20
description: >
  エンティティクラス
---

## 概要 {#overview}

Komapperでは、データベースのテーブルに対応するKotlinクラスをエンティティクラスと呼びます。

エンティティクラスをテーブルにマッピングさせるには別途アノテーションを用いたマッピング定義が必要です。

マッピング定義はコンパイル時に解析されその結果がメタモデルとなります。
メタモデルはクエリの構築や実行で利用されます。

## エンティティクラスの定義 {#entity-class-definition}

エンティティクラスは次の要件を満たさなければいけません。

- Data Classである
- 可視性がprivateでない
- 型パラメータを持っていない

例えば、次のようなテーブル定義があるとします。

```sql
create table if not exists ADDRESS (
  ADDRESS_ID integer not null auto_increment,
  STREET varchar(500) not null,
  VERSION integer not null,
  CREATED_AT timestamp,
  UPDATED_AT timestamp,
  constraint pk_ADDRESS primary key(ADDRESS_ID)
);
```

上記のテーブル定義に対応するエンティティクラス定義は次のようになります。

```kotlin
data class Address(
  val id: Int = 0,
  val street: String,
  val version: Int = 0,
  val createdAt: LocalDateTime? = null,
  val updatedAt: LocalDateTime? = null,
)
```

プロパティの型（Kotlinの型）とカラムの型（データベースの型）の対応関係は [Dialect]({{< relref "Dialect" >}}) で定義されます。

## マッピング定義 {#mapping-definition}

マッピング定義の作成方法は2種類あります。

- エンティティクラス自身がマッピング定義を持つ方法（セルフマッピング）
- エンティティクラスとは別にエンティティ定義クラスを作成する方法（分離マッピング）

同一のエンティティクラスに対して1つの方法のみ適用できます。

### セルフマッピング {#self-mapping-definition}

このときエンティティクラスは前のセクションで説明した要件に加えて次の条件を満たさなければいけません。

- `@KomapperEntity`で注釈される

例えば、前のセクションで示した`Address`クラスにこの方法を適用すると次のように変更できます。

```kotlin
@KomapperEntity
data class Address(
  @KomapperId
  @KomapperAutoIncrement
  @KomapperColumn(name = "ADDRESS_ID")
  val id: Int = 0,
  val street: String,
  @KomapperVersion
  val version: Int = 0,
  @KomapperCreatedAt
  val createdAt: LocalDateTime? = null,
  @KomapperUpdatedAt
  val updatedAt: LocalDateTime? = null,
)
```

### 分離マッピング {#separate-mapping-definition}

エンティティ定義クラスは次の要件を満たさなければいけません。

- Data Classである
- 可視性がprivateでない
- 型パラメータを持っていない
- `@KomapperEntityDef`で注釈され引数でエンティティクラスを受け取る
- エンティティクラスに定義されたプロパティと異なる名前のプロパティを持たない

例えば、前のセクションで示した`Address`クラスに対するエンティティ定義クラスは次のように記述できます。

```kotlin
@KomapperEntityDef(Address::class)
data class AddressDef(
  @KomapperId
  @KomapperAutoIncrement
  @KomapperColumn(name = "ADDRESS_ID")
  val id: Nothing,
  @KomapperVersion
  val version: Nothing,
  @KomapperCreatedAt
  val createdAt: Nothing,
  @KomapperUpdatedAt
  val updatedAt: Nothing,
)
```

エンティティ定義クラスは、参照するエンティティクラスに定義された同名のプロパティに対し様々な設定ができます。
定義されないプロパティに対してはデフォルトのマッピング定義が適用されます。
上記の例ではエンティティクラスに登場する`street`プロパティがエンティティ定義クラスには登場しませんが、
`street`プロパティにはテーブル上の`STREET`カラムにマッピングされます。

エンティティ定義クラスのプロパティの型に制約はありません。上記の例では`Nothing`を使っています。

## メタモデル {#metamodel}

マッピング定義からは下記のようなメタモデルが生成されます。

```kotlin
@org.komapper.core.dsl.metamodel.EntityMetamodelImplementor
class _Address private constructor(table: String = "address", catalog: String = "", schema: String = "", alwaysQuote: Boolean = false, disableSequenceAssignment: Boolean = false, declarations: List<org.komapper.core.dsl.metamodel.MetamodelDeclaration<Address, kotlin.Int, _Address>> = emptyList()) : org.komapper.core.dsl.metamodel.EntityMetamodel<Address, kotlin.Int, _Address> {
    private val __tableName = table
    private val __catalogName = catalog
    private val __schemaName = schema
    private val __alwaysQuote = alwaysQuote
    private val __disableSequenceAssignment = disableSequenceAssignment
    private val __declarations = declarations
    private object __EntityDescriptor {
        val id = org.komapper.core.dsl.metamodel.PropertyDescriptor<Address, kotlin.Int, kotlin.Int>(kotlin.Int::class, kotlin.Int::class, "id", "ADDRESS_ID", false, { it.id }, { e, v -> e.copy(id = v) }, { it }, { it }, false)
        val street = org.komapper.core.dsl.metamodel.PropertyDescriptor<Address, kotlin.String, kotlin.String>(kotlin.String::class, kotlin.String::class, "street", "street", false, { it.street }, { e, v -> e.copy(street = v) }, { it }, { it }, false)
        val version = org.komapper.core.dsl.metamodel.PropertyDescriptor<Address, kotlin.Int, kotlin.Int>(kotlin.Int::class, kotlin.Int::class, "version", "version", false, { it.version }, { e, v -> e.copy(version = v) }, { it }, { it }, false)
        val createdAt = org.komapper.core.dsl.metamodel.PropertyDescriptor<Address, java.time.LocalDateTime, java.time.LocalDateTime>(java.time.LocalDateTime::class, java.time.LocalDateTime::class, "createdAt", "created_at", false, { it.createdAt }, { e, v -> e.copy(createdAt = v) }, { it }, { it }, true)
        val updatedAt = org.komapper.core.dsl.metamodel.PropertyDescriptor<Address, java.time.LocalDateTime, java.time.LocalDateTime>(java.time.LocalDateTime::class, java.time.LocalDateTime::class, "updatedAt", "updated_at", false, { it.updatedAt }, { e, v -> e.copy(updatedAt = v) }, { it }, { it }, true)
    }
    val id: org.komapper.core.dsl.metamodel.PropertyMetamodel<Address, kotlin.Int, kotlin.Int> by lazy { org.komapper.core.dsl.metamodel.PropertyMetamodelImpl(this, __EntityDescriptor.id) }
    val street: org.komapper.core.dsl.metamodel.PropertyMetamodel<Address, kotlin.String, kotlin.String> by lazy { org.komapper.core.dsl.metamodel.PropertyMetamodelImpl(this, __EntityDescriptor.street) }
    val version: org.komapper.core.dsl.metamodel.PropertyMetamodel<Address, kotlin.Int, kotlin.Int> by lazy { org.komapper.core.dsl.metamodel.PropertyMetamodelImpl(this, __EntityDescriptor.version) }
    val createdAt: org.komapper.core.dsl.metamodel.PropertyMetamodel<Address, java.time.LocalDateTime, java.time.LocalDateTime> by lazy { org.komapper.core.dsl.metamodel.PropertyMetamodelImpl(this, __EntityDescriptor.createdAt) }
    val updatedAt: org.komapper.core.dsl.metamodel.PropertyMetamodel<Address, java.time.LocalDateTime, java.time.LocalDateTime> by lazy { org.komapper.core.dsl.metamodel.PropertyMetamodelImpl(this, __EntityDescriptor.updatedAt) }
    override fun klass() = Address::class
    override fun tableName() = __tableName
    override fun catalogName() = __catalogName
    override fun schemaName() = __schemaName
    override fun alwaysQuote() = __alwaysQuote
    override fun declarations() = __declarations
    override fun idAssignment(): org.komapper.core.dsl.metamodel.IdAssignment<Address>? = org.komapper.core.dsl.metamodel.IdAssignment.AutoIncrement(::toId, id)
    override fun idProperties(): List<org.komapper.core.dsl.metamodel.PropertyMetamodel<Address, *, *>> = listOf(id)
    override fun versionProperty(): org.komapper.core.dsl.metamodel.PropertyMetamodel<Address, *, *>? = version
    override fun createdAtProperty(): org.komapper.core.dsl.metamodel.PropertyMetamodel<Address, *, *>? = createdAt
    override fun updatedAtProperty(): org.komapper.core.dsl.metamodel.PropertyMetamodel<Address, *, *>? = updatedAt
    override fun properties(): List<org.komapper.core.dsl.metamodel.PropertyMetamodel<Address, *, *>> = listOf(
        id,
        street,
        version,
        createdAt,
        updatedAt)
    override fun toId(generatedKey: Long): kotlin.Int? = this.id.wrap(generatedKey.toInt())
    override fun getId(e: Address): kotlin.Int = e.id
    override fun preInsert(e: Address, c: java.time.Clock): Address = e.copy(version = e.version, createdAt = java.time.LocalDateTime.now(c), updatedAt = java.time.LocalDateTime.now(c))
    override fun preUpdate(e: Address, c: java.time.Clock): Address = e.copy(updatedAt = java.time.LocalDateTime.now(c))
    override fun postUpdate(e: Address): Address = e.copy(version = e.version.inc())
    override fun newEntity(m: Map<org.komapper.core.dsl.metamodel.PropertyMetamodel<*, *, *>, Any?>) = Address(
        id = m[this.id] as kotlin.Int,
        street = m[this.street] as kotlin.String,
        version = m[this.version] as kotlin.Int,
        createdAt = m[this.createdAt] as java.time.LocalDateTime?,
        updatedAt = m[this.updatedAt] as java.time.LocalDateTime?)
    override fun newMetamodel(table: String, catalog: String, schema: String, alwaysQuote: Boolean, disableSequenceAssignment: Boolean, declarations: List<org.komapper.core.dsl.metamodel.MetamodelDeclaration<Address, kotlin.Int, _Address>>) = _Address(table, catalog, schema, alwaysQuote, disableSequenceAssignment, declarations)
    fun clone(table: String = "address", catalog: String = "", schema: String = "", alwaysQuote: Boolean = false, disableSequenceAssignment: Boolean = false, declarations: List<org.komapper.core.dsl.metamodel.MetamodelDeclaration<Address, kotlin.Int, _Address>> = emptyList()) = _Address(table, catalog, schema, alwaysQuote, disableSequenceAssignment, declarations)
    companion object {
        val address = _Address()
    }
}

val org.komapper.core.dsl.Meta.address get() = _Address.address
```

生成されたメタモデルは`org.komapper.core.dsl.Meta`オブジェクトの拡張プロパティを介してアプリケーションに公開されます。

```kotlin
// get a generated metamodel
val a = Meta.address

// define a query
val query = QueryDsl.from(e).where { a.street eq "STREET 101" }.orderBy(a.id)
```

### aliases {#metamodel-aliases}

上述の例では拡張プロパティの名前は`address`ですが、これは`@KomapperEntity`や`@KomapperEntityDef`の`aliases`プロパティで変更できます。

```kotlin
@KomapperEntity(aliases = ["addr"])
data class Address(
  ...
)
```

`aliases`プロパティには複数の名前を指定できます。
その際、名前ごとに異なるインスタンスとして公開されます。
複数の異なるインスタンスが必要となる主なユースケースは自己結合です。

```kotlin
@KomapperEntity(aliases = ["employee", "manager"])
data class Employee(
  ...
)
```

例えば、マネージャーの一覧を取得するには上記のように複数の名前をつけた上で以下のようなクエリを作ります。

```kotlin
val e = Meta.employee
val m = Meta.manager
val query: Query<List<Employee>> = QueryDsl.from(m)
  .distinct()
  .innerJoin(e) {
    m.employeeId eq e.managerId
  }
```

なお、事前に名前を持ったメタモデルを定義しない場合でも、`clone`関数を使えば同じことが実現可能です。

```kotlin
val e = Meta.employee
val m = e.clone()
val query: Query<List<Employee>> = QueryDsl.from(m)
  .distinct()
  .innerJoin(e) {
    m.employeeId eq e.managerId
  }
```

### clone {#metamodel-clone}

`clone`関数を使って既存のメタモデルを基に別のメタモデルを生成できます。
主なユースケースは、データ構造が同じで名前だけが異なるテーブルにデータをコピーする場合です。

```kotlin
val a = Meta.address
val archive = a.clone(table = "ADDRESS_ARCHIVE")
val query = QueryDsl.insert(archive).select {
  QueryDsl.from(a).where { a.id between 1..5 }
}
```

cloneしたメタモデルを他のメタモデルと同様に公開したい場合は、
オブジェクトでインスタンスを保持した上で`Meta`オブジェクトの拡張プロパティを定義してください。

```kotlin
object MetamodelHolder {
  private val _addressArchive = Meta.address.clone(table = "ADDRESS_ARCHIVE")
  val Meta.addressArchive get() = _addressArchive
}
```

### define {#metamodel-define}

`define`関数を使ってメタモデルに対しデフォルトのWHERE句を定義できます。
あるメタモデルを使う際に必ず同じ検索条件を用いたいケースで便利です。

```kotlin
object MetamodelHolder {
  private val _bostonOnly = Meta.department.define { d ->
    where {
      d.location eq "BOSTON"
    }
  }
  val Meta.bostonOnly get() = _bostonOnly
}
```

上記のように`define`関数で作ったメモモデルを下記のように利用すると、
クエリ上でWHERE句を指定しなくてもWHERE句をもったSQLが生成されます。

```kotlin
val d = Meta.bostonOnly
val query = QueryDsl.from(d)
/*
select * from department as d where d.location = 'BOSTON'
*/
```

defineしたメタモデルを結合先としてクエリに含めた場合もこの機能は有効です。

```kotlin
val e = Meta.employee
val d = Meta.bostonOnly
val query = QueryDsl.from(e).innerJoin(d) {
  e.departmentId eq d.departmentId
}
/*
select * from employee as e inner join department as d on e.department_id = d.department_id where d.location = 'BOSTON'
*/
```

デフォルトのWHERE句にパラメータを渡したい場合は、拡張関数として定義することもできます。
ただし、メタモデルが毎回異なるインスタンスとなることは注意してください。

```kotlin
object MetamodelHolder {
    fun Meta.locationSpecificDepartment(value: String) = Meta.department.define { d ->
        where {
            d.location eq value
        }
    }
}
```

上記の拡張関数を呼び出す例です。

```kotlin
val d = Meta.locationSpecificDepartment("NEW YORK")
val query = QueryDsl.from(d)
val list = db.runQuery { query }
/*
select * from department as d where d.location = 'NEW YORK'
*/
```

## クラスに付与するアノテーション一覧 {#annotation-list-for-class}

ここで説明するアノテーションは全て`org.komapper.annotation`パッケージに属します。

### @KomapperEntity

エンティティクラスがマッピング定義を持つことを表します。

### @KomapperEntityDef

エンティティ定義クラスであることを表します。

### @KomapperTable

エンティティクラスとマッピングするテーブルの名前を明示的に指定します。

```kotlin
@KomapperEntityDef(Address::class)
@KomapperTable("ADDRESS", schema = "ACCOUNT", alwaysQuote = true)
data class AddressDef(
  ...
)
```

`catalog`プロパティや`schema`プロパティにはテーブルが属するカタログやスキーマの名前を指定できます。

`alwaysQuote`プロパティに`true`を設定すると生成されるSQLの識別子が引用符で囲まれます。

このアノテーションでテーブルの名前を指定しない場合、アノテーション処理の`komapper.namingStrategy`オプションに従って名前が解決されます。
以下のドキュメントも参照ください。

- [オプション]({{< relref "annotation-processing#options" >}})

## プロパティに付与するアノテーション一覧 {#annotation-list-for-property}

ここで説明するアノテーションは全て`org.komapper.annotation`パッケージに属します。

### @KomapperId

プライマリーキーであることを表します。
エンティティクラスのマッピングを行う上でこのアノテーションの存在は必須です。

### @KomapperSequence

プライマリキーがデータベースのシーケンスで生成されることを表します。
必ず`@KomapperId`と一緒に付与する必要があります。

このアノテーションを付与するプロパティの型は次のいずれかでなければいけません。

- Int
- Long
- UInt
- 上述の型をプロパティとして持つValue Class

```kotlin
@KomapperId
@KomapperSequence(name = "ADDRESS_SEQ", startWith = 1, incrementBy = 100)
val id: Int
```

`name`プロパティにはシーケンスの名前を指定しなければいけません。カタログやスキーマの指定もできます。

`startWith`プロパティと`incrementBy`プロパティの値はシーケンス定義に合わせなければいけません。

`alwaysQuote`プロパティに`true`を設定すると生成されるSQLの識別子が引用符で囲まれます。

### @KomapperAutoIncrement

プライマリーキーがデータベースの自動インクリメント機能で生成されることを表します。
必ず`@KomapperId`と一緒に付与する必要があります。

このアノテーションを付与するプロパティの型は次のいずれかでなければいけません。

- Int
- Long
- UInt
- 上述の型をプロパティとして持つValue Class

### @KomapperVersion

楽観的排他制御に使われるバージョン番号であることを表します。

このアノテーションを付与すると、 [QueryDsl]({{< relref "query/query-dsl.md" >}}) のUPDATE処理やDELETE処理で楽観的排他制御が行われます。
つまり、WHERE句にバージョン番号チェックが含まれ処理件数が0の場合に例外がスローされます。

このアノテーションを付与するプロパティの型は次のいずれかでなければいけません。

- Int
- Long
- UInt
- 上述の型をプロパティとして持つValue Class

### @KomapperCreatedAt

生成時のタイムスタンプであることを表します。

このアノテーションを付与すると、
[QueryDsl]({{< relref "query/query-dsl.md" >}}) のINSERT処理にてタイムスタンプがプロパティに設定されます。

このアノテーションを付与するプロパティの型は次のいずれかでなければいけません。

- java.time.LocalDateTime
- java.time.OffsetDateTime
- 上述の型をプロパティとして持つValue Class

### @KomapperUpdatedAt

更新時のタイムスタンプであることを表します。

このアノテーションを付与すると、
[QueryDsl]({{< relref "query/query-dsl.md" >}}) のINSERT処理とUPDATE処理にてタイムスタンプがプロパティに設定されます。

このアノテーションを付与するプロパティの型は次のいずれかでなければいけません。

- java.time.LocalDateTime
- java.time.OffsetDateTime
- 上述の型をプロパティとして持つValue Class

### @KomapperColumn

プロパティとマッピングするカラムの名前を明示的に指定します。

```kotlin
@KomapperColumn(name = "ADDRESS_ID", alwaysQuote = true)
val id: Nothing
```

`alwaysQuote`プロパティに`true`を設定すると生成されるSQLの識別子が引用符で囲まれます。

このアノテーションでカラムの名前を指定しない場合、アノテーション処理の`komapper.namingStrategy`オプションに従って名前が解決されます。
以下のドキュメントも参照ください。

- [オプション]({{< relref "annotation-processing#options" >}})

### @KomapperIgnore

マッピングの対象外であることを表します。
