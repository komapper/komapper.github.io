
---
title: "Examples"
linkTitle: "Examples"
weight: 3
date: 2017-01-05
description: >
  See your project in action!
---

We have following examples:

- [Console Application](https://github.com/komapper/komapper-examples/tree/main/console/)
- [Spring Boot Web Application](https://github.com/komapper/komapper-examples/tree/main/spring-boot)
- [Code generation from Database](https://github.com/komapper/komapper-examples/tree/main/codegen/)

## Clone

To run locally, clone the [komapper/komapper-examples](https://github.com/komapper/komapper-examples) repository:

```sh
$ git clone https://github.com/komapper/komapper-examples.git
```

```sh
$ cd komapper-examples
```

## Try it out!

### Console Application

Execute the following command:

```sh
$ ./gradlew :console:run
```

### Spring Boot Web Application

Execute the following command:

```sh
$ ./gradlew :spring-boot:bootRun
```

Once the application starts, open the following URL: `http://localhost:8080`

### Code generation from Database

Execute the following command to start MySQL and PostgreSQL databases:

```sh
$ docker compose -f codegen/docker-compose.yml up
```

Once the above databases start,
open another terminal and execute the following command to generate code:

```sh
$ ./gradlew :codegen:komapperGenerator
```

Check that source code has been generated under the `src` directory.
