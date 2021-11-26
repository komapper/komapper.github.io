---
title: "Introduction"
linkTitle: "Introduction"
weight: 1
description: >
  クエリの紹介
---

## 概要 {#overview}

Komapperではクエリの構築と実行は分離されています。
クエリの構築は各種のDSLが担い、実行はJDBCやR2DBCを表す [Database]({{< relref "../database.md" >}}) インスタンスが担います。

```kotlin
// create a query
val query: Query<List<Address>> = QueryDsl.from(a)
// run the query
val result: List<Address> = db.runQuery { query }
```

クエリの構築を担うDSLについては専用のページを参照ください。

- [Query DSL]({{< relref "QueryDsl" >}})
- [Template DSL]({{< relref "template-dsl.md" >}})
- [Script DSL]({{< relref "script-dsl.md" >}})
- [Schema DSL]({{< relref "schema-dsl.md" >}})

クエリの構築に役立つクエリや宣言の合成については下記のページを参照ください。

- [Composition]({{< relref "composition.md" >}})

## 前提条件 {#prerequisites}

[Query]({{< relref "../Query" >}}) の下位ページでは、下記のエンティティ定義、マッピング定義、変数が存在することを前提に説明をします。

```kotlin
data class Address(
    val addressId: Int,
    val street: String,
    val version: Int
)

data class Department(
  val departmentId: Int,
  val departmentNo: Int,
  val departmentName: String,
  val location: String,
  val version: Int,
)

data class Employee(
    val employeeId: Int,
    val employeeNo: Int,
    val employeeName: String,
    val managerId: Int?,
    val hiredate: LocalDate,
    val salary: BigDecimal,
    val departmentId: Int,
    val addressId: Int,
    val version: Int,
)

@KomapperEntityDef(Address::class)
data class AddressDef(
    @KomapperId @KomapperColumn(name = "ADDRESS_ID") val addressId: Nothing,
    @KomapperVersion val version: Nothing
)

@KomapperEntityDef(Department::class)
data class DepartmentDef(
  @KomapperId @KomapperColumn("DEPARTMENT_ID") val departmentId: Nothing,
  @KomapperColumn("DEPARTMENT_NO") val departmentNo: Nothing,
  @KomapperColumn("DEPARTMENT_NAME") val departmentName: Nothing,
  @KomapperVersion val version: Nothing,
)

@KomapperEntityDef(Employee::class)
data class EmployeeDef(
  @KomapperId @KomapperColumn("EMPLOYEE_ID") val employeeId: Nothing,
  @KomapperColumn("EMPLOYEE_NO") val employeeNo: Nothing,
  @KomapperColumn("EMPLOYEE_NAME") val employeeName: Nothing,
  @KomapperColumn("MANAGER_ID") val managerId: Nothing,
  @KomapperColumn("DEPARTMENT_ID") val departmentId: Nothing,
  @KomapperColumn("ADDRESS_ID") val addressId: Nothing,
  @KomapperVersion val version: Nothing,
)

val a = Meta.address
val d = Meta.department
val e = Meta.employee
```

### 注意点

クエリの生成例ではどのようなクエリが生成されるかわかりやすくするために型を明示的に記載しますが実際は省略可能です。

```kotlin
val query: Query<List<Address>> = QueryDsl.from(a)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_
*/
```

また、対応するSQLはコメント内に記述しますが、特に断りがなければH2 Database Engine向けのDialectを使った場合に生成されるSQLを示します。
利用するDialectによっては異なるSQLが生成されることがあります。
