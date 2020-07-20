package com.mashibing.mybatis.postprocessor;

import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author shadow
 * @create 2020-07-20
 * @description
 */
//@Component
public class MyCommonMapperFactoryBean implements FactoryBean {

	private Class mapper;

	public MyCommonMapperFactoryBean(Class mapper) {
		this.mapper = mapper;
	}

	@Override
	public Object getObject() throws Exception {
		// mapper 代理对象
		Object o = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{this.mapper}, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				if(Object.class.equals(method.getDeclaringClass())){
					return method.invoke(this,args);
				}
				return null;
			}
		});
		return o;
	}

	@Override
	public Class<?> getObjectType() {
		return mapper;
	}
}
