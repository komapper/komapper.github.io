---
title: "Starter"
linkTitle: "Starter"
weight: 100
description: >
---

{{% pageinfo %}}
We are currently working on the translation from Japanese to English. We would appreciate your cooperation.
{{% /pageinfo %}}

## 概要 {#overview}

Starterモジュールを使うとKomapperを使ったプロジェクトを簡単に始められます。
Starterモジュールにはいくつかの種類があります。

{{< alert title="Note" >}}
必要最低限の依存ライブラリで動作させたい場合はStarterライブラリを用いない方が良いでしょう。
{{< /alert >}}

## シンプルなスターター {#simple-starter}

### komapper-starter-jdbc

このスターターはKomapperをJDBCと組み合わせて動かすのに必要かつ便利なライブラリを含みます。
利用するにはGradleの依存関係の宣言で次のように記述します。

```kotlin
val komapperVersion: String by project
dependencies {
    implementation("org.komapper:komapper-starter-jdbc:$komapperVersion")
}
```

### komapper-starter-r2dbc

このスターターはKomapperをR2DBCと組み合わせて動かすのに必要かつ便利なライブラリを含みます。
利用するにはGradleの依存関係の宣言で次のように記述します。

```kotlin
val komapperVersion: String by project
dependencies {
    implementation("org.komapper:komapper-starter-r2dbc:$komapperVersion")
}
```

## Spring Boot連携のためのスターター {#spring-boot-starter}

### komapper-spring-boot-starter-jdbc

このスターターはKomapperをJDBCと組み合わせてSpring Boot上で動かすのに必要かつ便利なライブラリを含みます。
利用するにはGradleの依存関係の宣言で次のように記述します。

```kotlin
val komapperVersion: String by project
dependencies {
    implementation("org.komapper:komapper-spring-boot-starter-jdbc:$komapperVersion")
}
```

このスターターを使う上で特別な設定は不要です。
Spring Bootの仕様に従ってJDBCの接続文字列をapplication.propertiesに記述すれば動きます。

```
spring.datasource.url=jdbc:h2:mem:example-spring-boot;DB_CLOSE_DELAY=-1
```

### komapper-spring-boot-starter-r2dbc

このスターターはKomapperをR2DBCと組み合わせてSpring Boot上で動かすのに必要かつ便利なライブラリを含みます。
利用するにはGradleの依存関係の宣言で次のように記述します。

```kotlin
val komapperVersion: String by project
dependencies {
    implementation("org.komapper:komapper-spring-boot-starter-r2dbc:$komapperVersion")
}
```

このスターターを使う上で特別な設定は不要です。
Spring Bootの仕様に従ってR2DBCの接続文字列をapplication.propertiesに記述すれば動きます。

```
spring.r2dbc.url=r2dbc:h2:mem:///example;DB_CLOSE_DELAY=-1
```
