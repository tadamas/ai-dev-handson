# Quickstart: 商品検索機能

## Prerequisites

- Java 21
- Docker / Docker Compose
- PostgreSQL 15（リポジトリの dev container 構成を利用可能）

## Start the backend

```bash
cd backend
./mvnw spring-boot:run
```

バックエンドは `http://localhost:8080` で起動する。DB が未起動の場合は、リポジトリの PostgreSQL 構成を先に起動する。

## Validation scenarios

### 1. 全商品一覧

```bash
curl -i 'http://localhost:8080/api/products'
```

期待結果: `200 OK` と商品配列が返る。

### 2. キーワード検索

```bash
curl -i 'http://localhost:8080/api/products?keyword=%E3%83%9A%E3%83%B3'
```

期待結果: 商品名または説明に「ペン」を含む商品だけが返る。大文字・小文字の違いは結果に影響しない。

### 3. カテゴリ検索

```bash
curl -i 'http://localhost:8080/api/products?category=writing'
```

期待結果: `category` が `writing` と一致する商品だけが返る。

### 4. キーワードとカテゴリの組み合わせ

```bash
curl -i 'http://localhost:8080/api/products?keyword=%E3%83%9A%E3%83%B3&category=writing'
```

期待結果: キーワード条件とカテゴリ条件の両方を満たす商品だけが返る。

### 5. 該当なしと詳細

```bash
curl -i 'http://localhost:8080/api/products?keyword=does-not-exist'
curl -i 'http://localhost:8080/api/products/1'
curl -i 'http://localhost:8080/api/products/999999'
```

期待結果: 該当なしは `200 OK` と空配列、存在する商品は `200 OK` と詳細、存在しない id は `404 Not Found`。

## Automated validation

```bash
cd backend
./mvnw test
```

受け入れテストでは、キーワード、カテゴリ、両条件の組み合わせ、空結果、空白除去、大文字小文字非区別、商品詳細の表示、存在しない id の 404 を確認する。実装の詳細は [data-model.md](data-model.md) と [products-api.md](contracts/products-api.md) を参照する。
