---
title: "Query"
linkTitle: "Query"
weight: 30
description: >
  クエリ
---

本ページの子ページでは、下記のエンティティ定義、マッピング定義、変数が存在することを前提に説明をします。

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
  val employeeList: List<Employee> = emptyList()
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
    val address: Address? = null,
    val department: Department? = null
)

@KomapperEntityDef(Address::class)
data class AddressDef(
    @KomapperId @KomapperColumn(name = "ADDRESS_ID") val addressId: Nothing,
    @KomapperVersion val version: Nothing
) {
    companion object
}

@KomapperEntityDef(Department::class)
data class DepartmentDef(
  @KomapperId @KomapperColumn("DEPARTMENT_ID") val departmentId: Nothing,
  @KomapperColumn("DEPARTMENT_NO") val departmentNo: Nothing,
  @KomapperColumn("DEPARTMENT_NAME") val departmentName: Nothing,
  @KomapperVersion val version: Nothing,
  @KomapperIgnore val employeeList: Nothing
) {
  companion object
}

@KomapperEntityDef(Employee::class)
data class EmployeeDef(
  @KomapperId @KomapperColumn("EMPLOYEE_ID") val employeeId: Nothing,
  @KomapperColumn("EMPLOYEE_NO") val employeeNo: Nothing,
  @KomapperColumn("EMPLOYEE_NAME") val employeeName: Nothing,
  @KomapperColumn("MANAGER_ID") val managerId: Nothing,
  @KomapperColumn("DEPARTMENT_ID") val departmentId: Nothing,
  @KomapperColumn("ADDRESS_ID") val addressId: Nothing,
  @KomapperVersion val version: Nothing,
  @KomapperIgnore val address: Nothing,
  @KomapperIgnore val department: Nothing
) {
  companion object
}

val a = AddressDef.meta
val d = DepartmentDef.meta
val e = EmployeeDef.meta
```

クエリの生成例ではどのようなクエリが生成されるかわかりやすくするために型を明示的に記載しますが実際は省略可能です。

```kotlin
val query: Query<List<Address>> = QueryDsl.from(a)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_
*/
```

また、対応するSQLはコメント内に記述しますが、特に断りがなければH2 Database Engine向けのDialectを使った場合に生成されるSQLを示します。
利用するDialectによっては異なるSQLが生成されることがあります。

