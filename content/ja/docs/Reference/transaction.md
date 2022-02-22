---
title: "Transaction"
linkTitle: "Transaction"
weight: 40
description: >
  トランザクション
---

## 概要 {#overview}

KomapperはJDBCやR2DBCのConnectionが持つトランザクション機能をラップした高レベルAPIを提供します。

{{< alert color="warning" title="Warning" >}}
Spring Frameworkなどトランザクション機能を提供するフレームワークと組み合わせてKomapperを使う場合
このページで説明するAPIを使ってはいけません。
{{< /alert >}}

このAPIを使うには専用のモジュールをGradleの依存関係に宣言します。

JDBCを使う場合は次のようにkomapper-tx-jdbcを宣言します。

```kotlin
val komapperVersion: String by project

dependencies { 
    implementation("org.komapper:komapper-tx-jdbc:$komapperVersion")
}
```

R2DBCを使う場合は次のようにkomapper-tx-r2dbcを宣言します。

```kotlin
val komapperVersion: String by project

dependencies {
    implementation("org.komapper:komapper-tx-r2dbc:$komapperVersion")
}
```

{{< alert title="Note" >}}
Komapperが提供するkomapper-starter-jdbcやkomapper-starter-r2dbcモジュールは上述の設定を含んでいます。
したがって、これらのstarterモジュールを使う場合上述の設定は不要です。
{{< /alert >}}

{{< alert title="Note" >}}
上述のトランザクション用モジュールを依存関係に宣言する場合、全てのデータベースアクセスはトランザクション内で実行する必要があります。
{{< /alert >}}

## トランザクションの制御 {#transaction-control}

Komapperが提供するトランザクション制御のためのAPIはJDBC版とR2DBC版で異なりますが見た目上のインターフェースは統一されています。
ここでは明らかに異なる部分を除いてJDBC版とR2DBC版を合わせて説明します。

`JdbcDatabase`もしくは`R2dbcDatabase`が下記のように`db`という変数で宣言されていることを前提に説明を進めます。

```kotlin
val db = JdbcDatabase.create("jdbc:h2:mem:example;DB_CLOSE_DELAY=-1")
```

```kotlin
val db = R2dbcDatabase.create("r2dbc:h2:mem:///example;DB_CLOSE_DELAY=-1")
```

### 開始と終了 {#begin-and-end}

JDBC版とR2DBC版のそれぞれのモジュールに定義された`withTransaction`拡張関数の呼び出すことでトランザクションを開始できます。

```kotlin
db.withTransaction {
    ..
}
```

`withTransaction`拡張関数にはトランザクション属性とトランザクション分離レベルを指定できます。
下記のコードはJDBC版の`withTransaction`拡張関数の利用例です。

```kotlin
db.withTransaction(
  transactionAttribute = JdbcTransactionAttribute.REQUIRES_NEW, 
  isolationLevel = JdbcIsolationLevel.SERIALIZABLE) {
    ..
}
```

トランザクション属性を指定しない場合、トンランザクションがなければ作成しすでに存在すればそのトランザクションに参加します。

トランザクション分離レベルを指定しない場合にどのレベルになるかは利用するドライバの挙動に従います。

`withTransaction`拡張関数の呼び出しが終わるとトランザクションはコミットもしくはロールバックされます。

ロールバックされる条件は次のとおりです。

- `withTransaction`拡張関数の呼び出しが例外のスローにより終了する
- `withTransaction`拡張関数に渡されたラムダ式の中で明示的にロールバックを行う

ロールバックの条件に合致しない場合コミットされます。

### 明示的なロールバック {#explicit-rollback}

`setRollbackOnly`関数を呼び出すと`withTransaction`拡張関数終了時にロールバックが実行されます。

```kotlin
db.withTransaction { tx ->
    ..
    tx.setRollbackOnly()
    ..
}
```

すでに`setRollbackOnly`関数を呼び出したかどうかは`isRollbackOnly`関数で確認できます。

```kotlin
db.withTransaction { tx ->
    ..
    if (tx.isRollbackOnly()) {
        ..
    }
    ..
}
```

### 新規トランザクションの開始と終了 {#begin-and-end-of-new-transaction}

すでに開始されたトランザクションの中で別のトランザクションを新しく開始するには`requiresNew`関数を呼び出します。

```kotlin
db.withTransaction { tx ->
    ..
    tx.requiresNew {
        ..
    }
    ..
}
```

`requiresNew`関数にはトランザクション分離レベルを指定できます。

```kotlin
db.withTransaction { tx ->
    ..
    tx.requiresNew(isolationLevel = JdbcIsolationLevel.SERIALIZABLE) {
        ..
    }
    ..
}
```

トランザクション分離レベルを指定しない場合にどのレベルになるかは利用するドライバの挙動に従います。

`requiresNew`関数の呼び出しが終わるとトランザクションはコミットもしくはロールバックされます。

ロールバックされる条件は次のとおりです。

- `requiresNew`関数の呼び出しが例外のスローにより終了する
- `requiresNew`関数に渡されたラムダ式の中で明示的にロールバックを行う

ロールバックの条件に合致しない場合コミットされます。
