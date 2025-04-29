package org.dwarf.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 트레이스 데이터를 표현하는 내부 모델 클래스
 * OpenTelemetry 프로토콜 메시지를 변환하여 저장하는 중간 데이터 구조
 */
public class TraceData {
    private List<Span> spans = new ArrayList<>();
    private List<Resource> resources = new ArrayList<>();

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
    public List<Span> getSpans() {
        return spans;
    }

    /**
     * 스팬 목록 설정
     * 
     * @param spans 설정할 스팬 목록
     */
    public void setSpans(List<Span> spans) {
        this.spans = spans;
    }

    /**
     * 스팬 추가
     * 
     * @param span 추가할 스팬
     */
    public void addSpan(Span span) {
        this.spans.add(span);
    }

    /**
     * 리소스 목록 반환
     * 
     * @return 리소스 목록
     */
    public List<Resource> getResources() {
        return resources;
    }

    /**
     * 리소스 목록 설정
     * 
     * @param resources 설정할 리소스 목록
     */
    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    /**
     * 리소스 추가
     * 
     * @param resource 추가할 리소스
     */
    public void addResource(Resource resource) {
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

    /**
     * 에러를 포함한 스팬 목록 필터링
     * 
     * @return 에러를 포함한 스팬 목록
     */
    public List<Span> getErrorSpans() {
        List<Span> errorSpans = new ArrayList<>();
        for (Span span : spans) {
            if (span.hasError()) {
                errorSpans.add(span);
            }
        }
        return errorSpans;
    }

    /**
     * 지정된 서비스의 스팬 목록 필터링
     * 
     * @param serviceName 서비스 이름
     * @return 해당 서비스의 스팬 목록
     */
    public List<Span> getSpansByService(String serviceName) {
        List<Span> serviceSpans = new ArrayList<>();
        for (Span span : spans) {
            if (serviceName.equals(span.getServiceName())) {
                serviceSpans.add(span);
            }
        }
        return serviceSpans;
    }

    /**
     * 서비스 이름 목록 반환
     * 
     * @return 트레이스에 포함된 고유 서비스 이름 목록
     */
    public List<String> getServiceNames() {
        List<String> serviceNames = new ArrayList<>();
        for (Span span : spans) {
            String serviceName = span.getServiceName();
            if (serviceName != null && !serviceNames.contains(serviceName)) {
                serviceNames.add(serviceName);
            }
        }
        return serviceNames;
    }
}
