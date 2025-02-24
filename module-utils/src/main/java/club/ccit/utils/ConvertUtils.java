package club.ccit.utils;

/**
 * 进制转换工具
 * version: 1.0
 */
public class ConvertUtils {

    /**
     * 将十进制整数转换为二进制字符串
     *
     * @param decimal 十进制整数
     * @return 二进制字符串
     */
    public static String decimalToBinary(int decimal) {
        return Integer.toBinaryString(decimal);
    }

    /**
     * 将十进制整数转换为八进制字符串
     *
     * @param decimal 十进制整数
     * @return 八进制字符串
     */
    public static String decimalToOctal(int decimal) {
        return Integer.toOctalString(decimal);
    }

    /**
     * 将十进制整数转换为十六进制字符串
     *
     * @param decimal 十进制整数
     * @return 十六进制字符串
     */
    public static String decimalToHexadecimal(int decimal) {
        return Integer.toHexString(decimal);
    }

    /**
     * 将二进制字符串转换为十进制整数
     *
     * @param binary 二进制字符串
     * @return 十进制整数
     */
    public static int binaryToDecimal(String binary) {
        return Integer.parseInt(binary, 2);
    }

    /**
     * 将八进制字符串转换为十进制整数
     *
     * @param octal 八进制字符串
     * @return 十进制整数
     */
    public static int octalToDecimal(String octal) {
        return Integer.parseInt(octal, 8);
    }

    /**
     * 将十六进制字符串转换为十进制整数
     *
     * @param hexadecimal 十六进制字符串
     * @return 十进制整数
     */
    public static int hexadecimalToDecimal(String hexadecimal) {
        return Integer.parseInt(hexadecimal, 16);
    }

    /**
     * 将 byte 数组转换为十六进制字符串
     *
     * @param bytes byte 数组
     * @return 十六进制字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    /**
     * 将十六进制字符串转换为 byte 数组
     *
     * @param hex 十六进制字符串
     * @return byte 数组
     */
    public static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}
