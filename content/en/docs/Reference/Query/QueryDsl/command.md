---
title: "COMMAND Queries"
linkTitle: "COMMAND"
weight: 51
description: >
---

## Overview {#overview}

COMMAND queries are built on top of [TEMPLATE queries]({{< relref "template" >}}).

A command is a feature that treats an [SQL template]({{< relref "template#sql-template" >}}) and parameters as a single unit. You define an SQL template by annotating a class with the `org.komapper.annotation.KomapperCommand` annotation, and define the parameters in the class properties. How the SQL result is handled is expressed by inheriting from a specific class.

To use COMMAND queries, the following dependency declaration must be included in your Gradle build script:

```kotlin
val komapperVersion: String by project
dependencies {
    implementation("org.komapper:komapper-template:$komapperVersion")
}
```

{{< alert title="Note" >}}
The above dependency declaration is not necessary when using [Starters]({{< relref "../../Starter" >}}).
{{< /alert >}}

{{< alert title="Note" >}}
The `komapper-template` module uses reflection internally.
{{< /alert >}}

## Commands {#command}

Below is an example of a command that retrieves multiple records.

```kotlin
@KomapperCommand("""
    select * from ADDRESS where street = /*street*/'test'
""")
class ListAddresses(val street: String): Many<Address>({ selectAsAddress() })
```

When you define a command and run the build, an extension function named `execute` is generated in the `QueryDsl`. Therefore, the above command can be executed as follows:

```kotlin
val query: Query<List<Address>> = QueryDsl.execute(ListAddresses("STREET 10"))
```

Commands have the advantage of enabling SQL template validation at compile time compared to using methods like [fromTemplate]({{< relref "template#fromtemplate" >}}) or [executeTemplate]({{< relref "template#executetemplate" >}}). Specifically, the following can be achieved:

- Detect syntax errors in SQL templates. For example, if `/*%end*/` is missing, a compile-time error occurs.
- Detect unused parameters. If unused parameters are found, a warning message is output. You can suppress this warning by annotating the parameter with `org.komapper.annotation.KomapperUnused`.
- Validate the types and members of parameters. For instance, if `name` is a parameter of type `String`, attempting to access a non-existent member in the SQL template like `/* name.unknown */` will result in a compile-time error.

{{< alert title="Note" >}}
A command class can be defined as a top-level class, a nested class, or an inner class, but not as a local class.
{{< /alert >}}

There are five types of commands:

- One
- Many
- Exec
- ExecReturnOne
- ExecReturnMany

### One {#command-one}

A command that retrieves a single record inherits from `org.komapper.core.One`.

```kotlin
@KomapperCommand("""
    select * from ADDRESS where address_id = /*id*/0
""")
class GetAddressById(val id: Int): One<Address?>({ selectAsAddress().singleOrNull() })
```

The type parameter of `One` specifies the type of the value to be retrieved. The constructor of `One` takes a lambda expression to process the search result. What can be done within the lambda is the same as the `select` and `selectAsEntity` functions mentioned in [fromTemplate]({{< relref "template#fromtemplate" >}}).

The above example assumes that the `Address` class is annotated with `@KomapperProjection`. Therefore, the `selectAsAddress` function is used to convert the result to the `Address` class.

### Many {#command-many}

A command that retrieves multiple records inherits from `org.komapper.core.Many`.

```kotlin
@KomapperCommand("""
    select * from ADDRESS where street = /*street*/'test'
""")
class ListAddresses(val street: String): Many<Address>({ selectAsAddress() })
```

The type parameter of `Many` specifies the type representing a single record to be retrieved. The constructor of `Many` takes a lambda expression to process the search result. What can be done within the lambda is the same as the `select` and `selectAsEntity` functions mentioned in [fromTemplate]({{< relref "template#fromtemplate" >}}).

The above example assumes that the `Address` class is annotated with `@KomapperProjection`. Therefore, the `selectAsAddress` function is used to convert the result to the `Address` class.

### Exec {#command-exec}

A command that executes an update DML inherits from `org.komapper.core.Exec`.

```kotlin
@KomapperCommand("""
    update ADDRESS set street = /*street*/'' where address_id = /*id*/0
""")
class UpdateAddress(val id: Int, val street: String): Exec()
```

### ExecReturnOne {#command-exec-return-one}

A command that executes an update DML and returns a single record inherits from `org.komapper.core.ExecReturnOne`.

```kotlin
@KomapperCommand("""
    insert into ADDRESS
        (address_id, street, version)
    values
        (/* id */0, /* street */'', /* version */1)
    returning address_id, street, version
""")
class InsertAddressThenReturn(val id: Int, val street: String): ExecReturnOne<Address>({ selectAsAddress().single() })
```

The type parameter and constructor of `ExecReturnOne` are similar to those of [One]({{< relref "#command-one" >}}).

### ExecReturnMany {#command-exec-return-many}

A command that executes an update DML and returns multiple records inherits from `org.komapper.core.ExecReturnMany`.

```kotlin
@KomapperCommand("""
    update ADDRESS set street = /*street*/'' returning address_id, street, version
""")
class UpdateAddressThenReturn(val id: Int, val street: String): ExecReturnMany<Address>({ selectAsAddress() })
```

The type parameter and constructor of `ExecReturnMany` are similar to those of [Many]({{< relref "#command-many" >}}).

## Partials {#command-partial}

A partial is a feature for reusing parts of an SQL template.
A partial is defined as a class with the `org.komapper.annotation.KomapperPartial` annotation.

```kotlin
@KomapperPartial(
    """
    limit /* limit */0 offset /* offset */0
    """,
)
data class Pagination(val limit: Int, val offset: Int)
```

To use a partial, define a command that takes the partial as a parameter, and reference that parameter in the SQL template using the `/*> partialName */` notation.

```kotlin
@KomapperCommand(
    """
    select * from address order by address_id
    /*> pagination */
    """,
)
class UsePartial(val pagination: Pagination?) : Many<Address>({ selectAsAddress() })
```

If the partial parameter is `null`, it will not be included in the command's SQL.

{{< alert title="Note" >}}
One partial cannot reference another partial.
{{< /alert >}}

### Partials as sealed subclasses {#command-partial-sealed-subclass}

A partial class can be represented as a sealed subclass.
In this case, the partial SQL is determined polymorphically.

```kotlin
sealed interface FilterBy {
    @KomapperPartial(
        """
        where street = /* street */''
        """,
    )
    class Street(val street: String) : FilterBy

    @KomapperPartial(
        """
        where address_id = /* id */0
        """,
    )
    class Id(val id: Int) : FilterBy
}

@KomapperCommand(
    """
    select * from address
    /*> filterBy */
    """,
)
class UseSealedPartial(val filterBy: FilterBy?) : Many<Address>({ selectAsAddress() })
```

In the example above, the partial SQL in the command changes based on whether 
`FilterBy.Street` or `FilterBy.Id` is passed to the `filterBy` parameter of `UseSealedPartial`.