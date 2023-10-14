package space.atnibam.common.core.utils.text;

import static space.atnibam.common.core.constant.CommonConstants.*;

/**
 * @ClassName: StrFormatter
 * @Description: 字符串格式化工具类，用于处理字符串中的占位符等操作
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-13 14:19
 **/
public class StrFormatter {

    /**
     * 对输入的字符串模板进行格式化，将其中的占位符 {} 替换为提供的参数列表中对应的值。
     * 如果在字符串模板中需要输出 {} ，可以使用 \\ 对 { 进行转义。
     * 如果需要输出 \ ，则需要使用双转义符 \\\\。
     * 例如：
     * - 常规使用：format("this is {} for {}", "a", "b") -> this is a for b
     * - 转义{}： format("this is \\{} for {}", "a", "b") -> this is \{} for a
     * - 转义\： format("this is \\\\{} for {}", "a", "b") -> this is \a for b
     *
     * @param strPattern 字符串模板
     * @param argArray   参数列表
     * @return 格式化后的字符串结果
     */
    public static String format(final String strPattern, final Object... argArray) {
        // 如果模板为空或者参数为空，直接返回模板内容
        if (StringUtils.isEmpty(strPattern) || StringUtils.isEmpty(argArray)) {
            return strPattern;
        }

        final int strPatternLength = strPattern.length();

        // 初始化 StringBuilder，预定义长度以提高性能
        StringBuilder sbuf = new StringBuilder(strPatternLength + 50);

        // 已处理位置
        int handledPosition = 0;
        // 占位符所在位置
        int delimIndex;

        for (int argIndex = 0; argIndex < argArray.length; argIndex++) {
            delimIndex = strPattern.indexOf(EMPTY_JSON, handledPosition);

            if (delimIndex == -1) {
                // 未找到占位符
                if (handledPosition == 0) {
                    // 模板中没有占位符，直接返回原模板
                    return strPattern;
                } else {
                    // 模板剩余部分没有占位符，追加剩余部分并返回结果
                    sbuf.append(strPattern, handledPosition, strPatternLength);
                    return sbuf.toString();
                }
            } else {
                if (delimIndex > 0 && strPattern.charAt(delimIndex - 1) == C_BACKSLASH) {
                    // 占位符前有转义符
                    if (delimIndex > 1 && strPattern.charAt(delimIndex - 2) == C_BACKSLASH) {
                        // 转义符前还有转义符，占位符有效
                        sbuf.append(strPattern, handledPosition, delimIndex - 1);
                        sbuf.append(Convert.utf8Str(argArray[argIndex]));
                        handledPosition = delimIndex + 2;
                    } else {
                        // 占位符被转义
                        argIndex--;
                        sbuf.append(strPattern, handledPosition, delimIndex - 1);
                        sbuf.append(C_DELIM_START);
                        handledPosition = delimIndex + 1;
                    }
                } else {
                    // 正常占位符
                    sbuf.append(strPattern, handledPosition, delimIndex);
                    sbuf.append(Convert.utf8Str(argArray[argIndex]));
                    handledPosition = delimIndex + 2;
                }
            }
        }

        // 添加最后一个占位符后所有的字符
        sbuf.append(strPattern, handledPosition, strPattern.length());

        return sbuf.toString();
    }
}