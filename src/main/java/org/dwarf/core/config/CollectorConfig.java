package org.dwarf.core.config;

/**
 * 데이터 수집기 설정 클래스
 * 수집기 관련 설정 정보를 포함합니다.
 */
public class CollectorConfig {
    
    private int grpcPort = 4317;             // gRPC 포트 (기본값 4317)
    private int httpPort = 4318;             // HTTP 포트 (기본값 4318)
    private boolean enableCompression = true; // 압축 사용 여부
    private int batchSize = 100;             // 배치 처리 크기
    private int processingIntervalMs = 1000;  // 처리 간격 (밀리초)
    private int maxConcurrentRequests = 10;   // 최대 동시 요청 수
    
    /**
     * 기본 생성자
     */
    public CollectorConfig() {}
    
    /**
     * 포트 지정 생성자
     * 
     * @param grpcPort gRPC 포트
     * @param httpPort HTTP 포트
     */
    public CollectorConfig(int grpcPort, int httpPort) {
        this.grpcPort = grpcPort;
        this.httpPort = httpPort;
    }

    /**
     * gRPC 포트 반환
     * 
     * @return gRPC 포트
     */
    public int getGrpcPort() {
        return grpcPort;
    }

    /**
     * gRPC 포트 설정
     * 
     * @param grpcPort 설정할 gRPC 포트
     */
    public void setGrpcPort(int grpcPort) {
        this.grpcPort = grpcPort;
    }

    /**
     * HTTP 포트 반환
     * 
     * @return HTTP 포트
     */
    public int getHttpPort() {
        return httpPort;
    }

    /**
     * HTTP 포트 설정
     * 
     * @param httpPort 설정할 HTTP 포트
     */
    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    /**
     * 압축 사용 여부 반환
     * 
     * @return 압축 사용 여부
     */
    public boolean isEnableCompression() {
        return enableCompression;
    }

    /**
     * 압축 사용 여부 설정
     * 
     * @param enableCompression 설정할 압축 사용 여부
     */
    public void setEnableCompression(boolean enableCompression) {
        this.enableCompression = enableCompression;
    }

    /**
     * 배치 처리 크기 반환
     * 
     * @return 배치 처리 크기
     */
    public int getBatchSize() {
        return batchSize;
    }

    /**
     * 배치 처리 크기 설정
     * 
     * @param batchSize 설정할 배치 처리 크기
     */
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    /**
     * 처리 간격 반환 (밀리초)
     * 
     * @return 처리 간격 (밀리초)
     */
    public int getProcessingIntervalMs() {
        return processingIntervalMs;
    }

    /**
     * 처리 간격 설정 (밀리초)
     * 
     * @param processingIntervalMs 설정할 처리 간격 (밀리초)
     */
    public void setProcessingIntervalMs(int processingIntervalMs) {
        this.processingIntervalMs = processingIntervalMs;
    }

    /**
     * 최대 동시 요청 수 반환
     * 
     * @return 최대 동시 요청 수
     */
    public int getMaxConcurrentRequests() {
        return maxConcurrentRequests;
    }

    /**
     * 최대 동시 요청 수 설정
     * 
     * @param maxConcurrentRequests 설정할 최대 동시 요청 수
     */
    public void setMaxConcurrentRequests(int maxConcurrentRequests) {
        this.maxConcurrentRequests = maxConcurrentRequests;
    }
}
