package com.company.neophite.bot;

import com.company.neophite.entity.Order;
import com.company.neophite.entity.User;
import com.company.neophite.parser.DataParser;
import com.company.neophite.parser.model.OrderDetails;
import com.company.neophite.repos.OrderRepo;
import com.company.neophite.repos.UserRepo;
import com.company.neophite.service.UserServiceInterface;
import com.company.neophite.validation.Validator;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;


@Component
@PropertySource("classpath:bot.properties")
public class Bot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.tok}")
    private String token;

    private UserRepo userRepo;
    private OrderRepo orderRepo;
    private UserServiceInterface userServiceInterface;

    @Autowired
    public Bot(UserRepo userRepo, OrderRepo orderRepo, UserServiceInterface userServiceInterface) {
        this.userRepo = userRepo;
        this.orderRepo = orderRepo;
        this.userServiceInterface = userServiceInterface;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasCallbackQuery()) {
            if (update.getMessage().hasText()) {
                sendInstruction(update);
                User currentUser = userRepo.findUserByUsername(update.getMessage().getFrom().getUserName());
                if (currentUser == null) {
                    currentUser = userServiceInterface.saveUserFromMessage(update.getMessage());
                }
                if (update.getMessage().getText().startsWith("/orders")) {
                    getOrders(update, currentUser);
                } else if (update.getMessage().getText().startsWith("/set")) {
                    setOrder(update, currentUser);
                } else if (update.getMessage().getText().startsWith("/remove")) {
                    removeOrder(update, currentUser);
                } else {
                    DataParser dataParser = new DataParser(update.getMessage().getText());
                    OrderDetails orderDetails = dataParser.generateOrderDetails();
                    sendMsg(update.getMessage(), dataParser.toStringPath(orderDetails));
                }
            }
        }
        else if (update.hasCallbackQuery()) {
            DataParser dataParser = new DataParser(update.getCallbackQuery().getData());
            OrderDetails orderDetails = dataParser.generateOrderDetails();
            sendMsg(update.getCallbackQuery().getMessage(), dataParser.toStringPath(orderDetails));

        }
    }

    @Override
    public String getBotUsername() {
        return this.botName;
    }

    @Override
    public String getBotToken() {
        return this.token;
    }

    private void setOrder(Update update, User currentUser) {
        String track = update.getMessage().getText().trim().substring(4).trim();
        if(Validator.validate(track)) {
         if (orderRepo.findOrderByNumber(track) != null) {
                sendMsg(update.getMessage(), EmojiParser.parseToUnicode(":exclamation:") + track + " уже состоит в вашем профиле");
            } else {
                Order newOrder = new Order(track, false);
                orderRepo.save(newOrder);
                Set<Order> setOfOrders = currentUser.getOrders();
                setOfOrders.add(newOrder);
                currentUser.setOrders(setOfOrders);
                userServiceInterface.save(currentUser);
                sendMsg(update.getMessage(), EmojiParser.parseToUnicode(":white_check_mark:") + track + " успешно привязан за вашим аккаунтом");
            }
        }else{
            sendMsg(update.getMessage(),EmojiParser.parseToUnicode(":xте:")
                    +" Неверный формат трек-номера "
                    +'\n'
                    +EmojiParser.parseToUnicode(":white_check_mark:")+" Коректный Формат : XX000000000XX");
        }
    }

    private void removeOrder(Update update, User currentUser) {
        String orderId = update.getMessage().getText().trim().substring(7);
        for (Order order : currentUser.getOrders()) {
            if (order.getId() == Long.parseLong(orderId)) {
                currentUser.getOrders().remove(order);
                userServiceInterface.save(currentUser);
                break;
            }
        }
        orderRepo.deleteById(Long.parseLong(orderId));
        sendMsg(update.getMessage(), EmojiParser.parseToUnicode(":white_check_mark:") + " Трекинг-номер удалён!");
    }

    private void getOrders(Update update, User currentUser) {
        if (currentUser.getOrders().isEmpty()) {
            sendMsg(update.getMessage(), EmojiParser.parseToUnicode(":clipboard:") + "*Трек лист* пустой!");
            return;
        }
        List<InlineKeyboardButton> list = new ArrayList<>();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        StringBuilder usersOrdersMenu = new StringBuilder();
        usersOrdersMenu.append(EmojiParser.parseToUnicode(":clipboard:")).append("*Трек лист* \n\n");
        for (Order order : currentUser.getOrders()) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton().setText(order.getNumber()).setCallbackData(order.getNumber().trim());
            usersOrdersMenu.append(EmojiParser.parseToUnicode(":package:"))
                    .append(" ")
                    .append(order.getNumber())
                    .append(" . Удалить трек-номер - ")
                    .append(" /remove")
                    .append(order.getId().toString())
                    .append('\n');
            list.add(inlineKeyboardButton);
        }
        inlineKeyboardMarkup.setKeyboard(Collections.singletonList(list));
        getOrdersSendInlineKeyboardButtonsAndList(update, usersOrdersMenu, inlineKeyboardMarkup);
    }

    private void sendInstruction(Update update) {
        if (update.getMessage().getText().equals("/start")) {
            sendMsg(update.getMessage(), "Установить с помощью команды кто `/set` и через Пробел трек-номер которые ты хочешь отслеживать(они будут закреплены за вами). " + '\n' + "С  И кпомощью команды `/orders ` вы можете получить все трек-номера ваших поссылок и отслеживать их онлайн");
        }
    }

    private void getOrdersSendInlineKeyboardButtonsAndList(Update update, StringBuilder menu, InlineKeyboardMarkup keyboardListOrders) {
        SendMessage message = new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(menu.toString())
                .setReplyMarkup(keyboardListOrders)
                .enableMarkdown(true);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMsg(Message message, String text) {
        SendMessage mes = new SendMessage();
        mes.enableMarkdown(true);
        mes.setChatId(message.getChatId().toString());
        mes.setText(text);
        try {
            execute(mes);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

}

