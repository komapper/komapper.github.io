---
title: "Association API"
linkTitle: "Association API"
weight: 35
description: >
---

## 概要 {#overview}

Association APIは、 [include]({{< relref "Query/QueryDsl/select#include" >}})
や [includeAll]({{< relref "Query/QueryDsl/select#includeall" >}})
を指定したクエリを実行して得られる`EntityStore`から関連オブジェクトを簡単に辿れるようにします。
専用のアノテーションをエンティティクラス（もしくはエンティティ定義クラス）に注釈すると
関連オブジェクトの走査のためのユーティリティ関数が生成されます。

{{< alert title="Note" >}}
Association APIは実験的な機能です。
利用するには、以下のコードをGradleのビルドスクリプトに追加してください。

```kotlin
tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions.freeCompilerArgs += listOf("-opt-in=org.komapper.annotation.KomapperExperimentalAssociation")
    }
}
```
{{< /alert >}}


## アノテーション {#annotations}

### @KomapperOneToOne

注釈されたエンティティクラスとターゲットとなるエンティティクラスの間にone-to-oneの関係があることを示します。

```kotlin
@KomapperEntity
@KomapperOneToOne(targetEntity = Address::class)
data class Employee(...)
```

`targetEntity`プロパティには関連先のエンティティクラス（もしくはエンティティ定義クラス）を指定します。
指定は必須です。

`navigator`プロパティには関連を走査するためのユーティリティ関数の名前を指定します。
指定しない場合、`targetEntity`プロパティに指定した値から推測されます。

上述の定義からは以下のユーティリティ関数が生成されます。

```kotlin
fun example.Employee.`address`(
    store: org.komapper.core.dsl.query.EntityStore,
    source: example._Employee = org.komapper.core.dsl.Meta.`employee`,
    target: example._Address = org.komapper.core.dsl.Meta.`address`,
    ): example.Address? {
    return store.oneToOne(source, target)[this]
}
```

### @KomapperManyToOne

注釈されたエンティティクラスとターゲットとなるエンティティクラスの間にone-to-manyの関係があることを示します。

```kotlin
@KomapperEntity
@KomapperManyToOne(targetEntity = Department::class)
data class Employee(...)
```

`targetEntity`プロパティには関連先のエンティティクラス（もしくはエンティティ定義クラス）を指定します。
指定は必須です。

`navigator`プロパティには関連を走査するためのユーティリティ関数の名前を指定します。
指定しない場合、`targetEntity`プロパティに指定した値から推測されます。

上述の定義からは以下のユーティリティ関数が生成されます。

```kotlin
fun example.Employee.`department`(
    store: org.komapper.core.dsl.query.EntityStore,
    source: example._Employee = org.komapper.core.dsl.Meta.`employee`,
    target: example._Department = org.komapper.core.dsl.Meta.`department`,
    ): example.Department? {
    return store.manyToOne(source, target)[this]
}
```

### @KomapperOneToMany

注釈されたエンティティクラスとターゲットとなるエンティティクラスの間にmany-to-oneの関係があることを示します。

```kotlin
@KomapperEntity
@KomapperOneToMany(targetEntity = Employee::class, navigator = "employees")
data class Department(...)
```

`targetEntity`プロパティには関連先のエンティティクラス（もしくはエンティティ定義クラス）を指定します。
指定は必須です。

`navigator`プロパティには関連を走査するためのユーティリティ関数の名前を指定します。
指定しない場合、`targetEntity`プロパティに指定した値から推測されます。

上述の定義からは以下のユーティリティ関数が生成されます。

```kotlin
fun example.Department.`employees`(
    store: org.komapper.core.dsl.query.EntityStore,
    source: example._Department = org.komapper.core.dsl.Meta.`department`,
    target: example._Employee = org.komapper.core.dsl.Meta.`employee`,
    ): Set<example.Employee> {
    return store.oneToMany(source, target)[this] ?: emptySet()
}
```

### @KomapperLink

関連元のエンティティメタモデルと関連先のエンティティメタモデルを示します。
以下のアノテーションの`link`プロパティに指定できます。

- `@KomapperOneToOne`
- `@KomapperManyToOne`
- `@KomapperOneToMany`

### @KomapperAggregateRoot

注釈されたエンティティクラスが集約のルートになることを示します。

```kotlin
@KomapperEntity
@KomapperAggregateRoot(navigator = "departments")
data class Department(...)
```

`navigator`プロパティには集約のルートを取り出すためのユーティリティ関数の名前を指定します。
指定しない場合、注釈されたクラスから推測されます。

上述の定義からは以下のユーティリティ関数が生成されます。

```kotlin
fun org.komapper.core.dsl.query.EntityStore.`departments`(
    target: example._Department = org.komapper.core.dsl.Meta.`department`,
    ): Set<example.Department> {
    return this[target]
}
```

## 利用例 {#example}

### エンティティクラス定義 {#example-entity-class-definitions}

次のようなエンティティクラスの定義があるとします。

```kotlin
@KomapperEntity
@KomapperOneToOne(targetEntity = Employee::class)
data class Address(
    @KomapperId
    val addressId: Int,
    val street: String,
)

@KomapperEntity
@KomapperAggregateRoot("departments")
@KomapperOneToMany(targetEntity = Employee::class, navigator = "employees")
data class Department(
    @KomapperId
    val departmentId: Int,
    val departmentName: String,
)

@KomapperEntity
@KomapperManyToOne(targetEntity = Department::class)
@KomapperOneToOne(targetEntity = Address::class)
data class Employee(
    @KomapperId
    val employeeId: Int,
    val employeeName: String,
    val departmentId: Int,
    val addressId: Int,
)
```

### 走査のコード {#example-navigation-code}

生成されたユーティリティ関数を使うことで、`EntityStore`から関連オブジェクトを簡潔に取り出せます。

```kotlin
val a = Meta.address
val e = Meta.employee
val d = Meta.department

val query = QueryDsl.from(a)
  .innerJoin(e) {
    a.addressId eq e.addressId
  }.innerJoin(d) {
    e.departmentId eq d.departmentId
  }.includeAll()

val store: EntityStore = db.runQuery(query)

for (department in store.departments()) {
    val employees = department.employees(store)
    for (employee in employees) {
        val address = employee.address(store)
        println("department=${department.departmentName}, employee=${employee.employeeName}, address=${address?.street}")
    }
}
```

### context receiverを使った走査のコード {#example-navigation-code-using-context-receiver}

[komapper.enableEntityStoreContext]({{< relref "annotation-processing/#komapperenableentitystorecontext" >}})
オプションを有効にすると、`EntityStore`の引数を明示的に渡す必要がなくなるのでより簡潔に関連オブジェクトを走査できます。

```kotlin
val a = Meta.address
val e = Meta.employee
val d = Meta.department

val query = QueryDsl.from(a)
    .innerJoin(e) {
        a.addressId eq e.addressId
    }.innerJoin(d) {
        e.departmentId eq d.departmentId
    }.includeAll()

val store: EntityStore = db.runQuery(query)

with(store.asContext()) {
    for (department in store.departments()) {
        val employees = department.employees()
        for (employee in employees) {
            val address = employee.address()
            println("department=${department.departmentName}, employee=${employee.employeeName}, address=${address?.street}")
        }
    }
}
```
