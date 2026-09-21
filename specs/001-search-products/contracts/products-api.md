# Products API Contract

## List and Search Products

`GET /api/products`

### Query Parameters

| Name | Required | Description |
|---|---:|---|
| `keyword` | no | 前後空白を除いた入力全体を、商品名または説明に対する大文字小文字を区別しない部分一致検索に使用する |
| `category` | no | 商品カテゴリの完全一致条件。1つのカテゴリのみ指定する |

検索条件は検索ボタン押下時に送信する。パラメータを省略する、または `keyword` が空白だけの場合はその条件を指定しない。

### Responses

- `200 OK`: `ProductResponse` の配列。該当なしの場合は `[]`。
- `400 Bad Request`: 本契約で許容しない入力形式の場合。

### Example

```http
GET /api/products?keyword=pen&category=writing
```

```json
[
  {
    "id": 1,
    "name": "ボールペン",
    "description": "書きやすいペン",
    "price": 120,
    "imageUrl": "images/pen.jpg",
    "category": "writing",
    "stock": 120
  }
]
```

## Get Product Detail

`GET /api/products/{id}`

### Responses

- `200 OK`: 1件の `ProductResponse`。`id`, `name`, `description`, `price`, `imageUrl`, `category`, `stock` を含む。
- `404 Not Found`: 指定された商品が存在しない。
