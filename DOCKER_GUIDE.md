# 🚀 OpenTelemetry Test Environment Guide

완전한 OpenTelemetry 테스트 환경을 위한 Docker Compose 설정입니다.

## 🎯 포함된 서비스

- **otel-collector**: 텔레메트리 데이터 수집기 (포트 4317, 4318)
- **test-was**: Spring Boot 웹 애플리케이션 (포트 8080)


### 수동 실행

```bash
# 1. 프로젝트 빌드
./gradlew clean build

# 2. 모든 서비스 시작
docker-compose up --build

# 3. 백그라운드 실행
docker-compose up --build -d

# 4. 로그 확인
docker-compose logs -f

# 5. 특정 서비스 로그만 보기
docker-compose logs -f test-was
docker-compose logs -f otel-collector
```

## 📱 접속 정보

### Test WAS 애플리케이션
- **API 문서**: http://localhost:8080/swagger-ui.html

### OpenTelemetry Collector
- **gRPC**: localhost:4317
- **HTTP**: localhost:4318

## 🧪 테스트 시나리오

### 1. 기본 동작 확인
```bash
# 홈페이지 접속
curl http://localhost:8080

# 테스트 API 호출
curl http://localhost:8080/api/test
```


## 📊 모니터링

### 컨테이너 상태 확인
```bash
# 실행 중인 컨테이너 확인
docker-compose ps

# 리소스 사용량 확인
docker stats

```

### 로그 모니터링
```bash
# 모든 서비스 로그
docker-compose logs -f

# 특정 서비스만
docker-compose logs -f test-was
docker-compose logs -f otel-collector

# 최근 로그만 (마지막 100줄)
docker-compose logs --tail=100 -f
```

## 🔧 환경변수 설정

주요 OpenTelemetry 환경변수들이 docker-compose.yml에 설정되어 있습니다:

- `OTEL_SERVICE_NAME=test-was`
- `OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4317`
- `OTEL_TRACES_SAMPLER=always_on`
- `OTEL_INSTRUMENTATION_HTTP_*` - HTTP 요청/응답 헤더 캡처

## 🛑 정리

```bash
# 모든 컨테이너 정지 및 제거
docker-compose down

# 볼륨과 네트워크도 함께 제거
docker-compose down -v

# 이미지도 함께 제거
docker-compose down --rmi all

# 시스템 정리 (모든 중지된 컨테이너, 네트워크, 이미지 제거)
docker system prune -f
```

## 🔍 트러블슈팅

### 포트 충돌
```bash
# 포트 사용 확인
netstat -an | grep :8080
netstat -an | grep :4317

# 다른 포트로 실행
DOCKER_COMPOSE_HTTP_TIMEOUT=120 docker-compose up
```

### 컨테이너가 시작되지 않는 경우
```bash
# 상세 로그 확인
docker-compose logs test-was
docker-compose logs otel-collector

# 컨테이너 재시작
docker-compose restart test-was
```

### Windows에서 권한 문제
```bash
# Docker Desktop이 실행 중인지 확인
# WSL2 백엔드 사용 권장
```

## 📈 성능 튜닝

### 메모리 제한 조정
docker-compose.yml에서 JAVA_OPTS 조정:
```yaml
environment:
  - JAVA_OPTS=-Xmx2g -Xms1g  # 메모리 증가
```

### 샘플링 비율 조정
```yaml
environment:
  - OTEL_TRACES_SAMPLER=traceidratio
  - OTEL_TRACES_SAMPLER_ARG=0.1  # 10% 샘플링
```

---

이제 `./test-environment.sh` (또는 `test-environment.bat`)를 실행하면 완전한 OpenTelemetry 테스트 환경이 구성됩니다! 🎉
