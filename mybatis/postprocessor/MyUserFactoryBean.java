package com.mashibing.mybatis.postprocessor;

import com.mashibing.mybatis.entity.User;
import com.mashibing.mybatis.mapper.UserMapper;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;

/**
 * @author shadow
 * @create 2020-07-20
 * @description
 */

@Component
public class MyUserFactoryBean implements FactoryBean {

	@Override
	public Object getObject() throws Exception {

		return new User();
	}

	@Override
	public Class<?> getObjectType() {
		return User.class;
	}
}
