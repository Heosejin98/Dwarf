package org.dwarf.receiver;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import io.opentelemetry.proto.trace.v1.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * OpenTelemetry 트레이스 데이터 수신을 위한 gRPC 서버
 */
public class OtelTraceReceiver {
    private static final Logger logger = LoggerFactory.getLogger(OtelTraceReceiver.class);
    
    private final int port;
    private final Server server;
    
    public OtelTraceReceiver(int port) {
        this.port = port;
        this.server = ServerBuilder.forPort(port)
                .addService(new TraceServiceImpl())
                .build();
    }
    
    /**
     * 서버 시작
     */
    public void start() throws IOException {
        server.start();
        logger.info("OTel 트레이스 수신기가 포트 {}에서 시작되었습니다", port);
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("*** 서버 종료 신호 수신 ***");
            try {
                OtelTraceReceiver.this.stop();
            } catch (InterruptedException e) {
                logger.error("서버 종료 중 오류 발생", e);
            }
        }));
    }
    
    /**
     * 서버 중지
     */
    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
            logger.info("서버가 종료되었습니다");
        }
    }
    
    /**
     * 서버가 종료될 때까지 대기
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
    
    /**
     * OpenTelemetry TraceService 구현
     */
    static class TraceServiceImpl extends TraceServiceGrpc.TraceServiceImplBase {
        @Override
        public void export(ExportTraceServiceRequest request, 
                          StreamObserver<ExportTraceServiceResponse> responseObserver) {
            logger.info("트레이스 데이터 수신");
            
            // 수신된 트레이스 데이터 처리
            processTraceData(request);
            
            // 응답 전송
            ExportTraceServiceResponse response = ExportTraceServiceResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
        
        /**
         * 수신된 트레이스 데이터를 처리하는 메서드
         */
        private void processTraceData(ExportTraceServiceRequest request) {
            int totalSpans = 0;
            
            // ResourceSpans 처리
            for (ResourceSpans resourceSpans : request.getResourceSpansList()) {
                // 리소스 정보 로깅
                String serviceName = getServiceName(resourceSpans);
                logger.info("리소스 처리: {}", serviceName);
                
                // ScopeSpans 처리
                for (ScopeSpans scopeSpans : resourceSpans.getScopeSpansList()) {
                    String scopeName = scopeSpans.getScope().getName();
                    logger.info("스코프: {}", scopeName);
                    
                    // 각 스팬 처리
                    List<Span> spans = scopeSpans.getSpansList();
                    totalSpans += spans.size();
                    
                    for (Span span : spans) {
                        logger.debug("스팬 처리: {} (트레이스 ID: {}, 스팬 ID: {})", 
                                span.getName(),
                                bytesToHex(span.getTraceId().toByteArray()),
                                bytesToHex(span.getSpanId().toByteArray()));
                    }
                }
            }
            
            logger.info("트레이스 처리 완료: 총 {} 개의 스팬", totalSpans);
        }
        
        /**
         * ResourceSpans에서 서비스 이름을 추출
         */
        private String getServiceName(ResourceSpans resourceSpans) {
            return resourceSpans.getResource().getAttributesList().stream()
                    .filter(keyValue -> keyValue.getKey().equals("service.name"))
                    .map(keyValue -> keyValue.getValue().getStringValue())
                    .findFirst()
                    .orElse("unknown-service");
        }
        
        /**
         * 바이트 배열을 16진수 문자열로 변환
         */
        private static String bytesToHex(byte[] bytes) {
            StringBuilder hexString = new StringBuilder(2 * bytes.length);
            for (byte b : bytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        }
    }
}