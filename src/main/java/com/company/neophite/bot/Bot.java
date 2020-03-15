package com.company.neophite.bot;

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
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;

import static com.company.neophite.bot.MessageSender.sendInstruction;


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
    private BotService botService;
    private ReplyKeyboardMarkup c;
    private MessageSender messageSender;

    @Autowired
    public Bot(UserRepo userRepo, OrderRepo orderRepo, UserServiceInterface userServiceInterface, ReplyKeyboardMarkup replyKeyboardMarkup , BotService botService , MessageSender messageSender) {
        this.userRepo = userRepo;
        this.orderRepo = orderRepo;
        this.userServiceInterface = userServiceInterface;
        this.c = replyKeyboardMarkup;
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
                if (update.getMessage().getText().equals("/start")) {
                    sendInstruction(update);
                }
                User currentUser = userRepo.findUserByUsername(update.getMessage().getFrom().getUserName());
                if (currentUser == null) {
                    currentUser = userServiceInterface.saveUserFromMessage(update.getMessage());
                }
                if (update.getMessage().getText().startsWith("/orders")) {
                    botService.getOrders(update, currentUser);
                } else if (update.getMessage().getText().startsWith("/set")) {
                    botService.setOrder(update, currentUser);
                } else if (update.getMessage().getText().startsWith("/remove")) {
                    botService.removeOrder(update, currentUser);
                } else {
                    if (Validator.validate(update.getMessage().getText())) {
                        DataParser dataParser = new DataParser(update.getMessage().getText());
                        OrderDetails orderDetails = dataParser.generateOrderDetails();
                        MessageSender.sendMsg(update.getMessage(), dataParser.toStringPath(orderDetails.getPathList()));
                    } else {
                        MessageSender.sendErrorValiditiTrackNumber(update);
                    }
                }
            }
        } else if (update.hasCallbackQuery()) {
          /*  DataParser dataParser = new DataParser(update.getCallbackQuery().getData());
            OrderDetails orderDetails = dataParser.generateOrderDetails();
            ArrayList<KeyboardRow> keyboard = new ArrayList<>();
            KeyboardRow first = new KeyboardRow();
            KeyboardRow second = new KeyboardRow();
            first.add(EmojiParser.parseToUnicode(":bar_chart: ")+"Дополнительная информация");
            first.add(EmojiParser.parseToUnicode(":calendar: ")+"Состояние отправки");
            second.add(EmojiParser.parseToUnicode(":back: ")+"Назад");
            keyboard.add(first);
            keyboard.add(second);
            c.setKeyboard(keyboard);

           */
            SendMessage sendMessage = new SendMessage().setChatId(update.getCallbackQuery().getMessage().getChatId()).setReplyMarkup(c).enableMarkdown(true).setText("fe");
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }
    }


}

