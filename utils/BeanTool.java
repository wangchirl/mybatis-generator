package com.yidao.court.prelitigation.utils;

import com.yidao.court.prelitigation.utils.json.JacksonTool;
import com.yidao.court.prelitigation.utils.json.JsonTool;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author duwei
 * @date 2019/8/22
 */
public class BeanTool {
    /**
     * json格式转object
     *
     * @param <T>
     * @param jsonString
     * @param objectClass
     * @return
     */
    public static <T> T jsonToObject(String jsonString, Class<T> objectClass) {
        JsonTool jsonTool = new JacksonTool();
        return jsonTool.jsonToObject(jsonString,objectClass);
    }

    /**
     * object转json字符串
     *
     * @param object
     * @return
     */
    public static String objectToJson(Object object) {
        JsonTool jsonTool = new JacksonTool();
        return jsonTool.objectToJson(object);
    }

    //1、map转换为object

//    public static Object mapToObject(Map<String, Object> map, Class<?> beanClass)
//            throws Exception {
//        if (map == null) {
//            return null;
//
//        }
//        Object obj = beanClass.newInstance();
//        org.apache.commons.beanutils.BeanUtils.populate(obj, map);
//        return obj;
//    }

    //2、object转换为map
//    public static Map<?, ?> objectToMap(Object obj) {
//        if (obj == null) {
//            return null;
//        }
//        Map<?, ?> map = new org.apache.commons.beanutils.BeanMap(obj);  //会存在 "class",且无法移除
//        return map;
//    }

    /**
     * 对象转Map,过滤空值
     */
    public static <T> Map<String, T> objectToMap(Object obj) throws Exception {
        if (obj == null) {
            return null;
        }
        Map<String, T> map = new HashMap<>();
        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            if (field.get(obj) != null) {
                map.put(field.getName(), (T) field.get(obj));
            }
        }
        return map;
    }

}
