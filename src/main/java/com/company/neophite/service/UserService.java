package com.company.neophite.service;

import com.company.neophite.entity.User;
import com.company.neophite.repos.UserRepo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class UserService implements UserServiceInterface {

    private UserRepo userRepo;

    @Autowired
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public User save(User user) {
        return userRepo.save(user);
    }


    @Override
    public void updateUser(User oldUser, User newUser) {
        BeanUtils.copyProperties(newUser, oldUser);
        userRepo.save(oldUser);
    }

    @Override
    public User saveUserFromCallBack(CallbackQuery callbackQuery) {
        return userRepo.save(new User(
                callbackQuery.getFrom().getId(),
                callbackQuery.getFrom().getUserName(),
                callbackQuery.getFrom().getFirstName(),
                callbackQuery.getFrom().getLastName())
        );
    }

    @Override
    public User saveUserFromMessage(Message message) {
            return userRepo.save(new User(
                    message.getFrom().getId(),
                    message.getFrom().getUserName(),
                    message.getFrom().getFirstName(),
                    message.getFrom().getLastName())
            );
    }
}

