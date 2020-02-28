package com.company.neophite.bot;

import com.company.neophite.entity.Order;
import com.company.neophite.entity.User;
import com.company.neophite.parser.NodeOfPath;
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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static com.company.neophite.parser.DataParser.getFullPath;

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
    public Bot(UserRepo userRepo , OrderRepo orderRepo , UserServiceInterface userServiceInterface) {
        this.userRepo = userRepo;
        this.orderRepo = orderRepo;
        this.userServiceInterface = userServiceInterface;
    }

    @Override
    public void onUpdateReceived(Update update) {
        BotContxt botContxt = new BotContxt();
        User currentUser = userRepo.findUserByUsername(update.getMessage().getFrom().getUserName());
        if(currentUser==null){
            currentUser = new User(
                    update.getMessage().getFrom().getUserName(),
                    update.getMessage().getFrom().getFirstName(),
                    update.getMessage().getFrom().getLastName());
            userRepo.save(currentUser);
        }
        botContxt.setContext(this, update.getMessage().getChatId(),currentUser);
        if (update.getMessage().hasText()) {
            if(update.getMessage().getText().startsWith("/orders")){
                try {
                    sendMsg(update.getMessage(),botContxt.getCurrentUser().getUsersOrders().toString());
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            if (update.getMessage().getText().startsWith("/set")) {
            String track = update.getMessage().getText().substring(0,4).trim();
            Order order = new Order(track,false);
            orderRepo.save(order);
            botContxt.getCurrentUser().setUsersOrder(order);
            userServiceInterface.updateUser(currentUser , botContxt.getCurrentUser());
            } else {
                try {
                    sendMsg(update.getMessage() , toStringPath(getFullPath(update.getMessage().getText(), returnUrl())).toString());
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
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

    private void sendMsg(Message message,String text) throws TelegramApiException {
        SendMessage mes = new SendMessage();
        mes.enableMarkdown(true);
        mes.setChatId(message.getChatId().toString());
        mes.setText(text);
        execute(mes);

    }

    private String returnUrl() {
        return System.getenv("siteUrlFirst");
    }

    private static StringBuilder toStringPath(List<NodeOfPath> nodesOfPath) {
        StringBuilder string = new StringBuilder();
        for (int itter = nodesOfPath.size() - 1; itter > 0; itter--) {
            string.append(EmojiParser.parseToUnicode(":arrow_down:"));
            string.append(EmojiParser.parseToUnicode(":clock10:") + nodesOfPath.get(itter).getDate() + '\n' + "**Нахождение :** " + nodesOfPath.get(itter).getInfo() + '\n');
        }
        return string;
    }

    public String getBot() {
        return this.botName;
    }

    public String getToken() {
        return this.token;
    }
}