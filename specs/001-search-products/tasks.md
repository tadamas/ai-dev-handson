# Tasks: 商品検索機能

**Input**: Design documents from `/specs/001-search-products/`

**Prerequisites**: [plan.md](plan.md), [spec.md](spec.md), [research.md](research.md), [data-model.md](data-model.md), [contracts/products-api.md](contracts/products-api.md), [quickstart.md](quickstart.md)

**Organization**: Tasks are grouped by user story. User Story 1 is the MVP search flow; User Story 2 completes the list-to-detail flow.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: 既存プロジェクトの状態を確認し、実装・テストを実行できる前提を整える。

- [X] T001 [P] `backend/pom.xml` と `backend/src/test/java/com/example/ecsite/` の既存テスト構成を確認し、実装前の `cd backend && ./mvnw test` が成功することを記録する

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: すべてのユーザーストーリーが共有する商品説明データと API 表示モデルを準備する。

- [X] T002 [P] `backend/src/main/resources/db/migration/V3__add_product_description.sql` に既存商品へ空文字を設定できる `description` 列を追加する新規 Flyway migration を作成する
- [X] T003 [P] `backend/src/main/java/com/example/ecsite/entity/Product.java` に必須の商品説明属性と getter/setter を追加し、既存の商品項目との永続化マッピングを保つ
- [X] T004 [P] `backend/src/main/java/com/example/ecsite/dto/ProductResponse.java` に商品詳細・一覧で公開する `id`, `name`, `description`, `price`, `imageUrl`, `category`, `stock` の表示 DTO と Entity からの変換処理を作成する

**Checkpoint**: Flyway、Entity、表示 DTO の共有基盤が揃い、ユーザーストーリーの実装を開始できる。

## Phase 3: User Story 1 - 商品を検索する (Priority: P1) 🎯 MVP

**Goal**: 購入者がキーワード、カテゴリ、または両方で商品一覧を絞り込み、0件時も明確な結果を確認できるようにする。

**Independent Test**: `GET /api/products` にキーワード・カテゴリ・両条件を指定し、前後空白、大文字小文字、空白キーワード、0件の結果が仕様どおりになることを確認する。

### Tests for User Story 1

- [X] T005 [US1] `backend/src/test/java/com/example/ecsite/service/ProductServiceTest.java` に、キーワードの前後空白除去、商品名または説明の大文字小文字非区別部分一致、空白キーワードの未指定扱い、カテゴリ単独、キーワードとカテゴリの AND 条件を検証するテストを追加する
- [X] T006 [US1] `backend/src/test/java/com/example/ecsite/controller/ProductControllerTest.java` に、`GET /api/products?keyword=...&category=...` の 200 応答、結果項目、0件時の空配列を検証する MockMvc テストを追加する

### Implementation for User Story 1

- [X] T007 [US1] `backend/src/main/java/com/example/ecsite/repository/ProductRepository.java` に、keyword の商品名・説明部分一致と任意 category 完全一致をデータベース側で組み合わせる検索メソッドを追加する
- [X] T008 [US1] `backend/src/main/java/com/example/ecsite/service/ProductService.java` に、検索キーワードの trim と空文字除外、Repository 呼び出し、ProductResponse への変換を実装する
- [X] T009 [US1] `backend/src/main/java/com/example/ecsite/controller/ProductController.java` の一覧 GET に任意の `keyword` と `category` クエリパラメータを受け付ける処理を追加し、検索結果の DTO 配列を返す

**Checkpoint**: User Story 1 単独で、全件一覧、キーワード検索、カテゴリ検索、組み合わせ検索、空結果を確認できる。

## Phase 4: User Story 2 - 検索結果から商品詳細を見る (Priority: P1)

**Goal**: 購入者が一覧の商品を選択して商品名、説明、価格、カテゴリを含む詳細を確認し、存在しない商品には 404 相当の案内を受けられるようにする。

**Independent Test**: 商品 id の詳細取得で ProductResponse の必須項目を確認し、存在しない id が 404 になることを確認する。

### Tests for User Story 2

