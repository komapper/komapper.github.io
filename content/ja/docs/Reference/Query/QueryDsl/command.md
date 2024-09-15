---
title: " COMMANDクエリ"
linkTitle: "コマンド"
weight: 51
description: >
  コマンドを利用するクエリ
---

## 概要 {#overview}

COMMANDクエリは[TEMPLATEクエリ]({{< relref "template" >}})をベースにしています。

コマンドは、[SQLテンプレート]({{< relref "template#sql-template" >}})とパラメータを1つの単位として扱う機能です。クラスに`org.komapper.annotation.KomapperCommand`アノテーションを付けてSQLテンプレートを定義し、クラスのプロパティにパラメータを定義します。SQLの結果をどのように処理するかは、特定のクラスを継承することで表現されます。

COMMANDクエリを使用するには、Gradleビルドスクリプトに次の依存関係宣言を含める必要があります。

```kotlin
val komapperVersion: String by project
dependencies {
    implementation("org.komapper:komapper-template:$komapperVersion")
}
```

{{< alert title="Note" >}}
すべての [スターター]({{< relref "../../Starter" >}}) は上記の設定を含んでいます。
したがって、Starterを使う場合には上記の設定は不要です。
{{< /alert >}}

{{< alert title="Note" >}}
`komapper-template`モジュールは内部でリフレクションを使います。
{{< /alert >}}

## コマンド {#command}

下記は、複数件を取得するコマンドの例です。

```kotlin
@KomapperCommand("""
    select * from ADDRESS where street = /*street*/'test'
""")
class ListAddresses(val street: String): Many<Address>({ selectAsAddress() })
```

コマンドを定義しビルドを実行すると、`QueryDsl`に`execute`という名前の拡張関数が生成されます。したがって、上記のコマンドは次のようにして実行できます。

```kotlin
val query: Query<List<Address>> = QueryDsl.execute(ListAddresses("STREET 10"))
```

コマンドは、[fromTemplate]({{< relref "template#fromtemplate" >}})や[executeTemplate]({{< relref "template#executetemplate" >}})を使う方法に比べて、コンパイル時にSQLテンプレートを検証できるという利点があります。具体的には次のことが可能です。

- SQLテンプレートの構文上の誤りを検出できます。例えば、`/*%end*/`が不足している場合コンパイルエラーとなります。
- 利用されていないパラメータを検出できます。利用されていないパラメータが見つかった場合は警告メッセージを出力します。なお、パラメータに`org.komapper.annotation.KomapperUnused`アノテーションを付与することで警告を抑制できます。
- パラメータの型やメンバを検証できます。例えば、String型の`name`がパラメータの場合、SQLテンプレート上で`/* name.unknown */`のように存在しないメンバにアクセスしようとするとコンパイルエラーとなります。

{{< alert title="Note" >}}
コマンドのクラスは、トップレベルのクラス、ネストされたクラス、インナークラスとして定義できます。ローカルクラスとしては定義できません。
{{< /alert >}}

コマンドには5つの種類あります。

- One
- Many
- Exec
- ExecReturnOne
- ExecReturnMany

### One {#command-one}

1件を取得するコマンドは`org.komapper.core.One`を継承します。

```kotlin
@KomapperCommand("""
    select * from ADDRESS where address_id = /*id*/0
""")
class GetAddressById(val id: Int): One<Address?>({ selectAsAddress().singleOrNull() })
```

`One`の型パラメータには取得したい値の型を指定します。`One`のコンストラクタには、検索結果を処理するラムダ式を渡します。ラムダ式の中でできることは、[fromTemplate]({{< relref "template#fromtemplate" >}})で言及した`select`関数や`selectAsEntity`関数と同じです。

上述の例では、`Address`クラスに`@KomapperProjetion`が付与されていることを前提にしています。そのため、`selectAsAddress`関数を使って結果を`Address`クラスに変換しています。

### Many {#command-many}

複数件を取得するコマンドは`org.komapper.core.Many`を継承します。

