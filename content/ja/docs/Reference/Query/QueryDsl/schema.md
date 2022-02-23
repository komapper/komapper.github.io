---
title: "SCHEMA"
linkTitle: "SCHEMA"
weight: 60
description: >
  テーブルの作成や削除のためのクエリ
---

## 概要 {#overview}

SCHEMAクエリはマッピング定義から以下のDDLを組み立てます。

- エンティティに対応するテーブルのCREATE文/DROP文
- エンティティのIDを生成するシーケンスのCREATE文/DROP文

{{< alert title="Note" >}}
SCHEMAクエリは開発時のみの利用を推奨します。
例えば、次のようなユースケースを想定しています。

- サンプルアプリを作る
- プロジェクト初期に簡易的なDDLを手に入れる

プロダクション環境で使うDDLスクリプトを別途管理した方が良いでしょう。
{{< /alert >}}

## create

CREATE文を生成するには`create`を呼び出します。

```kotlin
val query: Query<Unit> = QueryDsl.create(Meta.address, Meta.employee)
/*
create table if not exists ADDRESS (ADDRESS_ID integer not null, STREET varchar(500) not null, VERSION integer not null, constraint pk_ADDRESS primary key(ADDRESS_ID));
create table if not exists EMPLOYEE (EMPLOYEE_ID integer not null, EMPLOYEE_NO integer not null, EMPLOYEE_NAME varchar(500) not null, MANAGER_ID integer, HIREDATE date not null, SALARY bigint not null, DEPARTMENT_ID integer not null, ADDRESS_ID integer not null, VERSION integer not null, constraint pk_EMPLOYEE primary key(EMPLOYEE_ID));
*/
```

{{< alert title="Note" >}}
対象のオブジェクトがすでに存在している場合、例外はスローされません。
また、後続処理も中断されません。
{{< /alert >}}

## drop

DROP文を生成するには`drop`を呼び出します。

```kotlin
val query: Query<Unit> = QueryDsl.drop(Meta.address, Meta.employee)
/*
drop table if exists ADDRESS;
drop table if exists EMPLOYEE;
*/
```

{{< alert title="Note" >}}
対象のオブジェクトが存在していない場合、例外はスローされません。
また、後続処理も中断されません。
{{< /alert >}}

## options

クエリの挙動をカスタマイズするには`options`を呼び出します。
ラムダ式のパラメータはデフォルトのオプションを表します。
変更したいプロパティを指定して`copy`メソッドを呼び出してください。

```kotlin
val query: Query<Unit> = QueryDsl.create(Meta.address, Meta.employee).options {
    it.copty(
      queryTimeoutSeconds = 5
    )
}
```

指定可能なオプションには以下のものがあります。

queryTimeoutSeconds
: クエリタイムアウトの秒数です。デフォルトは`null`でドライバの値を使うことを示します。

suppressLogging
: SQLのログ出力を抑制するかどうかです。デフォルトは`false`です。

[executionOptions]({{< relref "../../database-config/#executionoptions" >}})
の同名プロパティよりもこちらに明示的に設定した値が優先的に利用されます。