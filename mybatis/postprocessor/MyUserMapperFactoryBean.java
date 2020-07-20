package com.mashibing.mybatis.postprocessor;

import com.mashibing.mybatis.mapper.UserMapper;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;

/**
 * @author shadow
 * @create 2020-07-20
 * @description
 */
//@Component
public class MyUserMapperFactoryBean implements FactoryBean {
	@Override
	public Object getObject() throws Exception {
		UserMapper userMapper = (UserMapper) Proxy.newProxyInstance(this.getClass().getClassLoader(),new Class[]{UserMapper.class},(proxy, method, args)->{
			if(Object.class.equals(method.getDeclaringClass())){
				return method.invoke(this,args);
			}
			return null;
		});

		return userMapper;
	}

	@Override
	public Class<?> getObjectType() {
		return UserMapper.class;
	}
}
