package org.dwarf.collector.receiver;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;
import org.dwarf.collector.converter.OtelTraceConverter;
import org.dwarf.collector.processor.TraceProcessor;
import org.dwarf.core.config.CollectorConfig;
import org.dwarf.core.model.TraceData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * TraceReceiver는 OpenTelemetry 프로토콜을 통해 트레이스 데이터를 수신하는 컴포넌트입니다.
 * MVP 버전에서는 단순히 데이터를 수신하고 성공 응답만 반환합니다.
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
     * OpenTelemetry Protocol을 통해 전송된 트레이스 데이터를 수신하고 성공 응답만 반환합니다.
     */
    private class OtlpTraceServiceImpl extends TraceServiceGrpc.TraceServiceImplBase {
        @Override
        public void export(ExportTraceServiceRequest request,
                          StreamObserver<ExportTraceServiceResponse> responseObserver) {
            try {
                // 1. 내부 모델로 변환
                TraceData traceData = OtelTraceConverter.convertToTraceData(request);
                logger.debug("Converted to internal model with {} spans", traceData.getSpanCount());
                logger.debug("Converted to Trace model data {} ", traceData);

                // 2. 트레이스 프로세서로 전달
                traceProcessor.processTraces(traceData);

                // 3. 성공 응답
                responseObserver.onNext(ExportTraceServiceResponse.getDefaultInstance());
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
    }
}
