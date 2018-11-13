# Welcome
2018.11.10 [백기선](https://www.facebook.com/whiteship)님이 진행하신 '스프링 기반 Rest API 개발 - 그런 Rest API로 괜찮은가' 주제로 강의하신 내용을 복기한 코드입니다.   
[SlideShare](https://www.slideshare.net/whiteship/rest-api-development-with-spring) /
[GitHub](https://github.com/kees…/study/tree/master/ksug201811restapi) /
[GitLab](https://gitlab.com/whiteship/natural)

# Envirionment
* base: JDK 1.8.0_181
* SPRING INITALIZER https://start.spring.io/
  * Gradle & Java & 2.1.0.RELEASE
  * Web × [HATEOAS](https://spring.io/projects/spring-hateoas) × [REST Docs](https://spring.io/projects/spring-restdocs)
  * JPA × H2 × PostgreSQL × [Lombok](https://projectlombok.org/)

# Rest API Requirement
## 'Product Catalog' REST API [HAL](https://en.wikipedia.org/wiki/Hypertext_Application_Language)
상품 등록, 조회 및 수정 API

### GET /api/product
상품 목록 조회 REST API
* 응답에 보여줘야 할 데이터  
  * 상품 목록
  * 링크
    * self
    * profile: 상품 목록 조회 API link
    * get-a-product: 상품 한 개를 조회하는 API link
    * prev: 이전 페이지 (optional)
    * next: 다음 페이지 (optional)

### POST /api/product
* 상품 등록

### GET /api/product/{id}
* 상품 조회

### PUT /api/product/{id}
* 상품 수정

## 'Product Catalog' API 사용 예제
1. (토큰 없이) 상품 목록 조회  
  a. create 링크 안 보임
2. access token 발급 받기 (A 사용자 로그인)
3. (유효한 A 토큰 갖고) 상품 목록 조회  
  a. create 링크 보임
4. (유효한 A 토큰 갖고) 상품 생성
5. (토큰 없이) 상품 조회  
  a. update 링크 안 보임
6. (유효한 A 토큰 갖고) 상품 조회  
  a. update 링크 보임
7. access token 발급 받기 (B 사용자 로그인)  
  a. update 링크 보임
8. (유효한 B 토큰 갖고) 상품 조회  
  a. update 링크 안 보임

## Business Requirement
* 상품 생성 API 입력 항목
  * name: String
  * description: String
  * beginSaleDateTime: Instance
  * closeSaleDateTime: Instance
  * salePrice: Long
  * quantity: Integer
  * vendorRoleType: Enum
* 상품 생성 API 응답 항목
  * id
  * name
  * ...
  * saleStatus: RETAIL, CONSIGNMENT, THIRD_PARTY
  * _links
    * profile (for the self-descriptive message)
    * self
    * publish
