---
title: "Expression"
linkTitle: "Expression"
weight: 100
description: >
  Entity DSLとSQL DSLの中で利用可能な式
---

## 概要 {#overview}

Entity DSLとSQL DSLはいくつかの式を組み合わせてクエリを生成します。
ここでは式の要素である演算子や関数を説明します。

## 宣言 {#declaration}

Entity DSLとSQL DSLでは、例えば`where`関数や`having`関数に検索条件を表すラムダ式を渡しますが、
Komapperではこれらのラムダ式のことを宣言と呼びます。

宣言には以下のものがあります。

- Having宣言
- On宣言
- Set宣言
- Values宣言
- When宣言
- Where宣言

## 比較演算子 {#comparison-operator}

次の宣言の中で利用できます。

- Having宣言
- On宣言
- When宣言
- Where宣言

{{< alert title="Note" >}}
ただし、On宣言で利用できるのは以下の単純な演算子のみです。

- eq
- notEq
- less
- lessEq
- greater
- greaterEq
- isNull
- isNotNull

{{< /alert >}}

演算子の引数に`null`を渡した場合その演算子は評価されません。つまりSQLに変換されません。

```kotlin
val nullable: Int? = null
val query = EntityDsl.from(a).where { a.addressId eq nullable }
```

したがって、上記の`query`が実行された場合は次のSQLが発行されます。

```sql
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_
```

### eq

```kotlin
EntityDsl.from(a).where { a.addressId eq 1 }
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID = ?
*/
```

### notEq

```kotlin
EntityDsl.from(a).where { a.addressId notEq 1 }
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID <> ?
*/
```

### less

```kotlin
EntityDsl.from(a).where { a.addressId less 1 }
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID < ?
*/

```

### lessEq

```kotlin
EntityDsl.from(a).where { a.addressId lessEq 1 }
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID <= ?
*/
```

### greater

```kotlin
EntityDsl.from(a).where { a.addressId greater 1 }
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID > ?
*/
```

### greaterEq

```kotlin
EntityDsl.from(a).where { a.addressId greaterEq 1 }
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID >= ?
*/
```

### isNull

```kotlin
EntityDsl.from(e).where { e.managerId.isNull() }
/*
select t0_.EMPLOYEE_ID, t0_.EMPLOYEE_NO, t0_.EMPLOYEE_NAME, t0_.MANAGER_ID, t0_.HIREDATE, t0_.SALARY, t0_.DEPARTMENT_ID, t0_.ADDRESS_ID, t0_.VERSION from EMPLOYEE as t0_ where t0_.MANAGER_ID is null
*/
```
### isNotNull

```kotlin
EntityDsl.from(e).where { e.managerId.isNotNull() }
/*
select t0_.EMPLOYEE_ID, t0_.EMPLOYEE_NO, t0_.EMPLOYEE_NAME, t0_.MANAGER_ID, t0_.HIREDATE, t0_.SALARY, t0_.DEPARTMENT_ID, t0_.ADDRESS_ID, t0_.VERSION from EMPLOYEE as t0_ where t0_.MANAGER_ID is not null
*/
```

### like

```kotlin
EntityDsl.from(a).where { a.street like "STREET 1_" }.orderBy(a.addressId)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.STREET like ? escape ? order by t0_.ADDRESS_ID asc
*/
```

### notLike

```kotlin
EntityDsl.from(a).where { a.street notLike "STREET 1_" }.orderBy(a.addressId)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.STREET not like ? escape ? order by t0_.ADDRESS_ID asc
*/
```

### startsWith

```kotlin
EntityDsl.from(a).where { a.street startsWith "STREET 1" }.orderBy(a.addressId)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.STREET like ? escape ? order by t0_.ADDRESS_ID asc
*/
```

### notStartsWith

```kotlin
EntityDsl.from(a).where { a.street notStartsWith "STREET 1" }.orderBy(a.addressId)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.STREET not like ? escape ? order by t0_.ADDRESS_ID asc
*/
```

### contains

```kotlin
EntityDsl.from(a).where { a.street contains "T 1" }.orderBy(a.addressId)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.STREET like ? escape ? order by t0_.ADDRESS_ID asc
*/
```

