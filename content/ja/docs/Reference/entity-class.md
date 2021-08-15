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

マッピング定義はコンパイル時に解析されその結果がメタモデルとなりメタモデルがクエリの構築や実行で利用されます。

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

## エンティティクラスのマッピング定義 {#mapping-definition}

マッピング定義の作成方法は2種類あります。

- エンティティクラス自身がマッピング定義を持つ方法
- エンティティクラスとは別にエンティティ定義クラスを作成する方法

同一のエンティティクラスに対して1つの方法のみ適用できます。

### エンティティクラス自身がマッピング定義を持つ方法 {#self-mapping-definition}

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

### エンティティクラスとは別にエンティティ定義クラスを作成する方法 {#separate-mapping-definition}

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

## アノテーション一覧 {#annotation-list}

ここで説明するアノテーションは全て`org.komapper.annotation`パッケージに属します。

### クラスに付与するアノテーション {#annotation-list-for-class}

#### @KomapperEntity

エンティティクラスがマッピング定義を持つことを表します。

#### @KomapperEntityDef

エンティティマッピング定義クラスであることを表します。

#### @KomapperTable

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

- [オプション]({{< relref "#annotation-processing-options" >}})

### プロパティに付与するアノテーション {#annotation-list-for-property}

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

このアノテーションを付与すると、 [EntityDsl]({{< relref "query/entity-dsl.md" >}}) のUPDATE処理やDELETE処理で楽観的排他制御が行われます。
つまり、WHERE句にバージョン番号チェックが含まれ処理件数が0の場合に例外がスローされます。

このアノテーションを付与するプロパティの型は次のいずれかでなければいけません。

- Int
- Long
- UInt
- 上述の型をプロパティとして持つValue Class

#### @KomapperCreatedAt

生成時のタイムスタンプであることを表します。

このアノテーションを付与すると、
[EntityDsl]({{< relref "query/entity-dsl.md" >}}) のINSERT処理にてタイムスタンプがプロパティに設定されます。

このアノテーションを付与するプロパティの型は次のいずれかでなければいけません。

- java.time.LocalDateTime
- java.time.OffsetDateTime
- 上述の型をプロパティとして持つValue Class

#### @KomapperUpdatedAt

更新時のタイムスタンプであることを表します。

このアノテーションを付与すると、
[EntityDsl]({{< relref "query/entity-dsl.md" >}}) のINSERT処理とUPDATE処理にてタイムスタンプがプロパティに設定されます。

このアノテーションを付与するプロパティの型は次のいずれかでなければいけません。

- java.time.LocalDateTime
- java.time.OffsetDateTime
- 上述の型をプロパティとして持つValue Class

#### @KomapperColumn

プロパティとマッピングするカラムの名前を明示的に指定します。

```kotlin
@KomapperColumn(name = "ADDRESS_ID", alwaysQuote = true)
val id: Nothing
```

`alwaysQuote`プロパティに`true`を設定すると生成されるSQLの識別子が引用符で囲まれます。

このアノテーションでカラムの名前を指定しない場合、アノテーション処理の`komapper.namingStrategy`オプションに従って名前が解決されます。
以下のドキュメントも参照ください。

- [オプション]({{< relref "#annotation-processing-options" >}})

#### @KomapperIgnore

マッピングの対象外であることを表します。

## アノテーション処理 {#annotation-processing}

Komapperはコンパイル時にエンティティクラスのマッピング定義に付与されたアノテーションを処理し、結果をメタモデルのソースコードとして生成します。
アノテーションの処理とコードの生成には [Kotlin Symbol Processing API](https://github.com/google/ksp) (KSP)を利用します。

KSPを実行するには、KSPのGradleプラグインの設定と下記のGradleの依存関係の宣言が必要です。

```kotlin
val komapperVersion: String by project
dependencies {
  ksp("org.komapper:komapper-processor:$komapperVersion")
}
```

`komapper-processor`モジュールにはKSPを利用したKomapperのアノテーションプロセッサが含まれます。

上記設定後、Gradleのbuildタスクを実行すると`build/generated/ksp/main/kotlin`ディレクトリ以下にコードが生成されます。

### オプション {#annotation-processing-options}

オプション指定によりアノテーションプロセッサの挙動を変更できます。
利用可能なオプションは以下の3つです。

komapper.prefix
: 生成されるメタモデルクラスのプレフィックス。デフォルト値は`_`（アンダースコア）。

komapper.suffix
: 生成されるメタモデルクラスのサフィックス。デフォルト値は空文字。

komapper.namingStrategy
: Kotlinのエンティクラスとプロパティからデータベースのテーブルとカラムの名前をどう解決するのかの戦略。
値には`implicit`、`lower_snake_case`、`UPPER_SNAKE_CASE`のいずれかを選択でき、デフォルト値は`implicit`。
解決されたデータベースのテーブルとカラムの名前は生成されるメタモデルのコードの中に含まれます。
なお、`@KomapperTable`や`@KomapperColumn`で名前が指定される場合この戦略で決定される名前よりも優先されます。

`komapper.namingStrategy`オプションに指定可能な値の定義は次の通りです。

implicit
: エンティティクラスやプロパティの名前をそのままテーブルやカラムの名前とする。

lower_snake_case
: エンティティクラスやプロパティの名前をキャメルケースからスネークケースに変換した上で全て小文字にしテーブルやカラムの名前とする。

UPPER_SNAKE_CASE
: エンティティクラスやプロパティの名前をキャメルケースからスネークケースに変換した上で全て大文字にしテーブルやカラムの名前とする。

オプションを指定するにはGradleのビルドスクリプトで次のように記述します。

```kotlin
ksp {
  arg("komapper.prefix", "")
  arg("komapper.suffix", "Metamodel")
  arg("komapper.namingStrategy", "UPPER_SNAKE_CASE")
}
```