package com.yidao.court.prelitigation.utils;

import com.yidao.court.core.exception.ServiceException;
import com.yidao.court.prelitigation.enums.PreLitiErrorCodeEnum;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 *     时间工具类
 * </p>
 *
 * @author guojia
 * @since 2019-09-03
 */
public class DateUtils {

    public static String YYYYMMDD = "yyyyMMdd";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    /**
     * 格式化日期
     * @param format
     * @param date
     * @return
     */
    public static final String parseDateToStr(final String format, final Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 格式当前时间-字符串格式
     * @param format
     * @return
     */
    public static final String dateTimeNow(final String format) {
        return parseDateToStr(format, new Date());
    }

    /**
     * 获取当前日期字符串
     * @return
     */
    public static String getDate() {
        return dateTimeNow(YYYY_MM_DD);
    }

    /**
     * 获取当前时间字符串
     * @return
     */
    public static final String getTime() {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 字符串转为日期
     * @param ts
     * @param format
     * @return
     */
    public static Date parseStrToDate(String ts, String format) {
        try {
            return new SimpleDateFormat(format).parse(ts);
        } catch (ParseException e) {
            throw new ServiceException(PreLitiErrorCodeEnum.DATE_TRANSFER_ERROR);
        }
    }
}