### notContains

```kotlin
EntityDsl.from(a).where { a.street notContains "T 1" }.orderBy(a.addressId)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.STREET not like ? escape ? order by t0_.ADDRESS_ID asc
*/
```

### endsWith

```kotlin
EntityDsl.from(a).where { a.street endsWith "1" }.orderBy(a.addressId)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.STREET like ? escape ? order by t0_.ADDRESS_ID asc
*/
```

### notEndsWith

```kotlin
EntityDsl.from(a).where { a.street notEndsWith "1" }.orderBy(a.addressId)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.STREET not like ? escape ? order by t0_.ADDRESS_ID asc
*/
```

### between

```kotlin
EntityDsl.from(a).where { a.addressId between 5..10 }.orderBy(a.addressId)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID between ? and ? order by t0_.ADDRESS_ID asc
*/
```

### notBetween

```kotlin
EntityDsl.from(a).where { a.addressId notBetween 5..10 }.orderBy(a.addressId)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID not between ? and ? order by t0_.ADDRESS_ID asc
*/
```

### inList

```kotlin
EntityDsl.from(a).where { a.addressId inList listOf(9, 10) }.orderBy(a.addressId.desc())
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID in (?, ?) order by t0_.ADDRESS_ID desc
*/
```

サブクエリも使えます。

```kotlin
SqlDsl.from(e).where {
    e.addressId inList {
        SqlDsl.from(a)
            .where {
                e.addressId eq a.addressId
                e.employeeName like "%S%"
            }.select(a.addressId)
    }
}
/*
select t0_.EMPLOYEE_ID, t0_.EMPLOYEE_NO, t0_.EMPLOYEE_NAME, t0_.MANAGER_ID, t0_.HIREDATE, t0_.SALARY, t0_.DEPARTMENT_ID, t0_.ADDRESS_ID, t0_.VERSION from EMPLOYEE as t0_ where t0_.ADDRESS_ID in (select t1_.ADDRESS_ID from ADDRESS as t1_ where t0_.ADDRESS_ID = t1_.ADDRESS_ID and t0_.EMPLOYEE_NAME like ? escape ?)
*/
```

### notInList

```kotlin
EntityDsl.from(a).where { a.addressId notInList (1..9).toList() }.orderBy(a.addressId)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID not in (?, ?, ?, ?, ?, ?, ?, ?, ?) order by t0_.ADDRESS_ID asc
*/
```

サブクエリも使えます。

```kotlin
SqlDsl.from(e).where {
    e.addressId notInList {
        SqlDsl.from(a).where {
            e.addressId eq a.addressId
            e.employeeName like "%S%"
        }.select(a.addressId)
    }
}
/*
select t0_.EMPLOYEE_ID, t0_.EMPLOYEE_NO, t0_.EMPLOYEE_NAME, t0_.MANAGER_ID, t0_.HIREDATE, t0_.SALARY, t0_.DEPARTMENT_ID, t0_.ADDRESS_ID, t0_.VERSION from EMPLOYEE as t0_ where t0_.ADDRESS_ID not in (select t1_.ADDRESS_ID from ADDRESS as t1_ where t0_.ADDRESS_ID = t1_.ADDRESS_ID and t0_.EMPLOYEE_NAME like ? escape ?)
*/
```

### inList2

```kotlin
EntityDsl.from(a).where { a.addressId to a.version inList2 listOf(9 to 1, 10 to 1) }.orderBy(a.addressId.desc())
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where (t0_.ADDRESS_ID, t0_.VERSION) in ((?, ?), (?, ?)) order by t0_.ADDRESS_ID desc
*/
```

サブクエリも使えます。

```kotlin
SqlDsl.from(e).where {
    e.addressId to e.version inList2 {
        SqlDsl.from(a)
            .where {
                e.addressId eq a.addressId
                e.employeeName like "%S%"
            }.select(a.addressId, a.version)
    }
}
/*
select t0_.EMPLOYEE_ID, t0_.EMPLOYEE_NO, t0_.EMPLOYEE_NAME, t0_.MANAGER_ID, t0_.HIREDATE, t0_.SALARY, t0_.DEPARTMENT_ID, t0_.ADDRESS_ID, t0_.VERSION from EMPLOYEE as t0_ where (t0_.ADDRESS_ID, t0_.VERSION) in (select t1_.ADDRESS_ID, t1_.VERSION from ADDRESS as t1_ where t0_.ADDRESS_ID = t1_.ADDRESS_ID and t0_.EMPLOYEE_NAME like ? escape ?)
*/
```

