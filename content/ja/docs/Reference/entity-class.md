---
title: "Entity Class"
linkTitle: "Entity Class"
weight: 20
description: >
  エンティティクラス
---

## 概要

Komapperでは、データベースのテーブルに対応するKotlinクラスをエンティティクラスと呼びます。

エンティティクラスをテーブルにマッピングさせるには別途アノテーションを用いたマッピング定義が必要です。

エンティティクラスのプロパティとテーブルのカラムのマッピングには型の制約があります。

## エンティティクラスの定義

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

## エンティティクラスのマッピング定義

マッピング定義の作成方法は2種類あります。

- エンティティクラス自身がマッピング定義を持つ方法
- エンティティクラスとは別にエンティティ定義クラスを作成する方法

同一のエンティティクラスに対して1つの方法のみ適用できます。

### エンティティクラス自身がマッピング定義を持つ方法

このときエンティティクラスは前のセクションで説明した要件に加えて次の条件を満たさなければいけません。

- `@KomapperEntity`で注釈される
- `companion object`を持つ

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
) {
  companion object
}
```

### エンティティクラスとは別にエンティティ定義クラスを作成する方法

エンティティ定義クラスは次の要件を満たさなければいけません。

- Data Classである
- 可視性がprivateでない
- 型パラメータを持っていない
- `@KomapperEntityDef`で注釈され引数でエンティティクラスを受け取る
- `companion object`を持つ
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
) {
  companion object
}
```

エンティティ定義クラスは、参照するエンティティクラスに定義された同名のプロパティに対し様々な設定ができます。
定義されないプロパティに対してはデフォルトのマッピング定義が適用されます。
上記の例ではエンティティクラスに登場する`street`プロパティがエンティティ定義クラスには登場しませんが、
`street`プロパティにはテーブル上の`STREET`カラムにマッピングされます。

エンティティ定義クラスのプロパティの型に制約はありません。上記の例では`Nothing`を使っています。

## アノテーション一覧

ここで説明するアノテーションは全て`org.komapper.annotation`パッケージに属します。

### クラスに付与するアノテーション

#### @KomapperEntity

エンティティクラスがマッピング定義を持つことを表します。

#### @KomapperEntityDef

エンティティマッピング定義クラスであることを表します。

#### @KomapperTable

エンティティクラスとマッピングするテーブルの名称を明示的に指定します。
このアノテーションが存在しない場合はエンティティクラスの単純名と同名のテーブルにマッピングされます。

```kotlin
@KomapperEntityDef(Address::class)
@KomapperTable("ADDRESS", schema = "ACCOUNT", alwaysQuote = true)
data class AddressDef(
  ...
)
```

`catalog`プロパティや`schema`プロパティにはテーブルが属するカタログやスキーマの名前を指定できます。

`alwaysQuote`プロパティに`true`を設定すると生成されるSQLの識別子が引用符で囲まれます。

### プロパティに付与するアノテーション

#### @KomapperId

プライマリーキーであることを表します。
エンティティクラスのマッピングを行う上でこのアノテーションの存在は必須です。

#### @KomapperSequence

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

#### @KomapperAutoIncrement

プライマリーキーがデータベースの自動インクリメント機能で生成されることを表します。
必ず`@KomapperId`と一緒に付与する必要があります。

このアノテーションを付与するプロパティの型は次のいずれかでなければいけません。

- Int
- Long
- UInt
- 上述の型をプロパティとして持つValue Class

#### @KomapperVersion

楽観的排他制御に使われるバージョン番号であることを表します。

このアノテーションを付与すると、 [EntityDsl](../query/entity) のUPDATE処理やDELETE処理で楽観的排他制御が行われます。
つまり、WHERE句にバージョン番号チェックが含まれ処理件数が0の場合に例外がスローされます。

このアノテーションを付与するプロパティの型は次のいずれかでなければいけません。

- Int
- Long
- UInt
- 上述の型をプロパティとして持つValue Class

#### @KomapperCreatedAt

生成時のタイムスタンプであることを表します。

このアノテーションを付与すると、
[EntityDsl](../query/entity) のINSERT処理にてタイムスタンプがプロパティに設定されます。

このアノテーションを付与するプロパティの型は次のいずれかでなければいけません。

- java.time.LocalDateTime
- java.time.OffsetDateTime
- 上述の型をプロパティとして持つValue Class

#### @KomapperUpdatedAt

更新時のタイムスタンプであることを表します。

このアノテーションを付与すると、
[EntityDsl](../query/entity) のINSERT処理とUPDATE処理にてタイムスタンプがプロパティに設定されます。

このアノテーションを付与するプロパティの型は次のいずれかでなければいけません。

- java.time.LocalDateTime
- java.time.OffsetDateTime
- 上述の型をプロパティとして持つValue Class

#### @KomapperColumn

プロパティとマッピングするカラムの名称を明示的に指定します。
このアノテーションが存在しない場合はプロパティの単純名と同名のカラムにマッピングされます。

```kotlin
@KomapperColumn(name = "ADDRESS_ID", alwaysQuote = true)
val id: Nothing
```

`alwaysQuote`プロパティに`true`を設定すると生成されるSQLの識別子が引用符で囲まれます。

#### @KomapperIgnore

マッピングの対象外であることを表します。

## プロパティとカラムの型のマッピング

プロパティの型（Kotlinの型）とカラムの型（データベースの型）の対応関係を示します。

### H2 - JDBC

| Kotlin Type | Database Type |
|-------------|---------------|
| java.math.BigDecimal | BIGINT |
| java.math.BigInteger | BIGINT |
| java.sql.Array | ARRAY |
| java.sql.Blob | BLOB |
| java.sql.Clob | CLOB |
| java.sql.NClob | CLOB |
| java.sql.SQLXML | CLOB |
| java.time.LocalDate | DATE |
| java.time.LocalDateTime | TIMESTAMP |
| java.time.LocalTime | TIME |
| java.time.OffsetDateTime | TIMESTAMP WITH TIME ZONE |
| java.util.UUID | UUID |
| kotlin.Any | OTHER |
| kotlin.Boolean | BOOLEAN |
| kotlin.Byte | TINYINT |
| kotlin.ByteArray | BINARY |
| kotlin.Double | DOUBLE |
| kotlin.Float | REAL |
| kotlin.Int | INT |
| kotlin.Long | BIGINT |
| kotlin.Short | SMALLINT |
| kotlin.String | VARCHAR |
| kotlin.UByte | SMALLINT |
| kotlin.UInt | BIGINT |
| kotlin.UShort | INT |

### H2 - R2DBC

TODO

### MariaDB - JDBC

TODO

### MariaDB - R2DBC

TODO

### MySQL - JDBC

TODO

### MySQL - R2DBC

TODO

### PostgreSQL - JDBC

TODO

### PostgreSQL - R2DBC

TODO
