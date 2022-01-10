---
title: "FAQ"
linkTitle: "FAQ"
weight: 500
description: >
  よくある質問と回答
---

## バージョン要件 {#version-requirements}

Komapperと一緒に使う他のソフトウェアのバージョンに関するFAQです。

### どのバージョンのKotlinをサポートしますか？ {#kotlin-version}

Kotlin 1.5.31 以上のバージョンです。

### どのバージョンのJava（JDKおよびJVM）をサポートしますか？ {#java-version}

Java 11 以上のバージョンです。

### どのバージョンのGradleをサポートしますか？ {#gradle-version}

Gradle 7.2 以上のバージョンです。

Komapperが提供する [Gradle Plugin]({{< relref "../Reference/gradle-plugin" >}}) を使わない場合、
より小さいバージョンでも動作します。
しかし、その場合であっても、 [KSP](https://github.com/google/ksp) がサポートするGradleのバージョン以上でなければいけません。

KSPがサポートするバージョンについては下記リンク先に記述があります。

- https://github.com/google/ksp/blob/main/docs/faq.md#besides-kotlin-are-there-other-version-requirements-to-libraries

### どのバージョンのMavenをサポートしますか？ {#maven-version}

現在、Mavenはサポートしていません。
今後、[KSP](https://github.com/google/ksp) がMavenをサポートしたらKomapperにおいてもサポートできます。

### どのデータベースのどのバージョンをサポートしますか？ {#database-version}

4つのデータベースについてJDBCとR2DBCによる接続をサポートしています。

| Database           | Version | JDBC | R2DBC |
|--------------------|:-------:|:----:|:-----:|
| H2 Database Engine | 1.4.200 |  v   |   v   |
| MariaDB            |  10.6   |  v   |   v   |
| MySQL              |   8.0   |  v   |   v   |
| PostgreSQL         |  13.0   |  v   |   v   |

バージョンはサポートする最小バージョンを表していますが、より小さいバージョンでも動作することがあります。
