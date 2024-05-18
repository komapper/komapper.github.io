---
title: "Association API"
linkTitle: "Association API"
weight: 35
description: >
---

## Overview {#overview}

The Association API makes it easy to navigate association objects from 
the EntityStore obtained by executing a query specifying [include]({{< relref "Query/QueryDsl/select#include" >}})
or [includeAll]({{< relref "Query/QueryDsl/select#includeall" >}}).

Annotating an entity class (or entity definition class) with a dedicated annotation
generates a utility function for navigating association objects.

{{< alert title="Note" >}}
Sets returned by the Association API preserve the order of elements.
{{< /alert >}}

{{< alert title="Note" >}}
The Association API is an experimental feature.
To use it, add the following code to your Gradle build script:

```kotlin
tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions.freeCompilerArgs += listOf("-opt-in=org.komapper.annotation.KomapperExperimentalAssociation")
    }
}
```
{{< /alert >}}


## Annotations {#annotations}

### @KomapperOneToOne

Indicates a one-to-one relationship between the annotated entity class and the target entity class.

```kotlin
@KomapperEntity
@KomapperOneToOne(targetEntity = Address::class)
data class Employee(...)
```

The `targetEntity` property specifies the entity class (or entity definition class) associated with the annotated class.
The specification is required.

The `navigator` property specifies the name of a utility function for navigating the association.
If not specified, it is inferred from the value specified for the `targetEntity` property.

The following utility function is generated from the above definition:

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

Indicates a many-to-one relationship between the annotated entity class and the target entity class.

```kotlin
@KomapperEntity
@KomapperManyToOne(targetEntity = Department::class)
data class Employee(...)
```

The `targetEntity` property specifies the entity class (or entity definition class) associated with the annotated class.
The specification is required.

The `navigator` property specifies the name of a utility function for navigating the association.
If not specified, it is inferred from the value specified for the `targetEntity` property.

The following utility function is generated from the above definition:

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

Indicates a one-to-many relationship between the annotated entity class and the target entity class.

```kotlin
@KomapperEntity
@KomapperOneToMany(targetEntity = Employee::class, navigator = "employees")
data class Department(...)
```

The `targetEntity` property specifies the entity class (or entity definition class) associated with the annotated class.
The specification is required.

The `navigator` property specifies the name of a utility function for navigating the association.
If not specified, it is inferred from the value specified for the `targetEntity` property.

The following utility function is generated from the above definition:

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

Indicates the associated source entity metamodel and the associated target entity metamodel.
It can be specified in the `link` property of the following annotations:

- `@KomapperOneToOne`
- `@KomapperManyToOne`
- `@KomapperOneToMany`

### @KomapperAggregateRoot

Indicates that the annotated entity class (or entity definition class) is the aggregate root.

```kotlin
@KomapperEntity
@KomapperAggregateRoot(navigator = "departments")
data class Department(...)
```

The `navigator` property specifies the name of a utility function to retrieve the aggregate root.
If not specified, it is inferred from the annotated class.

The above definition generates the following utility function:

```kotlin
fun org.komapper.core.dsl.query.EntityStore.`departments`(
    target: example._Department = org.komapper.core.dsl.Meta.`department`,
): Set<example.Department> {
    return this[target]
}
```

## Example {#example}

### Entity class definitions {#example-entity-class-definitions}

Suppose we have the following entity class definitions:

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

### Navigation code {#example-navigation-code}

We can concisely retrieve the association objects from the `EntityStore` by using the generated utility functions:

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

### Navigation code using context receiver {#example-navigation-code-using-context-receiver}

Enabling the [komapper.enableEntityStoreContext]({{< relref "annotation-processing/#komapperenableentitystorecontext" >}})
option allows more concise navigation of association objects 
by eliminating the need to pass `EntityStore` arguments explicitly.

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
    for (department in departments()) {
        val employees = department.employees()
        for (employee in employees) {
            val address = employee.address()
            println("department=${department.departmentName}, employee=${employee.employeeName}, address=${address?.street}")
        }
    }
}
```
