---
title: "Database Config"
weight: 12
description: >
---

## Overview {#overview}

DatabaseConfig can be specified to customize the behavior of the Database instance.

Here is an example of creating a `JdbcDatabase` instance.

```kotlin
val dataSource: DataSource = ..
val dialect: JdbcDialect = ..
val config: JdbcDatabaseConfig = object: DefaultJdbcDatabaseConfig(dataSource, dialect) {
    // you can override properties here
}
val db = JdbcDatabase(config)
```

Here is an example of creating a `R2dbcDatabase` instance.

```kotlin
val connectionFactory: ConnectionFactory = ..
val dialect: R2dbcDialect = ..
val config: R2dbcDatabaseConfig = object: DefaultR2dbcDatabaseConfig(connectionFactory, dialect) {
  // you can override properties here
}
val db = R2dbcDatabase(config)
```

## Properties {#properties}

The behavior of the Database instance can be customized by overriding
the `JdbcDatabaseConfig` and `R2dbcDatabaseConfig` properties described below
or by using the service loader mechanism.

### clockProvider

The `clockProvider` provides the `Clock` object which is used to set timestamps on entity class properties.

Target entity class properties are those annotated with `@KomapperCreatedAt` or `@KomapperUpdatedAt`.

By default, this property returns a provider that generates the current time using the system default zone ID.

### executionOptions

Default runtime options for JDBC and R2DBC.
The following settings are available:

batchSize
: Batch size for batch updates with INSERT, UPDATE, and DELETE.
Default is `null`.
If the batch size is not specified even in the query options, `10` is used.

fetchSize
: Fetch size when a SELECT statement is issued.
Default is `null` to indicate that the driver value should be used.

maxRows
: The maximum number of rows when a SELECT statement is issued.
Default is `null` to indicate that the driver value should be used.

queryTimeoutSeconds
: Query timeout in seconds. 
Default is `null` to indicate that the driver value should be used.

suppressLogging
: Whether to suppress SQL log output.
Default is false.

All of these can be overridden by the query options.

### logger

The `logger` is an adapter for various logger implementations. 

By default, this property is resolved by the service loader.
If the service loader cannot resolve the logger, this property returns a logger that uses standard output.

The following modules provide logger implementations:

- komapper-slf4j

See also [Logging]({{< relref "logging.md" >}}).

### loggerFacade

The `loggerFacade` accepts log output instructions, formats log messages, and sends them to the logger.
All log output from Komapper goes through this facade.

To change the log message or log level, switch the facade implementation.

See also [Logging]({{< relref "logging.md" >}}).

### statementInspector

The `statementInspector` inspects `org.komapper.core.Statement` just before SQL execution.

By default, this property is resolved by the service loader.
If the service loader cannot resolve the statementInspector, this property returns an inspector that does nothing.

The following modules provide inspector implementations:

- komapper-sqlcommenter

### templateStatementBuilder

The `templateStatementBuilder` is a builder that constructs `org.komapper.core.Statement` from SQL templates.

By default, this property is resolved by the service loader.
If the service loader cannot resolve the builder, this property throws an exception.

The following modules provide builder implementations:

- komapper-template

See also [TEMPLATE]({{< relref "Query/QueryDsl/template.md" >}}).

### statisticManager

The `statisticManager` manages statistical information related to SQL execution.
It retains the following information for each SQL statement:

- execution count
- execution maximum time in milliseconds
- execution minimum time in milliseconds
- total execution time in milliseconds
- average execution time in milliseconds

To enable the `statisticManager`, set `enableStatistics = true` as follows:

```kotlin
val config: JdbcDatabaseConfig = DefaultJdbcDatabaseConfig(dataSource, dialect, enableStatistics = true)
```

```kotlin
val config: R2dbcDatabaseConfig = DefaultR2dbcDatabaseConfig(connectionFactory, dialect, enableStatistics = true)
```

By default, this property is resolved by the service loader.
If the service loader cannot resolve the statisticManager, this property returns a default statisticManager.

The default statisticManager collects statistical information indefinitely while enabled. 
To prevent memory exhaustion, either call the `clear` method of the statisticManager periodically 
or create an appropriate implementation class for `org.komapper.core.StatisticManager`.