package com.mashibing.mybatis.services;

import com.mashibing.mybatis.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author shadow
 * @create 2020-07-20
 * @description
 */

@Service
public class UserService {


	@Autowired
	private UserMapper userMapper;


	public void say(){
		System.out.println("user service say ..." + userMapper);
	}

}
