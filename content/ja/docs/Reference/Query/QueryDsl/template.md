---
title: "TEMPLATEクエリ"
linkTitle: "テンプレート"
weight: 50
description: >
  SQLテンプレートを利用するクエリ
---

## 概要 {#overview}

TEMPLATEクエリはSQLテンプレートを利用して構築するクエリです。

TEMPLATEクエリはコアのモジュールには含まれないオプション機能です。
利用するにはGradleの依存関係に次のような宣言が必要です。

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

## fromTemplate

検索を実施するには`fromTemplate`関数に [SQLテンプレート]({{< relref "#sql-template" >}})、`bind`関数にデータを渡します。
検索結果を任意の型に変換するために、`select`関数にラムダ式を渡します。

```kotlin
val sql = "select * from ADDRESS where street = /*street*/'test'"
val query: Query<List<Address>> = QueryDsl.fromTemplate(sql)
    .bind("street", "STREET 10")
    .select { row: Row -> 
        Address(
            row.getNotNull("address_id"),
            row.getNotNull("street"),
            row.getNotNull("version")
        )
}
```

`select`関数に渡すラムダ式に登場する`Row`は`java.sql.ResultSet`や`io.r2dbc.spi.Row`の薄いラッパーです。
カラムのラベル名やインデックスで値を取得する関数を持ちます。
なお、インデックスは0から始まります。

### selectAsEntity

結果を任意のエンティティとして受け取りたい場合は`selectAsEntity`を呼び出します。
第一引数にはエンティティのメタモデルを指定します。
SQLテンプレートのSELECT句にはエンティティの全プロパティに対応するカラムが含まれていなければいけません。

次の例では結果を`Address`エンティティとして受け取っています。

```kotlin
val sql = "select address_id, street, version from ADDRESS where street = /*street*/'test'"
val query: Query<List<Address>> = QueryDsl.fromTemplate(sql)
  .bind("street", "STREET 10")
  .selectAsEntity(a)
```

デフォルトではSELECTリストのカラムの順序でエンティティにマッピングしますが、
`selectAsEntity`の第二引数に`ProjectionType.NAME`を渡すことでカラムの名前でマッピングできます。

```kotlin
val sql = "select street, version, address_id from ADDRESS where street = /*street*/'test'"
val query: Query<List<Address>> = QueryDsl.fromTemplate(sql)
  .bind("street", "STREET 10")
  .selectAsEntity(a, ProjectionType.NAME)
```

結果として受け取りたいエンティティクラスに`@KomapperProjection`を付与している場合、
専用の拡張関数を使って以下のように簡潔に記述できます。

```kotlin
val sql = "select address_id, street, version from ADDRESS where street = /*street*/'test'"
val query: Query<List<Address>> = QueryDsl.fromTemplate(sql)
  .bind("street", "STREET 10")
  .selectAsAddress()
```

```kotlin
val sql = "select street, version, address_id from ADDRESS where street = /*street*/'test'"
val query: Query<List<Address>> = QueryDsl.fromTemplate(sql)
  .bind("street", "STREET 10")
  .selectAsAddress(ProjectionType.NAME)
```

### options {#select-options}

クエリの挙動をカスタマイズするには`options`を呼び出します。
ラムダ式のパラメータはデフォルトのオプションを表します。
変更したいプロパティを指定して`copy`メソッドを呼び出してください。

```kotlin
val sql = "select * from ADDRESS where street = /*street*/'test'"
val query: Query<List<Address>> = QueryDsl.fromTemplate(sql)
  .options {
    it.copy(
      fetchSize = 100,
      queryTimeoutSeconds = 5
    )
  }
  .bind("street", "STREET 10")
  .select { row: Row ->
    Address(
      row.getNotNull("address_id"),
      row.getNotNull("street"),
      row.getNotNull("version")
    )
}
```

指定可能なオプションには以下のものがあります。

escapeSequence
: LIKE句に指定されるエスケープシーケンスです。デフォルトは`null`で`Dialect`の値を使うことを示します。

fetchSize
: フェッチサイズです。デフォルトは`null`でドライバの値を使うことを示します。

maxRows
: 最大行数です。デフォルトは`null`でドライバの値を使うことを示します。

queryTimeoutSeconds
: クエリタイムアウトの秒数です。デフォルトは`null`でドライバの値を使うことを示します。

suppressLogging
: SQLのログ出力を抑制するかどうかです。デフォルトは`false`です。

[executionOptions]({{< relref "../../database-config/#executionoptions" >}})
の同名プロパティよりもこちらに明示的に設定した値が優先的に利用されます。

## executeTemplate

更新系のDMLを実行するには`executeTemplate`関数に [SQLテンプレート]({{< relref "#sql-template" >}})、`bind`関数にデータを渡します。

