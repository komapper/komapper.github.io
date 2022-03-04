---
title: "Logging"
linkTitle: "Logging"
weight: 50
description: >
  ロギング
---

## 概要 {#overview}

KomapperはSQLやトランザクションに関するログを出力します。

出力先のデフォルトは標準出力ですが変更可能です。

## ログカテゴリ {#log-category}

Komapperが出力するログのカテゴリは以下に示す4つです。

- org.komapper.Sql
- org.komapper.SqlWithArgs
- org.komapper.Transaction
- org.komapper.Other

### org.komapper.Sql

このカテゴリのログは次のようなバインド変数である`?`が含まれた形式のSQLでDEBUGレベルで出力されます。

```sql
insert into ADDRESS (STREET, VERSION, CREATED_AT, UPDATED_AT) values (?, ?, ?, ?)
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION, t0_.CREATED_AT, t0_.UPDATED_AT from ADDRESS as t0_ where t0_.ADDRESS_ID = ?
```

### org.komapper.SqlWithArgs

このカテゴリのログは次のようなバインド変数である`?`が実際の引数で置換された形式のSQLでTRACEレベルで出力されます。

```sql
insert into ADDRESS (STREET, VERSION, CREATED_AT, UPDATED_AT) values ('street A', 0, '2021-07-31T21:23:24.511', '2021-07-31T21:23:24.511')
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION, t0_.CREATED_AT, t0_.UPDATED_AT from ADDRESS as t0_ where t0_.ADDRESS_ID = 1
```

### org.komapper.Transaction

このカテゴリのログは次のようなトランザクションの開始やコミットを示すメッセージでTRACEレベルで出力されます。

```sql
Begin: JdbcTransaction(id=81f6e616-9fb2-4dd7-a7c1-0f05ff3adb98, name=JDBC_TEST)
```

### org.komapper.Other

このカテゴリは他のどのカテゴリにも属さないログを表します。
任意のログレベルで出力されます。

## LoggerFacadeの利用例 {#loggerfacade}

LoggerFacadeを使えば、ログメッセージやログレベルの変更ができます。

### SQLのログレベルを変更する場合の設定例 {#loggerfacade-loglevel-example}

例えば、SQLのログレベルをDEBUGからINFOに変更したい場合は、以下のような実装を作成します。

```kotlin
class MyLoggerFacade(private val logger: Logger): LoggerFacade by DefaultLoggerFacade(logger) {
    override fun sql(statement: Statement, format: (Int, StatementPart.PlaceHolder) -> CharSequence) {
        logger.info(LogCategory.SQL.value) {
            statement.toSql(format)
        }
    }
}
```

上記の実装クラスを`JdbcDatabaseConfig`に設定するには次のように記述します。

```kotlin
val dataSource: DataSource = ..
val dialect: JdbcDialect = ..
val config: JdbcDatabaseConfig = object: DefaultJdbcDatabaseConfig(dataSource, dialect) {
  override val loggerFacade: LoggerFacade by {
    MyLoggerFacade(logger)
  }
}
val db = JdbcDatabase(config)
```

## SLF4Jの利用 {#slf4j}

[SLF4J](http://www.slf4j.org/) を利用してログ出力するにはkomapper-slf4jモジュールをGradleの依存関係の宣言に含めます。

```kotlin
val komapperVersion: String by project

dependencies {
    runtimeOnly("org.komapper:komapper-slf4j:$komapperVersion")
}
```

また、SLF4Jの実装として [Logback](http://logback.qos.ch/) を使う場合はlogback-classicモジュールも依存関係の宣言に含めます。

```kotlin
val komapperVersion: String by project

dependencies {
    runtimeOnly("org.komapper:komapper-slf4j:$komapperVersion")
    runtimeOnly("ch.qos.logback:logback-classic:1.2.5")
}
```

{{< alert title="Note" >}}
Komapperが提供する各種starterモジュールはSLF4JとLogbackの設定を含んでいます。
starterモジュールを使う場合、上記の設定は不要です。
{{< /alert >}}

### LogbackでSQLのログ出力をする場合の設定例 {#slf4j-logback-example}

次のようなlogback.xmlをsrc/main/resourcesの下に配置すると、バインド変数`?`が含まれた形式のSQLがコンソールに出力されます。

```xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <logger name="org.komapper.Sql" level="debug"/>

    <root level="info">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
```

バインド変数を引数で置換した形式のSQLをコンソールに出力したい場合は以下のように設定してください。

```xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <logger name="org.komapper.SqlWithArgs" level="trace"/>

    <root level="info">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
```