### notInList2

```kotlin
EntityDsl.from(a).where { a.addressId to a.version notInList2 listOf(9 to 1, 10 to 1) }.orderBy(a.addressId)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where (t0_.ADDRESS_ID, t0_.VERSION) not in ((?, ?), (?, ?)) order by t0_.ADDRESS_ID asc
*/
```

サブクエリも使えます。

```kotlin
SqlDsl.from(e).where {
    e.addressId to e.version notInList2 {
        SqlDsl.from(a).where {
            e.addressId eq a.addressId
            e.employeeName like "%S%"
        }.select(a.addressId, a.version)
    }
}
/*
select t0_.EMPLOYEE_ID, t0_.EMPLOYEE_NO, t0_.EMPLOYEE_NAME, t0_.MANAGER_ID, t0_.HIREDATE, t0_.SALARY, t0_.DEPARTMENT_ID, t0_.ADDRESS_ID, t0_.VERSION from EMPLOYEE as t0_ where (t0_.ADDRESS_ID, t0_.VERSION) not in (select t1_.ADDRESS_ID, t1_.VERSION from ADDRESS as t1_ where t0_.ADDRESS_ID = t1_.ADDRESS_ID and t0_.EMPLOYEE_NAME like ? escape ?)
*/
```

### exists

```kotlin
EntityDsl.from(e).where {
    exists {
        EntityDsl.from(a).where {
            e.addressId eq a.addressId
            e.employeeName like "%S%"
        }
    }
}
/*
select t0_.EMPLOYEE_ID, t0_.EMPLOYEE_NO, t0_.EMPLOYEE_NAME, t0_.MANAGER_ID, t0_.HIREDATE, t0_.SALARY, t0_.DEPARTMENT_ID, t0_.ADDRESS_ID, t0_.VERSION from EMPLOYEE as t0_ where exists (select t1_.ADDRESS_ID, t1_.STREET, t1_.VERSION from ADDRESS as t1_ where t0_.ADDRESS_ID = t1_.ADDRESS_ID and t0_.EMPLOYEE_NAME like ? escape ?)
*/
```

### notExists

```kotlin
EntityDsl.from(e).where {
    notExists {
        EntityDsl.from(a).where {
            e.addressId eq a.addressId
            e.employeeName like "%S%"
        }
    }
}
/*
select t0_.EMPLOYEE_ID, t0_.EMPLOYEE_NO, t0_.EMPLOYEE_NAME, t0_.MANAGER_ID, t0_.HIREDATE, t0_.SALARY, t0_.DEPARTMENT_ID, t0_.ADDRESS_ID, t0_.VERSION from EMPLOYEE as t0_ where not exists (select t1_.ADDRESS_ID, t1_.STREET, t1_.VERSION from ADDRESS as t1_ where t0_.ADDRESS_ID = t1_.ADDRESS_ID and t0_.EMPLOYEE_NAME like ? escape ?)
*/
```

## 論理演算子 {#logical-operator}

次の宣言の中で利用できます。

- Having宣言
- When宣言
- Where宣言

### and

宣言の中で式を並べるとAND演算子で連結されます。

```kotlin
EntityDsl.from(a).where {
    a.addressId greater 1
    a.street startsWith "S"
    a.version less 100
}
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID > ? and t0_.STREET like ? escape ? and t0_.VERSION < ?
*/
```

明示的にAND演算子を使いたい場合は`and`関数にラムダ式を渡します。

```kotlin
EntityDsl.from(a).where {
  a.addressId greater 1
  and {
    a.street startsWith "S"
    a.version less 100
  }
}
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID > ? and (t0_.STREET like ? escape ? and t0_.VERSION < ?)
*/
```

### or

