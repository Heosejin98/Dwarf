package org.dwarf;

import org.dwarf.collector.processor.TraceProcessor;
import org.dwarf.collector.receiver.TraceReceiver;
import org.dwarf.core.config.CollectorConfig;
import org.dwarf.core.model.TraceData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OpenTelemetry 트레이스 수신기 애플리케이션
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Starting Dwarf Solution");

        try {
            // 1. 설정 로드
            logger.info("Loading configuration...");
            CollectorConfig collectorConfig = loadCollectorConfig();

            // 2. 리시버 초기화 및 시작
            logger.info("Starting trace receiver on port {}...", collectorConfig.getGrpcPort());
            TraceProcessor traceProcessor = new TraceProcessor() {
                @Override
                public void processTraces(TraceData traceData) {

                }
            };
            TraceReceiver traceReceiver = new TraceReceiver(collectorConfig, traceProcessor);
            traceReceiver.start();

            logger.info("LightweightDwarf started successfully!");
            logger.info("Listening for traces on gRPC port: {}", collectorConfig.getGrpcPort());
            logger.info("Press Ctrl+C to shutdown");

            // 3. 종료 신호 대기
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutting down application...");
                try {
                    traceReceiver.stop();
                    logger.info("Application shutdown completed");
                } catch (Exception e) {
                    logger.error("Error during shutdown", e);
                }
            }));

            // 메인 스레드 대기
            Thread.currentThread().join();

        } catch (Exception e) {
            logger.error("Error starting application", e);
            System.exit(1);
        }
    }

    /**
     * 수집기 설정 로드
     *
     * @return 수집기 설정
     */
    private static CollectorConfig loadCollectorConfig() {
        // 실제 구현에서는 YAML 등의 파일에서 설정을 로드할 수 있음
        CollectorConfig config = new CollectorConfig();
        config.setGrpcPort(4317);
        config.setHttpPort(4318);
        config.setBatchSize(100);
        config.setProcessingIntervalMs(1000);
        config.setMaxConcurrentRequests(10);
        return config;
    }
}