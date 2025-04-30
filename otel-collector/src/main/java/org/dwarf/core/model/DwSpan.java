package org.dwarf.core.model;

import io.opentelemetry.proto.trace.v1.Span;

import java.util.Map;

/**
 * 트레이스의 단위 작업을 나타내는 스팬 모델 클래스
 */
public class DwSpan {
    private final String traceId;              // 트레이스 ID
    private final String spanId;               // 스팬 ID
    private final String parentSpanId;         // 부모 스팬 ID
    private final String name;                 // 스팬 이름
    private final String serviceName;
    private final long startTimeUnixNano;      // 시작 시간 (나노초, Unix epoch)
    private final long endTimeUnixNano;        // 종료 시간 (나노초, Unix epoch)
    private final Map<String, String> attributes; // 속성
    private final Span.SpanKind spanKind;      // 스팬 종류 (SPAN_KIND_SERVER, SPAN_KIND_CLIENT, SPAN_KIND_PRODUCER, SPAN_KIND_CONSUMER, SPAN_KIND_INTERNAL)

    public DwSpan(String traceId, String spanId, String parentSpanId, String name,
                  String serviceName, long startTimeUnixNano, long endTimeUnixNano,
                  Map<String, String> attributes, Span.SpanKind spanKind
    ) {
        this.traceId = traceId;
        this.spanId = spanId;
        this.parentSpanId = parentSpanId;
        this.name = name;
        this.serviceName = serviceName;
        this.startTimeUnixNano = startTimeUnixNano;
        this.endTimeUnixNano = endTimeUnixNano;
        this.attributes = attributes;
        this.spanKind = spanKind;
    }

    @Override
    public String toString() {
        return "DwSpan{" +
                "traceId='" + traceId + '\'' +
                ", spanId='" + spanId + '\'' +
                ", parentSpanId='" + parentSpanId + '\'' +
                ", name='" + name + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", startTimeUnixNano=" + startTimeUnixNano +
                ", endTimeUnixNano=" + endTimeUnixNano +
                ", attributes=" + attributes +
                ", spanKind=" + spanKind +
                '}';
    }
}
