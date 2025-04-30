package org.dwarf.collector.converter;

import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import io.opentelemetry.proto.trace.v1.Span;
import io.opentelemetry.proto.trace.v1.Status;
import org.dwarf.core.model.DwResource;
import org.dwarf.core.model.DwSpan;
import org.dwarf.core.model.TraceData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OpenTelemetry 프로토콜 메시지를 내부 모델로 변환하는 유틸리티 클래스
 */
public class OtelTraceConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(OtelTraceConverter.class);

    /**
     * OTLP 요청을 내부 TraceData 모델로 변환
     *
     * @param request OTLP 요청 객체
     * @return 변환된 내부 TraceData 모델
     */
    public static TraceData convertToTraceData(ExportTraceServiceRequest request) {
        TraceData traceData = new TraceData();

        int resourceSpansCount = request.getResourceSpansCount();
        LOGGER.debug("Converting {} resource spans to internal model", resourceSpansCount);

        // ResourceSpans 순회
        for (int i = 0; i < resourceSpansCount; i++) {
            ResourceSpans resourceSpans = request.getResourceSpans(i);
            Resource otlpResource = resourceSpans.getResource();

            // 리소스 변환
            DwResource resource = convertResource(otlpResource);
            traceData.addResource(resource);

            // ScopeSpans 순회
            for (int j = 0; j < resourceSpans.getScopeSpansCount(); j++) {
                ScopeSpans scopeSpans = resourceSpans.getScopeSpans(j);
                InstrumentationScope scope = scopeSpans.getScope();

                // Spans 순회
                for (int k = 0; k < scopeSpans.getSpansCount(); k++) {
                    Span otlpSpan = scopeSpans.getSpans(k);

                    // Span 변환
                    DwSpan span = convertSpan(otlpSpan);

                    // 서비스 이름 설정
                    if (resource.getServiceName() != null) {
                        span.setServiceName(resource.getServiceName());
                    }

                    // 계측 라이브러리 정보 추가
                    if (scope != null) {
                        span.addAttribute("instrumentation.name", scope.getName());
                        if (!scope.getVersion().isEmpty()) {
                            span.addAttribute("instrumentation.version", scope.getVersion());
                        }
                    }

                    // 트레이스 데이터에 스팬 추가
                    traceData.addSpan(span);
                }
            }
        }

        LOGGER.debug("Converted {} spans from OTLP format", traceData.getSpanCount());
        return traceData;
    }

    /**
     * OTLP Resource를 내부 Resource 모델로 변환
     *
     * @param resource OTLP Resource 객체
     * @return 변환된 내부 Resource 모델
     */
    private static DwResource convertResource(Resource resource) {
        DwResource internalResource = new DwResource();

        // 리소스 속성 처리
        for (int i = 0; i < resource.getAttributesCount(); i++) {
            KeyValue attr = resource.getAttributes(i);
            String key = attr.getKey();
            String value = AttributeConverter.getAttributeValue(attr);

            // 리소스 속성을 내부 모델에 매핑
            if ("service.name".equals(key)) {
                internalResource.setServiceName(value);
            } else if ("service.version".equals(key)) {
                internalResource.setServiceVersion(value);
            } else if ("service.namespace".equals(key)) {
                internalResource.setServiceNamespace(value);
            } else if ("service.instance.id".equals(key)) {
                internalResource.setServiceInstanceId(value);
            } else if ("host.name".equals(key)) {
                internalResource.setHostName(value);
            } else if ("host.id".equals(key)) {
                internalResource.setHostId(value);
            } else if ("container.id".equals(key)) {
                internalResource.setContainerId(value);
            } else if ("container.name".equals(key)) {
                internalResource.setContainerName(value);
            } else {
                // 다른 모든 속성도 저장
                internalResource.addAttribute(key, value);
            }
        }

        return internalResource;
    }

    /**
     * OTLP Span을 내부 Span 모델로 변환
     *
     * @param span OTLP Span 객체
     * @return 변환된 내부 Span 모델
     */
    private static DwSpan convertSpan(Span span) {
        DwSpan internalSpan = new DwSpan();

        // 기본 스팬 정보 설정
        internalSpan.setTraceId(HexUtils.bytesToHex(span.getTraceId().toByteArray()));
        internalSpan.setSpanId(HexUtils.bytesToHex(span.getSpanId().toByteArray()));
        internalSpan.setParentSpanId(HexUtils.bytesToHex(span.getParentSpanId().toByteArray()));
        internalSpan.setName(span.getName());
        internalSpan.setStartTimeUnixNano(span.getStartTimeUnixNano());
        internalSpan.setEndTimeUnixNano(span.getEndTimeUnixNano());

        // 스팬 종류 설정
        internalSpan.setKind(SpanKindConverter.convertSpanKind(span.getKind()));

        // 스팬 상태 처리
        internalSpan.setHasError(span.getStatus().getCode() == Status.StatusCode.STATUS_CODE_ERROR);

        // 속성 처리
        for (int i = 0; i < span.getAttributesCount(); i++) {
            KeyValue attr = span.getAttributes(i);
            String key = attr.getKey();
            String value = AttributeConverter.getAttributeValue(attr);

            // 특정 속성 처리
            if (key.startsWith("error.")) {
                processErrorAttribute(internalSpan, key, value);
            } else if (key.startsWith("db.")) {
                processDatabaseAttribute(internalSpan, key, value);
            } else if (key.startsWith("http.")) {
                processHttpAttribute(internalSpan, key, value);
            } else {
                // 기타 속성
                internalSpan.addAttribute(key, value);
            }
        }

        // 이벤트 처리 (로그 등)
        for (int i = 0; i < span.getEventsCount(); i++) {
            Span.Event event = span.getEvents(i);
            if ("exception".equals(event.getName())) {
                processExceptionEvent(internalSpan, event);
            }
        }

        return internalSpan;
    }

    /**
     * 에러 관련 속성 처리
     *
     * @param span 내부 Span 모델
     * @param key 속성 키
     * @param value 속성 값
     */
    private static void processErrorAttribute(DwSpan span, String key, String value) {
        if ("error.type".equals(key)) {
            span.setErrorType(value);
            span.setHasError(true);
        } else if ("error.message".equals(key)) {
            span.setErrorMessage(value);
            span.setHasError(true);
        } else if ("error.stack".equals(key)) {
            span.setErrorStacktrace(value);
            span.setHasError(true);
        }
    }

    /**
     * 데이터베이스 관련 속성 처리
     *
     * @param span 내부 Span 모델
     * @param key 속성 키
     * @param value 속성 값
     */
    private static void processDatabaseAttribute(DwSpan span, String key, String value) {
        if ("db.system".equals(key)) {
            span.setDbSystem(value);
        } else if ("db.statement".equals(key)) {
            span.setDbStatement(value);
        }
    }

    /**
     * HTTP 관련 속성 처리
     *
     * @param span 내부 Span 모델
     * @param key 속성 키
     * @param value 속성 값
     */
    private static void processHttpAttribute(DwSpan span, String key, String value) {
        if ("http.status_code".equals(key)) {
            try {
                int statusCode = Integer.parseInt(value);
                span.setHttpStatusCode(statusCode);

                // 4xx, 5xx는 에러로 처리
                if (statusCode >= 400) {
                    span.setHasError(true);
                }
            } catch (NumberFormatException e) {
                LOGGER.warn("Invalid HTTP status code: {}", value);
            }
        }
    }

    /**
     * 예외 이벤트 처리
     *
     * @param span 내부 Span 모델
     * @param event 예외 이벤트
     */
    private static void processExceptionEvent(DwSpan span, Span.Event event) {
        for (int i = 0; i < event.getAttributesCount(); i++) {
            KeyValue attr = event.getAttributes(i);
            String key = attr.getKey();
            String value = AttributeConverter.getAttributeValue(attr);

            if ("exception.type".equals(key)) {
                span.setErrorType(value);
                span.setHasError(true);
            } else if ("exception.message".equals(key)) {
                span.setErrorMessage(value);
                span.setHasError(true);
            } else if ("exception.stacktrace".equals(key)) {
                span.setErrorStacktrace(value);
                span.setHasError(true);
            }
        }
    }
}
