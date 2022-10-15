# PovoGiga

https://github.com/vascarpenter/PovoGiga
- PovoGigaPost の Jetpack Compose / retrofit2 で書き直した版
  - 当然こちらのほうがわかりやすいしコード量も少ない

### 問題点

- 日本語入力にATOKを入れている場合、Composeにおける 数字入力専用欄が ATOKのバグのために "." がIMEキーボード内に表示されない
  - EditTextの時は問題なかったのに。
  - Gboard なら問題なし。
  - キーボードを切り替えれば当然入力可能。

### このandroidアプリをコンパイルする前に

- build.gradle :app から ３つの文字列を参照しているので
- `~/.gradle/gradle.properties` に追加しておく
```
# 自分のサイトにあった設定に差し替えてね

povoserverurl=https://ogehage.tk
povopostapi=post
povogetapi=get?apikey=THEKEY
povoapikey=THEKEY
```
- kotlin ソース内で参照してます

### 変更点
- HiltとviewModel Kotlin flowで書き直し