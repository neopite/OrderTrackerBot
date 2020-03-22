package com.company.neophite.bot;

import com.company.neophite.bot.util.KeyboardProvider;
import com.company.neophite.bot.util.MessageSender;
import com.company.neophite.entity.Order;
import com.company.neophite.entity.User;
import com.company.neophite.parser.model.OrderDetails;
import com.company.neophite.repos.OrderRepo;
import com.company.neophite.repos.UserRepo;
import com.company.neophite.service.UserServiceInterface;
import com.company.neophite.validation.Validator;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.Map;
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

    void setOrderInUsersList(Update update, User currentUser) {
        String track = update.getMessage().getText().trim().substring(4).trim();
        if (Validator.validate(track)) {
            if (orderRepo.findOrderByNumber(track.toUpperCase()) != null &&
                    orderRepo.findOrderByNumber(track.toUpperCase()).getNumber().equalsIgnoreCase(track)) {
                MessageSender.sendMessage(update.getMessage(), EmojiParser.parseToUnicode(":exclamation:") + track + " уже состоит в вашем профиле");
            } else {
                Order newOrder = new Order(track, false);
                orderRepo.save(newOrder);
                Set<Order> setOfOrders = currentUser.getOrders();
                setOfOrders.add(newOrder);
                currentUser.setOrders(setOfOrders);
                userServiceInterface.save(currentUser);
                MessageSender.sendMessage(update.getMessage(), EmojiParser.parseToUnicode(":white_check_mark:") + track + " успешно привязан за вашим аккаунтом");
            }
        } else {
            MessageSender.sendErrorValiditiTrackNumber(update);
        }
    }

    void removeOrderFromUsersList(Update update, User currentUser) {
        String orderId = update.getMessage().getText().trim().substring(7);
        for (Order order : currentUser.getOrders()) {
            if (order.getId() == Long.parseLong(orderId)) {
                currentUser.getOrders().remove(order);
                userServiceInterface.save(currentUser);
                break;
            }
        }
        orderRepo.deleteById(Long.parseLong(orderId));
        MessageSender.sendMessage(update.getMessage(), EmojiParser.parseToUnicode(":white_check_mark:") + " Трекинг-номер удалён!");
    }

}
