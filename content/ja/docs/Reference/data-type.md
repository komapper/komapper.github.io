---
title: "データ型"
weight: 18
description: >
---

## 概要 {#overview}

Kotlinとデータベースのデータ型の対応関係について説明します。

## ダイアレクトのデータ型 {#dialect-data-types}

データ型に関するKotlinとデータベースのデフォルトの対応関係は利用する [ダイアレクト]({{< relref "dialect.md" >}}) によって決まります。

## ユーザー定義のデータ型 {#user-defined-data-types}

独自に定義したKotlinのデータ型とデータベースのデータ型をマッピングさせるには、
Service Provider Interfaceの仕様に則ったクラスの作成と登録が必要です。

例えば、次のような`example.Age`というKotlinのデータ型をデータベースのINTEGER型にマッピングするとします。

```kotlin
package example

data class Age(val value: Int)
```

### JDBCの場合 {#user-defined-data-types-for-jdbc}

マッピングを実行するクラスを`org.komapper.jdbc.spi.JdbcUserDefinedDataType`を実装して作成します。

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

上述のクラスは次の名前のファイルに登録します。

- `META-INF/services/org.komapper.jdbc.spi.JdbcUserDefinedDataType`

ファイルには次のようにクラスの完全修飾名を記載します。

```
example.jdbc.AgeType
```

この例では1つのクラスのみを登録していますが、行を分けて記載すれば複数のクラスをまとめて登録できます。

### R2DBCの場合 {#user-defined-data-types-for-r2dbc}

マッピングを実行するクラスを`org.komapper.r2dbc.spi.R2dbcUserDefinedDataType`を実装して作成します。

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

上述のクラスは次の名前のファイルに登録します。

- `META-INF/services/org.komapper.r2dbc.spi.R2dbcUserDefinedDataType`

ファイルには次のようにクラスの完全修飾名を記載します。

```
example.r2dbc.AgeType
```

この例では1つのクラスのみを登録していますが、行を分けて記載すれば複数のクラスをまとめて登録できます。

## Value class {#value-classes}

value classを利用する場合、value classの内側の型がマッピングに利用されます。

## kotlinx-datetimeのサポート {#kotlinx-datetime-support}

Komapperは [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime) の以下のデータ型をサポートします。

- kotlinx.datetime.Instant
- kotlinx.datetime.LocalDate
- kotlinx.datetime.LocalDateTime

これらの型を利用するには、Gradleの依存関係の宣言で次のように宣言してください。

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
}
```

また、`komapper-datetime-jdbc`もしくは`komapper-datetime-r2dbc`を追加してください。

```kotlin
val komapperVersion: String by project
dependencies {
    runtimeOnly("org.komapper:komapper-datetime-jdbc:$komapperVersion")
}
```

{{< alert title="Note" >}}
[スターター]({{< relref "starter.md" >}}) を利用する場合、
`komapper-datetime-jdbc`や`komapper-datetime-r2dbc`の宣言は不要です。
{{< /alert >}}
