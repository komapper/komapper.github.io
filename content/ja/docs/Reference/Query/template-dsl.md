---
title: "Template DSL"
linkTitle: "Template DSL"
weight: 30
description: >
  テンプレートからSQLを組み立てるためのDSL
---

## 概要 {#overview}

Template DSLはSQLテンプレートを使ってSQLを組み立てます。

Template DSLはコアのモジュールには含まれないオプション機能です。
利用するにはGradleの依存関係に次のような宣言が必要です。

```kotlin
val komapperVersion: String by project
dependencies {
    implementation("org.komapper:komapper-template:$komapperVersion")
}
```

{{< alert title="Note" >}}
すべての [Starter]({{< relref "../Starter" >}}) は上記の設定を含んでいます。
したがって、Starterを使う場合には上記の設定は不要です。
{{< /alert >}}

{{< alert title="Note" >}}
`komapper-template`モジュールは内部でリフレクションを使います。
{{< /alert >}}

## SELECT

検索を実施するには`from`関数に [SQLテンプレート]({{< relref "#sql-template" >}})、`bind`関数にSQLテンプレート内で利用したいデータを渡します。
SQLテンプレート内の各種ディレクティブではデータのpublicなメンバを参照できます。
検索結果を任意の型に変換するために`select`関数にラムダ式を渡します。

```kotlin
data class Condition(val street: String)

val sql = "select * from ADDRESS where street = /*street*/'test'"
val data = Condition("STREET 10")
val query: Query<List<Address>> = TemplateDsl.from(sql).bind(data).select { row: Row ->
    Address(
        row.asInt("address_id")!!,
        row.asString("street")!!,
        row.asInt("version")!!
    )
}
```

上述の例では`bind`関数に`Condition`クラスのインスタンスを渡していますが、代わりにobject式を渡すこともできます。

```kotlin
val sql = "select * from ADDRESS where street = /*street*/'test'"
val query: Query<List<Address>> = TemplateDsl.from(sql).bind(
    object {
        val street = "STREET 10"
    }
).select { row: Row ->
    Address(
        row.asInt("address_id")!!,
        row.asString("street")!!,
        row.asInt("version")!!
    )
}
```

`select`関数に渡すラムダ式に登場する`Row`は`java.sql.ResultSet`や`io.r2dbc.spi.Row`の薄いラッパーです。
カラムのラベル名やインデックスで値を取得する関数を持ちます。
なお、インデックスは0から始まります。

## EXECUTE

更新系のDMLを実行するには`execute`関数に [SQLテンプレート]({{< relref "#sql-template" >}})、`bind`関数にSQLテンプレート内で利用したいデータを渡します。
SQLテンプレート内の各種ディレクティブではデータのpublicなメンバを参照できます。

クエリ実行時にキーが重複した場合、`org.komapper.core.UniqueConstraintException`がスローされます。

```kotlin
data class Condition(val id: Int, val street: String)

val sql = "update ADDRESS set street = /*street*/'' where address_id = /*id*/0"
val data = Condition(15, "NY street")
val query = Query<Int> = TemplateDsl.execute(sql).bind(data)
```

上述の例では`bind`関数に`Condition`クラスのインスタンスを渡していますが、代わりにobject式を渡すこともできます。

```kotlin
val sql = "update ADDRESS set street = /*street*/'' where address_id = /*id*/0"
val query = Query<Int> = TemplateDsl.execute(sql).bind( object { id = 15, street = "NY street" } )
```

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
なお、`asPrefix()`、`asInfix()`、`asSuffix()`は内部でエスケープ処理を実行するので別途`escape()`を呼び出す必要はありません。
