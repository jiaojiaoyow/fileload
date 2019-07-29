package com.example.demo.Service.Impl;

import com.example.demo.Dao.UserMapper;
import com.example.demo.Service.UserService;
import com.example.demo.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("UserService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User selectByPrimaryKey(String username) {
        return this.userMapper.selectByPrimaryKey(username);
    }
}
