package com.mashibing.mybatis;

import com.mashibing.mybatis.config.MyConfig;
import com.mashibing.mybatis.services.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author shadow
 * @create 2020-07-18
 * @description
 */
public class AnnotationIOC {
	public static void main(String[] args) {

		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
		ac.register(MyConfig.class);
		ac.refresh();

		System.out.println(ac.getBean("myUserFactoryBean"));
		System.out.println(ac.getBean("&myUserFactoryBean"));


//		System.out.println(ac.getBean("myUserMapperFactoryBean")); // null 这里调用了 toString方法导致的,修改逻辑 去掉 Object 的方法

		// 注入 到 service
		UserService userService = (UserService) ac.getBean("userService");
		userService.say();


		// 通用 mapper 代理
		System.out.println(ac.getBean("UserMapper"));
		System.out.println(ac.getBean("AccountMapper"));

	}
}
