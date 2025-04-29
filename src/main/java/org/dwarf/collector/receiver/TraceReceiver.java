package org.dwarf.collector.receiver;

import org.dwarf.collector.processor.TraceProcessor;
import org.dwarf.core.config.CollectorConfig;
import org.dwarf.core.model.TraceData;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * TraceReceiver는 OpenTelemetry 프로토콜을 통해 트레이스 데이터를 수신하는 컴포넌트입니다.
 * 데이터를 수신한 후 프로세서로 전달하는 최소한의 역할만 수행합니다.
 */
public class TraceReceiver {
    private static final Logger logger = LoggerFactory.getLogger(TraceReceiver.class);

    private final CollectorConfig config;
    private final TraceProcessor traceProcessor;
    private Server grpcServer;

    /**
     * TraceReceiver 생성자
     *
     * @param config         수집기 설정
     * @param traceProcessor 트레이스 처리 프로세서
     */
    public TraceReceiver(CollectorConfig config, TraceProcessor traceProcessor) {
        this.config = config;
        this.traceProcessor = traceProcessor;
    }

    /**
     * 트레이스 리시버 시작
     *
     * @throws IOException 서버 시작 중 오류 발생 시
     */
    public void start() throws IOException {
        // gRPC 서버 구성 및 시작
        grpcServer = ServerBuilder.forPort(config.getGrpcPort())
                .addService(new OtlpTraceServiceImpl())
                .build()
                .start();

        logger.info("TraceReceiver started on port {}", config.getGrpcPort());

        // 종료 훅 등록
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down TraceReceiver");
            try {
                TraceReceiver.this.stop();
            } catch (InterruptedException e) {
                logger.error("Error during shutdown", e);
            }
        }));
    }

    /**
     * 트레이스 리시버 종료
     *
     * @throws InterruptedException 종료 중 인터럽트 발생 시
     */
    public void stop() throws InterruptedException {
        if (grpcServer != null) {
            grpcServer.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    /**
     * OTLP 트레이스 서비스 구현
     * OpenTelemetry Protocol을 통해 전송된 트레이스 데이터를 수신합니다.
     */
    private class OtlpTraceServiceImpl extends io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc.TraceServiceImplBase {
        @Override
        public void export(io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest request,
                          StreamObserver<io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse> responseObserver) {
            try {
                // 1. 요청에서 트레이스 데이터 추출
                logger.debug("Received trace data with {} resource spans",
                        request.getResourceSpansCount());

                // 2. 내부 모델로 변환
                TraceData traceData = convertToInternalModel(request);

                // 3. 트레이스 프로세서로 전달
                traceProcessor.processTraces(traceData);

                // 4. 성공 응답
                responseObserver.onNext(io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse.getDefaultInstance());
                responseObserver.onCompleted();

            } catch (Exception e) {
                // 오류 발생 시 로깅 및 오류 응답
                logger.error("Error processing trace data", e);
                responseObserver.onError(
                        io.grpc.Status.INTERNAL
                                .withDescription("Internal error while processing traces: " + e.getMessage())
                                .asRuntimeException()
                );
            }
        }

        /**
         * OTLP 프로토콜 메시지를 내부 모델로 변환
         *
         * @param request OTLP 요청 객체
         * @return 내부 모델 TraceData 객체
         */
        private TraceData convertToInternalModel(io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest request) {
            // 여기서는 프로토콜 버퍼 메시지를 내부 모델(TraceData)로 변환하는 로직 구현
            // 실제 구현은 별도의 변환 유틸리티 클래스로 분리하는 것이 좋습니다.

            TraceData traceData = new TraceData();

            // 필수 변환 로직 구현 (간략화)
            // ... 변환 로직 ...

            return traceData;
        }
    }
}
