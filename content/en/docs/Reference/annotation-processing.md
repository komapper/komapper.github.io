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
  id("com.google.devtools.ksp") version "1.6.21-1.0.5"
  kotlin("jvm") version "1.6.21"
}

dependencies {
  val komapperVersion = "1.1.2"
  ksp("org.komapper:komapper-processor:$komapperVersion")
}
```

The `komapper-processor` module contains the KSP annotation processor.

After the above settings, running Gradle's build task will generate code
under the `build/generated/ksp/main/kotlin` directory.

## Options {#options}

Options allow you to change the behavior of the annotation processor.
There are four available options:

- komapper.prefix
- komapper.suffix
- komapper.enumStrategy
- komapper.namingStrategy
- komapper.metaObject

The options can be specified in the Gradle build script as follows:

```kotlin
ksp {
  arg("komapper.prefix", "")
  arg("komapper.suffix", "Metamodel")
  arg("komapper.enumStrategy", "ordinal")
  arg("komapper.namingStrategy", "UPPER_SNAKE_CASE")
  arg("komapper.metaObject", "example.Metamodels")
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

The default strategy is `implicit`.

### komapper.metaObject

This option specifies the name of the object that provides metamodel instances as extension properties.
Default is `org.komapper.core.dsl.Meta`.
