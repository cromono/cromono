# 크론 모니터링

## 프로젝트 소개

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

### 인증 관련

- 로그인
- 로그아웃
- root 관리자
- 유저 관리자
- 유저 별 크론 조회 권한
- 유저 그룹(팀)
- 팀 별 크론 조회 권한

### 알람 관련

- 크론 작업 별 알람 발생 조건(몇분 이상 실행, 정해진 시간에 시작 안됨 등) 및 알람 대상 설정
- 알람 방법 : 하이웍스 메신저, 모니터링 서버에 자체적인 알람

## Language, Frameworks, IDE, DBMS

- Node.js + Typescript
- Express
- MySQL
- InfluxDB + Telegraf
- Kafka

## API Documentation

[![Run in Postman](https://run.pstmn.io/button.svg)](https://app.getpostman.com/run-collection/8f9f0d4b75afcfcda430?action=collection%2Fimport)
