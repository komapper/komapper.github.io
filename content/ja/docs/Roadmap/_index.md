---
title: "Roadmap"
linkTitle: "Roadmap"
weight: 100
description: >
  Komapperのこれから
---

## 概要 {#overview}

Komapperのロードマップを示します。

## 現在のステータス {#status}

- 開発中
  - ただし、ほとんどの機能は実装済み
  - サポートする4つのデータベース全てを使って [CI](https://github.com/komapper/komapper/actions/workflows/build.yml) を実施するなど正しく動作することも確認ずみ

## 今後の予定 {#plan}

- ドキュメントを網羅的に記載できたらKomapper 1.0 GAをリリース

## 方針 {#policy}
### 依存ライブラリ {#dependencies}

- KotlinのバージョンはKSPが対応するバージョンに合わせる
- kotlinx.coroutinesのバージョンはKotlinに合わせる
- 1.0 GAリリースまでは依存ライブラリは基本的に最新版を使う
  - 1.0 GA以降の依存ライブラリへの対応方針は要検討

### R2DBC対応 {#r2dbc}

- R2DBC対応はR2DBC SPIの0.9 GAに合わせて安定させる
  - R2DBCの [ロードマップ](https://r2dbc.io/2021/02/25/r2dbc-0.9.m1-released#roadmap) によると0.9 GAは2021年9月リリース予定
  - KomapperはR2DBC対応の各データベースの実装ドライバーがSPIの0.9 GAに対応する時期の影響を受けるため、各ドライバーの挙動が安定するまでは破壊的変更も止む無しとする

### ドキュメント {#documentation}

- まず日本語版を整備しその後英語版を用意する

### コミュニティ {#community}

- もし利用者が増えるようであればコミュニティで意見交換するような場を検討する
  - まずは利用者や興味を持ってくれる人を増やさねば...
  - それまではGitHubのディスカッション機能などすでに利用可能なものを使う
