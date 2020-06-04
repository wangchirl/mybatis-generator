package com.yidao.court.prelitigation.utils;

import com.yidao.court.core.enums.ErrorCodeEnum;
import com.yidao.court.core.exception.ServiceException;
import com.yidao.court.prelitigation.enums.PreLitiErrorCodeEnum;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.*;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

/**
 * 高频方法集合
 *
 * @author zizhan
 * @date 8/18/2019
 */
public class ToolUtil extends ValidateUtil {

    /**
     * 默认密码盐长度
     */
    public static final int SALT_LENGTH = 6;

    /**
     * md5加密(加盐)
     */
    public static String md5Hex(String password, String salt) {
        return md5Hex(password + salt);
    }

    /**
     * md5加密(不加盐)
     */
    public static String md5Hex(String str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bs = md5.digest(str.getBytes());
            StringBuffer md5StrBuff = new StringBuffer();
            for (int i = 0; i < bs.length; i++) {
                if (Integer.toHexString(0xFF & bs[i]).length() == 1) {
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & bs[i]));
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & bs[i]));
                }
            }
            return md5StrBuff.toString();
        } catch (Exception e) {
            throw new ServiceException(ErrorCodeEnum.ENCRYPT_ERROR);
        }
    }

    /**
     * 过滤掉掉字符串中的空白
     */
    public static String removeWhiteSpace(String value) {
        if (isEmpty(value)) {
            return "";
        } else {
            return value.replaceAll("\\s*", "");
        }
    }

    /**
     * 获取某个时间间隔以前的时间 时间格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getCreateTimeBefore(int seconds) {
        long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();
        Date date = new Date(currentTimeInMillis - seconds * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    /**
     * 获取异常的具体信息
     */
    public static String getExceptionMsg(Throwable e) {
        StringWriter sw = new StringWriter();
        try {
            e.printStackTrace(new PrintWriter(sw));
        } finally {
            try {
                sw.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return sw.getBuffer().toString().replaceAll("\\$", "T");
    }


    /**
     * 获取ip地址
     */
    public static String getIP() {
        try {
            StringBuilder IFCONFIG = new StringBuilder();
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
                        IFCONFIG.append(inetAddress.getHostAddress().toString() + "\n");
                    }

                }
            }
            return IFCONFIG.toString();

        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 判断是否是windows操作系统
     */
    public static Boolean isWinOs() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取临时目录
     */
    public static String getTempPath() {
        return System.getProperty("java.io.tmpdir");
    }

    /**
     * 把一个数转化为int
     */
    public static Integer toInt(Object val) {
        if (val instanceof Double) {
            BigDecimal bigDecimal = new BigDecimal((Double) val);
            return bigDecimal.intValue();
        } else {
            return Integer.valueOf(val.toString());
        }

    }

    /**
     * 是否为数字
     */
    public static boolean isNum(Object obj) {
        try {
            Integer.parseInt(obj.toString());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 获取项目路径
     */
    public static String getWebRootPath(String filePath) {
        try {
            String path = ToolUtil.class.getClassLoader().getResource("").toURI().getPath();
            path = path.replace("/WEB-INF/classes/", "");
            path = path.replace("/target/classes/", "");
            path = path.replace("file:/", "");
            if (ToolUtil.isEmpty(filePath)) {
                return path;
            } else {
                return path + "/" + filePath;
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取文件后缀名 不包含点
     */
    public static String getFileSuffix(String fileWholeName) {
        if (ToolUtil.isEmpty(fileWholeName)) {
            return "none";
        }
        int lastIndexOf = fileWholeName.lastIndexOf(".");
        return fileWholeName.substring(lastIndexOf + 1);
    }

    /**
     * 获取客户端IP地址
     *
     * @param request
     * @return
     */
    public static String getClientIp(HttpServletRequest request) {
        //X-Forwarded-For，不区分大小写
        String possibleIpStr = request.getHeader("X-Forwarded-For");
        String remoteIp = request.getRemoteAddr();
        String clientIp;
        if (!StringUtils.isEmpty(possibleIpStr) && !"unknown".equalsIgnoreCase(possibleIpStr)) {
            //可能经过好几个转发流程，第一个是用户的真实ip，后面的是转发服务器的ip
            clientIp = possibleIpStr.split(",")[0].trim();
        } else {
            //如果转发头ip为空，说明是直接访问的，没有经过转发
            clientIp = remoteIp;
        }
        return clientIp;
    }

    public static String ToCH(int intInput) {
        String si = String.valueOf(intInput);
        String sd = "";
        if (si.length() == 1) // 個
        {
            sd += GetCH(intInput);
            return sd;
        } else if (si.length() == 2)// 十
        {
            if (si.substring(0, 1).equals("1"))
                sd += "十";
            else
                sd += (GetCH(intInput / 10) + "十");
            sd += ToCH(intInput % 10);
        } else if (si.length() == 3)// 百
        {
            sd += (GetCH(intInput / 100) + "百");
            if (String.valueOf(intInput % 100).length() < 2)
                sd += "零";
            sd += ToCH(intInput % 100);
        } else if (si.length() == 4)// 千
        {
            sd += (GetCH(intInput / 1000) + "千");
            if (String.valueOf(intInput % 1000).length() < 3)
                sd += "零";
            sd += ToCH(intInput % 1000);
        } else if (si.length() == 5)// 萬
        {
            sd += (GetCH(intInput / 10000) + "萬");
            if (String.valueOf(intInput % 10000).length() < 4)
                sd += "零";
            sd += ToCH(intInput % 10000);
        }

        return sd;
    }

    private static String GetCH(int input) {
        String sd = "";
        switch (input) {
            case 1:
                sd = "一";
                break;
            case 2:
                sd = "二";
                break;
            case 3:
                sd = "三";
                break;
            case 4:
                sd = "四";
                break;
            case 5:
                sd = "五";
                break;
            case 6:
                sd = "六";
                break;
            case 7:
                sd = "七";
                break;
            case 8:
                sd = "八";
                break;
            case 9:
                sd = "九";
                break;
            default:
                break;
        }
        return sd;
    }

    public  static void setProperty(Object obj, String propertyName, Object value) {
        Class<? extends Object> clazz = obj.getClass();

        try {
        //防止字段为private时，无法修改字段值的情况
        Field field = null;//暴力反射获取字段

            field = clazz.getDeclaredField(propertyName);

            field.setAccessible(true);//去除权限

            field.set(obj, value);
        } catch (Exception e) {
            throw new ServiceException(PreLitiErrorCodeEnum.FIELD_DECLARED_ERROR, propertyName);
        }
    }

}