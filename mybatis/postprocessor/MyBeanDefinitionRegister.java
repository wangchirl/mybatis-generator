package com.mashibing.mybatis.postprocessor;

import com.mashibing.mybatis.annotation.MyScan;
import com.mashibing.mybatis.mapper.AccountMapper;
import com.mashibing.mybatis.mapper.UserMapper;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author shadow
 * @create 2020-07-20
 * @description 批量 注册 beanDefinition
 */
public class MyBeanDefinitionRegister implements ImportBeanDefinitionRegistrar {

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
		// 扫描
		Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(MyScan.class.getName());

		String o = (String) attributes.get("value");

		System.out.println(o); // com.mashibing.mybatis 拿到扫描的路径

		// 模拟生成 代理对象
		List<Class> list = new ArrayList<>();
		list.add(UserMapper.class);
		list.add(AccountMapper.class);
		// 模拟 扫描器 扫描出所有的 mapper 接口 ,注册 beanDefinition
		for (Class mapper : list) {
			BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
			AbstractBeanDefinition definition = builder.getBeanDefinition();
			definition.getConstructorArgumentValues().addGenericArgumentValue(mapper);
			definition.setBeanClass(MyCommonMapperFactoryBean.class);
			registry.registerBeanDefinition(mapper.getSimpleName(),definition);
		}
	}
}