OR演算子で連結したい場合は`or`関数にラムダ式を渡します。

```kotlin
EntityDsl.from(a).where {
  a.addressId greater 1
  or {
    a.street startsWith "S"
    a.version less 100
  }
}
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID > ? or (t0_.STREET like ? escape ? and t0_.VERSION < ?)
*/
```

### not

NOT演算子を使うには`not`関数にラムダ式を渡します。

```kotlin
EntityDsl.from(a).where {
    a.addressId greater 5
    not {
        a.addressId greaterEq 10
    }
}.orderBy(a.addressId)
/*
select t0_.ADDRESS_ID, t0_.STREET, t0_.VERSION from ADDRESS as t0_ where t0_.ADDRESS_ID > ? and not (t0_.ADDRESS_ID >= ?) order by t0_.ADDRESS_ID asc
*/
```


## 算術演算子 {#arithmetic-operator}

以下の演算子が使えます。

- `+`
- `-`
- `*`
- `/`
- `%`

`+`演算子を使った例を次に示します。

```kotlin
SqlDsl.update(a).set {
    a.version set (a.version + 10)
}.where {
    a.addressId eq 1
}
/*
update ADDRESS as t0_ set VERSION = (t0_.VERSION + ?) where t0_.ADDRESS_ID = ?
*/
```

## 文字列関数 {#string-function}

次の関数が使えます。

- concat
- lower
- upper
- trim
- ltrim
- rtrim

`concat`関数を使った例を次に示します。

```kotlin
SqlDsl.update(a).set {
  a.street set (concat(concat("[", a.street), "]"))
}.where {
  a.addressId eq 1
}
/*
update ADDRESS as t0_ set STREET = (concat((concat(?, t0_.STREET)), ?)) where t0_.ADDRESS_ID = ?
*/
```

## 集約関数 {#aggregate-function}

集約関数はどこからでも呼び出せますが、呼び出して得られる式は`having`や`select`で使われることを想定しています。

```kotlin
SqlDsl.from(e)
    .groupBy(e.departmentId)
    .having {
        count(e.employeeId) greaterEq 4L
    }
    .orderBy(e.departmentId)
    .select(e.departmentId, count(e.employeeId))
/*
select t0_.DEPARTMENT_ID, count(t0_.EMPLOYEE_ID) from EMPLOYEE as t0_ group by t0_.DEPARTMENT_ID having count(t0_.EMPLOYEE_ID) >= ? order by t0_.DEPARTMENT_ID asc
*/
```

### avg

```kotlin
SqlDsl.from(a).select(avg(a.addressId))
/*
select avg(t0_.ADDRESS_ID) from ADDRESS as t0_
*/
```

### count

SQLの`count(*)`に変換するには`count`関数を引数なしで呼び出します。

```kotlin
SqlDsl.from(a).select(count()).first()
/*
select count(*) from ADDRESS as t0_
*/
```

`count`関数をカラムを指定して呼び出すこともできます。

```kotlin
SqlDsl.from(a).select(count(a.street)).first()
/*
select count(t0_.STREET) from ADDRESS as t0_
*/
```

### sum

```kotlin
SqlDsl.from(a).select(sum(a.addressId)).first()
/*
select sum(t0_.ADDRESS_ID) from ADDRESS as t0_
*/
```

### max

```kotlin
SqlDsl.from(a).select(max(a.addressId))
/*
select max(t0_.ADDRESS_ID) from ADDRESS as t0_
*/
```

### min

```kotlin
SqlDsl.from(a).select(min(a.addressId))
/*
select min(t0_.ADDRESS_ID) from ADDRESS as t0_
*/
```

## リテラル {#literal}

バインド変数を介さず直接値をリテラルとしてSQLに埋め込みたい場合は`literal`を呼び出します。

`literal`に渡せるKotlinの型は以下のものです。

- Boolean
- Int
- Long
- String

使用例です。

```kotlin
SqlDsl.insert(a).values {
  a.addressId set 100
  a.street set literal("STREET 100")
  a.version set literal(100)
}
/*
insert into ADDRESS (ADDRESS_ID, STREET, VERSION) values (?, 'STREET 100', 100)
*/
```