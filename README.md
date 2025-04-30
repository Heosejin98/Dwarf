# Dwarf 초경량 온프레미스 모니터링 솔루션

OpenTelemetry Protocol(OTLP)을 활용 초경량 온프레미스 모니터링 솔루션

## 스팩

- Java 8 이상
- Gradle

## 빌드 및 실행

### 빌드

```bash
./gradlew build
```

### 실행

기본 포트(4317)로 실행:

```bash
java -jar build/libs/otel-receiver-1.0-SNAPSHOT-all.jar
```

## Zero-code Instrumentation으로 클라이언트 애플리케이션 설정

Java 애플리케이션에서 코드 변경 없이 OpenTelemetry를 사용하려면 OpenTelemetry Java 에이전트를 사용하세요:

1. [OpenTelemetry Java 에이전트](https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases) 다운로드

2. Java 애플리케이션 실행 시 다음과 같이 에이전트 사용:

```bash
java -javaagent:path/to/opentelemetry-javaagent.jar \
     -Dotel.traces.exporter=otlp \
     -Dotel.exporter.otlp.endpoint=http://localhost:4317 \
     -Dotel.service.name=your-service-name \
     -Dotel.exporter.otlp.protocol=grpc
     -jar your-application.jar
```

### 주요 환경 변수

| 환경 변수 | 설명 | 기본값 |
|------------|-------------|---------|
| OTEL_TRACES_EXPORTER | 트레이스 내보내기 방식 | otlp |
| OTEL_EXPORTER_OTLP_ENDPOINT | 데이터 전송 엔드포인트 | http://localhost:4317 |
| OTEL_SERVICE_NAME | 서비스 이름 | unknown_service |
| OTEL_RESOURCE_ATTRIBUTES | 추가 리소스 속성 | |

## 구조

- `OtelTraceReceiver.java`: gRPC 서버와 OpenTelemetry 트레이스 서비스 구현
- `Main.java`: 애플리케이션 진입점

## 로깅

로그는 콘솔과 `logs/otel-receiver.log` 파일에 기록됩니다. 로그 설정은 `src/main/resources/logback.xml` 파일에서 변경할 수 있습니다.
