# Java가 이미 설치되어 있는 베이스 이미지 사용
FROM openjdk:17-jdk-slim

# 작성자 정보
LABEL authors="sejin"

# JAR 파일을 컨테이너 내부로 복사
COPY /build/libs/test-was-0.0.1-SNAPSHOT.jar /app/myapp.jar

# 작업 디렉토리 설정 (선택)
WORKDIR /app

# JAR 실행
ENTRYPOINT ["java", "-jar", "myapp.jar"]