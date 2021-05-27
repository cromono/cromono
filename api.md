# cron-monitor

# 프로젝트 소개

> cron은 유닉스 계열 컴퓨터 운영 체제의 시간 기반 잡 스케줄러이다. 소프트웨어 환경을 설정하고 관리하는 사람들은 작업을 고정된 시간, 날짜, 간격에 주기적으로 실행할 수 있도록 스케줄링하기 위해 cron을 사용한다.

- 위키백과

매일 새벽 4시 일일 수행 작업 예) `0 0 4 * * * * /home/users/everydayjob`

크론 작업을 여러 서버에 접속하지 않고 한개의 화면에서 실행여부, 로그를 확인하고 싶음

## 필수 기능

- 모니터링 할 크론 등록
- 크론 실행 기록(실행시각) 조회
- 실행된 프로세스의 로그 조회
- 크론 종료 기록 조회
- 실행된 프로세스의 실행 시간 조회

## Indices

- [process](#process)

  - [프로세스 로그 조회](#1-프로세스-로그-조회)
  - [프로세스 시간 조회](#2-프로세스-시간-조회)

- [Ungrouped](#ungrouped)

  - [서버별 크론 job 목록 조회](#1-서버별-크론-job-목록-조회)
  - [크론 job 등록](#2-크론-job-등록)
  - [크론 job 프로세스 목록 조회](#3-크론-job-프로세스-목록-조회)
  - [크론 서버 목록 조회](#4-크론-서버-목록-조회)

---

## process

### 1. 프로세스 로그 조회

**_Endpoint:_**

```bash
Method: GET
Type:
URL: http://localhost:3000/process/logs
```

**_Query params:_**

| Key              | Value    | Description     |
| ---------------- | -------- | --------------- |
| cron_procress_id | 54717194 | 조회할 크론 pid |

**_More example Requests/Responses:_**

##### I. Example Request: 로그 조회 성공

**_Query:_**

| Key              | Value    | Description |
| ---------------- | -------- | ----------- |
| cron_procress_id | 54717194 |             |

**_Status Code:_** 200

<br>

##### II. Example Request: Not found

**_Query:_**

| Key              | Value    | Description |
| ---------------- | -------- | ----------- |
| cron_procress_id | 54717194 |             |

**_Status Code:_** 404

<br>

### 2. 프로세스 시간 조회

**_Endpoint:_**

```bash
Method: GET
Type:
URL: http://localhost:3000/process/time
```

**_Query params:_**

| Key              | Value    | Description     |
| ---------------- | -------- | --------------- |
| cron_procress_id | 54717194 | 조회할 크론 pid |

**_More example Requests/Responses:_**

##### I. Example Request: 시간 조회 성공

**_Query:_**

| Key              | Value    | Description |
| ---------------- | -------- | ----------- |
| cron_procress_id | 54717194 |             |

**_Status Code:_** 200

<br>

##### II. Example Request: Not found

**_Query:_**

| Key              | Value    | Description |
| ---------------- | -------- | ----------- |
| cron_procress_id | 54717194 |             |

**_Status Code:_** 404

<br>

## Ungrouped

### 1. 서버별 크론 job 목록 조회

**_Endpoint:_**

```bash
Method: GET
Type:
URL: http://localhost:3000/cron-server/jobs
```

**_URL variables:_**

| Key       | Value               | Description                 |
| --------- | ------------------- | --------------------------- |
| server_ip | consectetur nisi do | (Required) 조회할 서버의 ip |

**_More example Requests/Responses:_**

##### I. Example Request: 크론 job 목록 조회 성공

**_Status Code:_** 200

<br>

##### II. Example Request: Not found

**_Status Code:_** 404

<br>

### 2. 크론 job 등록

**_Endpoint:_**

```bash
Method: POST
Type: RAW
URL: http://localhost:3000/cron-jobs
```

**_Headers:_**

| Key          | Value            | Description |
| ------------ | ---------------- | ----------- |
| Content-Type | application/json |             |

**_Body:_**

```js
{
    "server_ip": "ut commodo est cillum",
    "cron_name": "i",
    "cron_expr": "ea fugiat"
}
```

**_More example Requests/Responses:_**

##### I. Example Request: 정상적으로 등록됨

**_Body:_**

```js
{
    "server_ip": "ut commodo est cillum",
    "cron_name": "i",
    "cron_expr": "ea fugiat"
}
```

**_Status Code:_** 201

<br>

##### II. Example Request: Bad request

**_Body:_**

```js
{
    "server_ip": "ut commodo est cillum",
    "cron_name": "i",
    "cron_expr": "ea fugiat"
}
```

**_Status Code:_** 400

<br>

### 3. 크론 job 프로세스 목록 조회

**_Endpoint:_**

```bash
Method: GET
Type:
URL: http://localhost:3000/server/job/actions
```

**_Query params:_**

| Key         | Value    | Description        |
| ----------- | -------- | ------------------ |
| cron_job_id | 54717194 | 조회할 크론 job id |

**_More example Requests/Responses:_**

##### I. Example Request: job 실행 목록 조회 성공

**_Query:_**

| Key         | Value    | Description |
| ----------- | -------- | ----------- |
| cron_job_id | 54717194 |             |

**_Status Code:_** 200

<br>

##### II. Example Request: Not found

**_Query:_**

| Key         | Value    | Description |
| ----------- | -------- | ----------- |
| cron_job_id | 54717194 |             |

**_Status Code:_** 404

<br>

### 4. 크론 서버 목록 조회

**_Endpoint:_**

```bash
Method: GET
Type:
URL: http://localhost:3000/cron-servers
```

**_More example Requests/Responses:_**

##### I. Example Request: 서버 목록 조회 성공

**_Status Code:_** 200

<br>

##### II. Example Request: Not found

**_Status Code:_** 404

<br>

**_Available Variables:_**

| Key     | Value                 | Type   |
| ------- | --------------------- | ------ |
| baseUrl | http://localhost:3000 | string |

---

[Back to top](#cron-monitor)

> Made with &#9829; by [thedevsaddam](https://github.com/thedevsaddam) | Generated at: 2021-05-27 10:43:17 by [docgen](https://github.com/thedevsaddam/docgen)
