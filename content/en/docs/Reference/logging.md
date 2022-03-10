---
title: "Logging"
linkTitle: "Logging"
weight: 50
description: >
---

## Overview {#overview}

Komapper outputs log messages related to SQL and transactions.

The default output destination is standard output, but it can be changed.

## Log categories {#log-category}

Komapper has four log categories, as follows:

- org.komapper.Sql
- org.komapper.SqlWithArgs
- org.komapper.Transaction
- org.komapper.Other

### org.komapper.Sql

In the `org.komapper.Sql` category, SQL statements with bind variables are output at the DEBUG level:

```sql
insert into ADDRESS (STREET, VERSION, CREATED_AT, UPDATED_AT) values (?, ?, ?, ?)
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION, t0_.CREATED_AT, t0_.UPDATED_AT from ADDRESS as t0_ where t0_.ADDRESS_ID = ?
```

### org.komapper.SqlWithArgs

In the `org.komapper.SqlWithArgs` category, 
SQL statements in which bind variables are resolved with arguments are output at the TRACE level:

```sql
insert into ADDRESS (STREET, VERSION, CREATED_AT, UPDATED_AT) values ('street A', 0, '2021-07-31T21:23:24.511', '2021-07-31T21:23:24.511')
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION, t0_.CREATED_AT, t0_.UPDATED_AT from ADDRESS as t0_ where t0_.ADDRESS_ID = 1
```

### org.komapper.Transaction

In the `org.komapper.Transaction` category, 
messages indicating transaction beginning, commit, and rollback are output at the TRACE level:

```
Begin:    JdbcTransaction(id=032d9623-919f-43d4-ad00-f7d4c1518393, name=null)
Rollback: JdbcTransaction(id=032d9623-919f-43d4-ad00-f7d4c1518393, name=null)
Begin:    JdbcTransaction(id=a108b824-3353-475c-a29b-cb1575951803, name=null)
Commit:   JdbcTransaction(id=a108b824-3353-475c-a29b-cb1575951803, name=null)
```

### org.komapper.Other

In the `org.komapper.Other` category, any message will be output at any log level.

## Use of LoggerFacade {#loggerfacade}

To change log messages and log levels, create your `LoggerFacade` implementation.

### Example configuration for changing SQL log level {#loggerfacade-loglevel-example}

For example, if you want to change the SQL log level from DEBUG to INFO, 
create an implementation like the following:

```kotlin
class MyLoggerFacade(private val logger: Logger): LoggerFacade by DefaultLoggerFacade(logger) {
    override fun sql(statement: Statement, format: (Int, StatementPart.PlaceHolder) -> CharSequence) {
        logger.info(LogCategory.SQL.value) {
            statement.toSql(format)
        }
    }
}
```
To set the above implementation to `JdbcDatabaseConfig`, write as follows:

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

## Use of SLF4J {#slf4j}

To use [SLF4J](http://www.slf4j.org/) as a logging library, 
include the komapper-slf4j module in the Gradle dependency declaration:

```kotlin
val komapperVersion: String by project
dependencies {
    runtimeOnly("org.komapper:komapper-slf4j:$komapperVersion")
}
```

Also, if [Logback](http://logback.qos.ch/) is used as an implementation of SLF4J, 
the logback-classic module should be included in the dependency declaration:

```kotlin
val komapperVersion: String by project

dependencies {
    runtimeOnly("org.komapper:komapper-slf4j:$komapperVersion")
    runtimeOnly("ch.qos.logback:logback-classic:1.2.5")
}
```

{{< alert title="Note" >}}
The above dependency declaration is not necessary when using [Starters]({{< relref "Starter" >}}).
{{< /alert >}}

### Example configuration for SQL logging with Logback {#slf4j-logback-example}

Place the following logback.xml under src/main/resources:

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

In the above example, the log category `org.komapper.Sql` is used.
Instead, to use the log category `org.komapper.SqlWithArgs`, write as follows:

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
