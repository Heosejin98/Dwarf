package org.dwarf;

import org.dwarf.collector.receiver.TraceReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OpenTelemetry 트레이스 수신기 애플리케이션
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
    // 기본 포트 설정
    private static final int DEFAULT_PORT = 4317;

    public static void main(String[] args) {
        logger.info("OpenTelemetry 트레이스 수신기 시작 중...");
        
        // 명령행 인수에서 포트 가져오기
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                logger.warn("잘못된 포트 번호: {}. 기본 포트 {}를 사용합니다.", args[0], DEFAULT_PORT);
            }
        }
        
        try {
            // 트레이스 수신기 초기화 및 시작
            TraceReceiver receiver = new TraceReceiver(port);
            receiver.start();
            
            // 애플리케이션이 계속 실행되도록 대기
            logger.info("OpenTelemetry 트레이스 수신기가 포트 {}에서 실행 중...", port);
            logger.info("종료하려면 Ctrl+C를 누르세요");
            
            // 종료 신호가 올 때까지 대기
            receiver.blockUntilShutdown();
            
        } catch (Exception e) {
            logger.error("서버 실행 중 오류 발생", e);
            System.exit(1);
        }
    }
}