package com.yidao.court.prelitigation.utils;

import com.yidao.court.core.context.UserContext;

import java.lang.reflect.Field;
import java.util.Date;

/** 临时工具类
 *  填充公共的字段 TODO 抽到框架中处理
 * @author duwei
 * @date 2019/11/6
 */
public class BizUtils {
     public static <T> void fillSave(T entity){
        Class clazz = entity.getClass();
        try {
            String tenantId= UserContext.getCurrentUser().getTenantId();
            String loginCnName= UserContext.getCurrentUser().getLoginCnName();
            Date now= new Date();
            Field tenantIdField = clazz.getDeclaredField("tenantId");
            tenantIdField.setAccessible(true);
            tenantIdField.set(entity, tenantId);
            Field gmtCreateField = clazz.getDeclaredField("gmtCreate");
            gmtCreateField.setAccessible(true);
            gmtCreateField.set(entity, now);

            Field gmtModifiedField = clazz.getDeclaredField("gmtModified");
            gmtModifiedField.setAccessible(true);
            gmtModifiedField.set(entity, now);

            Field createByField = clazz.getDeclaredField("createBy");
            createByField.setAccessible(true);
            createByField.set(entity, loginCnName);

            Field modifiedByField = clazz.getDeclaredField("modifiedBy");
            modifiedByField.setAccessible(true);
            modifiedByField.set(entity, loginCnName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static <T> void fillUpdate(T entity){
        Class clazz = entity.getClass();
        try {
            String loginCnName= UserContext.getCurrentUser().getLoginCnName();
            Date now= new Date();

            Field gmtModifiedField = clazz.getDeclaredField("gmtModified");
            gmtModifiedField.setAccessible(true);
            gmtModifiedField.set(entity, now);

            Field modifiedByField = clazz.getDeclaredField("modifiedBy");
            modifiedByField.setAccessible(true);
            modifiedByField.set(entity, loginCnName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
