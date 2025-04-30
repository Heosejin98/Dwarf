package org.dwarf.collector.processor;

import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;

/**
 * 트레이스 데이터를 처리하는 프로세서 인터페이스
 * 수신된 트레이스 데이터를 분석하고 필터링하여 필요한 정보만 추출합니다.
 */
public interface TraceProcessor {
    
    /**
     * 트레이스 데이터 처리
     * 수신된 트레이스 데이터를 처리하고 필요한 정보를 추출하여 저장합니다.
     * 
     * @param traceData 처리할 트레이스 데이터
     */
    void processTraces(ExportTraceServiceRequest traceData);
}
