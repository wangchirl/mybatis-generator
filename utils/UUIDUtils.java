package com.yidao.court.prelitigation.utils;

import java.util.UUID;

/**
 * <p>
 *     uuid工具类
 * </p>
 *
 * @author guojia
 * @since 2019-09-03
 */
public class UUIDUtils {
    /**
     * 获取生成一个字符UUID，并且去除横杆“-”(包含大小写)
     *
     * @return
     */
    public static String getStringValue() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
