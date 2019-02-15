
1. keystore 만들기

https://cheese10yun.github.io/spring-https/

2. HttpsConfig

- local 에서 실행할 경우에만 동작하도록 한다.

https://stackoverflow.com/questions/22625699/how-to-redirect-automatically-to-https-with-spring-boot

http://localhost 로 요청이 올 경우 https://localhost로 이동하도록 한다.

- 프로덕션 환경(AWS)

AWS Certificate Manager 를 사용

AWS를 사용할 경우 무료 인증서 사용
https://aws.amazon.com/ko/blogs/korea/new-aws-certificate-manager-deploy-ssltls-based-apps-on-aws/
https://ap-northeast-2.console.aws.amazon.com/acm/home?region=ap-northeast-2#/


3. Spring Security 설정

WebSecurityConfig
ClientResources

OAuth2 참고
https://opentutorials.org/course/3405
https://www.popit.kr/spring-security-oauth2-%EC%86%8C%EC%85%9C-%EC%9D%B8%EC%A6%9D/
https://www.popit.kr/spring-security-oauth2-%EC%86%8C%EC%85%9C-%EC%9D%B8%EC%A6%9D-%EB%8D%B0%EC%9D%B4%ED%84%B0%EB%B2%A0%EC%9D%B4%EC%8A%A4-%EC%A0%80%EC%9E%A5/


MethodSecurityConfig
특정 권한이 있는 사용자만 메소드에 접근 할 수 있도록 설정하기
https://www.baeldung.com/spring-security-method-security


4. SebSocket & Spring Security Websocket 설정

WebSocketColnfig
SocketSecurityConfig

5. ArgumentResolver 설정

WebConfig

6. Entity를 web api를 통해 json 으로 변경하고자 할 때 hibernate에서 추가한 몇가지 필드가 변환이 안될 경우

WebConfig
Hibernate5Module 설정

7. Entity를 web api를 통해 json 으로 변경할 때 무한히 lazy로딩 되는 문제 해결

https://stackoverflow.com/questions/21177191/spring-mvc-rest-jpa-hibernate-jackson-infinite-recursion-one-to-many-json-error

