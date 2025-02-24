package club.ccit.utils;

/**
 * 一个字符串处理类
 * version: 1.0
 */
public class StringUtils {
    /**
     * 检查给定字符串是空还是空，如果是则返回空字符串。
     *
     * @param str 要检查的输入字符串
     * @return 一个空字符串，如果输入为null或空，否则返回原始字符串
     */
    public static String checkEmpty(String str) {
        if (str == null) {
            return "";
        }
        if (str.isEmpty()) {
            return "";
        }
        return str;
    }

    /**
     * 将提供的字符串连接成单个字符串，跳过任何null或空字符串。
     *
     * @param strings 要连接的字符串。
     * @return 包含所提供字符串的连接的单个字符串，不包括任何null或空字符串。
     **/
    public static String jointString(String... strings) {
        StringBuilder sb = new StringBuilder();
        for (String str : strings) {
            if (str != null && !str.isEmpty()) {
                sb.append(str);
            }
        }
        return sb.toString();
    }

    /**
     * 将传入的对象转换为字符串
     * 如果对象为null，则返回空字符串
     * 如果对象是字符串类型，则直接返回
     * 如果对象是整数、浮点数或双精度数类型，则调用其toString()方法返回字符串表示
     * 否则，返回对象的toString()方法的结果
     *
     * @param object 要转换的对象
     * @return 转换后的字符串
     */
    public static String convertString(Object object) {
        // 如果对象为null，则返回空字符串
        if (object == null) {
            return "";
        }
        // 如果对象是字符串类型，则直接返回
        if (object instanceof String) {
            return (String) object;
        }
        // 如果对象是整数类型，则调用其toString()方法返回字符串表示
        else if (object instanceof Integer) {
            return String.valueOf(object);
        }
        // 如果对象是双精度数类型，则调用其toString()方法返回字符串表示
        else if (object instanceof Double) {
            return String.valueOf(object);
        }
        // 如果对象是浮点数类型，则调用其toString()方法返回字符串表示
        else if (object instanceof Float) {
            return String.valueOf(object);
        }
        // 否则，返回对象的toString()方法的结果
        return object.toString();
    }

    /**
     * 将传入的对象转换为double类型
     * 如果对象为null，则返回0
     * 如果对象是字符串类型，则尝试解析为double
     * 如果对象是整数、浮点数或双精度数类型，则直接转换
     * 否则，尝试调用其toString()方法并解析为double
     * 如果转换失败，抛出NumberFormatException异常
     *
     * @param object 要转换的对象
     * @return 转换后的double值
     * @throws NumberFormatException 如果对象无法转换为 double
     */
    public static double convertDouble(Object object) {
        if (object == null) {
            return 0;
        }
        try {
            if (object instanceof Float) {
                return Double.parseDouble(object.toString());
            } else if (object instanceof Double) {
                return (Double) object;
            } else if (object instanceof Integer) {
                return ((Integer) object).doubleValue();
            } else if (object instanceof String) {
                return Double.parseDouble(object.toString());
            }
        } catch (NumberFormatException e) {
            return 0;
        }
        return 0;
    }

    /**
     * 将传入的对象转换为int类型
     * 如果对象为null，则返回0
     * 如果对象是字符串类型，则尝试解析为int
     * 如果对象是整数、浮点数或双精度数类型，则直接转换
     * 否则，尝试调用其toString()方法并解析为int
     * 如果转换失败，抛出NumberFormatException异常
     *
     * @param object 要转换的对象
     * @return 转换后的int值
     * @throws NumberFormatException 如果对象无法转换为int
     */
    public static int convertInt(Object object) {
        if (object == null) {
            return 0;
        }
        try {
            if (object instanceof Float) {
                return ((Float) object).intValue();
            } else if (object instanceof Double) {
                return ((Double) object).intValue();
            } else if (object instanceof Integer) {
                return (Integer) object;
            } else if (object instanceof String) {
                return Integer.parseInt(object.toString());
            }
        } catch (NumberFormatException e) {
            return 0;
        }
        return 0;
    }
}
