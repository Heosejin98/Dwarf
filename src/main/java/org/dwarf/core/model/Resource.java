package org.dwarf.core.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 트레이스 데이터 소스를 식별하는 리소스 모델 클래스
 * 서비스 인스턴스, 호스트, 컨테이너 등의 정보 포함
 */
public class Resource {
    private String serviceName;          // 서비스 이름
    private String serviceVersion;       // 서비스 버전
    private String serviceNamespace;     // 서비스 네임스페이스
    private String serviceInstanceId;    // 서비스 인스턴스 ID
    private String hostName;             // 호스트 이름
    private String hostId;               // 호스트 ID
    private String containerId;          // 컨테이너 ID (있는 경우)
    private String containerName;        // 컨테이너 이름 (있는 경우)
    private Map<String, String> attributes = new HashMap<>(); // 추가 속성

    /**
     * 기본 생성자
     */
    public Resource() {
    }

    /**
     * 특정 키의 속성 값 반환
     * 
     * @param key 속성 키
     * @return 속성 값, 없으면 null
     */
    public String getAttribute(String key) {
        return attributes.get(key);
    }

    /**
     * 속성 추가
     * 
     * @param key 속성 키
     * @param value 속성 값
     */
    public void addAttribute(String key, String value) {
        attributes.put(key, value);
    }

    /**
     * 환경 정보 반환 (prod, dev, test 등)
     * 
     * @return 환경 정보, 없으면 null
     */
    public String getEnvironment() {
        return getAttribute("environment") != null ? 
               getAttribute("environment") : 
               getAttribute("deployment.environment");
    }

    /**
     * 클라우드 제공자 반환 (aws, azure, gcp 등)
     * 
     * @return 클라우드 제공자, 없으면 null
     */
    public String getCloudProvider() {
        return getAttribute("cloud.provider");
    }

    // Getters and Setters
    
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public String getServiceNamespace() {
        return serviceNamespace;
    }

    public void setServiceNamespace(String serviceNamespace) {
        this.serviceNamespace = serviceNamespace;
    }

    public String getServiceInstanceId() {
        return serviceInstanceId;
    }

    public void setServiceInstanceId(String serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
}
