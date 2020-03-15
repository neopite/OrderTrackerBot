package com.company.neophite.bot;

import com.company.neophite.entity.Order;
import com.company.neophite.entity.User;
import com.company.neophite.repos.OrderRepo;
import com.company.neophite.repos.UserRepo;
import com.company.neophite.service.UserServiceInterface;
import com.company.neophite.validation.Validator;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Component
public class BotService {

    private UserRepo userRepo;
    private OrderRepo orderRepo;
    private UserServiceInterface userServiceInterface;

    @Autowired
    public BotService(UserRepo userRepo, OrderRepo orderRepo, UserServiceInterface userServiceInterface) {
        this.userRepo = userRepo;
        this.orderRepo = orderRepo;
        this.userServiceInterface = userServiceInterface;
    }

    void setOrder(Update update, User currentUser) {
        String track = update.getMessage().getText().trim().substring(4).trim();
        if (Validator.validate(track)) {
            if (orderRepo.findOrderByNumber(track.toUpperCase()) != null && orderRepo.findOrderByNumber(track.toUpperCase()).getNumber().equalsIgnoreCase(track)) {
                MessageSender.sendMsg(update.getMessage(), EmojiParser.parseToUnicode(":exclamation:") + track + " уже состоит в вашем профиле");
            } else {
                Order newOrder = new Order(track, false);
                orderRepo.save(newOrder);
                Set<Order> setOfOrders = currentUser.getOrders();
                setOfOrders.add(newOrder);
                currentUser.setOrders(setOfOrders);
                userServiceInterface.save(currentUser);
                MessageSender.sendMsg(update.getMessage(), EmojiParser.parseToUnicode(":white_check_mark:") + track + " успешно привязан за вашим аккаунтом");
            }
        } else {
            MessageSender.sendErrorValiditiTrackNumber(update);
        }
    }

    void removeOrder(Update update, User currentUser) {
        String orderId = update.getMessage().getText().trim().substring(7);
        for (Order order : currentUser.getOrders()) {
            if (order.getId() == Long.parseLong(orderId)) {
                currentUser.getOrders().remove(order);
                userServiceInterface.save(currentUser);
                break;
            }
        }
        orderRepo.deleteById(Long.parseLong(orderId));
        MessageSender.sendMsg(update.getMessage(), EmojiParser.parseToUnicode(":white_check_mark:") + " Трекинг-номер удалён!");
    }

    void getOrders(Update update, User currentUser) {
        if (currentUser.getOrders().isEmpty()) {
            MessageSender.sendMsg(update.getMessage(), EmojiParser.parseToUnicode(":clipboard:") + "*Трек лист* пустой!");
            return;
        }
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        StringBuilder usersOrdersMenu = new StringBuilder();
        usersOrdersMenu.append(EmojiParser.parseToUnicode(":clipboard:")).append("*Трек лист* \n\n");
        for (Order order : currentUser.getOrders()) {
            usersOrdersMenu.append(EmojiParser.parseToUnicode(":package:"))
                    .append(" ")
                    .append(order.getNumber())
                    .append(" . Удалить трек-номер - ")
                    .append(" /remove")
                    .append(order.getId().toString())
                    .append('\n');
            rowsInline.add(new ArrayList<>(
                    Arrays.asList((new InlineKeyboardButton().setText(order.getNumber()).setCallbackData(order.getNumber().trim()))
                    )));
        }
        inlineKeyboardMarkup.setKeyboard(rowsInline);
        MessageSender.getInlineKeyboardButtonsAndList(update, usersOrdersMenu, inlineKeyboardMarkup);
    }
}
