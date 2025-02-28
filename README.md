# このプロジェクトのルール

## ファイルの消去は原則行わない
  - すべてのファイル削除は、必要に応じてGitHub上で行わる
  - これは**既存**のファイルに適用される。必要に応じて、自分のブランチでファイルを変更・削除して大丈夫
## 開発は自分のブランチで行う
  - 各開発者は、**作業名-名前**という命名規則に従って自分のブランチを作成する必要がある
  - 例えば、**ログイン-Connor**
  - ブランチのプログラムが実行可能であることを確認してから、メインにプッシュしてください
  - 古いブランチは**2日後**に削除される
  - COPY_OF_MAIN_バックアップブランチは、メインブランチのデータが失われないよう、2日ごとに更新される
### ブランチ管理の手順
  - 空白部分を右クリックする 
  - 「TortoiseGit」をクリックする  
  - 「ブランチ作成」をクリックする  
  - ガイドラインに従って名前と説明を入力する  
  - 「OK」をクリックする  
  - ブランチはあなたのコンピュータ上に作成されますが、GitHubにはまだ作成されていない。ブランチは初回のプッシュが行われるまでGitHub上には作成されない。このプッシュは、作成直後でも最初のコミットの際でも実行できる
  - 空白部分を右クリックする  
  - 「切替/チェックアウト」をクリックする  
  - 作成した新しいブランチを選択する  
  - これで新しいブランチで作業できるようになる  
  - ここからは、ブランチを編集できる（ファイルを追加したり、既存のファイルや機能を編集したりする）  
  - ファイルをコミットに追加するときは、すぐにプッシュせず、「Git同期」を使用してください  
  - ブランチをGitHubにプッシュする準備ができたら、空白部分を右クリックする  
  - 左上で作業中のブランチを選択する 
  - 「プッシュ」をクリックする  
  - これで作業中のブランチをGitHubにプッシュした  
  - このブランチはメインブランチとは別であり、GitHub上で**管理者だけ**がメインブランチにマージすることができる 
  - 新しい作業を始める前に、メインブランチからプルすることを忘れないでください  
  - 空白部分を右クリックする  
  - 「Git同期」をクリックする  
  - 左上でメインブランチを選択する 
  - 「プル」をクリックする 
  - 新しい作業を始める準備ができたら、この手順を繰り返してください  

