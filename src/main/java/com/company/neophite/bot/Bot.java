package com.company.neophite.bot;

import com.company.neophite.entity.Order;
import com.company.neophite.entity.User;
import com.company.neophite.parser.DataParser;
import com.company.neophite.parser.model.OrderDetails;
import com.company.neophite.repos.OrderRepo;
import com.company.neophite.repos.UserRepo;
import com.company.neophite.service.UserServiceInterface;
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
        User currentUser = null;
        if (!update.hasCallbackQuery()) {
            if (update.getMessage().hasText()) {
                sendInstruction(update);
                currentUser = userRepo.findUserByUsername(update.getMessage().getFrom().getUserName());
                if (currentUser == null) {
                    currentUser = userServiceInterface.saveUserFromMessage(update.getMessage());
                }
                if (update.getMessage().getText().startsWith("/orders")) {
                    getOrders(update, currentUser);
                } else if (update.getMessage().getText().startsWith("/set")) {
                        setOrder(update, currentUser);
                } else if (update.getMessage().getText().startsWith("/remove")) {
                        removeOrder(update,currentUser);
                } else {
                    try {
                        DataParser dataParser = new DataParser(update.getMessage().getText().toString());
                        OrderDetails orderDetails = dataParser.generateOrderDetails(update.getMessage().getText().toString());
                        String text = dataParser.toStringPath(orderDetails).toString();
                        sendMsg(update.getMessage(), text);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (update.hasCallbackQuery()) {
            DataParser dataParser = new DataParser(update.getCallbackQuery().getData().toString());
            OrderDetails orderDetails = dataParser.generateOrderDetails(update.getCallbackQuery().getData().toString());
            String text = dataParser.toStringPath(orderDetails).toString();
            try {
                execute(new SendMessage().enableMarkdown(true).setChatId(update.getCallbackQuery().getMessage().getChatId()).setText(text));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }


    private void setOrder(Update update, User currentUser) {
        String track = update.getMessage().getText().trim().substring(4);
        if (orderRepo.findOrderByNumber(track) != null) {
            try {
                sendMsg(update.getMessage(), "Трек-номер : " + track + " уже состоит в вашем профиле");
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            Order newOrder = new Order(track, false);
            orderRepo.save(newOrder);
            Set<Order> setOfOrders = currentUser.getOrders();
            setOfOrders.add(newOrder);
            currentUser.setOrders(setOfOrders);
            userServiceInterface.save(currentUser);
            try {
                sendMsg(update.getMessage(), "Трек-номер : " + track + " успешно привязан за вашим аккаунтом");
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }

    }

    private void removeOrder(Update update, User currentUser) {
        String orderId = update.getMessage().getText().trim().substring(7);
        for(Order order : currentUser.getOrders()){
            if(order.getId()==Long.parseLong(orderId)){
                currentUser.getOrders().remove(order);
                userServiceInterface.save(currentUser);
            }
        }
        orderRepo.deleteById(Long.parseLong(orderId));
        try {
            sendMsg(update.getMessage(), EmojiParser.parseToUnicode(":white_check_mark:")+" Трекинг-номер удалён!");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void getOrders(Update update, User currentUser) {
        List<InlineKeyboardButton> list = new ArrayList<>();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("*Трек лист* \n\n");
        for (Order order : currentUser.getOrders()) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton().setText(order.getNumber()).setCallbackData(order.getNumber().trim());
            stringBuilder.append(EmojiParser.parseToUnicode(":package:") + " " + order.getNumber() +" . Удалить трек-номер - " +" /remove"+""+order.getId().toString()+'\n');
            list.add(inlineKeyboardButton);
        }
        inlineKeyboardMarkup.setKeyboard(Collections.singletonList(list));
        SendMessage message = new SendMessage().setChatId(update.getMessage().getChatId()).setText(stringBuilder.toString()).setReplyMarkup(inlineKeyboardMarkup).enableMarkdown(true);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendInstruction(Update update) {
        if (update.getMessage().getText().equals("/start")) {
            try {
                sendMsg(update.getMessage(), "Установить с помощью команды кто`/set` и через Пробел трек-номер которые ты хочешь отслеживать(они будут закреплены за вами). " + '\n' + "С Изи кпомощью команды `/orders ` вы можете получить все трек-номера ваших поссылок и отслеживать их онлайн");
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return;
        }
    }

    @Override
    public String getBotUsername() {
        return getBot();
    }

    @Override
    public String getBotToken() {
        return getToken();
    }

    private void sendMsg(Message message, String text) throws TelegramApiException {
        SendMessage mes = new SendMessage();
        mes.enableMarkdown(true);
        mes.setChatId(message.getChatId().toString());
        mes.setText(text);
        execute(mes);

    }

    private String returnUrl() {
        return System.getenv("siteUrlFirst");
    }

    private String getBot() {
        return this.botName;
    }

    private String getToken() {
        return this.token;
    }


}

