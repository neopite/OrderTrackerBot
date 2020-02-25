package com.company.neophite.service;

import com.company.neophite.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface UserServiceInterface {
    User save(User user);
    void updateUser(User oldUser , User newUser);
}
