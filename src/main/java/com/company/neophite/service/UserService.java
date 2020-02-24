package com.company.neophite.service;

import com.company.neophite.entity.User;
import com.company.neophite.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserServiceInterface {

    private UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public User save(User user) {
        return userRepo.save(user);
    }

    @Override
    public User findByChatId(int chatId) {
        return userRepo.findUserByChatId(chatId);
    }
}
