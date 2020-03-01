package com.company.neophite.service;

import com.company.neophite.entity.User;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public interface UserServiceInterface {
    User save(User user);
    void updateUser(User oldUser , User newUser);
    public User saveUserFromCallBack(CallbackQuery callbackQuery);
    public User saveUserFromMessage(Message message);
}
