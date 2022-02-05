---
title: "FAQ"
linkTitle: "FAQ"
weight: 500
description: >
  Frequently Asked Questions
---

## Version Requirements {#version-requirements}

### Which Kotlin version do you support？ {#kotlin-version}

Kotlin 1.5.31 or later.

### Which Java (JDK and JVM) version do you support？ {#java-version}

Java 11 or later.

### Which Gradle version do you support？ {#gradle-version}

Gradle 7.2 or later.

### Which Maven version do you support？ {#maven-version}

Currently, we don't support Maven.

### Which databases do you support？ {#database-version}

We support the following databases and connectivity types:

| Database           | version | JDBC support | R2DBC support |
|--------------------|---------|:------------:|:-------------:|
| H2 Database        | 2.1.210 |      v       |       v       |
| MariaDB            | 10.6.3  |      v       |      N/A      |
| MySQL              | 8.0.25  |      v       |      N/A      |
| Oracle Database XE | 18.4.0  |      v       |       v       |
| PostgreSQL         | 12.9    |      v       |       v       |
| SQL Server         | 2019    |      v       |       v       |

The version number above is the minimum version.

Supported connectivity types are JDBC 4.3 and R2DBC 0.9.1.
