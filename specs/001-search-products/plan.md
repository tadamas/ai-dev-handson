# Implementation Plan: 商品検索機能

**Branch**: `001-search-products` | **Date**: 2026-09-09 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `/specs/001-search-products/spec.md`

## Summary

購入者が商品名・説明のキーワードとカテゴリで商品を絞り込み、一覧から商品詳細を確認できるようにする。既存の `GET /api/products` と `GET /api/products/{id}` を拡張し、データベース側で任意条件を組み合わせて検索する。現行商品モデルに不足する説明項目は新規 Flyway マイグレーションで追加し、検索・詳細レスポンスは DTO で公開する。

## Technical Context

**Language/Version**: Java 21

**Primary Dependencies**: Spring Boot 3.x, Spring Web, Spring Data JPA, Flyway, PostgreSQL driver

**Storage**: PostgreSQL 15+。`products` に `description` 列を追加する新規 Flyway マイグレーションを使用

**Testing**: JUnit 5、Mockito、MockMvc、`backend/mvnw test`

**Target Platform**: Linux 上で動作する Spring Boot Web サービス。既存のローカル開発環境を対象

**Project Type**: REST Web サービス（EC バックエンド）

**Performance Goals**: 代表的な検索で購入者が10秒以内に結果を確認できること。検索は全件取得後のアプリ内フィルタではなく、データベース側で条件を適用する

**Constraints**: `keyword` は前後空白を除去した入力全体の大文字小文字非区別部分一致、`category` は1値の完全一致。結果0件は `200 OK` と空配列、存在しない商品 id は `404 Not Found`

**Scale/Scope**: 既存の商品一覧・詳細 API の v1 拡張。ページ分割、並び替え、価格帯、複数カテゴリ、検索履歴、候補表示は対象外

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

以下のゲートを満たすため、違反なし。

- REST エンドポイントは既存の複数形 `/api/products` を使用する。
- 新規・変更する API の表示レスポンスは DTO と Entity を分離する。
- 検索入力はリクエストパラメータとして受け、空白除去などをサービス層で正規化する。
- 商品説明のスキーマ変更は既存 migration を編集せず、新しい migration を追加する。
- 受け入れシナリオは Service / Controller テストとして追加し、テストを真の判定基準にする。
- 不在の商品 id は HTTP 404 とする。

## Project Structure

### Documentation (this feature)

```text
specs/001-search-products/
├── plan.md              # This file
├── research.md          # Phase 0 research decisions
├── data-model.md        # Phase 1 data model
├── quickstart.md        # Phase 1 validation guide
├── contracts/
│   └── products-api.md  # REST contract
└── tasks.md             # Phase 2 output from /speckit-tasks
```

### Source Code (repository root)

```text
backend/
├── src/main/java/com/example/ecsite/
│   ├── controller/ProductController.java       # 検索クエリと詳細レスポンス
│   ├── dto/ProductResponse.java                # API 表示 DTO
│   ├── entity/Product.java                     # description 属性
│   ├── repository/ProductRepository.java       # 条件付き検索
│   └── service/ProductService.java             # 条件正規化と DTO 変換
├── src/main/resources/db/migration/
│   └── V3__add_product_description.sql         # 既存 products への新規列
└── src/test/java/com/example/ecsite/
    ├── controller/ProductControllerTest.java  # MockMvc 契約テスト
    └── service/ProductServiceTest.java        # 検索条件・結果テスト
```

**Structure Decision**: 既存の単一 Spring Boot バックエンド構成を維持し、商品 API の既存 controller / service / repository / entity を拡張する。新しい frontend は存在しないため本計画では作成せず、REST 契約と curl による検証を提供する。

### Design Artifacts

- [research.md](research.md): 技術判断と代替案
- [data-model.md](data-model.md): 商品・検索条件・表示 DTO
- [contracts/products-api.md](contracts/products-api.md): REST 契約
- [quickstart.md](quickstart.md): curl と Maven による検証手順

## Complexity Tracking

憲章違反がないため、追加の複雑性追跡は不要。
