package com.sonin.utils;

import java.awt.*;
import java.io.File;

/**
 * <pre>
 * 获取字体工具类
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/25 17:12
 */
public class FontUtils {

    /**
     * <pre>
     * 获取指定地址字体
     * </pre>
     *
     * @param fontName
     * @param fontSize
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    public static Font getNativeFont(String fontPath, String fontName, float fontSize) {
        Font font;
        try {
            File file = new File(fontPath + File.separator + fontName);
            font = Font.createFont(Font.TRUETYPE_FONT, file);
            font = font.deriveFont(fontSize);
        } catch (Exception e) {
            font = new Font("宋体", Font.PLAIN, (int) fontSize);
        }
        return font;
    }

}