クエリ実行時にキーが重複した場合、`org.komapper.core.UniqueConstraintException`がスローされます。

```kotlin
val sql = "update ADDRESS set street = /*street*/'' where address_id = /*id*/0"
val query: Query<Long> = QueryDsl.executeTemplate(sql)
    .bind("id", 15)
    .bind("street", "NY street")
```

### returning {#executeTemplate-returning}

`returning`関数を使うことで、更新系のDMLを実行しかつ結果を取得できます。
`returning`関数実行後は、[fromTemplate]({{< relref "#fromtemplate" >}})で言及した`select`関数や`selectAsEntity`関数が利用できます。

```kotlin
val sql = """
    insert into address
        (address_id, street, version)
    values
        (/*id*/0, /*street*/'', /*version*/0)
    returning address_id, street, version
""".trimIndent()
QueryDsl.executeTemplate(sql)
    .returning()
    .bind("id", 16)
    .bind("street", "NY street")
    .bind("version", 1)
    .select { row: Row ->
        Address(
            row.getNotNull("address_id"),
            row.getNotNull("street"),
            row.getNotNull("version")
        )
    }
    .single()
```

{{< alert title="Note" >}}
上述のSQLテンプレートではPostgreSQLなどでサポートされているRETURNING句を使用していますが、 更新系のDMLから結果を返すSQLはDBMSごとに異なることに注意ください。
{{< /alert >}}

### options {#execute-options}

クエリの挙動をカスタマイズするには`options`を呼び出します。
ラムダ式のパラメータはデフォルトのオプションを表します。
変更したいプロパティを指定して`copy`メソッドを呼び出してください。

```kotlin
val sql = "update ADDRESS set street = /*street*/'' where address_id = /*id*/0"
val query: Query<Long> = QueryDsl.executeTemplate(sql)
  .bind("id", 15)
  .bind("street", "NY street")
  .options {
    it.copy(
      queryTimeoutSeconds = 5
    )
}
```

指定可能なオプションには以下のものがあります。

escapeSequence
: LIKE句に指定されるエスケープシーケンスです。デフォルトは`null`で`Dialect`の値を使うことを示します。

queryTimeoutSeconds
: クエリタイムアウトの秒数です。デフォルトは`null`でドライバの値を使うことを示します。

suppressLogging
: SQLのログ出力を抑制するかどうかです。デフォルトは`false`です。

[executionOptions]({{< relref "../../database-config/#executionoptions" >}})
の同名プロパティよりもこちらに明示的に設定した値が優先的に利用されます。

## コマンド {#command}

コマンドはSQLテンプレートとパラメータを一つのまとまりとして扱うための機能です。クラスに`org.komapper.annotation.KomapperCommand`アノテーションを付与してSQLテンプレートを定義し、クラスのプロパティでパラメータを定義します。SQLの結果をどのように扱うかは特定のクラスを継承することで表現します。

以下は、複数件を取得するコマンドの例です。

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

コマンドは、[fromTemplate]({{< relref "#fromtemplate" >}})や[executeTemplate]({{< relref "#executetemplate" >}})を使う方法に比べて、コンパイル時にSQLテンプレートを検証できるという利点があります。具体的には次のことが可能です。

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
class GetAddressById(val street: String): One<Address?>({ selectAsAddress().singleOrNull() })
```

`One`の型パラメータには取得したい値の型を指定します。`One`のコンストラクタには、検索結果を処理するラムダ式を渡します。ラムダ式の中でできることは、[fromTemplate]({{< relref "#fromtemplate" >}})で言及した`select`関数や`selectAsEntity`関数と同じです。

上述の例では、`Address`クラスに`@KomapperProjetion`が付与されていることを前提にしています。そのため、`selectAsAddress`関数を使って結果を`Address`クラスに変換しています。

### Many {#command-many}

複数件を取得するコマンドは`org.komapper.core.Many`を継承します。

```kotlin
@KomapperCommand("""
    select * from ADDRESS where street = /*street*/'test'
""")
class ListAddresses(val street: String): Many<Address>({ selectAsAddress() })
```

`Many`の型パラメータには取得したい1件を表現する型を指定します。`Many`のコンストラクタには、検索結果を処理するラムダ式を渡します。ラムダ式の中でできることは、[fromTemplate]({{< relref "#fromtemplate" >}})で言及した`select`関数や`selectAsEntity`関数と同じです。

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

### パーシャル {#command-partial}

パーシャルはSQLテンプレートの一部を再利用するための機能です。
パーシャルはトップレベルの`const`に`org.komapper.annotation.KomapperPartial`アノテーションを使って定義します。

```kotlin
@KomapperPartial(
    """
    /*%if pagination != null */
    limit /* pagination.limit */0 offset /*pagination.offset*/0
    /*%end*/
    """,
)
private const val paginationPartial = ""
```

パーシャルを利用するにはパーシャルが定義されたファイルと同じファイル内でコマンドを定義し、SQLテンプレートから`/*> パーシャル名 */`という記法でパーシャルを参照します。

```kotlin
@KomapperCommand(
    """
    select * from address order by address_id
    /*> paginationPartial */
    """,
)
class UsePartial(val pagination: Pagination?) : Many<Address>({ selectAsAddress() })

