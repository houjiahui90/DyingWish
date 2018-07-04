package com.houjiahui.DyingWish.service;

import com.houjiahui.DyingWish.entity.User;
import com.houjiahui.DyingWish.mapper.UserMapper;
import com.houjiahui.core.service.BaseService;
import com.houjiahui.core.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService extends CrudService<UserMapper,User> {

    @Autowired
    protected UserMapper mapper;

    public User selectUser(String id){
        return mapper.selectUser(id);
//        User temp = new User(id);
//        return mapper.get(temp);
//        return temp;
//        return mapper.testSql().get(0);
    }
}
