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

Komapperが提供する [Gradleプラグイン]({{< relref "../Reference/gradle-plugin" >}}) を使わない場合、
より小さいバージョンでも動作します。
しかし、その場合であっても、 [KSP](https://github.com/google/ksp) がサポートするGradleのバージョン以上でなければいけません。

KSPがサポートするバージョンについては下記リンク先に記述があります。

- https://github.com/google/ksp/blob/main/docs/faq.md#besides-kotlin-are-there-other-version-requirements-to-libraries

### どのバージョンのMavenをサポートしますか？ {#maven-version}

現在、Mavenはサポートしていません。
今後、[KSP](https://github.com/google/ksp) がMavenをサポートしたらKomapperにおいてもサポートできます。

### どのデータベースのどのバージョンをサポートしますか？ {#database-version}

6つのデータベースをサポートしています。

| Database           | version | JDBC support | R2DBC support |
|--------------------|---------|:------------:|:-------------:|
| H2 Database        | 2.1.210 |      v       |       v       |
| MariaDB            | 10.6.3  |      v       |      N/A      |
| MySQL              | 8.0.25  |      v       |      N/A      |
| Oracle Database XE | 18.4.0  |      v       |       v       |
| PostgreSQL         | 12.9    |      v       |       v       |
| SQL Server         | 2019    |      v       |       v       |

バージョンはサポートする最小バージョンを表していますが、より小さいバージョンでも動作することがあります。

サポートしている接続タイプは JDBC 4.3 と R2DBC 0.9.1 です。
