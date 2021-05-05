
---
title: "Examples"
linkTitle: "Examples"
weight: 3
date: 2017-01-05
description: >
  See your project in action!
---

We have two examples:

- [Console Application](https://github.com/komapper/komapper-examples/tree/main/console/)
- [Spring Boot Web Application](https://github.com/komapper/komapper-examples/tree/main/spring-boot)

## Clone

To run locally, clone the [komapper/komapper-examples](https://github.com/komapper/komapper-examples) repository:

```sh
$ git clone https://github.com/komapper/komapper-examples.git
```

```sh
$ cd komapper-examples
```

## Run

To run the application, execute Gradle command.

### Console Application

Execute the following command:

```sh
$ ./gradlew :console:run
```

### Spring Boot Web Application

Execute the following command:

```sh
./gradlew :spring-boot:bootRun
```

Once the application starts, open the following URL: `http://localhost:8080`