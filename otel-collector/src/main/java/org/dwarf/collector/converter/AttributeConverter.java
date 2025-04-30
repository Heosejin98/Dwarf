package org.dwarf.collector.converter;

import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.ArrayValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.common.v1.KeyValueList;

/**
 * OpenTelemetry 속성 값을 변환하는 유틸리티 클래스
 */
public class AttributeConverter {

    /**
     * KeyValue 속성에서 값 추출
     *
     * @param attr OTLP KeyValue 객체
     * @return 추출된 값 문자열
     */
    public static String getAttributeValue(KeyValue attr) {
        AnyValue value = attr.getValue();
        
        switch (value.getValueCase()) {
            case STRING_VALUE:
                return value.getStringValue();
            case BOOL_VALUE:
                return String.valueOf(value.getBoolValue());
            case INT_VALUE:
                return String.valueOf(value.getIntValue());
            case DOUBLE_VALUE:
                return String.valueOf(value.getDoubleValue());
            case ARRAY_VALUE:
                return convertArrayValueToString(value.getArrayValue());
            case KVLIST_VALUE:
                return convertKvListValueToString(value.getKvlistValue());
            case BYTES_VALUE:
                return HexUtils.bytesToHex(value.getBytesValue().toByteArray());
            default:
                return "";
        }
    }

    /**
     * 배열 값을 문자열로 변환
     *
     * @param arrayValue OTLP ArrayValue 객체
     * @return 문자열로 변환된 배열 값
     */
    private static String convertArrayValueToString(ArrayValue arrayValue) {
        StringBuilder result = new StringBuilder("[");
        
        for (int i = 0; i < arrayValue.getValuesCount(); i++) {
            if (i > 0) {
                result.append(",");
            }
            
            AnyValue value = arrayValue.getValues(i);
            
            switch (value.getValueCase()) {
                case STRING_VALUE:
                    result.append("\"").append(value.getStringValue()).append("\"");
                    break;
                case BOOL_VALUE:
                    result.append(value.getBoolValue());
                    break;
                case INT_VALUE:
                    result.append(value.getIntValue());
                    break;
                case DOUBLE_VALUE:
                    result.append(value.getDoubleValue());
                    break;
                case BYTES_VALUE:
                    result.append("\"0x").append(HexUtils.bytesToHex(value.getBytesValue().toByteArray())).append("\"");
                    break;
                default:
                    result.append("null");
            }
        }
        
        result.append("]");
        return result.toString();
    }

    /**
     * 키-값 리스트를 문자열로 변환
     *
     * @param kvListValue OTLP KeyValueList 객체
     * @return 문자열로 변환된 키-값 리스트
     */
    private static String convertKvListValueToString(KeyValueList kvListValue) {
        StringBuilder result = new StringBuilder("{");
        
        for (int i = 0; i < kvListValue.getValuesCount(); i++) {
            if (i > 0) {
                result.append(",");
            }
            
            KeyValue kv = kvListValue.getValues(i);
            result.append("\"").append(kv.getKey()).append("\":");
            
            switch (kv.getValue().getValueCase()) {
                case STRING_VALUE:
                    result.append("\"").append(kv.getValue().getStringValue()).append("\"");
                    break;
                case BOOL_VALUE:
                    result.append(kv.getValue().getBoolValue());
                    break;
                case INT_VALUE:
                    result.append(kv.getValue().getIntValue());
                    break;
                case DOUBLE_VALUE:
                    result.append(kv.getValue().getDoubleValue());
                    break;
                default:
                    result.append("null");
            }
        }
        
        result.append("}");
        return result.toString();
    }
}
