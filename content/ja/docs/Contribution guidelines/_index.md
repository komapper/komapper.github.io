---
title: "Contribution Guidelines"
linkTitle: "Contribution Guidelines"
weight: 500
description: >
  Komapperへ貢献する方法
---

## 概要 {#overview}

Komapperはオープンソースプロジェクトです。
KomapperとKomapperのドキュメンテーションを改善するためのパッチやコントリビューションを歓迎します。

## Contributing to Komapper

Komapperのソースコードは <https://github.com/komapper/komapper> にあります。

### Code reviews

プロジェクトメンバーによる修正を含め、すべての修正にはレビューが必要です。
この目的のためにGitHubのプルリクエストを使用します。
プルリクエストの使い方については、[GitHub Help](https://help.github.com/articles/about-pull-requests/) を参照してください。

### Creating issues

Komapperで期待通りに動かないものを見つけたが解決する方法がわからない場合は、
[issue](https://github.com/komapper/komapper/issues) を作成してください。

## Contributing to these docs

本ドキュメントは [Hugo](https://gohugo.io/) で作成されたサイトから提供されます。

サイトのデプロイメントを管理するためにGitHubとNetlifyを使用しています。
ここでの説明はGitHubの基本的なワークフローに精通していることを前提としています。

### Quick start

1. GitHubで [Komapperのドキュメンテーションレポジトリ](https://github.com/komapper/komapper.github.io) をフォークします。
1. 変更を加えプルリクエストを送ります。

### Updating a single page

ドキュメントを参照しているときに変更したい点を見つけた場合、以下のショートカットが使えます。

1. ページの右上にある **ページの編集** をクリックします。
2. フォークの適切なページが編集モードで表示されます。
3. 変更を加えプルリクエストを送ります。

### Previewing your changes locally

自分のローカルHugoサーバーを実行し、作業しながら変更点をプレビューしたい場合は次の手順に従ってください。

1. `git clone`を使って [Komapperのドキュメンテーションレポジトリ](https://github.com/komapper/komapper.github.io) のローカルコピーを作ります。
   その際、`--recurse-submodules`の指定を忘れないようにしてください。さもないと動作するサイトを生成するために必要なコードの一部を取得できません。

    ```
    git clone --recurse-submodules --depth 1 https://github.com/komapper/komapper.github.io.git
    ```

2. `komapper.github.io`ディレクトリに移動後、次のdockerコマンドの実行によりサイトをビルドしHugoサーバーを起動させます。

    ```
    docker compose up
    ```
    
    デフォルトでは、サイトは <http://localhost:1313/> で利用できます。
    Hugoはコンテンツの変更を監視し自動的にサイトを更新します。
   
3. GitHubの通常のワークフローに従って、ファイルを編集し、コミットし、フォークに変更をプッシュし、プルリクエストを作成します。

### Creating an issue

もし、ドキュメントを修正したいが解決する方法がわからない場合は、[このリポジトリ](https://github.com/komapper/komapper.github.io) にissueを作成してください。
特定のページに関するissueを作成するには、そのページの右上にある **ドキュメントのissueを作成** リンクをクリックします。
