---
title: "Examples"
linkTitle: "Examples"
weight: 3
description: >
  Here are some examples.
---

## Overview

We have the following examples:

- Console applications
- Spring Boot web applications
- Code generation from databases

## Clone

To run locally, clone the [komapper/komapper-examples](https://github.com/komapper/komapper-examples) repository:

```sh
$ git clone https://github.com/komapper/komapper-examples.git
```

```sh
$ cd komapper-examples
```

## Try it out!

### Console applications

Execute the following command to run the JDBC version:

```sh
$ ./gradlew :console-jdbc:run
```

Execute the following command to run the R2DBC version:

```sh
$ ./gradlew :console-r2dbc:run
```

### Spring Boot web applications

Execute the following command to run the JDBC version:

```sh
$ ./gradlew :spring-boot-jdbc:bootRun
```

Execute the following command to run the R2DBC version:

```sh
$ ./gradlew :spring-boot-r2dbc:bootRun
```

Once the application starts, open the following URL: `http://localhost:8080`

To add data, use the `text` parameter as follows: `http://localhost:8080/?text=Hi`

When you open the following URL `http://localhost:8080` again, the added data will be shown.

### Code generation from databases

We provide the gradle plugin that generates code from database schemas.

Execute the following command to generate code from MySQL:

```sh
$ ./gradlew :codegen:komapperMysqlGenerator
```

Execute the following command to generate code from PostgreSQL:

```sh
$ ./gradlew :codegen:komapperPostgresqlGenerator
```

Execute the following command to generate code from both MySQL and PostgreSQL:

```sh
$ ./gradlew :codegen:komapperGenerator
```

Make sure the source code is generated under the `codgen/src/main/kotlin` directory.
