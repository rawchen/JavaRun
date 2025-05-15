package com.rawchen.javarun.util;

/**
 * @author RawChen
 * @date 2021/9/26 14:21
 */
public class StringUtil {

    /**
     * 非空判断
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return (str == null || "".equals(str));
    }

    /**
     * 传入代码是否危险
     *
     * @param javaSource
     * @return
     */
    public static boolean isDangerousWithLinux(String javaSource) {
        if (isEmpty(javaSource)) {
            return false;
        }

        // 移除字符串内容和注释，只保留代码结构
        String processedCode = removeStringLiteralsAndComments(javaSource);

        // 危险操作检测列表（只检测实际的代码调用）
        String[] dangerousPatterns = {
                "java.nio", "java.io",
                "newFile(", "Files.", "Class.forName(", "System.getenv(",
                ".exec(", "ProcessBuilder(", "System.exit(", "RuntimePermission(",
                "SecurityManager(", "ReflectPermission(", "FilePermission("
        };
//        String[] dangerousPatterns = {
//                "newFile(", "Files.", "Class.forName(", "System.getenv(",
//                "Runtime.getRuntime().exec(", "ProcessBuilder(",
//                "System.exit(", "Thread.stop(", "RuntimePermission(",
//                "SecurityManager(", "ReflectPermission(", "FilePermission(",
//                "SocketPermission(", "NetPermission(", "URLClassLoader(",
//                "defineClass(", "MethodHandles.", "NativeLibrary.",
//                "ProcessImpl."
//        };

        for (String pattern : dangerousPatterns) {
            if (processedCode.contains(pattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 移除字符串内容和注释
     * @param code 原始代码
     * @return 处理后的代码
     */
    private static String removeStringLiteralsAndComments(String code) {
        // 移除单行和多行注释
        code = code.replaceAll("//.*|(\"(?:\\\\[^\"]|\\\\\"|.)*?\")|(?s)/\\*.*?\\*/", "$1");
        // 移除字符串内容（保留空字符串标记）
        code = code.replaceAll("\"(?:\\\\\"|[^\"])*\"", "\"\"");
        code = code.replaceAll("\\s+", "");
        return code;
    }

    /**
     * 参数格式化为字符数组
     *
     * @param executeArgsStr
     * @return
     */
    public static String[] getInputArgs(String executeArgsStr) {
        if (StringUtil.isEmpty(executeArgsStr)) {
            return null;
        } else {
            return executeArgsStr.trim().split("\\s+");
        }
    }

    /**
     * 合并两数组
     *
     * @param a
     * @param b
     * @return
     */
    public static String[] concat(String[] a, String[] b) {
        String[] c= new String[a.length+b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    /**
     * 是Win系统，不然就是linux
     *
     * @return
     */
    public static boolean isWinOs() {
        String os = System.getProperty("os.name");
        return os.toLowerCase().startsWith("win");
    }

    /**
     * 生成6位随机字符(包含英文大小写和数字)
     * @return 6位随机字符串
     */
    public static String generateRandomCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int index = (int)(Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 转义字符串中的JSON特殊字符
     * @param input 原始字符串
     * @return 转义后的JSON安全字符串
     */
    public static String escapeJsonString(String input) {
        if (isEmpty(input)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '/': sb.append("\\/"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c <= '\u001F' || c >= '\u007F' && c <= '\u009F') {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

}
