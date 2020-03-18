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
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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

    OrderDetails getAndPrintOrderPath(Update update, OrderDetails orderDetails) {
        MessageSender.sendMsg(update.getMessage(), new DataParser().toStringPath(orderDetails.getPathList()));
        return orderDetails;
    }

    ArrayList<KeyboardRow> getFunctionalKeyboard(String trackNumber) {
        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow zero = new KeyboardRow();
        KeyboardRow first = new KeyboardRow();
        KeyboardRow second = new KeyboardRow();
        zero.add(EmojiParser.parseToUnicode(":package:") + trackNumber);
        first.add(EmojiParser.parseToUnicode(":bar_chart:") + "Дополнительная информация");
        first.add(EmojiParser.parseToUnicode(":calendar:") + "Состояние отправки");
        second.add(EmojiParser.parseToUnicode(":back:") + "Выход из меню");
        keyboard.add(zero);
        keyboard.add(first);
        keyboard.add(second);
        return keyboard;
    }



    void getExtraInfoAboutOrder(Update update, OrderDetails orderDetails) {
        StringBuilder info = new StringBuilder();
        info.append(EmojiParser.parseToUnicode(":outbox_tray:")).append(" Откуда: ").append(orderDetails.getFrom()).append('\n')
                .append(EmojiParser.parseToUnicode(":inbox_tray:")).append("Куда : ").append(orderDetails.getTo()).append('\n')
                .append(EmojiParser.parseToUnicode(":office:")).append("Службы(а) доставки : ").append(orderDetails.getOrderService()).append('\n')
                .append(EmojiParser.parseToUnicode(":o:")).append("Вес : ").append(orderDetails.getWeight()).append('\n')
                .append(EmojiParser.parseToUnicode(":hourglass:")).append("В пути : ").append(orderDetails.getOnTheWay()).append(" дней").append('\n')
                .append(EmojiParser.parseToUnicode(":airplane_arriving:")).append("Время прибытия : ").append(orderDetails.getArrivalTime());
        MessageSender.sendMsg(update.getMessage(), info.toString());
    }

   static ArrayList<KeyboardRow> returnEmptyKeyboard(){
        ArrayList<KeyboardRow> emptyKeyboard = new ArrayList<>();
        KeyboardRow keyboardButtons = new KeyboardRow();
        keyboardButtons.add(" ");
        emptyKeyboard.add(keyboardButtons);
        return emptyKeyboard;
    }
}
