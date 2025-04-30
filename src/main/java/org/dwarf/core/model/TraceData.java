package org.dwarf.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 트레이스 데이터를 표현하는 내부 모델 클래스
 * OpenTelemetry 프로토콜 메시지를 변환하여 저장하는 중간 데이터 구조
 */
public class TraceData {
    private List<DwSpan> spans = new ArrayList<>();
    private List<DwResource> resources = new ArrayList<>();

    /**
     * 기본 생성자
     */
    public TraceData() {
    }

    /**
     * 스팬 목록을 반환
     *
     * @return 스팬 목록
     */
    public List<DwSpan> getSpans() {
        return spans;
    }

    /**
     * 스팬 추가
     * 
     * @param span 추가할 스팬
     */
    public void addSpan(DwSpan span) {
        this.spans.add(span);
    }

    /**
     * 리소스 목록 반환
     * 
     * @return 리소스 목록
     */
    public List<DwResource> getResources() {
        return resources;
    }


    /**
     * 리소스 추가
     * 
     * @param resource 추가할 리소스
     */
    public void addResource(DwResource resource) {
        this.resources.add(resource);
    }

    /**
     * 스팬 개수 반환
     * 
     * @return 스팬 개수
     */
    public int getSpanCount() {
        return spans.size();
    }
}
