# Data Model: 商品検索機能

## Product（商品）

既存の商品を購入者が一覧・検索・詳細確認するための表示対象。

| Field | Type | Required | Description | Validation / Notes |
|---|---|---:|---|---|
| id | long | yes | 商品を一意に識別する値 | 既存の主キー。詳細 URL に使用 |
| name | string | yes | 商品名 | 既存項目。キーワード部分一致の対象 |
| description | string | yes | 商品説明 | 新規項目。既存行は空文字。キーワード部分一致と詳細表示の対象 |
| price | integer | yes | 商品価格 | 既存項目。検索結果・詳細に表示 |
| imageUrl | string | yes | 商品画像 URL | 既存項目。表示用 DTO に含める |
| category | string | yes | 商品カテゴリ | 既存項目。1商品1カテゴリ、カテゴリ条件は完全一致 |
| stock | integer | yes | 在庫数 | 既存項目。既存 API 契約との互換性のため表示用 DTO に含める |

### Relationships and Lifecycle

- Product は1つの category 値を持つ。v1では複数カテゴリ選択を扱わない。
- Product の識別子は既存の `id` を使用する。新しい検索用エンティティは作成しない。
- 商品の作成・更新・削除のライフサイクルは本機能で変更しない。

## ProductSearchCriteria（検索条件）

検索要求から構成される一時的な条件で、永続化しない。

| Field | Type | Required | Description | Validation / Notes |
|---|---|---:|---|---|
| keyword | string | no | 商品名・説明を検索する文字列 | 前後空白を除去。空白のみは未指定。入力全体を部分一致 |
| category | string | no | 絞り込むカテゴリ | 未指定なら全カテゴリ。指定時はカテゴリ完全一致 |

### Query Semantics

- keyword と category が未指定なら全商品を返す。
- keyword のみなら商品名または説明に keyword を含む商品を返す。
- category のみなら category が一致する商品を返す。
- 両方なら keyword 条件と category 条件を同時に満たす商品を返す。
- keyword の大文字・小文字は区別しない。

## ProductResponse（商品表示 DTO）

API が一覧・詳細で公開する商品情報。Entity の内部表現とは分離する。

- `id`, `name`, `description`, `price`, `imageUrl`, `category`, `stock` を含む。
- 検索結果が0件の場合は空配列を返し、HTTP 200 とする。
- 指定された商品 id が存在しない場合は HTTP 404 とする。
