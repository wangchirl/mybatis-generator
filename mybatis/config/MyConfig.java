package com.mashibing.mybatis.config;

import com.mashibing.mybatis.annotation.MyScan;
import com.mashibing.mybatis.postprocessor.MyBeanDefinitionRegister;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author shadow
 * @create 2020-07-20
 * @description
 */
@Configuration
@ComponentScan("com.mashibing.mybatis")
@MyScan(value = "com.mashibing.mybatis")
@Import(MyBeanDefinitionRegister.class) // 导入这个类 会执行这个类下面的方法 批量导入 beanDefinition
public class MyConfig {
}
