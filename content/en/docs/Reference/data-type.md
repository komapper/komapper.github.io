---
title: "Data Types"
weight: 18
description: >
---

## Overview {#overview}

This section describes data type mapping between Kotlin and databases.

## Dialect data types {#dialect-data-types}

A [dialect]({{< relref "dialect.md" >}}) determines the default data type mapping between Kotlin and database.

## User-defined data types {#user-defined-data-types}

To map a user-defined Kotlin data type to a database data type, 
you must create and register a class that conforms to the Service Provider Interface specification.

For example, suppose you want to map the following Kotlin data type `example.Age` 
to an INTEGER type in a database.

```kotlin
package example

data class Age(val value: Int)
```

### In the case of JDBC {#user-defined-data-types-for-jdbc}

Create a class that implements `org.komapper.jdbc.spi.JdbcUserDefinedDataType` to perform the mapping:

```kotlin
package example.jdbc

import example.Age
import org.komapper.jdbc.spi.JdbcUserDefinedDataType
import java.sql.JDBCType
import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlin.reflect.KClass

class AgeType : JdbcUserDefinedDataType<Age> {
    override val name: String = "integer"

    override val klass: KClass<Age> = Age::class

    override val jdbcType: JDBCType = JDBCType.INTEGER

    override fun getValue(rs: ResultSet, index: Int): Age {
        return Age(rs.getInt(index))
    }

    override fun getValue(rs: ResultSet, columnLabel: String): Age {
        return Age(rs.getInt(columnLabel))
    }

    override fun setValue(ps: PreparedStatement, index: Int, value: Age) {
        ps.setInt(index, value.value)
    }

    override fun toString(value: Age): String {
        return value.value.toString()
    }
}
```

Register the above class in a file with the following name:

- `META-INF/services/org.komapper.jdbc.spi.JdbcUserDefinedDataType`

This file contains the fully qualified name of the class as follows:

```
example.jdbc.AgeType
```

You can register multiple classes together by separating lines.

### In the case of R2DBC {#user-defined-data-types-for-r2dbc}

Create a class that implements `org.komapper.r2dbc.spi.R2dbcUserDefinedDataType` to perform the mapping:

```kotlin
package example.r2dbc

import example.Age
import io.r2dbc.spi.Row
import io.r2dbc.spi.Statement
import org.komapper.r2dbc.spi.R2dbcUserDefinedDataType
import kotlin.reflect.KClass

class AgeType : R2dbcUserDefinedDataType<Age> {

    override val name: String = "integer"

    override val klass: KClass<Age> = Age::class

    override val javaObjectType: Class<*> = Int::class.javaObjectType

    override fun getValue(row: Row, index: Int): Age? {
        return row.get(index, Int::class.javaObjectType)?.let { Age(it) }
    }

    override fun getValue(row: Row, columnLabel: String): Age? {
        return row.get(columnLabel, Int::class.javaObjectType)?.let { Age(it) }
    }

    override fun setValue(statement: Statement, index: Int, value: Age) {
        statement.bind(index, value.value)
    }

    override fun setValue(statement: Statement, name: String, value: Age) {
        statement.bind(name, value.value)
    }

    override fun toString(value: Age): String {
        return value.value.toString()
    }
}
```

Register the above class in a file with the following name:

- `META-INF/services/org.komapper.r2dbc.spi.R2dbcUserDefinedDataType`

This file contains the fully qualified name of the class as follows:

```
example.r2dbc.AgeType
```

You can register multiple classes together by separating lines.

## Data type conversion {#data-type-conversion}

To convert a data type to another type,
you must create and register a class that conforms to the Service Provider Interface specification.

For example, suppose you want to treat `Int` as the following `example.PhoneNumber` in your application.

```kotlin
package example

data class PhoneNumber(val value: Int)
```

Create a class that implements `org.komapper.core.spi.DataTypeConverter` to perform the conversion:

```kotlin
package example

import org.komapper.core.spi.DataTypeConverter
import kotlin.reflect.KClass

class PhoneNumberTypeConverter : DataTypeConverter<PhoneNumber, Int> {
    override val exteriorClass: KClass<PhoneNumber> = PhoneNumber::class
    override val interiorClass: KClass<Int> = Int::class

    override fun unwrap(exterior: PhoneNumber): Int {
        return exterior.value
    }

    override fun wrap(interior: Int): PhoneNumber {
        return PhoneNumber(interior)
    }
}
```

Register the above class in a file with the following name:

- `META-INF/services/org.komapper.core.spi.DataTypeConverter`

This file contains the fully qualified name of the class as follows:

```
example.PhoneNumberTypeConverter
```

You can register multiple classes together by separating lines.

## Value classes {#value-classes}

When you use a value class, the inner type of the value class is used for mapping.

## Support for kotlinx-datetime {#kotlinx-datetime-support}

Komapper supports following data types of [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime):

- kotlinx.datetime.Instant
- kotlinx.datetime.LocalDate
- kotlinx.datetime.LocalDateTime

To use these types, declare `kotlinx-datetime` in the Gradle dependencies declaration as follows:

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
}
```

Also, declare `komapper-datetime-jdbc` or `komapper-datetime-r2dbc`:

```kotlin
val komapperVersion: String by project
dependencies {
    runtimeOnly("org.komapper:komapper-datetime-jdbc:$komapperVersion")
}
```

{{< alert title="Note" >}}
If you use one of the [Starters]({{< relref "starter.md" >}}) ,
you do not need to declare `komapper-datetime-jdbc` and `komapper-datetime-r2dbc`.
{{< /alert >}}