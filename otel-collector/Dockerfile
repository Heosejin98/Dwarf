# Java 8이 설치되어 있는 베이스 이미지 사용
FROM openjdk:8-jdk-slim

# 작성자 정보
LABEL authors="sejin"

# JAR 파일을 컨테이너 내부로 복사
COPY /build/libs/otel-collector-1.0-SNAPSHOT-all.jar /app/otel-collector.jar

# 작업 디렉토리 설정
WORKDIR /app

# gRPC와 HTTP 포트 노출
EXPOSE 4317 4318

# JAR 실행
ENTRYPOINT ["java", "-jar", "otel-collector.jar"]
