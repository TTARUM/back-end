# TTARUM 백엔드

회원가입, 상품 조회, 찜, 장바구니, 배송지, 쿠폰, 주문, 리뷰, 문의 기능을 제공하는 **NestJS REST API 서버**입니다. 화면을 제공하는 프런트엔드는 포함되어 있지 않습니다. API는 HTTP 요청을 받아 JSON으로 결과를 반환합니다.

서버와 테스트는 TypeScript로 작성되어 있습니다. DB 초기화용 SQL과 Docker·CI 설정도 함께 제공합니다.

## 목차

- [1. 준비할 것](#1-준비할-것)
- [2. 처음 실행하기](#2-처음-실행하기)
- [3. API 직접 사용하기](#3-api-직접-사용하기)
- [4. API 목록](#4-api-목록)
- [5. 환경 변수](#5-환경-변수)
- [6. 코드를 읽는 순서](#6-코드를-읽는-순서)
- [7. 데이터와 업무 규칙](#7-데이터와-업무-규칙)
- [8. 테스트와 명령어](#8-테스트와-명령어)
- [9. Docker와 배포](#9-docker와-배포)
- [10. 자주 발생하는 오류](#10-자주-발생하는-오류)

## 1. 준비할 것

| 도구 또는 서비스 | 용도 | 필요한 시점 |
| --- | --- | --- |
| Node.js 22 이상과 npm | TypeScript 서버 실행 및 의존성 설치 | 항상 |
| MySQL | 회원·상품·주문 등의 영구 데이터 저장 | 서버 실행 시 |
| Redis | 이메일 인증번호와 인증 상태 저장 | 이메일 인증·아이디 찾기 사용 시 |
| SMTP 계정 | 실제 인증 이메일 발송 | 이메일 발송 시 |
| S3 버킷과 AWS 접근 권한 | 이미지 저장 | 이미지 첨부 시 |
| Docker + Compose | MySQL·Redis 또는 서버를 컨테이너로 실행 | Docker 실행 방식을 선택할 때만 |

프로젝트는 NestJS 11, TypeORM 0.3, TypeScript, Jest를 사용합니다. 정확한 설치 버전은 `package-lock.json`에 기록되어 있습니다. Compose와 CI의 MySQL 이미지 설정은 `mysql:8.3`입니다.

**처음에는 MySQL과 JWT 설정만으로 회원가입·로그인·상품 조회를 실행할 수 있습니다.** Redis는 지연 연결하며, 메일·S3 설정은 해당 기능을 호출할 때 필요합니다. 이미지 없는 리뷰·문의 생성도 가능합니다.

## 2. 처음 실행하기

아래 명령은 저장소 최상위 폴더, 즉 `package.json`이 있는 폴더에서 실행합니다. 명령 예제는 Windows PowerShell 기준입니다.

### 2-1. 의존성 설치

```powershell
node --version
npm --version
npm ci
```

`npm ci`는 잠금 파일에 기록된 버전대로 필요한 패키지를 `node_modules`에 설치합니다.

### 2-2. 환경 설정 파일 만들기

`.env`가 없다면 한 번만 복사합니다. 이미 있다면 기존 설정을 사용하세요.

```powershell
Copy-Item .env.example .env
```

`.env.example`은 예시이고, 서버가 읽는 파일은 **`.env`**입니다. `.env`는 Git 추적 대상에서 제외되어 있습니다. 실제 비밀번호와 키를 `.env.example`에 넣지 마세요.

우선 다음 값을 설정합니다.

```dotenv
PORT=8080
DATABASE_HOST=localhost
DATABASE_PORT=3306
DATABASE_NAME=ttarum
DATABASE_USERNAME=root
DATABASE_PASSWORD=실제_MySQL_비밀번호
JWT_SECRET_KEY=생성한_무작위_키
JWT_EXPIRES_IN_SECONDS=86400
```

다음 명령으로 키를 생성한 뒤 출력값을 `JWT_SECRET_KEY`에 붙여 넣습니다. 최소 32바이트가 필요합니다.

```powershell
node -e "console.log(require('node:crypto').randomBytes(48).toString('hex'))"
```

`DATABASE_PASSWORD=secret`은 예시일 뿐입니다. 기존 MySQL을 사용한다면 해당 계정의 실제 비밀번호로 바꾸세요. `.env` 변경 후에는 서버를 종료하고 다시 시작하는 것이 확실합니다.

### 2-3. MySQL 준비: 두 방법 중 하나 선택

**A. 이미 설치된 MySQL 사용**

MySQL 서비스를 실행하고 `.env`에 주소, 포트, 계정, 비밀번호를 설정합니다. 새 DB를 초기화할 계정에는 DB·테이블 생성 및 외래 키 추가 권한이 필요합니다. 이미 준비된 DB를 사용할 때 서버는 DB 생성 권한을 요구하지 않습니다.

**B. Docker로 MySQL과 Redis 실행**

Docker를 실행한 상태에서 다음 명령을 사용합니다.

```powershell
docker compose up -d mysql redis
docker compose ps
```

로컬 `.env`는 `DATABASE_HOST=localhost`, `REDIS_HOST=localhost`, `DATABASE_USERNAME=root`, `REDIS_PASSWORD=`로 설정합니다. MySQL 비밀번호는 `.env`의 `DATABASE_PASSWORD`를 사용합니다. Compose는 별도의 일반 DB 사용자를 생성하지 않습니다.

이미 다른 MySQL이 3306 포트를 사용 중이면 A 방식을 사용하거나 포트 구성을 조정해야 합니다. 기존 Docker 볼륨이 있다면 `.env`의 비밀번호 변경만으로 MySQL 계정 비밀번호가 바뀌지는 않습니다.

### 2-4. 새 DB 초기화

**새로 시작하는 경우에만** 실행합니다.

```powershell
npm run db:init
```

이 명령은 다음 작업을 합니다.

1. 설정한 데이터베이스가 없으면 생성합니다.
2. DB가 비어 있는지 확인합니다. 테이블이 이미 있으면 중단합니다.
3. 엔티티에 맞는 테이블과 외래 키를 생성합니다.
4. ID가 `1`인 신규 가입 10% 할인 쿠폰을 등록합니다.

성공하면 `Empty database initialized.`가 출력됩니다. 회원과 상품은 생성하지 않습니다.

기존 DB 또는 이미 초기화한 DB는 이 단계를 건너뜁니다. 서버는 `synchronize: false`이므로 실행할 때 스키마를 자동 변경하지 않습니다. 엔티티를 수정해도 기존 테이블은 바뀌지 않습니다. 현재 별도의 버전별 DB 마이그레이션 명령은 없습니다.

### 2-5. 서버 실행 및 종료

```powershell
npm run start:dev
```

`Nest application successfully started`가 출력되면 기동한 것입니다. 코드 변경 시 자동으로 다시 시작합니다. 종료하려면 실행한 터미널에서 **Ctrl+C**를 누릅니다.

| 주소 | 설명 |
| --- | --- |
| http://localhost:8080/api/items/list | 로그인 없이 상품 목록 확인 |
| http://localhost:8080/api-docs/swagger | Swagger API 문서 |
| http://localhost:8080/api-docs | OpenAPI JSON |

처음 상품 목록은 다음과 같습니다. 오류가 아니라 데이터가 없는 상태입니다.

```json
{ "itemSummaryResponseList": [] }
```

`/`에는 홈페이지가 없습니다. 확인할 때 위의 API 주소를 사용하세요. Swagger에는 요청 필드가 충분히 정의되지 않은 부분이 있으므로, 아래 예제와 DTO를 함께 참고하세요.

Swagger에서 로그인 후 응답의 `token` 값을 상단 **Authorize**에 입력하세요(`Bearer ` 접두어 없이 토큰만 입력). 이후 실행하는 요청에 인증 헤더가 붙습니다. 상품 목록은 비회원도 조회할 수 있지만, 내 찜 여부인 `inWishList`를 확인하려면 찜한 계정의 토큰이 필요합니다. 찜 추가·삭제 후에는 `GET /api/items/list`를 다시 실행해야 새 상태가 표시됩니다. 토큰 없이 조회하면 `inWishList`는 `false`입니다. Swagger의 공통 인증 표시는 토큰 전달을 위한 설정이며 공개 API의 비회원 접근을 차단하지 않습니다.

## 3. API 직접 사용하기

서버를 켜둔 채 **별도 PowerShell 터미널**에서 진행합니다. 아래 예제는 연결된 DB에 회원과 주문 등을 실제로 저장하므로 개발용 DB에서 사용하세요.

### 3-1. 회원가입과 로그인

```powershell
$baseUrl = 'http://localhost:8080/api'

$registerBody = @{
  name = 'Demo User'
  nickname = 'demo01'
  phoneNumber = '010-1234-5678'
  loginId = 'demo_user01'
  password = 'Demo1234!'
  email = 'demo01@example.com'
} | ConvertTo-Json

Invoke-RestMethod -Method Post -Uri "$baseUrl/members/register" `
  -ContentType 'application/json' -Body $registerBody

$loginBody = @{
  loginId = 'demo_user01'
  password = 'Demo1234!'
} | ConvertTo-Json

$login = Invoke-RestMethod -Method Post -Uri "$baseUrl/auth/login" `
  -ContentType 'application/json' -Body $loginBody

$headers = @{ Authorization = "Bearer $($login.token)" }
Invoke-RestMethod -Uri "$baseUrl/members/coupons" -Headers $headers
```

회원가입 결과는 `{ "memberId": 1 }` 형태이며 ID는 DB에 따라 다릅니다. 로그인 응답의 `token`을 이후 요청의 `Authorization: Bearer <token>` 헤더에 전달합니다. 예제 계정을 이미 생성했다면 회원가입을 건너뛰고 로그인하세요.

회원가입 조건:

- 이름 1~45자, 닉네임 1~10자, 전화번호 1~15자.
- 로그인 ID 5~20자, 유효한 이메일 주소.
- 비밀번호 8~20자이며 영문·숫자·허용 특수문자를 각각 포함. 예제의 `!`는 허용됩니다. 정확한 허용 문자는 `RegisterDto`의 정규식을 참고하세요.
- 닉네임·로그인 ID·이메일 중복 차단. 가입 시 ID 1 쿠폰 지급.

현재 회원가입 서비스는 **이메일 인증 완료 여부를 검사하지 않습니다.** 인증메일 API는 별도로 구현되어 있습니다.

### 3-2. 개발용 상품 하나 준비

상품 생성 API와 자동 상품 시드는 없습니다. 주문을 시험하려면 MySQL Workbench 또는 MySQL 클라이언트에서 개발용 DB에 다음 SQL을 **한 번** 실행합니다. DB 이름을 바꿨다면 `USE`도 수정하세요.

```sql
USE ttarum;

INSERT INTO category (name) VALUES ('Demo category');
SET @demo_category_id = LAST_INSERT_ID();

INSERT INTO item (
  name, description, price, item_image_url,
  item_description_image_url, category_id,
  rating_sum, rating_count, order_count, created_at
) VALUES (
  'Demo wine', 'Sample item for local API testing', 20000,
  'https://example.com/wine.jpg', NULL, @demo_category_id,
  0, 0, 0, CURRENT_TIMESTAMP
);

SELECT LAST_INSERT_ID() AS item_id;
```

이미지 URL은 예시 문자열이며 실제 이미지가 제공되지는 않습니다. 실제 상품 데이터가 있다면 이 SQL 대신 기존 상품 ID를 사용하세요.

### 3-3. 상품 조회, 찜, 장바구니

```powershell
$items = Invoke-RestMethod -Uri "$baseUrl/items/list?query=Demo&page=0&size=9"
$itemId = $items.itemSummaryResponseList[0].id

Invoke-RestMethod -Method Post -Uri "$baseUrl/members/wish-item" `
  -Headers $headers -ContentType 'application/json' `
  -Body (@{ itemId = $itemId } | ConvertTo-Json)

Invoke-RestMethod -Method Post -Uri "$baseUrl/members/carts" `
  -Headers $headers -ContentType 'application/json' `
  -Body (@{ itemId = $itemId; amount = 2 } | ConvertTo-Json)

Invoke-RestMethod -Uri "$baseUrl/members/carts" -Headers $headers
```

같은 상품을 장바구니에 다시 추가하면 수량을 더합니다. 같은 상품을 다시 찜하면 오류를 반환합니다. `itemId`가 비어 있다면 상품 SQL 실행 여부와 조회 결과를 먼저 확인하세요.

### 3-4. 주문 생성과 조회

위 예제 상품 20,000원짜리 2개를 쿠폰 없이 주문합니다.

```powershell
$orderBody = @{
  comment = 'Leave at the door'
  phoneNumber = '010-1234-5678'
  address = 'Demo address 101'
  recipient = 'Demo User'
  orderItems = @(@{ itemId = $itemId; quantity = 2 })
  totalPrice = 40000
} | ConvertTo-Json -Depth 4

$orderId = Invoke-RestMethod -Method Post -Uri "$baseUrl/orders" `
  -Headers $headers -ContentType 'application/json' -Body $orderBody

Invoke-RestMethod -Uri "$baseUrl/orders/$orderId" -Headers $headers
```

주문 생성 응답은 객체가 아닌 **주문 ID 숫자**입니다. 실제 상품 가격을 사용하므로 다른 상품을 골랐다면 금액도 바꿔야 합니다.

`totalPrice`는 **쿠폰 할인 후 상품 금액이며 배송비는 제외**합니다. 신규 가입 쿠폰을 쓰려면 본문에 `couponId = 1`을 추가하고 `totalPrice = 36000`으로 바꿉니다. 할인 후 10만원 미만이면 배송비는 3,000원, 이상이면 0원입니다. 쿠폰은 주문 성공 시 소비됩니다.

주문이 생성되어도 장바구니는 자동으로 비워지지 않습니다. 현재 실제 카드 결제는 실행하지 않습니다.

### 3-5. 리뷰·문의 작성 및 파일 업로드

리뷰와 문의 생성은 이미지가 없어도 **multipart/form-data**를 사용합니다. Postman 등의 API 클라이언트에서 Body → form-data를 선택하세요. Authorization 헤더에는 로그인 토큰을 전달합니다. `Content-Type`의 boundary는 클라이언트가 생성하도록 두세요.

| 요청 | 텍스트 필드 | 파일 필드 |
| --- | --- | --- |
| `POST /api/reviews` | `reviewCreationRequest`: 아래 JSON 문자열 | `images` (선택, 여러 개 가능) |
| `POST /api/inquiries` | `inquiryRequest`: 아래 JSON 문자열 | `images` (선택, 여러 개 가능) |
| `POST /api/members/profile-image` | 없음 | `image` (필수, 한 개) |

리뷰 JSON 예시 — `orderId`, `itemId`는 위에서 생성한 실제 ID로 교체합니다.

```json
{
  "orderId": 1,
  "itemId": 1,
  "title": "Good wine",
  "content": "I enjoyed it.",
  "rating": 5
}
```

문의 JSON 예시:

```json
{
  "itemId": 1,
  "title": "Delivery question",
  "content": "When will it arrive?",
  "isSecret": true
}
```

파일은 PNG/JPEG/GIF, 개별 최대 5MB입니다. 리뷰·문의는 최대 10개를 받으며 파일의 실제 헤더도 검사합니다. 이미지를 첨부하면 S3 설정이 필요합니다.

### 3-6. 이메일 인증과 아이디 찾기

Redis와 SMTP 설정 후 사용합니다. 인증번호는 문자열로 보내며, 유효기간은 3분입니다. 같은 목적·이메일로 재전송하려면 60초 기다려야 하고 인증 확인은 최대 5회입니다.

- 가입 인증: `POST /members/mail/send`에 `{ "email": "..." }` → 받은 번호를 `POST /members/mail/check`에 `{ "email": "...", "verificationCode": "123456" }`로 전달.
- 아이디 찾기: `POST /members/mail/send/find-id`에 `{ "name": "...", "email": "..." }` → `POST /members/mail/check/find-id`에서 번호 확인 → 반환된 `uuid`를 `sessionId`로 사용.
- 마지막으로 `GET /members/mail/find-id`에 `name`, `email`, `verificationCode`, `sessionId` 쿼리를 보냅니다. 쿼리 값은 URL 인코딩하세요.

아이디 찾기의 마지막 응답은 호환성을 위해 `{ "email": "로그인ID" }` 형태입니다. 필드 이름은 `email`이지만 값은 로그인 ID이며, 성공 후 인증 세션은 삭제됩니다.

## 4. API 목록

아래 경로 앞에는 모두 **`/api`**를 붙입니다. `:id`에는 실제 숫자 ID를 넣습니다. '인증 필요'는 Bearer 토큰이 필요한 요청입니다. 공개 API도 잘못된 토큰을 함께 보내면 401을 반환합니다.

### 인증·회원

| 메서드 | 경로 | 인증 | 요청 / 동작 |
| --- | --- | --- | --- |
| POST | `/auth/login` | 공개 | `loginId`, `password` → 회원 정보와 `token` |
| POST | `/members/register` | 공개 | 회원가입 DTO → `memberId` |
| DELETE | `/members/withdraw` | 필요 | 회원 탈퇴, 이후 토큰 사용 차단 |
| POST | `/members/profile-image` | 필요 | multipart `image` → URL 문자열 |
| GET | `/members/wish-item` | 필요 | 찜 목록, `page`, `size` |
| POST | `/members/wish-item` | 필요 | JSON `{ "itemId": 1 }` |
| DELETE | `/members/wish-item` | 필요 | 쿼리 `itemId` |
| GET | `/members/carts` | 필요 | 장바구니 목록 |
| POST | `/members/carts` | 필요 | JSON `itemId`, `amount` |
| PUT | `/members/carts/:id` | 필요 | 상품 ID, JSON `amount`로 수량 교체 |
| DELETE | `/members/carts` | 필요 | JSON `{ "itemIdList": [1, 2] }` |
| GET | `/members/address` | 필요 | 배송지 목록 |
| POST | `/members/address` | 필요 | 배송지 추가 |
| POST | `/members/address/:id` | 필요 | 배송지 수정 (PUT이 아님) |
| DELETE | `/members/address/:id` | 필요 | 기본 배송지는 삭제 불가 |
| GET | `/members/coupons` | 필요 | 보유 쿠폰 목록 |
| POST | `/members/mail/send` | 공개 | 가입 인증메일 전송 |
| POST | `/members/mail/check` | 공개 | 가입 인증번호 확인 |
| POST | `/members/mail/send/find-id` | 공개 | 이름·이메일 확인 후 메일 전송 |
| POST | `/members/mail/check/find-id` | 공개 | 번호 확인 → `uuid` |
| GET | `/members/mail/find-id` | 공개 | 인증 세션 확인 후 로그인 ID 반환 |

배송지 JSON은 `addressAlias`, `recipient`, `address`, `detailAddress`, `phoneNumber`와 선택 필드 `isDefault` 또는 `default`를 사용합니다. 응답의 기본 배송지 필드는 `default`입니다.

### 상품·주문·리뷰·문의

| 메서드 | 경로 | 인증 | 요청 / 동작 |
| --- | --- | --- | --- |
| GET | `/items/list` | 공개 | 이름 검색 `query`, `page`, `size` |
| GET | `/items/popular-list` | 공개 | 판매량 순, `number` 기본 5, 최대 100 |
| GET | `/items/similar-price` | 공개 | 필수 `price` ±10,000원, `page`, `size` |
| GET | `/items/popular-in-category/:id` | 공개 | 카테고리별 판매량 순, `page`, `size` |
| GET | `/items/category/:id` | 공개 | 카테고리별 목록, `page`, `size` |
| GET | `/items/:id` | 공개 | 상품 상세 |
| POST | `/orders` | 필요 | 주문 생성 → 주문 ID |
| GET | `/orders/list` | 필요 | 내 주문 목록, `page`, `size` |
| GET | `/orders/:id` | 필요 | 내 주문 상세 |
| GET | `/reviews` | 필요 | 필수 `itemId`, `page`, `size` |
| GET | `/reviews/member` | 필요 | 내 리뷰 목록, `page`, `size` |
| POST | `/reviews` | 필요 | multipart 리뷰 생성 |
| GET | `/reviews/:id/update` | 필요 | 내 리뷰 수정용 데이터 조회 |
| PUT | `/reviews/:id` | 필요 | JSON `content`, `rating` |
| DELETE | `/reviews/:id` | 필요 | 내 리뷰 삭제 |
| GET | `/inquiries/list` | 공개 | 필수 `itemId`, `page`, `size` |
| GET | `/inquiries/:id` | 필요 | 문의 상세, 비밀글은 작성자만 조회 |
| POST | `/inquiries` | 필요 | multipart 문의 생성 |

공통 페이지 번호는 **0부터** 시작하며 `size`는 1~100입니다. 기본 크기는 상품 9, 비슷한 가격·카테고리 인기상품 7, 찜 8, 주문 5, 리뷰·문의 10입니다. 페이지 응답에 총 개수나 총 페이지 정보는 없습니다.

응답 포장 방식은 API마다 다릅니다. 상품 목록은 `itemSummaryResponseList`, 비슷한 가격은 `itemSummaryList`, 찜 목록은 `wishlist` 배열을 담은 객체이며 주문·리뷰·문의 목록은 배열입니다. 생성 API도 기본적으로 200을 반환합니다. 오류는 다음 형태입니다.

```json
{
  "dateTime": "2026-09-21T00:00:00.000Z",
  "message": "주문 금액이 일치하지 않습니다."
}
```

DTO에 없는 필드를 보내면 400이 발생합니다. JSON의 수량·가격·평점은 문자열이 아닌 숫자로 보내세요.

## 5. 환경 변수

`.env.example`에 있는 변수의 용도입니다. 실제 `.env` 값은 이 문서에 포함하지 않습니다.

| 변수 | 기본 / 예시 | 설명 |
| --- | --- | --- |
| `PORT` | `8080` | 서버가 직접 수신하는 포트 |
| `DATABASE_HOST` | `localhost` | MySQL 호스트 |
| `DATABASE_PORT` | `3306` | MySQL 포트 |
| `DATABASE_NAME` | `ttarum` | DB 이름 |
| `DATABASE_USERNAME` | `root` | 실제 DB 계정 |
| `DATABASE_PASSWORD` | `secret`은 예시 | 실제 계정 비밀번호 |
| `JWT_SECRET_KEY` | 직접 생성 | JWT 서명용 키, 최소 32바이트 |
| `JWT_EXPIRES_IN_SECONDS` | `86400` | 새 토큰 유효기간, 초 단위 |
| `REDIS_HOST` / `REDIS_PORT` | `localhost` / `6379` | 인증 세션 저장소 |
| `REDIS_PASSWORD` | 빈 값 | 인증 없는 로컬 Redis에서는 비움 |
| `MAIL_HOST` / `MAIL_PORT` | `smtp.gmail.com` / `587` | SMTP 서버, 465이면 secure 연결 |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | 직접 설정 | SMTP 로그인 및 발신자 계정 |
| `AWS_REGION` | `ap-northeast-2` | S3 리전 |
| `AWS_ACCESS_KEY` / `AWS_SECRET_KEY` | 선택 | 둘 다 있으면 명시적 자격 증명 사용. 없으면 AWS SDK의 기본 자격 증명 경로 사용 |
| `S3_BUCKET` | 직접 설정 | 파일 업로드 시 필수 |
| `S3_PUBLIC_URL` | 선택 | 객체 조회용 기본 URL. 없으면 버킷·리전 기반 주소 사용 |
| `CORS_ORIGINS` | `http://localhost:3000` | 허용할 프런트엔드 출처, 여러 개는 공백 없이 쉼표로 구분 |
| `APP_PORT` | `8080` | Compose의 호스트 공개 포트. `.env.example`에는 없으며 필요 시 추가 |

`DATABASE_URL`은 사용하지 않습니다. S3 업로드에는 객체 생성 권한이, 실패한 업로드 정리에는 객체 삭제 권한이 필요합니다. 코드가 이미지 공개 권한을 자동 설정하거나 서명 URL을 발급하지는 않습니다. 반환된 URL에서 이미지를 읽을 수 있도록 버킷 또는 CDN 설정을 별도로 준비해야 합니다. 저장할 이미지 URL은 전체 길이 100자 이하로 제한됩니다.

## 6. 코드를 읽는 순서

```text
src/
  main.ts                    서버 시작, 전역 검증·오류 처리·CORS·Swagger
  app.module.ts              기능 모듈 연결, .env 로드
  common/http.ts             인증 요청 타입, 페이지 DTO, 공통 오류 응답
  database/
    database.module.ts       MySQL 연결 설정
    entities.ts              테이블과 TypeScript 클래스 매핑
    init.ts                  새 DB 초기화
  auth/auth.module.ts        로그인 서비스, JWT Guard, 공개 API 표시
  member/
    member.module.ts         회원 Controller와 Module
    member.dto.ts            요청 필드와 검증 규칙
    member.service.ts        가입·찜·장바구니·배송지·쿠폰 처리
    mail.service.ts          SMTP 발송과 Redis 인증 상태
  item/item.module.ts        상품 조회 Controller·Service·Module
  order/order.module.ts      주문 DTO·Controller·Service·Module
  review/review.module.ts     리뷰 DTO·Controller·Service·Module
  inquiry/inquiry.module.ts   문의 DTO·Controller·Service·Module
  storage/storage.module.ts  이미지 검사와 S3 연동
 test/                       HTTP 통합 테스트와 서비스 테스트
```

요청이 처리되는 순서는 다음과 같습니다.

```text
HTTP 요청 → JWT Guard → DTO 검증 → Controller → Service → TypeORM → MySQL
                                     ↓
                           JSON 응답 또는 공통 오류 응답
```

- **Module**: NestJS가 어떤 Controller와 Service를 연결할지 선언합니다.
- **Controller**: URL과 HTTP 메서드를 함수에 연결합니다. `@Get`, `@Post` 등을 찾으면 API 진입점을 알 수 있습니다.
- **DTO**: 클라이언트가 보낼 데이터 모양과 조건입니다. 예: `@Min(1)`은 최소 1이라는 뜻입니다.
- **Service**: 금액 계산, 소유권 확인, DB 저장 같은 실제 업무 처리를 담당합니다.
- **Entity**: DB 테이블의 각 컬럼을 클래스 속성과 연결합니다.
- **Guard**: 기본적으로 JWT와 회원 상태를 확인합니다. `@Public()`이 있으면 토큰 없는 요청도 허용합니다.

예를 들어 장바구니 기능을 바꾸려면 `member.module.ts`의 `POST carts` → `member.dto.ts`의 `CartAddDto` → `member.service.ts`의 `addCart` → `entities.ts`의 `Cart` 순서로 읽으면 됩니다. 현재 회원 외 여러 기능은 DTO·Controller·Service가 하나의 `*.module.ts` 파일에 함께 있습니다.

## 7. 데이터와 업무 규칙

| 데이터 영역 | 테이블 |
| --- | --- |
| 회원·계정 | `member`, `normal_member`, `oauth_member`, `member_provider` |
| 상품 | `category`, `item` |
| 회원별 상품·주소 | `wishlist`, `cart`, `address` |
| 쿠폰 | `coupon`, `member_coupon` |
| 주문 | `order`, `order_item` |
| 리뷰 | `review`, `review_image` |
| 문의 | `inquiry`, `inquiry_image`, `inquiry_answer` |

`normal_member.member_id`는 회원 ID와 같고, 장바구니·찜은 회원 ID와 상품 ID를 함께 기본 키로 사용합니다. 주문 상품은 주문 ID와 상품 ID를 함께 기본 키로 사용합니다. SQL에서 `order` 테이블을 직접 다룰 때는 예약어이므로 백틱으로 감싸야 합니다.

- 비밀번호는 BCrypt 해시로 저장합니다. JWT 기본 만료는 24시간이며 탈퇴 회원의 토큰은 매 요청 차단합니다.
- 장바구니 수량은 1~1,000,000이고 같은 상품 추가 시 합산합니다. 배송지의 기본 여부 변경과 장바구니 추가는 회원 행 잠금으로 처리합니다.
- 주문에는 중복 상품 ID를 넣을 수 없습니다. 상품 가격 합계, 할인, 클라이언트가 보낸 금액을 대조합니다. 주문·쿠폰 소비·판매량 갱신은 하나의 트랜잭션입니다.
- 주문 상태는 생성 시 `COMPLETE`, 결제 수단은 `CREDIT_CARD`로 저장됩니다. 결제 승인, 재고 차감, 배송 추적, 주문 취소 API는 구현되어 있지 않습니다.
- 리뷰는 본인이 주문한 상품에만 작성 가능하며 평점은 정수 0~5입니다. 주문·상품 조합당 한 번만 작성할 수 있고, 기존 리뷰를 삭제해도 다시 작성할 수 없습니다. 수정은 내용·평점만 가능하며 제목·이미지는 수정하지 않습니다.
- 리뷰와 회원은 `is_deleted` 표시로 삭제 처리합니다. 리뷰 생성·수정·삭제 시 상품 평점 집계를 함께 갱신합니다.
- 비밀 문의글은 공개 목록에서 제목을 가리고 작성자 이름도 마스킹합니다. 상세 내용은 작성자만 볼 수 있습니다. 일반 문의 상세도 현재는 로그인이 필요합니다.
- OAuth 계정과 문의 답변 테이블은 있으나 OAuth 로그인, 답변 작성, 상품 관리용 API는 없습니다.

## 8. 테스트와 명령어

| 명령 | 동작 |
| --- | --- |
| `npm ci` | 잠금 파일 기준 의존성 설치 |
| `npm run start:dev` | TypeScript 개발 서버 실행·변경 감지 |
| `npm run typecheck` | 타입 검사, 파일 생성 없음 |
| `npm run build` | `dist/`에 JavaScript 생성 |
| `npm start` | 이미 빌드된 `dist/main.js` 실행 |
| `npm test` | Jest 테스트 실행 |
| `npm run format` | `src/`, `test/`의 TypeScript 포맷 수정 |
| `npm run db:init` | 새 DB·테이블·가입 쿠폰 초기화 |

일반 개발 검증:

```powershell
npm run typecheck
npm test
npm run build
```

기본 테스트는 메모리 SQL.js를 사용하고 메일·S3를 대체 객체로 바꾸므로 별도 MySQL·Redis·SMTP·AWS 연결 없이 실행할 수 있습니다. `test/api.spec.ts`는 HTTP 요청, `test/order.spec.ts`는 주문 계산과 트랜잭션 호출, `test/review.spec.ts`는 구매자·구매 상품 검증을 다룹니다.

MySQL 전용 테스트 2개는 기본 실행에서 건너뜁니다. 실제 MySQL 테스트는 코드에 고정된 **127.0.0.1:3306 / root / 비밀번호 test / DB ttarum_test**를 사용합니다. `.env`의 DB 설정을 사용하지 않으며 `dropSchema: true`로 **해당 테스트 DB의 기존 테이블을 삭제·재생성**합니다. 반드시 버려도 되는 테스트 환경에서만 사용하세요.

```powershell
$env:TEST_MYSQL = '1'
try {
  npm test
} finally {
  Remove-Item Env:TEST_MYSQL
}
```

이 테스트는 MySQL 서버와 빈 `ttarum_test` DB가 준비되어 있어야 합니다. CI는 별도 MySQL 컨테이너를 준비해 실행합니다. 테스트 통과가 실제 SMTP·S3 연결까지 검증했다는 뜻은 아닙니다.

## 9. Docker와 배포

### 로컬 전체 컨테이너 실행

먼저 2장의 `.env` 설정을 완료합니다. **처음 만든 DB라면** 다음 순서로 호스트에서 초기화한 뒤 앱 컨테이너를 실행합니다. 이미 초기화된 DB는 `db:init`을 건너뜁니다.

```powershell
docker compose up -d mysql redis
# docker compose ps에서 MySQL 준비 상태를 확인한 뒤 실행
npm run db:init
docker compose up -d --build app
docker compose logs -f app
```

이 방식의 초기화 명령은 호스트에서 실행하므로 `.env`의 DB 호스트는 `localhost`입니다. 앱 컨테이너 내부에서는 Compose가 DB 호스트를 `mysql`, Redis 호스트를 `redis`로 덮어씁니다.

컨테이너 내부 서버 포트는 `PORT=8080`으로 유지하세요. 외부 포트를 바꾸려면 `.env`에 `APP_PORT=8081`처럼 설정합니다. 같은 포트로 `npm run start:dev`와 앱 컨테이너를 동시에 실행할 수는 없습니다.

```powershell
docker compose stop
```

위 명령은 컨테이너를 정지하고 DB 볼륨은 남깁니다. Dockerfile은 Node.js 22 Alpine에서 빌드하고 최종 이미지에서는 개발 의존성을 제거합니다. 최종 이미지에서 `ts-node` 기반 `npm run db:init`은 실행할 수 없으므로 초기화는 호스트 등 개발 의존성이 설치된 환경에서 수행합니다.

### GitHub Actions / EC2

- `ci.yml`: push와 pull request에서 타입 검사·테스트·빌드 및 별도 MySQL 통합 테스트 실행.
- `cicd.yml`: 수동 실행(`workflow_dispatch`). 테스트·빌드 후 해당 커밋을 EC2에 체크아웃하고 Compose로 실행.
- 필요한 GitHub secrets: `SSH_PRIVATE_KEY`, `EC2_HOST`, `SSH_KNOWN_HOSTS`.
- 원격 저장소 위치: `/home/ec2-user/back-end`, 환경 파일: `/home/ec2-user/.env`.
- `docker-compose.production.yml`은 환경 파일 경로와 외부 포트 80을 덮어씁니다. `!override`를 지원하는 Compose가 필요합니다.

배포 워크플로는 새 DB의 테이블 초기화를 수행하지 않습니다. 최초 배포 전에 DB·쿠폰과 환경 설정을 준비해야 합니다. 메일 전송, S3 파일 접근, 기존 데이터와 스키마 호환성도 배포 환경에서 확인해야 합니다.

## 10. 자주 발생하는 오류

| 메시지 / 증상 | 원인과 확인 방법 |
| --- | --- |
| `Configuration key "JWT_SECRET_KEY" does not exist` | `.env`가 없거나 키가 누락됨. `.env.example`만 수정한 것은 아닌지 확인 |
| `JWT_SECRET_KEY must contain at least 32 bytes` | 서명 키가 너무 짧음. 2장의 명령으로 새 키 생성 |
| `Access denied for user ...` | MySQL 계정·비밀번호 또는 접근 권한 불일치. 예시 `secret` 대신 실제 값 사용 |
| `Unknown database 'ttarum'` | DB가 없음. 새 환경은 `npm run db:init`, 기존 DB는 `DATABASE_NAME` 확인 |
| `db:init only supports an EMPTY database` | 이미 테이블이 있음. 정상 초기화된 DB라면 재초기화 없이 서버 실행. 초기화가 중간 실패한 경우는 남은 테이블 상태부터 확인 |
| `Table ... doesn't exist` | DB만 있고 테이블이 없거나 다른 스키마에 연결. 초기화 및 DB 이름 확인 |
| `ECONNREFUSED` / DB 연결 재시도 | MySQL이 꺼졌거나 호스트·포트가 틀림 |
| `EADDRINUSE ... 8080` | 다른 서버가 같은 포트를 사용 중. 기존 서버 종료 또는 `PORT` 변경 |
| 상품 목록이 빈 배열 | 초기화 시 상품은 생성되지 않음. 개발용 상품 SQL 또는 기존 데이터 필요 |
| `신규 가입 쿠폰이 설정되지 않았습니다.` | `coupon` 테이블의 ID 1 데이터가 없음. 기존 DB의 쿠폰 설정 확인 |
| 401 / `유효하지 않은 인증 정보입니다.` | 토큰 누락·위조·만료, JWT 키 변경, 탈퇴 회원 여부 확인 |
| 403 | 다른 회원의 주문·리뷰·배송지 또는 비밀 문의글에 접근한 경우 |
| `주문 금액이 일치하지 않습니다.` | DB 상품 가격 × 수량에서 쿠폰 할인한 금액과 `totalPrice` 비교. 배송비는 본문 금액에서 제외 |
| 메일 설정·전송 오류 | SMTP 계정과 비밀번호, Redis 연결 확인. 공급자가 요구하는 SMTP 인증 방식 사용 |
| `S3_BUCKET 설정이 필요합니다.` | 이미지 업로드 시 버킷 미설정. 이미지 없이 먼저 API 테스트 가능 |
| 이미지 URL 길이 오류 | 저장 URL이 100자를 초과함. 실제 객체를 서비스하는 짧은 CDN URL 등으로 구성 |
| 브라우저에서만 CORS 오류 | `CORS_ORIGINS`에 프런트엔드의 정확한 프로토콜·호스트·포트 추가 |
| `Namespace 'global.Express' has no exported member 'Multer'` | `npm ci`로 개발 의존성을 설치하고 `tsconfig.json`의 `types`에 `multer`가 있는지 확인 |
| `moduleResolution=node10 is deprecated` | 현재 설정은 `module`·`moduleResolution` 모두 `Node16`. 이전 설정이나 다른 tsconfig 사용 여부 확인 |
| IDE에만 TypeScript 오류가 남음 | VS Code 명령 팔레트에서 `TypeScript: Select TypeScript Version` → 작업 영역 버전 선택 후 `TypeScript: Restart TS Server` |
| `Cannot find module ... npm-cli.js` | `npm --version`으로 npm 자체 실행을 확인. 설치 경로와 실행 환경의 파일 접근 권한 확인. 프로젝트 TypeScript 오류와는 별개 |

설정 문제를 찾을 때 `.env`의 실제 비밀번호·키·토큰을 로그나 이슈에 붙이지 말고, 오류 메시지와 사용한 명령을 기준으로 확인하세요.
