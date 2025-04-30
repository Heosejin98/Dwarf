package org.dwarf.core.model;

import java.util.Map;

/**
 * 트레이스 데이터 소스를 식별하는 리소스 모델 클래스
 * 서비스 인스턴스, 호스트, 컨테이너 등의 정보 포함
 */
public class DwResource {
    private final String serviceName;             // 서비스 이름
    private final String serviceVersion;          // 서비스 버전
    private final String serviceNamespace;        // 서비스 네임스페이스
    private final String serviceInstanceId;       // 서비스 인스턴스 ID
    private final String hostName;                // 호스트 이름
    private final String hostId;                  // 호스트 ID
    private final String containerId;             // 컨테이너 ID (있는 경우)
    private final String containerName;           // 컨테이너 이름 (있는 경우)
    private final Map<String, String> attributes; // 추가 속성


    public DwResource(String serviceName, String serviceVersion, String serviceNamespace,
                      String serviceInstanceId, String hostName, String hostId,
                      String containerId, String containerName, Map<String, String> attributes) {
        this.serviceName = serviceName;
        this.serviceVersion = serviceVersion;
        this.serviceNamespace = serviceNamespace;
        this.serviceInstanceId = serviceInstanceId;
        this.hostName = hostName;
        this.hostId = hostId;
        this.containerId = containerId;
        this.containerName = containerName;
        this.attributes = attributes;
    }

    public String getServiceName() {
        return serviceName;
    }

    @Override
    public String toString() {
        return "DwResource{" +
                "serviceName='" + serviceName + '\'' +
                ", serviceVersion='" + serviceVersion + '\'' +
                ", serviceNamespace='" + serviceNamespace + '\'' +
                ", serviceInstanceId='" + serviceInstanceId + '\'' +
                ", hostName='" + hostName + '\'' +
                ", hostId='" + hostId + '\'' +
                ", containerId='" + containerId + '\'' +
                ", containerName='" + containerName + '\'' +
                ", attributes=" + attributes +
                '}';
    }
}
