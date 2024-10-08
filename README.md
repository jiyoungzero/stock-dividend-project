## 내 배당금은 얼마나 나올까? 주식 배당금 서비스 💰

미국 주식 배당금 정보를 제공하는 API 서비스입니다. 

### 핵심 목표
✅ 야후파이넨스 홈페이지(+ Scrapper 인터페이스로 다른 홈페이지도 가능하게끔 범용성을 높였습니다)를 분석하고 스크래핑하여 필요한 데이터를 추출/저장/Schedule추가 <br>
✅ 회원별 데이터를 관리하고 예상 배당금 액수를 계산 <br>
✅ 서비스에서 캐시 서버(Redis) 구성 <br>

### 기술스택
Spring Boot, Java, JPA, H2, Redis, Jsoup


### 🚩 최종 구현 API 리스트
**1) GET - finance/dividend/{companyName}**
- 회사 이름을 인풋으로 받아서 해당 회사의 메타 정보와 배당금 정보를 반환
- 잘못된 회사명이 입력으로 들어온 경우 400 status 코드와 에러메시지 반환

**2) GET - company/autocomplete**
- 자동완성 기능을 위한 API
- 검색하고자 하는 prefix 를 입력으로 받고, 해당 prefix 로 검색되는 회사명 리스트 중 10개 반환

**3) GET - company**
- 서비스에서 관리하고 있는 모든 회사 목록을 반환
- 반환 결과는 Page 인터페이스 형태

**4) POST - company**
- 새로운 회사 정보 추가
- 추가하고자 하는 회사의 ticker 를 입력으로 받아 해당 회사의 정보를 스크래핑하고 저장
- 이미 보유하고 있는 회사의 정보일 경우 400 status 코드와 적절한 에러 메시지 반환
- 존재하지 않는 회사 ticker 일 경우 400 status 코드와 적절한 에러 메시지 반환

**5) DELETE - company/{ticker}**
- ticker 에 해당하는 회사 정보 삭제
- 삭제시 회사의 배당금 정보와 캐시도 모두 삭제
  
 **6) POST - auth/signup**
- 회원가입 API 
- 중복 ID 방지
- 패스워드는 암호화된 형태로 저장

**7) POST - auth/signin**
- 로그인 API
- 회원가입이 되어있고, 아이디/패스워드 정보가 옳은 경우 JWT 발급
  
---
#### 회원 권한 : 'READ', 'WRITE' 

```@PreAuthorize("hasRole('권한명')")``` 사용 

##### 'READ' : 전체 회사 조회, 배당금 조회
##### 'WRITE' : 회사 추가, 회사 삭제


