---
title: "SCHEMA Query"
linkTitle: "SCHEMA"
weight: 60
description: >
---

## Overview {#overview}

The SCHEMA query generates the following DDL statements from the mapping definition:

- CREATE/DROP statements for tables corresponding to entity classes
- CREATE/DROP statements for sequences that generate the entity's ID values

{{< alert title="Note" >}}
It is recommended that SCHEMA queries are used only for development purposes.
For example, it is suitable for the following use cases:

- Create a sample application
- Get a base DDL statements in the early phases of the development project

DDL statements for production environments should be managed separately.
{{< /alert >}}

## create

To generate CREATE statements, call the `create` function:

```kotlin
val query: Query<Unit> = QueryDsl.create(Meta.address, Meta.employee)
/*
create table if not exists ADDRESS (ADDRESS_ID integer not null, STREET varchar(500) not null, VERSION integer not null, constraint pk_ADDRESS primary key(ADDRESS_ID));
create table if not exists EMPLOYEE (EMPLOYEE_ID integer not null, EMPLOYEE_NO integer not null, EMPLOYEE_NAME varchar(500) not null, MANAGER_ID integer, HIREDATE date not null, SALARY bigint not null, DEPARTMENT_ID integer not null, ADDRESS_ID integer not null, VERSION integer not null, constraint pk_EMPLOYEE primary key(EMPLOYEE_ID));
*/
```

{{< alert title="Note" >}}
Even if the object to be created already exists, no exception is thrown.
Also, subsequent processing is not interrupted.
{{< /alert >}}

## drop

To generate DROP statements, call the `drop` function:

```kotlin
val query: Query<Unit> = QueryDsl.drop(Meta.address, Meta.employee)
/*
drop table if exists ADDRESS;
drop table if exists EMPLOYEE;
*/
```

{{< alert title="Note" >}}
Even if the object to be dropped does not already exist, no exception is thrown.
Also, subsequent processing is not interrupted.
{{< /alert >}}

## options

To customize the behavior of the query, call the `options` function.
The `options` function accept a lambda expression whose parameter represents default options.
Call the `copy` function on the parameter to change its properties:

```kotlin
val query: Query<Unit> = QueryDsl.create(Meta.address, Meta.employee).options {
    it.copty(
      queryTimeoutSeconds = 5
    )
}
```

The options that can be specified are as follows:

queryTimeoutSeconds
: Default is `null` to indicate that the driver value should be used.

suppressLogging
: Whether to suppress SQL log output. Default is `false`.

Properties explicitly set here will be used in preference to properties with the same name that exist
in [executionOptions]({{< relref "../../database-config/#executionoptions" >}}).