class Pagination(val limit: Int, val offset: Int)
```

{{< alert title="Note" >}}
パーシャルから別のパーシャルを参照することはできません。
{{< /alert >}}

## SQLテンプレート  {#sql-template}

Komapperが提供するSQLテンプレートはいわゆる2-Way-SQL対応のテンプレートです。
バインド変数や条件分岐に関する記述をSQLコメントで表現するため、
テンプレートをアプリケーションで利用できるだけでなく、[pgAdmin](https://www.pgadmin.org/)
など一般的なSQLツールでも実行できます。

例えば条件分岐とバインド変数を含んだSQLテンプレートは次のようになります。

```sql
select name, age from person where
/*%if name != null*/
  name = /*name*/'test'
/*%end*/
order by name
```

上記のテンプレートはアプリケーション上で`name != null`が真と評価されるとき次のSQLに変換されます。

```sql
select name, age from person where name = ? order by name
```

`name != null`が偽と評価されるとき次のSQLに変換されます。

```sql
select name, age from person order by name
```

{{< alert title="Note" >}}
上述の例で`name != null`が偽と評価されるとき最終的にSQLに`where`キーワードが含まれていないことに気づいたでしょうか？
KomapperのSQLテンプレートは、WHERE句、HAVING句、GROUP BY句、ORDER BY句の内側にSQLの要素が1つも含まれない場合その句を表すキーワードを出力しません。
したがって、不正なSQLが生成されることを防ぐために`1 = 1`を必ずWHERE句に含めるなどの対応は不要です。

```kotlin
select name, age from person where 1 = 1  // このような対応は不要
/*%if name != null*/
  and name = /*name*/'test'
/*%end*/
order by name
```
{{< /alert >}}


### バインド変数ディレクティブ  {#sql-template-bind-variable-directive}

バインド変数は`/*expression*/`のように`/*`と`*/`で囲んで表します。
`expression`には任意の値を返す式が入ります。

次の`'test'`のようにディレクティブの直後にはテスト用の値が必須です。

```sql
where name = /*name*/'test'
```

最終的にはテスト用の値は取り除かれ上述のテンプレートは次のようなSQLに変換されます。
`/*name*/`は`?`に置換され、`?`には`name`が返す値がバインドされます。

```sql
where name = ?
```

IN句にバインドするには`expression`は`Iterable`型の値でなければいけません。

```sql
where name in /*names*/('a', 'b')
```

IN句にタプル形式で値をバインドするには`expression`を`Iterable<Pair>`型や`Iterable<Triple>`型の値にします。

```sql
where (name, age) in /*pairs*/(('a', 'b'), ('c', 'd'))
```

### リテラル変数ディレクティブ {#sql-template-literal-variable-directive}

リテラル変数は`/*^expression*/`のように`/*^`と`*/`で囲んで表します。
`expression`には任意の値を返す式が入ります。

次の`'test'`のようにディレクティブの直後にはテスト用の値が必須です。

```sql
where name = /*^name*/'test'
```

最終的にはテスト用の値は取り除かれ上述のテンプレートは次のようなSQLに変換されます。
`/*^name*/`は`name`が返す値（この例では`"abc"`）のリテラル表現（`'abc'`）で置換されます。

```sql
where name = 'abc'
```

### 埋め込み変数ディレクティブ {#sql-template-embedded-variable-directive}

埋め込み変数は`/*#expression*/`のように`/*#`と`*/`で囲んで表します。
`expression`には任意の値を返す式が入ります。

```sql
select name, age from person where age > 1 /*# orderBy */
```

上述の`orderBy`の式が`"order by name"`という文字列を返す場合、最終的なSQLは次のように変換されます。

```sql
select name, age from person where age > 1 order by name
```

### ifディレクティブ {#sql-template-if-directive}

ifの条件分岐は`/*%if expression*/`で始めて`/*%end*/`で終わります。
`expression`には真偽値を返す式が入ります。

```kotlin
/*%if name != null*/
  name = /*name*/'test'
/*%end*/
```

`/*%if expression*/`と`/*%end*/`の間に`/*%else*/`を入れることもできます。

```kotlin
/*%if name != null*/
  name = /*name*/'test'
/*%else*/
  name is null
