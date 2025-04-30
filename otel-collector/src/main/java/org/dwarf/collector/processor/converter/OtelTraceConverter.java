package org.dwarf.collector.processor.converter;

import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import org.dwarf.core.model.DwResource;
import org.dwarf.core.model.DwSpan;
import org.dwarf.core.model.TraceData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        LOGGER.debug("Converting {} resource spans to internal model", request.getResourceSpansCount());

        // 결과 데이터를 담을 컬렉션
        List<DwResource> resources = new ArrayList<>();
        List<DwSpan> spans = new ArrayList<>();

        // 스트림 API를 사용하여 모든 데이터 처리
        request.getResourceSpansList().forEach(resourceSpans -> {
            // 리소스 변환 및 추가
            DwResource resource = convertResource(resourceSpans.getResource());
            resources.add(resource);

            // 모든 ScopeSpans 처리
            resourceSpans.getScopeSpansList().forEach(scopeSpans -> {
                InstrumentationScope scope = scopeSpans.getScope();

                // 모든 Span 처리
                scopeSpans.getSpansList().forEach(otlpSpan -> {
                    DwSpan span = toDwSpan(otlpSpan, resource.getServiceName(), scope);
                    spans.add(span);
                });
            });
        });

        // 수집된 데이터로 TraceData 객체 생성
        TraceData traceData = new TraceData(spans, resources);

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
        Map<ResourceAttributeKey, String> resourceAttributes = new HashMap<>();
        Map<String, String> additionalAttributes = new HashMap<>();

        // 리소스 속성 수집
        for (int i = 0; i < resource.getAttributesCount(); i++) {
            KeyValue attr = resource.getAttributes(i);
            String key = attr.getKey();
            String value = AttributeConverter.getAttributeValue(attr);

            // 리소스 속성을 내부 모델에 매핑
            ResourceAttributeKey attributeKey = ResourceAttributeKey.fromString(key);
            if (attributeKey != null) {
                resourceAttributes.put(attributeKey, value);
            } else {
                // 매핑되지 않은 속성은 추가 속성으로 수집
                additionalAttributes.put(key, value);
            }
        }

        return new DwResource( //resourceAttributes에 key가 있으면 value가 들어간다.
                resourceAttributes.getOrDefault(ResourceAttributeKey.SERVICE_NAME, null),
                resourceAttributes.getOrDefault(ResourceAttributeKey.SERVICE_VERSION, null),
                resourceAttributes.getOrDefault(ResourceAttributeKey.SERVICE_NAMESPACE, null),
                resourceAttributes.getOrDefault(ResourceAttributeKey.SERVICE_INSTANCE_ID, null),
                resourceAttributes.getOrDefault(ResourceAttributeKey.HOST_NAME, null),
                resourceAttributes.getOrDefault(ResourceAttributeKey.HOST_ID, null),
                resourceAttributes.getOrDefault(ResourceAttributeKey.CONTAINER_ID, null),
                resourceAttributes.getOrDefault(ResourceAttributeKey.CONTAINER_NAME, null),
                additionalAttributes
        );
    }

    /**
     * OTLP Span을 내부 Span 모델로 변환
     *
     * @param otlpSpan OTLP Span 객체
     * @param serviceName 서비스 이름
     * @param scope 계측 라이브러리 정보
     * @return 변환된 내부 DwSpan 모델
     */
    private static DwSpan toDwSpan(Span otlpSpan, String serviceName, InstrumentationScope scope) {
        // 기본 스팬 정보 변환
        String traceId = HexUtils.bytesToHex(otlpSpan.getTraceId().toByteArray());
        String spanId = HexUtils.bytesToHex(otlpSpan.getSpanId().toByteArray());
        String parentSpanId = HexUtils.bytesToHex(otlpSpan.getParentSpanId().toByteArray());

        // 모든 속성 수집
        Map<String, String> attributes = collectAllAttributes(otlpSpan, scope);

        return new DwSpan(
                traceId,
                spanId,
                parentSpanId,
                otlpSpan.getName(),
                serviceName,
                otlpSpan.getStartTimeUnixNano(),
                otlpSpan.getEndTimeUnixNano(),
                attributes,
                otlpSpan.getKind()
        );
    }

    /**
     * OTLP Span의 모든 속성과 계측 정보를 수집
     */
    private static Map<String, String> collectAllAttributes(Span span, InstrumentationScope scope) {
        Map<String, String> attributes = new HashMap<>();

        // 계측 라이브러리 정보 추가
        if (scope != null) {
            attributes.put("instrumentation.name", scope.getName());

            if (!scope.getVersion().isEmpty()) {
                attributes.put("instrumentation.version", scope.getVersion());
            }
        }

        // 속성 추가
        span.getAttributesList().forEach(attr ->
                attributes.put(attr.getKey(), AttributeConverter.getAttributeValue(attr))
        );

        return attributes;
    }

    /**
     * 리소스 속성 키를 정의하는 Enum
     */
    private enum ResourceAttributeKey {
        SERVICE_NAME("service.name"),
        SERVICE_VERSION("service.version"),
        SERVICE_NAMESPACE("service.namespace"),
        SERVICE_INSTANCE_ID("service.instance.id"),
        HOST_NAME("host.name"),
        HOST_ID("host.id"),
        CONTAINER_ID("container.id"),
        CONTAINER_NAME("container.name");

        private final String key;

        ResourceAttributeKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        /**
         * 문자열 키를 enum 상수로 변환
         * @param key 찾을 속성 키
         * @return 매칭되는 enum 상수 또는 null
         */
        public static ResourceAttributeKey fromString(String key) {
            for (ResourceAttributeKey attrKey : ResourceAttributeKey.values()) {
                if (attrKey.getKey().equals(key)) {
                    return attrKey;
                }
            }
            return null;
        }
    }
}
