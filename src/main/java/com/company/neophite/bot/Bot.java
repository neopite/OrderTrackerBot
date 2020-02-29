package com.company.neophite.bot;

import com.company.neophite.entity.Order;
import com.company.neophite.entity.User;
import com.company.neophite.parser.DataParser;
import com.company.neophite.repos.OrderRepo;
import com.company.neophite.repos.UserRepo;
import com.company.neophite.service.UserServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Set;

import static com.company.neophite.parser.DataParser.getFullPath;
import static com.company.neophite.parser.DataParser.toStringPath;

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
        User currentUser = userRepo.findUserByUsername(update.getMessage().getFrom().getUserName());
        if (currentUser == null) {
            currentUser = new User(
                    update.getMessage().getFrom().getUserName(),
                    update.getMessage().getFrom().getFirstName(),
                    update.getMessage().getFrom().getLastName());
            userRepo.save(currentUser);
        }
        if (update.getMessage().hasText()) {
            if (update.getMessage().getText().startsWith("/orders")) {
                getOrders(update, currentUser);
            }
            if (update.getMessage().getText().startsWith("/set")) {
                try {
                    setOrder(update, currentUser);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    sendMsg(update.getMessage(), DataParser.toStringPath(getFullPath(update.getMessage().getText(), returnUrl())).toString());
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setOrder(Update update, User currentUser) throws TelegramApiException {
        String track = update.getMessage().getText().substring(4).trim();
        if (orderRepo.findOrderByNumber(track) != null) {
            sendMsg(update.getMessage(), "Заказ : "+track+" уже состоит в вашем профиле");
        } else {
            Order newOrder = new Order(track , false);
            orderRepo.save(newOrder);
            Set<Order> setOfOrders = currentUser.getOrders();
            setOfOrders.add(newOrder);
            currentUser.setOrders(setOfOrders);
            userServiceInterface.save(currentUser);
            sendMsg(update.getMessage(), "Заказ : "+track +" успешно привязан за вашим аккаунтом");

        }

    }

    public void getOrders(Update update, User currentUser) {
        StringBuilder str = new StringBuilder();
        for(Order s : currentUser.getOrders())
            str.append(s.getNumber() + " : ");
        try {
            sendMsg(update.getMessage(), str.toString());
        } catch (TelegramApiException e) {
            e.printStackTrace();
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

    public String getBot() {
        return this.botName;
    }

    public String getToken() {
        return this.token;
    }
}