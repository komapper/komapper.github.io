---
title: "Transaction"
linkTitle: "Transaction"
weight: 40
description: >
  トランザクション
---

## 概要 {#overview}

Komapperはトランザクション管理の高レベルAPIを提供します。

{{< alert title="Note" >}}
KomapperをSpring FrameworkやQuarkusと組み合わせる場合、
このAPIはSpring FrameworkやQuarkusのトランザクションマネージャと連動します。
{{< /alert >}}

## トランザクションの制御 {#transaction-control}

Komapperが提供するトランザクション制御のためのAPIはJDBC版とR2DBC版で異なりますが見た目上のインターフェースは統一されています。
`JdbcDatabase`もしくは`R2dbcDatabase`が下記のように`db`という変数で宣言されていることを前提に説明を進めます。

```kotlin
val db = JdbcDatabase("jdbc:h2:mem:example;DB_CLOSE_DELAY=-1")
```

```kotlin
val db = R2dbcDatabase("r2dbc:h2:mem:///example;DB_CLOSE_DELAY=-1")
```

### 開始と終了 {#begin-and-end}

`db`に定義された`withTransaction`関数の呼び出すことでトランザクションを開始できます。

```kotlin
db.withTransaction {
    ..
}
```

`withTransaction`拡張関数にはトランザクション属性とトランザクションプロパティを指定できます。
下記のコードはJDBC版の`withTransaction`拡張関数の利用例です。

```kotlin
db.withTransaction(
  transactionAttribute = TransactionAttribute.REQUIRES_NEW, 
  transactionProperty = TransactionProperty.IsolationLevel.SERIALIZABLE) {
    ..
}
```

トランザクション属性を指定しない場合、トンランザクションがなければ作成しすでに存在すればそのトランザクションに参加します。

トランザクションプロパティには、トランザクション分離レベル、トランザクションの名前、ReadOnlyとするかどうかなどさまざまなプロパティを設定できます。
複数のプロパティを表すには`+`演算子で合成してください。

```kotlin
val property = TransactionProperty.IsolationLevel.SERIALIZABLE + TransactionProperty.Name("myTx") + TransactionProperty.ReadOnly(true)
```

`withTransaction`関数の呼び出しが終わるとトランザクションはコミットもしくはロールバックされます。

ロールバックされる条件は次のとおりです。

- `withTransaction`関数の呼び出しが例外のスローにより終了する
- `withTransaction`関数に渡されたラムダ式の中で明示的にロールバックを行う

ロールバックの条件に合致しない場合コミットされます。

### 明示的なロールバック {#explicit-rollback}

`setRollbackOnly`関数を呼び出すと`withTransaction`関数終了時にロールバックが実行されます。

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

`requiresNew`関数にはトランザクションプロパティを指定できます。

```kotlin
db.withTransaction { tx ->
    ..
    tx.requiresNew(transactionProperty = TransactionProperty.IsolationLevel.SERIALIZABLE) {
        ..
    }
    ..
}
```

`requiresNew`関数の呼び出しが終わるとトランザクションはコミットもしくはロールバックされます。

ロールバックされる条件は次のとおりです。

- `requiresNew`関数の呼び出しが例外のスローにより終了する
- `requiresNew`関数に渡されたラムダ式の中で明示的にロールバックを行う

ロールバックの条件に合致しない場合コミットされます。

## トランザクションを表すFlow {#transactional-flow}

R2DBCを利用している場合、`withTransaction`の代わりに`flowTransaction`を使うことでトランザクションを表現する`Flow`を構築できます。

```kotlin
val db = R2dbcDatabase("r2dbc:h2:mem:///example;DB_CLOSE_DELAY=-1")
val transactionalFlow: Flow<Address> = db.flowTransaction {
    val a = Meta.address
    val address = db.runQuery {
        QueryDsl.from(a).where { a.addressId eq 15 }.first()
    }
    db.runQuery { 
        QueryDsl.update(a).single(address.copy(street = "TOKYO")) 
    }
    val addressFlow = db.flowQuery { 
        QueryDsl.from(a).orderBy(a.addressId)
    }
    emitAll(addressFlow)
}
```

上記の例では、`transactionalFlow`を構築しているだけであってまだ実行はされていません。
`transactionalFlow`を`collect`する際に初めてトランザクションが実行されます