package com.yidao.court.prelitigation.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.InputStream;
import java.util.Map;

/**
 * 类名：XMLUtils
 * 创建者：huangyonghua
 * 日期：2017-04-29 14:08
 * 版权：厦门维途信息技术有限公司 Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class XMLUtils{

    private static XmlMapper xmlMapper = new XmlMapper();

    static {
        xmlMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        xmlMapper.setDefaultUseWrapper(false);
    }

    public static XmlMapper getXmlMapper(){
        return xmlMapper;
    }

    @SuppressWarnings("unchecked")
	public static <T> T convert(String xml, Class<?> clazz){
        try{
            return (T) xmlMapper.readValue(xml, clazz);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T convert(InputStream xml, Class<?> clazz){
        try{
            return (T) xmlMapper.readValue(xml, clazz);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static String convert(Object object){
        try {
            return xmlMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String convert(String rootName, Object object){
        try {
            ObjectWriter writer = xmlMapper.writer().withRootName(rootName);
            return writer.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) throws Exception{
        String str = convert("Request", MapUtils.newHashMap("a", "b", "c", "d"));
        System.out.println(str);
        Map<String, Object> map = convert(str, Map.class);
        System.out.println(map);

        map = convert("<s><c>d</c><a>b</a></s>", Map.class);
        System.out.println(map);
    }
}
