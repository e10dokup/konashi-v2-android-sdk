# konashi SDK for Android
[![wercker status](https://app.wercker.com/status/6617295015c7f7518afc67a3182bd241/s/master "wercker status")](https://app.wercker.com/project/bykey/6617295015c7f7518afc67a3182bd241)
[![Download](https://api.bintray.com/packages/konashi-dev/maven/konashi-android-sdk/images/download.svg)](https://bintray.com/konashi-dev/maven/konashi-android-sdk/_latestVersion)

---

<img src="http://konashi.ux-xu.com/img/documents/i2c.png" width="600" />

---

## Supported version

現在、konashi2.0以降のみをサポートしています。

将来、konashi ver1 (緑色のモジュールを搭載) 用のsdk [konashi-v1-android-sdk](https://github.com/YUKAI/konashi-v1-android-sdk)を統合予定です。

## Installation

```groovy
dependencies {
    compile 'com.uxxu.konashi:konashi-android-sdk:0.5.1'
}
```

## Getting Started

\{Android版ドキュメントは2015年8月末をめどに更新予定です\}

## 開発について

### 機能要望やバグ報告をするには
開発者に要望を伝える報告する方法は以下です。

- GitHub の Issues に投稿
  - [https://github.com/YUKAI/konashi-android-sdk/issues](https://github.com/YUKAI/konashi-android-sdk/issues)
  - feature-requests、bug、discussion などのラベルをご使用ください。
- Pull Request
  - バグ見つけて修正しといたよ、というときは Pull Request を **develop ブランチ**に送ってください。
  - 詳細は ブランチの運用 をご覧ください。
- “konashi" をキーワードにつぶやく
  - twitter で #konashi のハッシュをつけるか、 konashi というキーワードを使って tweet してください。
  - もしくは konashi をキーワードにブログに書いてください。
- [contact@ux-xu.com](contact@ux-xu.com) にメールする
  - メールでの報告も受け付けています。

### ブランチの運用

[git-flow](https://github.com/nvie/gitflow) を使用しています。各ブランチの役割は以下です。

- master
  - リリース用のブランチです。GitHubでは master ブランチがデフォルトのブランチです。
- develop
  - 開発用のブランチです。
- feature/***
  - 新機能追加やバグ修正を行うブランチです。develop ブランチから feature ブランチを切り、開発が完了後に develop ブランチに merge します。
- release/v***
  - リリース前ブランチです。develop ブランチから release ブランチを切り、テストが終わり次第 master ブランチにマージされます。(現在は基本的に origin に push されません)


### タグの運用
基本的にリリース時にバージョン名でタグを切ります。konashi 公式ページからリンクされる zip ダウンロード先は最新のリリースタグの zip です。

タグ一覧は[こちら](https://github.com/YUKAI/konashi-android-sdk/tags)。

### Pull Request
**規模の大小関わらず、バグ修正や機能追加などの Pull Request 大歓迎！**

Pull Request を送るにあたっての注意点は以下です。

- 最新の develop ブランチから任意の名前でブランチを切り、実装後に develop ブランチに対して Pull Request を送ってください。
  - master ブランチへの Pull Request は(なるべく)ご遠慮ください。

## ライセンス
konashi のソフトウェアのソースコード、ハードウェアに関するドキュメント・ファイルのライセンスは以下です。

- ソフトウェア
  - konashi-android-sdk のソースコードは [Apache License Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) のもと公開されています。
- ハードウェア
  - konashi の回路図などハードウェア関連のドキュメント・ファイルのライセンスは [クリエイティブ・コモンズ・ライセンス「表示-継承 2.1 日本」](http://creativecommons.org/licenses/by-sa/2.1/jp/deed.ja)です。これに従う場合に限り、自由に複製、頒布、二次的著作物を作成することができます。
  - 回路図のデータ(eagleライブラリ)は公開予定です。
- konashi および konashi2.0 のBLEモジュールのファームウェアは公開しておりません。
