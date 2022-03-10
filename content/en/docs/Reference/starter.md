---
title: "Starters"
weight: 100
description: >
---

## Overview {#overview}

The Starter module makes it easy to start a project using Komapper.
There are several types of starter modules.

## Simple starter {#simple-starter}

### komapper-starter-jdbc

This starter includes all the necessary and useful libraries to run Komapper with JDBC.
To use it, you must include the following in your Gradle dependency declaration:

```kotlin
val komapperVersion: String by project
dependencies {
    implementation("org.komapper:komapper-starter-jdbc:$komapperVersion")
}
```

### komapper-starter-r2dbc

This starter includes all the necessary and useful libraries to run Komapper with R2DBC.
To use it, you must include the following in your Gradle dependency declaration:

```kotlin
val komapperVersion: String by project
dependencies {
    implementation("org.komapper:komapper-starter-r2dbc:$komapperVersion")
}
```

## Spring Boot starter {#spring-boot-starter}

### komapper-spring-boot-starter-jdbc

This starter includes all the necessary and useful libraries to 
run Komapper on Spring Boot in combination with JDBC.
To use it, you must include the following in your Gradle dependency declaration:

```kotlin
val komapperVersion: String by project
dependencies {
    implementation("org.komapper:komapper-spring-boot-starter-jdbc:$komapperVersion")
}
```

No special configuration is required to use this starter.
Just write the JDBC connection string in your application.properties 
according to the Spring Boot specification.

```
spring.datasource.url=jdbc:h2:mem:example-spring-boot;DB_CLOSE_DELAY=-1
```

### komapper-spring-boot-starter-r2dbc

This starter includes all the necessary and useful libraries to
run Komapper on Spring Boot in combination with R2DBC.
To use it, you must include the following in your Gradle dependency declaration:

```kotlin
val komapperVersion: String by project
dependencies {
    implementation("org.komapper:komapper-spring-boot-starter-r2dbc:$komapperVersion")
}
```

No special configuration is required to use this starter.
Just write the R2DBC connection string in your application.properties
according to the Spring Boot specification.

```
spring.r2dbc.url=r2dbc:h2:mem:///example;DB_CLOSE_DELAY=-1
```
