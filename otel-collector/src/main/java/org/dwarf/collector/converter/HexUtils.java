package org.dwarf.collector.converter;

/**
 * 바이트 배열과 16진수 문자열 간 변환 유틸리티 클래스
 */
public class HexUtils {

    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

    /**
     * 바이트 배열을 16진수 문자열로 변환
     *
     * @param bytes 변환할 바이트 배열
     * @return 16진수 문자열
     */
    public static String bytesToHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * 16진수 문자열을 바이트 배열로 변환
     *
     * @param hexString 변환할 16진수 문자열
     * @return 바이트 배열
     */
    public static byte[] hexToBytes(String hexString) {
        if (hexString == null || hexString.isEmpty()) {
            return new byte[0];
        }
        
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }
}
