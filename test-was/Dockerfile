# Java 8이 설치되어 있는 베이스 이미지 사용 (Spring Boot 2.7.18에 맞춤)
FROM openjdk:8-jdk-slim

# 작성자 정보
LABEL authors="sejin"

# JAR 파일을 컨테이너 내부로 복사
COPY /build/libs/test-was-0.0.1-SNAPSHOT.jar /app/myapp.jar

# OpenTelemetry Java Agent 복사
COPY /opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

# 작업 디렉토리 설정
WORKDIR /app

# OpenTelemetry 환경변수 설정
ENV OTEL_SERVICE_NAME=test-was
ENV OTEL_RESOURCE_ATTRIBUTES=service.name=test-was,service.version=0.0.1
ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://host.docker.internal:4317
ENV OTEL_EXPORTER_OTLP_PROTOCOL=grpc
ENV OTEL_METRICS_EXPORTER=otlp
ENV OTEL_LOGS_EXPORTER=otlp

# OpenTelemetry Agent와 함께 JAR 실행
ENTRYPOINT ["java", "-javaagent:opentelemetry-javaagent.jar", "-jar", "myapp.jar"]