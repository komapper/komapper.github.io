---
title: "Getting Started"
linkTitle: "Getting Started"
weight: 2
description: >
  What does your user need to know to try your project?
---

{{% pageinfo %}}
This is a placeholder page that shows you how to use this template site.
{{% /pageinfo %}}

Information in this section helps your user try your project themselves.

* What do your users need to do to start using your project? This could include downloading/installation instructions, including any prerequisites or system requirements.

* Introductory “Hello World” example, if appropriate. More complex tutorials should live in the Tutorials section.

Consider using the headings below for your getting started page. You can delete any that are not applicable to your project.

## Prerequisites

- JDK 11 or later
- Gradle 7 or later
- Kotlin 15 or later

## Setup

Setup using Gradle Kotlin DSL.

```groovy
dependencies {
    implementation("org.komapper:komapper-starter:0.4.0")
    ksp("org.komapper:komapper-processor:0.4.0")
}
```

To work ksp, see google/ksp [quickstart.md](https://github.com/google/ksp/blob/master/docs/quickstart.md).

## Try it out!

Create an `Employee.kt` file and include the following code:

```kotlin
@KmEntity
data class Employee(@KmId val id: Int, val name: String) {
    companion object
}
```

Run the following command:

```sh
$ ./grdlew build
```

Check that the `_Employee.kt` file has been generated under the `build/generated/ksp/main/kotlin` directory.