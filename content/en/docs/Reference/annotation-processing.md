---
title: "Annotation Processing"
weight: 25
description: >
---

## Overview {#overview}

Komapper uses the [Kotlin Symbol Processing API](https://github.com/google/ksp) (KSP) to 
process annotations in [mapping definitions]({{< relref "entity-class/#mapping-definition" >}}) and 
generate the result as metamodel source code at compile-time.

To run KSP, you need to configure your Gradle build script as follows:

```kotlin
plugins {
  id("com.google.devtools.ksp") version "1.9.0-1.0.13"
  kotlin("jvm") version "1.9.0"
}

dependencies {
  val komapperVersion = "1.13.0"
  ksp("org.komapper:komapper-processor:$komapperVersion")
}
```

The `komapper-processor` module contains the KSP annotation processor.

After the above settings, running Gradle's build task will generate code
under the `build/generated/ksp/main/kotlin` directory.

## Options {#options}

Options allow you to change the behavior of the annotation processor.
Available options are as follows:

- komapper.prefix
- komapper.suffix
- komapper.enumStrategy
- komapper.namingStrategy
- komapper.metaObject
- komapper.alwaysQuote
- komapper.enableEntityMetamodelListing
- komapper.enableEntityStoreContext

The options can be specified in the Gradle build script as follows:

```kotlin
ksp {
  arg("komapper.prefix", "")
  arg("komapper.suffix", "Metamodel")
  arg("komapper.enumStrategy", "ordinal")
  arg("komapper.namingStrategy", "UPPER_SNAKE_CASE")
  arg("komapper.metaObject", "example.Metamodels")
  arg("komapper.alwaysQuote", "true")
  arg("komapper.enableEntityMetamodelListing", "true")
  arg("komapper.enableEntityStoreContext", "true")
}
```

### komapper.prefix

This option specifies the prefix for the simple name of generated metamodel class. 
The default value is `_` (underscore).

### komapper.suffix

This option specifies the suffix for the name of generated metamodel class.
The default value is an empty string.

### komapper.enumStrategy

This option specifies the strategy for how to map the enum properties to database columns.
The value can be either `name` or `ordinal`.
The default value is `name`.

Note that specification by `@KomapperEnum` takes precedence over this strategy.

The possible values for the `komapper.enumStrategy` option are defined as follows:

name
: map the `name` property of `Enum` class to the string type column.

ordinal
: map the `ordinal` property of `Enum` class to the integer type column.

### komapper.namingStrategy

This option specifies the strategy for how to resolve database table and column names 
from Kotlin entity classes and properties.

The resolved names will be included in the generated metamodel code.
Note that if a name is specified in @KomapperTable or @KomapperColumn, 
it takes precedence over the name determined by this strategy.

The possible values for the `komapper.namingStrategy` option are defined as follows:

implicit
: This strategy converts nothing. Entity class and property names are used as-is as table and column names.

lower_snake_case
: This strategy converts entity class and property names to snake_case and then to lowercase.

UPPER_SNAKE_CASE
: This strategy converts entity class and property names to snake_case and then to UPPERCASE.

The default strategy is `lower_snake_case`.

### komapper.metaObject

This option specifies the name of the object that provides metamodel instances as extension properties.
The default value is `org.komapper.core.dsl.Meta`.

### komapper.alwaysQuote

This option indicates whether to quote table or column names in SQL statements.
The default value is `flase`.

### komapper.enableEntityMetamodelListing

This option indicates whether a list of entity metamodels should be available.
The default value is `flase`.

If you set this option to `true`, you can get the list in the following ways:

```kotlin
val metamodels: List<EntityMetamodel<*, *, *>> = Meta.all()
```

```kotlin
val metamodels: List<EntityMetamodel<*, *, *>> = EntityMetamodels.list(Meta)
```

### komapper.enableEntityStoreContext

Whether to enable the `EntityStore` context.
The default value is `false`.

If this option is set to `true`, the Komapper`s annotation processor will generate code for 
the [Association API]({{< relref "association" >}}) that uses the [context receiver](https://kotlinlang.org/docs/whatsnew1620.html#prototype-of-context-receivers-for-kotlin-jvm).

See also [Navigation code using context receiver]({{< relref "association#example-navigation-code-using-context-receiver" >}}).

{{< alert title="Note" >}}
To enable this option, add the following code to your Gradle build script:

```kotlin
tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions.freeCompilerArgs += listOf("-Xcontext-receivers")
    }
}
```
{{< /alert >}}

