---
title: "MariaDB - R2DBC"
linkTitle: "MariaDB - R2DBC"
weight: 21
description: >
  MariaDBにR2DBCでアクセスするための方言
---

## 概要 {#overview}

利用するにはGradleの依存関係の宣言で次のように記述します。

```kotlin
val komapperVersion: String by project
dependencies {
    implementation("org.komapper:komapper-dialect-mariadb-r2dbc:$komapperVersion")
}
```

プログラムの中で利用するには以下のようにインスタンス化します。

```kotlin
val dialect = MariaDbR2dbcDialect()
```

## データ型の対応関係 {#data-type-mapping}

{{% pageinfo %}} Under Construction {{% /pageinfo %}}
