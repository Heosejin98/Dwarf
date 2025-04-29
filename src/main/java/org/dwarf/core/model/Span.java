package org.dwarf.core.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 트레이스의 단위 작업을 나타내는 스팬 모델 클래스
 */
public class Span {
    private String traceId;              // 트레이스 ID
    private String spanId;               // 스팬 ID
    private String parentSpanId;         // 부모 스팬 ID
    private String name;                 // 스팬 이름
    private String serviceName;          // 서비스 이름
    private long startTimeUnixNano;      // 시작 시간 (나노초, Unix epoch)
    private long endTimeUnixNano;        // 종료 시간 (나노초, Unix epoch)
    private Map<String, String> attributes = new HashMap<>(); // 속성
    private boolean hasError;            // 에러 여부
    private String errorType;            // 에러 타입
    private String errorMessage;         // 에러 메시지
    private String errorStacktrace;      // 스택 트레이스
    private String kind;                 // 스팬 종류 (SERVER, CLIENT, PRODUCER, CONSUMER, INTERNAL)
    private Integer httpStatusCode;      // HTTP 상태 코드 (있는 경우)
    private String dbStatement;          // DB 쿼리문 (있는 경우)
    private String dbSystem;             // DB 시스템 (있는 경우)

    /**
     * 기본 생성자
     */
    public Span() {
    }

    /**
     * 에러 포함 여부 확인
     * 
     * @return 에러 포함 여부
     */
    public boolean hasError() {
        return hasError || (httpStatusCode != null && httpStatusCode >= 400) || errorType != null;
    }

    /**
     * DB 스팬 여부 확인
     * 
     * @return DB 스팬 여부
     */
    public boolean isDatabaseSpan() {
        return dbStatement != null || "db".equals(kind) || 
               (attributes.containsKey("db.type") || attributes.containsKey("db.system"));
    }

    /**
     * 스팬 지속 시간 계산 (밀리초)
     * 
     * @return 지속 시간 (밀리초)
     */
    public long getDurationMs() {
        return (endTimeUnixNano - startTimeUnixNano) / 1_000_000;
    }

    /**
     * 예외 속성 포함 여부 확인
     * 
     * @return 예외 속성 포함 여부
     */
    public boolean hasExceptionAttribute() {
        return errorType != null || attributes.containsKey("exception.type") || 
               attributes.containsKey("error.type");
    }

    // Getters and Setters
    
    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public String getParentSpanId() {
        return parentSpanId;
    }

    public void setParentSpanId(String parentSpanId) {
        this.parentSpanId = parentSpanId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public long getStartTimeUnixNano() {
        return startTimeUnixNano;
    }

    public void setStartTimeUnixNano(long startTimeUnixNano) {
        this.startTimeUnixNano = startTimeUnixNano;
    }

    public long getEndTimeUnixNano() {
        return endTimeUnixNano;
    }

    public void setEndTimeUnixNano(long endTimeUnixNano) {
        this.endTimeUnixNano = endTimeUnixNano;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(String key, String value) {
        this.attributes.put(key, value);
    }

    public String getAttribute(String key) {
        return this.attributes.get(key);
    }

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorStacktrace() {
        return errorStacktrace;
    }

    public void setErrorStacktrace(String errorStacktrace) {
        this.errorStacktrace = errorStacktrace;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getDbStatement() {
        return dbStatement;
    }

    public void setDbStatement(String dbStatement) {
        this.dbStatement = dbStatement;
    }

    public String getDbSystem() {
        return dbSystem;
    }

    public void setDbSystem(String dbSystem) {
        this.dbSystem = dbSystem;
    }
}
