package com.yidao.court.prelitigation.utils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * <p>
 * java对象和xml转换的工具类
 * </p>
 * @author jingtian
 * @since 2019-09-23
 */
public class Ben2XmlUtil {
    /**
     * 将对象直接转换成String类型的 XML输出
     *
     * @param obj
     * @return
     */
    public static String convertToXml(Object obj) {
        // 创建输出流
        StringWriter sw = new StringWriter();
        try {
            // 利用jdk中自带的转换类实现
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            Marshaller marshaller = context.createMarshaller();
            // 格式化xml输出的格式
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            // 将对象转换成输出流形式的xml
            marshaller.marshal(obj, sw);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return sw.toString();
    }

    /**
     * 将xml文件转成 java对象
     *
     * @param str xml 字符串
     * @param t 对象类型
     * @return
     */
    public static <T> T xmlToConvert(String str, Class<T> t) {
        // 创建输出流
        try {
            JAXBContext ctx = JAXBContext.newInstance(t);
            Unmarshaller unmarshaller = ctx.createUnmarshaller();
            T obj = (T)unmarshaller.unmarshal(new StringReader(str));
            return obj;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }
}
