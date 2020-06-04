package com.yidao.court.prelitigation.utils.json;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author duwei
 * @date 2019/8/26
 */
public interface JsonTool {
    Logger logger = LoggerFactory.getLogger(JsonTool.class);

    /**
     * json字符串 转对象
     * @param jsonStr json字符串
     * @param <T>
     * @return
     */
    <T> T jsonToObject(String jsonStr, Class<T> clazz);

    /**
     *  对象转json字符串
     * @param obj
     * @return
     */
    String objectToJson(Object obj);
}
