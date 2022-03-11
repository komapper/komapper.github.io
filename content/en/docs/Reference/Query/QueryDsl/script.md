---
title: "SCRIPT Queries"
linkTitle: "SCRIPT"
weight: 60
description: >
---

## Overview {#overview}

The SCRIPT query executes any SQL script.

The SCRIPT query is constructed by calling `QueryDsl.executeScript` and subsequent functions.

If a duplicate key is detected during SCRIPT query execution,
the `org.komapper.core.UniqueConstraintException` is thrown.

## executeScript

To execute an SQL script, call the `executeScript` function:

```kotlin
val query: Query<Unit> = QueryDsl.executeScript("""
    drop table if exists example;
    create table example (id integer not null primary key, value varchar(20));
    insert into example (id, value) values(1, 'test');
""".trimIndent())
```

## options

To customize the behavior of the query, call the `options` function.
The `options` function accept a lambda expression whose parameter represents default options.
Call the `copy` function on the parameter to change its properties:

```kotlin
val query: Query<Unit> = QueryDsl.executeScript("""
    drop table if exists example;
    create table example (id integer not null primary key, value varchar(20));
    insert into example (id, value) values(1, 'test');
""".trimIndent()).options {
    it.copty(
      queryTimeoutSeconds = 5
    )
}
```

The options that can be specified are as follows:

queryTimeoutSeconds
: Query timeout in seconds. Default is `null` to indicate that the driver value should be used.

suppressLogging
: Whether to suppress SQL log output. Default is `false`.

separator
: The separator of the SQL statement. Default is `;`.

Properties explicitly set here will be used in preference to properties with the same name that exist
in [executionOptions]({{< relref "../../database-config/#executionoptions" >}}).