- [X] T010 [US2] `backend/src/test/java/com/example/ecsite/service/ProductServiceTest.java` に、既存商品を ProductResponse へ変換する詳細取得と存在しない id の空結果を検証するテストを追加する
- [X] T011 [US2] `backend/src/test/java/com/example/ecsite/controller/ProductControllerTest.java` に、`GET /api/products/{id}` の詳細項目、検索結果から利用できる id、存在しない id の 404 を検証する MockMvc テストを追加する

### Implementation for User Story 2

- [X] T012 [US2] `backend/src/main/java/com/example/ecsite/service/ProductService.java` の商品詳細取得を ProductResponse の Optional に変換し、Entity を API 層へ直接返さないようにする
- [X] T013 [US2] `backend/src/main/java/com/example/ecsite/controller/ProductController.java` の商品詳細 GET を ProductResponse で返し、存在しない id は既存契約どおり 404 にする

**Checkpoint**: User Story 1 の検索結果から取得した商品 id で詳細を表示でき、商品が存在しない場合も独立して検証できる。

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: 受け入れ条件、契約、実行手順を横断して確認する。

- [X] T014 [P] `specs/001-search-products/contracts/products-api.md` と `specs/001-search-products/data-model.md` を実装したレスポンス項目・検索条件に合わせて更新する
- [X] T015 [P] `specs/001-search-products/quickstart.md` の curl 例を実装済みのサンプルデータとレスポンス項目に合わせて確認・更新する
- [X] T016 `backend/src/test/java/com/example/ecsite/controller/ProductControllerTest.java` と `backend/src/test/java/com/example/ecsite/service/ProductServiceTest.java` を含む全テストを `cd backend && ./mvnw test` で実行し、受け入れシナリオが成功することを確認する
- [X] T017 `specs/001-search-products/` の成果物と実装差分をレビューし、既存 migration を変更していないこと、未指定条件・0件・404 の契約が一貫していることを確認する

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1**: 依存なし。既存テストの基準を確認する。
- **Phase 2**: Phase 1 の基準確認後。T002、T003、T004 は互いに異なるファイルのため並列実行可能。
- **Phase 3 (US1)**: Phase 2 完了後。T005 と T006 はテスト作成として先行し、T007 → T008 → T009 の順で実装する。
- **Phase 4 (US2)**: Phase 2 完了後に開始可能だが、T012 の DTO 変換を共有するため、通常は US1 の T008 完了後に進める。T010、T011 は実装前に追加する。
- **Phase 5**: US1、US2 の実装とテスト完了後。

### User Story Dependencies

- **User Story 1 (P1)**: Phase 2 にのみ依存し、MVP として独立して検証できる。
- **User Story 2 (P1)**: Phase 2 に依存する。既存の detail endpoint を DTO 化するため、通常は US1 の DTO 変換方針を引き継ぐが、検索機能なしでも単独検証できる。

## Parallel Opportunities

- Setup: T001 は単独で実行する。
- Foundational: T002、T003、T004 は異なるファイルのため並列実行可能。
- US1: T005 と T006 は異なるテストファイルではないため同時編集せず、担当を分ける場合も順番に統合する。T007 は T005/T006 のテスト方針を確認後に実装する。
- US2: T010 と T011 は同じ既存テストファイルを扱うため並列実行しない。T014 と T015 は異なる設計資料のため並列実行可能。
- US1 と US2: Phase 2 完了後に別担当で並列着手できるが、`ProductService.java` と `ProductController.java` の競合を避けるため、実際には US1 の共有変換方針確定後に US2 を進める。

## Implementation Strategy

### MVP First

1. Phase 1 で既存テストの基準を確認する。
2. Phase 2 で商品説明、Entity、DTO を準備する。
3. Phase 3 の US1 をテスト先行で実装する。
4. T016 相当の範囲で US1 の独立検証を行い、検索 MVP として確認する。

### Incremental Delivery

1. US1 で検索一覧と空結果を提供する。
2. US2 で検索結果からの商品詳細と 404 を完成させる。
3. Polish で API 契約・quickstart・全テストを同期する。

## Notes

- すべての実装タスクは `- [ ] Txxx` 形式で、ユーザーストーリー内のタスクには `[US1]` または `[US2]` を付ける。
- `[P]` は異なるファイルを扱い、未完了タスクへの依存がないものだけに付ける。
- スキーマ変更は `backend/src/main/resources/db/migration/` に新規ファイルを追加し、既存 migration は変更しない。
