package space.atnibam.common.core.utils.text;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static space.atnibam.common.core.constant.CommonConstants.*;

/**
 * @ClassName: CharsetKit
 * @Description: 字符集工具类，提供字符集常量和字符集转换方法
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-13 14:18
 **/
public class CharsetKit {

    /**
     * ISO-8859-1字符集对象
     */
    public static final Charset CHARSET_ISO_8859_1 = Charset.forName(ISO_8859_1);

    /**
     * UTF-8字符集对象
     */
    public static final Charset CHARSET_UTF_8 = Charset.forName(UTF8);

    /**
     * GBK字符集对象
     */
    public static final Charset CHARSET_GBK = Charset.forName(GBK);

    /**
     * 将字符串形式的字符集转换为Charset对象
     *
     * @param charset 字符集的字符串表示形式，为空则返回默认字符集
     * @return Charset对象
     */
    public static Charset charset(String charset) {
        return StringUtils.isEmpty(charset) ? Charset.defaultCharset() : Charset.forName(charset);
    }

    /**
     * 转换字符串的字符集编码
     *
     * @param source      需要转换的字符串
     * @param srcCharset  源字符集的字符串表示形式，默认ISO-8859-1
     * @param destCharset 目标字符集的字符串表示形式，默认UTF-8
     * @return 转换后的字符串
     */
    public static String convert(String source, String srcCharset, String destCharset) {
        return convert(source, Charset.forName(srcCharset), Charset.forName(destCharset));
    }

    /**
     * 转换字符串的字符集编码
     *
     * @param source      需要转换的字符串
     * @param srcCharset  源字符集对象，默认ISO-8859-1
     * @param destCharset 目标字符集对象，默认UTF-8
     * @return 转换后的字符串
     */
    public static String convert(String source, Charset srcCharset, Charset destCharset) {
        // 判断源字符集是否为空，如果为空设置为ISO_8859_1
        if (null == srcCharset) {
            srcCharset = StandardCharsets.ISO_8859_1;
        }

        // 判断目标字符集是否为空，如果为空设置为UTF_8
        if (null == destCharset) {
            destCharset = StandardCharsets.UTF_8;
        }

        // 如果源字符串为空或者源字符集与目标字符集相同，直接返回源字符串
        if (StringUtils.isEmpty(source) || srcCharset.equals(destCharset)) {
            return source;
        }

        // 否则，将源字符串从源字符集转换到目标字符集，并返回
        return new String(source.getBytes(srcCharset), destCharset);
    }

    /**
     * 获取系统默认字符集编码
     *
     * @return 系统默认字符集编码的字符串表示形式
     */
    public static String systemCharset() {
        return Charset.defaultCharset().name();
    }
}