/*%end*/
```

### forディレクティブ {#sql-template-for-directive}

forを使ったループは`/*%for identifier in expression */`で始めて`/*%end*/`で終わります。
`expression`には`Iterable`を返す式が入り`identifier`は`Iterable`のそれぞれの要素を表す識別子となります。
forのループの中では`identifier`に`_has_next`のプレフィックをつけた識別子が利用可能になります。
これは次の繰り返し要素が存在するかどうかを表す真偽値を返します。

```sql
/*%for name in names */
employee_name like /* name */'hoge'
  /*%if name_has_next */
/*# "or" */
  /*%end */
/*%end*/
```

### endディレクティブ {#sql-template-end-directive}

条件分岐やループ処理を終了するには、endディレクティブを使います。

endディレクティブは`/*%end*/`というSQLコメントで表現されます。

### パーサーレベルのコメントディレクティブ {#sql-template-parser-level-comment-directive}

パーサーレベルのコメントディレクティブを使用すると、SQLテンプレートが解析された後にコメントを削除できます。

パーサーレベルのコメントを表現するには、`/*%! コメント */` という構文を使います。

次のようなSQLテンプレートがあるとします。

```sql
select
  name
from
  employee
where /*%! このコメントは削除されます */
  employee_id = /* employeeId */99
```

上記のSQLテンプレートは、次のSQLへと解析されます。

```sql
select
  name
from 
  employee
where
  employee_id = ?
```

### 式 {#sql-template-expression}

ディレクティブ内で参照される式の中では以下の機能がサポートされています。

- 演算子の実行
- プロパティアクセス
- 関数呼び出し
- クラス参照
- 拡張プロパティや拡張関数の利用

#### 演算子 {#sql-template-expression-operator}

次の演算子がサポートされています。意味はKotlinの演算子と同じです。

- `==`
- `!=`
- `>=`
- `<=`
- `>`
- `<`
- `!`
- `&&`
- `||`

次のように利用できます。

```kotlin
/*%if name != null && name.length > 0 */
  name = /*name*/'test'
/*%else*/
  name is null
/*%end*/
```

#### プロパティアクセス {#sql-template-expression-property-access}

`.`や`?.`を使ってプロパティにアクセスできます。`?.`はKotlinのsafe call operatorと同じ挙動をします。

```kotlin
/*%if person?.name != null */
  name = /*person?.name*/'test'
/*%else*/
  name is null
/*%end*/
```

#### 関数呼び出し {#sql-template-expression-function-invocation}

関数を呼び出せます。

```kotlin
/*%if isValid(name) */
  name = /*name*/'test'
/*%else*/
  name is null
/*%end*/
```

#### クラス参照 {#sql-template-expression-class-reference}

`@クラスの完全修飾名@`という記法でクラスを参照できます。
例えば`example.Direction`というenum classに`WEST`という要素がある場合、次のように参照できます。

```kotlin
/*%if direction == @example.Direction@.WEST */
  direction = 'west'
/*%end*/
```

#### 拡張プロパティと拡張関数 {#sql-template-expression-extensions}

Kotlinが提供する以下の拡張プロパティと拡張関数をデフォルトで利用できます。

- `val CharSequence.lastIndex: Int`
- `fun CharSequence.isBlank(): Boolean`
- `fun CharSequence.isNotBlank(): Boolean`
- `fun CharSequence.isNullOrBlank(): Boolean`
- `fun CharSequence.isEmpty(): Boolean`
- `fun CharSequence.isNotEmpty(): Boolean`
- `fun CharSequence.isNullOrEmpty(): Boolean`
- `fun CharSequence.any(): Boolean`
- `fun CharSequence.none(): Boolean`

```kotlin
/*%if name.isNotBlank() */
  name = /*name*/'test'
/*%else*/
  name is null
/*%end*/
```

また、Komapperが定義する以下の拡張関数も利用できます。

- `fun String?.asPrefix(): String?`
- `fun String?.asInfix(): String?`
- `fun String?.asSuffix(): String?`
- `fun String?.escape(): String?`

例えば、asPrefix()を呼び出すと`"hello"`という文字列が`"hello%"`となり前方一致検索で利用できるようになります。

```kotlin
where name like /*name.asPrefix()*/
```

同様に`asInfix()`を呼び出すと中間一致検索用の文字列に変換し、`asSuffix()`を呼び出すと後方一致検索用の文字列に変換します。

`escape()`は特別な文字をエスケープします。例えば、`"he%llo_"`という文字列を`"he\%llo\_"`のような文字列に変換します。

{{< alert title="Note" >}}
`asPrefix()`、`asInfix()`、`asSuffix()`は内部でエスケープ処理を実行するので別途`escape()`を呼び出す必要はありません。
{{< /alert >}}
