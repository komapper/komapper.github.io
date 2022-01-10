---
title: "Roadmap"
linkTitle: "Roadmap"
weight: 100
description: >
  Roadmap for Komapper
---

{{% pageinfo %}}
We are currently working on the translation from Japanese to English. We would appreciate your cooperation.
{{% /pageinfo %}}

## 概要 {#overview}

2021年12月時点におけるKomapperのロードマップを示します。

## 現在のステータス {#status}

- 開発中
  - ただし、ほとんどの機能は実装済み
  - サポートする4つのデータベース全てを使って [CI](https://github.com/komapper/komapper/actions/workflows/build.yml) を実施するなど正しく動作することも確認ずみ

## 今後の予定 {#plan}

- ドキュメントを網羅的に記載できたらKomapper 1.0 GAをリリース
  - ただし、R2DBC SPI 0.9に対応したドライバが出揃った後にする予定

## 方針 {#policy}
### 依存ライブラリ {#dependencies}

- Kotlinのバージョンは1.5.31以上をサポートする
  - kotlinx.coroutinesのバージョンはKotlinに合わせる
  - KSPのバージョンはKotlinに合わせる
- 1.0 GAリリースまでは依存ライブラリは基本的に最新版を使う
  - 1.0 GA以降の依存ライブラリへの対応方針は要検討

### ビルドツール {#build-tools}

- Gradle 7.2以上をサポートする
- MavenをサポートするかはKSP対応次第
  - KSPさえ対応すればMavenでもビルドできると思われる
  - ただし、データベースからコードを生成するGradleプラグインと同等のMavenプラグインを作る予定はない

### R2DBC対応 {#r2dbc}

- R2DBC対応はR2DBC SPIの1.0 GAに合わせて安定させる
  - R2DBCの [ブログ](https://r2dbc.io/2021/12/06/r2dbc-0.9.0-goes-ga) によると1.0 GAのリリースは2022年のQ1もしくはQ2に予定されている
  - KomapperはR2DBC対応の各データベースの実装ドライバーの影響を受けるため、各ドライバーの挙動が安定するまでは破壊的変更も止む無しとする

### ドキュメント {#documentation}

- まず日本語版を整備しその後英語版を用意する

### コミュニティ {#community}

- もし利用者が増えるようであればコミュニティで意見交換するような場を検討する
  - まずは利用者や興味を持ってくれる人を増やさねば...
  - それまではGitHubのディスカッション機能などすでに利用可能なものを使う
