package com.company.neophite.bot;

import com.company.neophite.entity.User;
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
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import static com.company.neophite.bot.MessageSender.sendInstruction;


@Component
@PropertySource("classpath:bot.properties")
public class Bot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.tok}")
    private String token;

    private static String lastOrderTrack;

    private UserRepo userRepo;
    private OrderRepo orderRepo;
    private UserServiceInterface userServiceInterface;
    private BotService botService;
    private ReplyKeyboardMarkup orderKeyboard;
    private MessageSender messageSender;

    @Autowired
    public Bot(UserRepo userRepo, OrderRepo orderRepo, UserServiceInterface userServiceInterface, ReplyKeyboardMarkup replyKeyboardMarkup, BotService botService, MessageSender messageSender) {
        this.userRepo = userRepo;
        this.orderRepo = orderRepo;
        this.userServiceInterface = userServiceInterface;
        this.orderKeyboard = replyKeyboardMarkup;
        this.botService = botService;
        this.messageSender = messageSender;
    }

    @Override
    public String getBotUsername() {
        return this.botName;
    }

    @Override
    public String getBotToken() {
        return this.token;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (!update.hasCallbackQuery()) {
            if (update.getMessage().hasText()) {
                User currentUser = userRepo.findUserByUsername(update.getMessage().getFrom().getUserName());
                if (currentUser == null) {
                    currentUser = userServiceInterface.saveUserFromMessage(update.getMessage());
                }
                if((EmojiParser.parseToUnicode(":bar_chart: ")+"Дополнительная информация").equalsIgnoreCase(update.getMessage().getText())){
                    botService.getAndPrintOrderPath(update,lastOrderTrack);
                }
                else if (update.getMessage().getText().equals("/start")) {
                    sendInstruction(update);
                }
                else if (update.getMessage().getText().startsWith("/orders")) {
                    botService.getOrders(update, currentUser);
                } else if (update.getMessage().getText().startsWith("/set")) {
                    botService.setOrder(update, currentUser);
                } else if (update.getMessage().getText().startsWith("/remove")) {
                    botService.removeOrder(update, currentUser);
                } else {
                    if (Validator.validate(update.getMessage().getText())) {
                        botService.getAndPrintOrderPath(update , update.getMessage().getText());
                    } else {
                        MessageSender.sendErrorValiditiTrackNumber(update);
                    }
                }
            }
        } else if (update.hasCallbackQuery()) {
            lastOrderTrack = orderRepo.findOrderByNumber(update.getCallbackQuery().getData()).getNumber();
            MessageSender.sendKeyboard(update,orderKeyboard.setKeyboard(botService.getFunctionalKeyboard()));
        }
    }

}

