---
title: "Examples"
linkTitle: "Examples"
weight: 3
description: >
  Here are some examples
---

## overview {#overview}

We provide several sample applications including JDBC access, R2DBC access, 
[Spring Boot](https://spring.io/projects/spring-boot) integration, 
[Ktor](https://spring.io/projects/spring-boot) integration.

## Requirements {#prerequisites}

These are requirements for running the sample applications:

- JDK 11 or later version
- Docker (used by some applications)

## Get the repository {#clone}

Clone the [komapper/komapper-examples](https://github.com/komapper/komapper-examples) repository.

```sh
$ git clone https://github.com/komapper/komapper-examples.git
```

```sh
$ cd komapper-examples
```

The repository has a multi-project configuration of Gradle.
Each subproject is implemented as a sample application.

## Sample applications {#applications}

### console-jdbc

This project is a console application that uses JDBC to access the database.
To run it, execute the following command:

```sh
$ ./gradlew :console-jdbc:run
```

### console-r2dbc

This project is a console application that uses R2DBC to access the database.
To run it, execute the following command:

```sh
$ ./gradlew :console-r2dbc:run
```

### quarkus-jdbc

This project is a Quarkus web application that uses JDBC to access PostgreSQL database.

See[README](https://github.com/komapper/komapper-examples/blob/main/quarkus-jdbc/README.md) for more details.

### spring-boot-jdbc

This project is a Spring Boot web application that uses JDBC to access the database.
To run it, execute the following command:

```sh
$ ./gradlew :spring-boot-jdbc:bootRun
````

Once the application is running, open `http://localhost:8080` in your browser.
The message returned from the database will be displayed in your browser.

To add a message to the database, pass it as a query parameter, like `http://localhost:8080/?text=Hi`.
If you open `http://localhost:8080` again, you will see the list with the added data.

### spring-boot-r2dbc

This project is a Spring Boot web application that uses R2DBC to access the database.
To run it, execute the following command:

```sh
$ ./gradlew :spring-boot-r2dbc:bootRun
````

Once the application is running, open `http://localhost:8080` in your browser.
The message returned from the database will be displayed in your browser.

To add a message to the database, pass it as a query parameter, like `http://localhost:8080/?text=Hi`.
If you open `http://localhost:8080` again, you will see the list with the added data.

### spring-native-jdbc

This project is a Spring Boot web application that supports 
[Spring Native](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/)
and uses JDBC to access the database.

You can build the native application with the following command:

```sh
$ ./gradlew :spring-native-jdbc:bootBuildImage
````

To run the application, start Docker as follows:

```sh
$ docker run --rm -p 8080:8080 docker.io/library/spring-native-jdbc:0.0.1
````

Once the application is running, open `http://localhost:8080` in your browser.
The message returned from the database will be displayed in your browser.

To add a message to the database, pass it as a query parameter, like `http://localhost:8080/?text=Hi`.
If you open `http://localhost:8080` again, you will see the list with the added data.

### spring-native-r2dbc

This project is a Spring Boot web application that supports
[Spring Native](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/)
and uses R2DBC to access the database.

In the current version, there is a limitation regarding Kotlin coroutines.

- https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/#limitations

### repository-pattern-jdbc

This project contains an example implementation of the repository pattern.

To run a test that invokes the repository, execute the following command:

```sh
$ ./gradlew :repository-pattern-jdbc:check
```

### codegen

This project uses the [Gradle plugin]({{< relref "../Reference/gradle-plugin" >}}).
to generate the source code for entity classes from database metadata.

{{< alert title="Note" >}}
Since this project uses [Testcontainers](https://www.testcontainers.org/),
Docker is required.
{{< /alert >}}

The configuration of the Komapper Gradle plugin is described in the `komapper` block in the build.gradle.kts file.
In this example, we will generate code from MySQL and PostgreSQL.

To generate code from MySQL, run the following command:

```sh
$ ./gradlew :codegen:komapperMysqlGenerator
```

To generate code from PostgreSQL, issue the following command:

```sh
$ ./gradlew :codegen:komapperPostgresqlGenerator
```

To generate code from both MySQL and PostgreSQL at onece, execute the following command:

```sh
$ ./gradlew :codegen:komapperGenerator
```

The generated code will be output under `codgen/src/main/kotlin`.

### comparison-with-exposed

This project is based on the [JetBrains Exposed sample code](https://github.com/JetBrains/Exposed#sql-dsl).
and rewritten for Komapper.

To run it, execute the following command:

```sh
$ ./gradlew :comparison-with-exposed:run
```

### jpetstore

This project is a Spring Boot web application that uses JDBC to access the database.

The application is based on [jpetstore-6](https://github.com/mybatis/jpetstore-6),
which was created by the MyBatis team.

To run it, execute the following command:

```sh
$ ./gradlew :jpetstore:bootRun
````

Once the application is running, open `http://localhost:8080` in your browser.
Where you are prompted to sign in, you can use the following username and password.

- username: jpetstore
- password: jpetstore

### kweet

This project is a Spring Boot web application that uses R2DBC to access the database.

The application is based on [Kweet](https://github.com/ktorio/ktor-samples/tree/main/kweet),
which was created by the Ktor team.

To run it, execute the following command:

```sh
$ ./gradlew :kweet:run
````

Once the application is running, open `http://localhost:8080` in your browser.