```kotlin
@KomapperCommand("""
    select * from ADDRESS where street = /*street*/'test'
""")
class ListAddresses(val street: String): Many<Address>({ selectAsAddress() })
```

`Many`の型パラメータには取得したい1件を表現する型を指定します。`Many`のコンストラクタには、検索結果を処理するラムダ式を渡します。ラムダ式の中でできることは、[fromTemplate]({{< relref "template#fromtemplate" >}})で言及した`select`関数や`selectAsEntity`関数と同じです。

上述の例では、`Address`クラスに`@KomapperProjetion`が付与されていることを前提にしています。そのため、`selectAsAddress`関数を使って結果を`Address`クラスに変換しています。

### Exec {#command-exec}

更新系のDMLを実行するコマンドは`org.komapper.core.Exec`を継承します。

```kotlin
@KomapperCommand("""
    update ADDRESS set street = /*street*/'' where address_id = /*id*/0
""")
class UpdateAddress(val id: Int, val street: String): Exec()
```

### ExecReturnOne {#command-exec-return-one}

更新系のDMLを実行し1件を返すコマンドは`org.komapper.core.ExecReturnOne`を継承します。

```kotlin
@KomapperCommand("""
    insert into ADDRESS
        (address_id, street, version)
    values
        (/* id */0, /* street */'', /* version */1)
    returning address_id, street, version
""")
class InsertAddressThenReturn(val id: Int, val street: String): ExecReturnOne<Address>({ selectAsAddress().single() })
```

`ExecReturnOne`の型パラメータやコンストラクタについては、[One]({{< relref "#command-one" >}})と同様です。

### ExecReturnMany {#command-exec-return-many}

更新系のDMLを実行し複数件を返すコマンドは`org.komapper.core.ExecReturnMany`を継承します。

```kotlin
@KomapperCommand("""
    update ADDRESS set street = /*street*/'' returning address_id, street, version
""")
class UpdateAddressThenReturn(val id: Int, val street: String): ExecReturnMany<Address>({ selectAsAddress() })
```

`ExecReturnMany`の型パラメータやコンストラクタについては、[Many]({{< relref "#command-many" >}})と同様です。

## パーシャル {#command-partial}

パーシャルはSQLテンプレートの部品を再利用するための機能です。
パーシャルは`org.komapper.annotation.KomapperPartial`アノテーションを注釈したクラスとして定義します。

```kotlin
@KomapperPartial(
    """
    limit /* limit */0 offset /* offset */0
    """,
)
data class Pagination(val limit: Int, val offset: Int)
```

パーシャルを使用するには、パーシャルをパラメータとして受け取るコマンドを定義し、そのパラメータをSQLテンプレート内で`/*> partialName */`のように参照します。

```kotlin
@KomapperCommand(
    """
    select * from address order by address_id
    /*> pagination */
    """,
)
class UsePartial(val pagination: Pagination?) : Many<Address>({ selectAsAddress() })
```

パーシャルのパラメータが`null`の場合、パーシャルのSQLはコマンドのSQLには含まれません。

{{< alert title="Note" >}}
パーシャルは他のパーシャルを参照することはできません。
{{< /alert >}}

### シールドされたサブクラスとしてのパーシャル {#command-partial-sealed-subclass}

パーシャルクラスをシールドされたサブクラスとして表現することができます。
その場合、パーシャルなSQLはポリモーフィックに決定されます。

```kotlin
sealed interface FilterBy {
    @KomapperPartial(
        """
        where street = /* street */''
        """,
    )
    class Street(val street: String) : FilterBy

    @KomapperPartial(
        """
        where address_id = /* id */0
        """,
    )
    class Id(val id: Int) : FilterBy
}

@KomapperCommand(
    """
    select * from address
    /*> filterBy */
    """,
)
class UseSealedPartial(val filterBy: FilterBy?) : Many<Address>({ selectAsAddress() })
```

上記の例では、`FilterBy.Street`と`FilterBy.Id`のどちらのインスタンスが
`UseSealedPartial`クラスの`filterBy`パラメータに渡されるかによって、
コマンドのSQLに取り込まれるパーシャルなSQLが変わります。