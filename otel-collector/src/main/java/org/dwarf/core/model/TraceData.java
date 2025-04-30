package org.dwarf.core.model;

import java.util.List;

/**
 * 트레이스 데이터를 표현하는 내부 모델 클래스
 * OpenTelemetry 프로토콜 메시지를 변환하여 저장하는 중간 데이터 구조
 */
public class TraceData {

    private final List<DwSpan> spans;
    private final List<DwResource> resources;

    public TraceData(List<DwSpan> spans, List<DwResource> resources) {
        this.spans = spans;
        this.resources = resources;
    }

    public int getSpanCount() {
        return spans.size();
    }

    @Override
    public String toString() {
        return "TraceData{" +
                "spans=" + spans +
                ", resources=" + resources +
                '}';
    }
}
