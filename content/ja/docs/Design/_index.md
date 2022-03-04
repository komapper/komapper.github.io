---
title: "Design Docs"
linkTitle: "Design Docs"
weight: 90
description: >
  Komapperの開発者に向けた設計指針
---

## 概要 {#overview}

本ドキュメントは、[nakamura-to](https://github.com/nakamura-to)
が自身を含めたKomapperの開発者に対してKomapperの設計指針を示すものである。

## 目的 {#purpose}

KomapperはデータベースアクセスのためのKotlin向け高レベルAPIを提供する。

## 背景 {#background}

JavaとKotlinを比べるとKotlinは以下の魅力を持ちKotlinを使いたいモチベーションとなり得る。

- Null Safety、Data Class、Propertyなど便利な言語機能が豊富にある
- コレクションAPIが充実している
- コルーチンにより非同期処理を簡潔に扱える

しかし、KotlinからJavaで作られたライブラリを呼び出す際、
Kotlinの一部機能を利用できないもしくは利用のために追加の設定が求められることがある。
それはJavaで書かれたデータベースアクセスライブラリについても同様である。

そこで、これらの課題を解決すべくKotlinによるKotlinのためのデータベースアクセスライブラリを目指す。

また、Javaには多くのデータベースアクセスライブラリがあるが
Kotlinの言語仕様を最大限利用することでより使いやすいAPIの作成が可能であるように思われる。

## スコープ {#scope}

Komapperが動作する環境はサーバーサイドである。

データベース接続にはJDBC及びR2DBCのドライバを用いる。
逆に言えば、ドライバが提供されていないデータベースはサポート対象としない。

## 既存のものとの相違点 {#differences-from-other-database-libraries}

2021年7月現在、Kotlinで作られたデータベースアクセスライブラリでよく使われていると思われるものは以下のプロダクトである。

- [Exposed](https://github.com/JetBrains/Exposed)
- [Ktorm](https://github.com/kotlin-orm/ktorm)

これらのプロダクトとは異なりKomapperは以下の方針をとる。

- JDBCとR2DBCの両方をサポートする
- タイプセーフにクエリを組み立てる手段とSQLテンプレートでクエリを組み立てる手段の両方を提供する
- 基本的に実行時にリフレクションを呼び出さない
- 初期化時にデータベースのメタデータを読まない

{{< alert color="warning" title="補足" >}}ExposedもKtormもR2DBCサポートは検討しているようだがまだ実装されていないと思われる。{{< /alert >}}

## アーキテクチャの検討 {#architectural-considerations}

主な検討項目は以下の通りである。

- コンパイル時のコード生成
- イミュータブルなデータモデル
- データモデルに対するアノテーション 
- クエリの構築と実行の分離
- 疎結合なアーキテクチャ
- キャッシュ

### コンパイル時のコード生成 {#compile-time-code-generation}

実行時にリフレクション呼び出しやデータベースのメタデータ読み取りを避けるために
[Kotlin Symbol Processing API](https://github.com/google/ksp) を使ってコンパイル時にコード生成を行う。

リフレクション呼び出しを避けたい理由は以下の通りである。

- コードが複雑化する
- 実行時にエラーが発生しやすくなる
- ネイティブイメージ化と相性が悪い

データベースのメタデータ読み取りを避けたい理由は以下の通りである。

- 読み取りに時間がかかる
  - 接続を短時間で何度も繰り返すテスト環境で影響が無視できなくなることが多い

コンパイル時にコード生成を行う手法としては他に [kapt](https://kotlinlang.org/docs/kapt.html) があるが、
kaptは生成したJavaのスタブに注釈処理を実行する仕組みであることからエラーメッセージが分かりにくくなりがちである。
また単純に処理速度が遅いという問題も聞かれる。
そのためkaptは選択肢から外した。

### イミュータブルなデータモデル {#immutable-data-model}

KomapperではエンティティクラスはData Classとして定義することを求める。

一般的にイミュータブルなデータモデルを使った方が不具合が起きにくく、
KotlinにおいてイミュータブルなデータモデルはData Classで定義するのが通例であるからである。

### データモデルに対するアノテーション {#annotations-to-data-model}

エンティティクラスのようなデータモデルは様々な場所で利用されるので特定のライブラリのアノテーションに依存したくないという意見を聞く。

これはもっともだと思われるので、
Komapperではエンティティクラスとマッピング定義とを別のクラスで表現しマッピング定義を表すクラスに対してアノテーションを付与するものとする。

```kotlin
// エンティティクラス
data class Address(
    val id: Int,
    val street: String,
    val version: Int
)

// AddressクラスとADDRESSテーブルとのマッピング定義を表すクラス
@KomapperEntityDef(Address::class)
data class AddressDef(
    @KomapperId val id: Nothing,
    @KomapperVersion val version: Nothing,
)
```

### クエリの構築と実行の分離 {#separation-of-query-construction-and-execution}

JDBCとR2DBCのそれぞれに対応したAPIにおいて同一のクエリを受け取れるようにしたい。
そのためクエリの構築と実行はAPIとして分離した設計とする。

```kotlin
val jdbcDb = JdbcDatabase("jdbc:h2:mem:example;DB_CLOSE_DELAY=-1")
val r2dbcDb = R2dbcDatabase("r2dbc:h2:mem:///example;DB_CLOSE_DELAY=-1")
val a = Meta.address

// クエリの構築
val query = QueryDsl.from(a).where { a.street startsWith "TOKYO" }.orderBy(a.id)

// クエリの実行（JDBC版）
jdbcDb.runQuery { query }

// クエリの実行（R2DBC版）
r2dbcDb.runQuery { query }
```

### 疎結合なアーキテクチャ {#loosely-coupled-architecture}

Komapperの利用にあたっては様々な状況を想定している。

- JDBCを使う/使わない
- R2DBCを使う/使わない
- トランザクション機能を使う/使わない 
- SQLテンプレート機能を使う/使わない

上述の状況において利用するクラスのみをロードできるようにライブラリレベルのモジュール分割を細かく行い、
切り出した別ライブラリのクラスをロードするには [ServiceLoader](https://docs.oracle.com/javase/8/docs/api/?java/util/ServiceLoader.html) を用いる。

### キャッシュ {#cache}

複雑さを避けるためデータベースから取得したデータのキャッシュは基本的に行わない。

例外的にIDの生成に使うシーケンスの値はキャッシュし値の増分はKomapperの中で行う。

## テスト自動化 {#test-automation}

[Testcontainers](https://www.testcontainers.org/) を用いてサポートする全てのデータベースに対してテストを実施する。

テストはプルリクエストを受け付けるたびもしくはmainブランチにマージするたびにGitHub Actionsで実行する。

## リリース自動化 {#release-automation}

GitHub Actionsのワークフローで下記のようなリリース作業は全て自動化する。

- バージョンアップ
- タグ付
- Mavenリポジトリへの公開
- リリースノートの作成
- リリースのアナウンス

## 既知の問題や懸念事項 {#known-issues-and-concerns}

- R2DBCのSPIと各ドライバの実装がまだ安定していない
  - しばらくメンテナンスされていないようなドライバもあり継続性が心配である