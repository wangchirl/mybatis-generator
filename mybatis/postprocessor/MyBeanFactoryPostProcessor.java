package com.mashibing.mybatis.postprocessor;

import com.mashibing.mybatis.mapper.AccountMapper;
import com.mashibing.mybatis.mapper.UserMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shadow
 * @create 2020-07-20
 * @description
 */
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// 修改 beanDefinition信息
//		GenericBeanDefinition bd = (GenericBeanDefinition) beanFactory.getBeanDefinition("user");
//		bd.setBeanClass(UserMapper.class);

		// 添加新的 beanDefinition
//		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
//		AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
		// 这里是接口 ，肯定不行，需要存入代理类
//		beanDefinition.setBeanClass(UserMapper.class);
//		((BeanDefinitionRegistry)beanDefinition).registerBeanDefinition(UserMapper.class.getName(),beanDefinition);


	}
}
