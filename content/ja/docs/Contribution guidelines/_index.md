---
title: "Contribution Guidelines"
linkTitle: "Contribution Guidelines"
weight: 500
description: >
  Komapperへ貢献する方法
---

Komapperはオープンソースプロジェクトであり、Komapperとそのドキュメントをより良くするためのパッチやコントリビューションを歓迎しています。

## Contributing to Komapper

Komapperのソースコードは <https://github.com/komapper/komapper> にあります。

### Code reviews

プロジェクトメンバーによる投稿を含め、すべての投稿にはレビューが必要です。
この目的のために、GitHubのプルリクエストを使用しています。
プルリクエストの使い方については、[GitHub Help](https://help.github.com/articles/about-pull-requests/) を参照してください。

### Creating issues

あるいは、Komapperで検討したいものがある（あるいは期待通りに動かないものを見つけた）が、自分で解決する方法がわからない場合には、
[issue](https://github.com/komapper/komapper/issues) を作成してください。

## Contributing to these docs

本ドキュメントは、Hugo static site generatorを使用したサイトから提供されます。

サイトのデプロイメントを管理するためにGitHub Pagesを使用しています。ここでの説明は、GitHubの基本的なワークフローに精通していることを前提としています。

### Quick start

1. GitHubで [Komapperのドキュメンテーションレポジトリ](https://github.com/komapper/komapper.github.io) をフォークします。
1. 変更を加えプルリクエストを送ります。

### Updating a single page

ドキュメントを使用しているときに変更したい点を見つけた場合、そのためのショートカットを用意しています。

1. ページの右上にある **ページの編集** をクリックします。
3. **Fork this repository and propose changes** または **Update your Fork** をクリックして、編集するプロジェクトの最新版を取得します。フォークの適切なページが編集モードで表示されます。
4. 変更を加えプルリクエストを送ります。

### Previewing your changes locally

自分のローカルHugoサーバーを実行して作業中に変更点をプレビューしたい場合は次の手順に従ってください。

1. `git clone`を使って [Komapperのドキュメンテーションレポジトリ](https://github.com/komapper/komapper.github.io) をあなたのプロジェクトにフォークしてローカルコピーを作ってください。
   その際、`--recurse-submodules`の指定を忘れないようにしてください。さもないと動作するサイトを生成するために必要なコードの一部を取得できません。

    ```
    git clone --recurse-submodules --depth 1 https://github.com/komapper/komapper.github.io.git
    ```

2. `komapper.github.io`ディレクトリに移動し、サイトをビルドしHugoサーバーを起動させるために次のdockerコマンドを実行します。

    ```
    docker compose up
    ```
    
    デフォルトでは、あなたのサイトは <http://localhost:1313/> で利用できます。これで、サイトがローカルに提供されているので、Hugoはコンテンツの変更を監視し自動的にサイトを更新します。
   
3. GitHub の通常のワークフローに従って、ファイルを編集し、コミットし、フォークに変更をプッシュし、プルリクエストを作成します。

### Creating an issue

もし、ドキュメントに何かを求めているが、自分で解決する方法がわからない場合は、[このリポジトリ](https://github.com/komapper/komapper.github.io) に課題を作成してください。
また、特定のページに関する問題を作成するには、そのページの右上にある **ドキュメントのissueを作成** リンクをクリックします。