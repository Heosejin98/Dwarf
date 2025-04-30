package org.dwarf.collector.converter;

import io.opentelemetry.proto.trace.v1.Span.SpanKind;

/**
 * OpenTelemetry 스팬 종류를 변환하는 유틸리티 클래스
 */
public class SpanKindConverter {

    /**
     * OTLP 스팬 종류를 내부 문자열 표현으로 변환
     *
     * @param kind OTLP SpanKind 열거형
     * @return 문자열로 된 스팬 종류
     */
    public static String convertSpanKind(SpanKind kind) {
        switch (kind) {
            case SPAN_KIND_CLIENT:
                return "CLIENT";
            case SPAN_KIND_SERVER:
                return "SERVER";
            case SPAN_KIND_PRODUCER:
                return "PRODUCER";
            case SPAN_KIND_CONSUMER:
                return "CONSUMER";
            case SPAN_KIND_INTERNAL:
                return "INTERNAL";
            case UNRECOGNIZED:
            default:
                return "INTERNAL";
        }
    }

    /**
     * 문자열로 된 스팬 종류를 OTLP 스팬 종류로 변환
     *
     * @param kindString 스팬 종류 문자열
     * @return OTLP SpanKind 열거형
     */
    public static SpanKind convertStringToSpanKind(String kindString) {
        if (kindString == null) {
            return SpanKind.SPAN_KIND_INTERNAL;
        }
        
        switch (kindString.toUpperCase()) {
            case "CLIENT":
                return SpanKind.SPAN_KIND_CLIENT;
            case "SERVER":
                return SpanKind.SPAN_KIND_SERVER;
            case "PRODUCER":
                return SpanKind.SPAN_KIND_PRODUCER;
            case "CONSUMER":
                return SpanKind.SPAN_KIND_CONSUMER;
            case "INTERNAL":
            default:
                return SpanKind.SPAN_KIND_INTERNAL;
        }
    }
}
