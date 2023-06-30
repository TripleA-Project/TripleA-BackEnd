# Triple A
미국 주식 뉴스 분석 서비스
- 미국 주식에 관한 글로벌 뉴스 제공(번역 포함)
- 주식 심볼별 정보(기업명 등) 및 날짜별 주가, 감성지수, 버스량, 미국 3재 주가지수(나스낙, 다우존스, S&P 500) 제공
- 관심 뉴스 카테고리, 심별 및 뉴스 북마크 기능 제공
- 히스토리 뉴스 내역 제공
- 구독 시스템을 통해 혜택 제공
  - 심볼별 주식 차트
  - 뉴스 요약문 무제한 조회(일반 회원은 매일 10번 제한)
  - AI 뉴스 분석(하루 10건)
<br/>

## STACKS & TOOLS
<img src="https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot"/> <img src="https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white"/>  <img src="https://img.shields.io/badge/redis-%23DD0031.svg?&style=for-the-badge&logo=redis&logoColor=white"/> <img src="https://img.shields.io/badge/Junit5-25A162?style=for-the-badge&logo=junit5&logoColor=white"/> <img src="https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white"/> <img src="https://img.shields.io/badge/Jenkins-D24939?style=for-the-badge&logo=Jenkins&logoColor=white"/> <img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=Swagger&logoColor=white"/> <img src="https://img.shields.io/badge/Amazon_AWS-FF9900?style=for-the-badge&logo=amazonaws&logoColor=white"/> <img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white"/> <img src="https://img.shields.io/badge/IntelliJ_IDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white"/> <img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSON%20web%20tokens&logoColor=white"/>

<br/>

## ERD
![TipleA ERD](https://github.com/TripleA-Project/TripleA-BackEnd/assets/107831692/b51223b9-13e0-48ea-92a4-5f33c81d9c24)

<br/>

## 아키텍처
![architecture](https://github.com/TripleA-Project/TripleA-BackEnd/assets/107831692/5ca83964-7863-4796-b84c-83bc6bece88d)

<br/>

## 핵심 기능
- 뉴스 조회
    - 심볼별
    - 카테고리별
    - 키워드별
- 검색
    - 심볼
    - 카테고리
- 북마크 및 관심 설정
    - 뉴스 북마크 설정
    - 관심 카테고리 및 심볼 설정
- 차트
  - 심볼별 주식 차트(월간, 주간, 일간)
- 구독 서비스
  - [Step Pay](https://www.steppay.kr)를 통한 구독 및 결제 시스템
- 번역 제공
  - Naver Papago API를 통한 번역 제공
- AI 뉴스 분석
  - chatGPT를 통한 뉴스별 주식에 대한 분석
  - 예시: `impact: 부정적, action: 보류, comment: FTC의 임시 금지조치는 Microsoft 주식의 거래를 방해할 것입니다. 그러나 이 문제는 미국과 영국의 규제 기관들 사이에서 아직 해결되지 않았으므로 중요한 영향을 끼치지는 않을 것입니다.`

<br/>

## 사용 기술
|      사용 기술       | 기술 설명                                        |
|:----------------:|----------------------------------------------|
|       JPA        | 자바에서 객체와 관계형 데이터베이스를 매핑하기 위해 사용              |
|     QueryDSL     | Runtime 시 발생할 수 있는 SQL 문제를 해소하기 위해 사용        |
| Spring Security  | 인증, 인가를 관리하기 위해 사용                           |
|      Docker      | 애플리케이션을 컨테이너화하여 같은 환경에서 배포 및 실행하기 위해 사용      |
|     Jenkins      | CI/CD (추후 확장성을 생각해서 github actions보다 좋다고 판단) |
|       JWT        | JWT를 쉽게 만들고 검증하기 위해 사용                       |
|      JUnit       | 단위 테스트를 위해 사용(Mockito와 함께)                   |
|      Lombok      | 반복적인 코드 작성(getter, constructor등)을 줄이기 위해 사용  |
|      MySQL       | 가장 익숙한 RDBMS로, 데이터 저장 및 검색을 위해 사용            |
|      Redis       | Refresh token 및 캐시 등을 저장하기 위해 사용             |

<br/>

## 트러블 슈팅
[WIKI 기술 이슈](https://github.com/TripleA-Project/TripleA-BackEnd/wiki/기술-이슈)
<br/>

## Member
| 포지션 | 이름  | 담당                                                                        | GitHub |
| --- |-----|---------------------------------------------------------------------------| --- |
| `BE` `팀장` | 박주영 | - CI / CD<br/>- 회원가입 및 이메일 인증<br/>- 구독<br/>- 뉴스 조회 및 검색<br/>- 히스토리<br/>- 웹 크롤링 | https://github.com/ju-ei8ht |
| `BE` | 임진묵 | - 뉴스 조회 및 검색<br/>- 심볼 조회 및 검색<br/>- 주식 차트                                 | https://github.com/zidanemook |
| `BE` | 김우람 | - JWT 인증 / 인가<br/>-회원정보 수정<br/>-관심 카테고리 생성 및 삭제                           | https://github.com/ramloper |
| `BE` | 예병창 | - 관심 심볼 생성 및 삭제                                                 | https://github.com/DW-RYU |
