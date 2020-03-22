package com.company.neophite.bot.util;

import com.company.neophite.bot.Bot;
import com.company.neophite.entity.Order;
import com.company.neophite.entity.User;
import com.company.neophite.parser.DataParser;
import com.company.neophite.parser.model.OrderDetails;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class MessageSender {

    private static Bot thisBotContext;

    @Autowired
    public void setThisBotContext(@Lazy Bot bot) {
        thisBotContext = bot;
    }

    public static void sendInstruction(Update update) {
        sendMessage(update.getMessage(), "Установить с помощью команды кто `/set` и через Пробел трек-номер которые ты хочешь отслеживать(они будут закреплены за вами). " + '\n' + "С  И кпомощью команды `/orders ` вы можете получить все трек-номера ваших поссылок и отслеживать их онлайн");
    }


    public static void sendErrorValiditiTrackNumber(Update update) {
        sendMessage(update.getMessage(), EmojiParser.parseToUnicode(":x:")
                + " Неверный формат трек-номера "
                + '\n'
                + EmojiParser.parseToUnicode(":white_check_mark:") + " Коректный Формат : XX000000000XX");
    }

    public static void sendMessage(Message message, String text) {
        SendMessage mes = new SendMessage();
        mes.enableMarkdown(true);
        mes.setChatId(message.getChatId().toString());
        mes.setText(text);
        try {
            thisBotContext.execute(mes);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public static void sendKeyboard(Update update, ReplyKeyboardMarkup orderKeyboard) {
        SendMessage sendMessage;
        if (update.getCallbackQuery() != null) {
            sendMessage = new SendMessage().setChatId(update.getCallbackQuery().getMessage().getChatId()).setReplyMarkup(orderKeyboard).enableMarkdown(true).setText("Выбирете...");
        } else {
            sendMessage = new SendMessage().setChatId(update.getMessage().getChatId()).setReplyMarkup(orderKeyboard).enableMarkdown(true).setText("Выбирете...");
        }
        try {
            thisBotContext.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void sendClearKeyboard(Update update, ReplyKeyboardMarkup orderKeyboard) {
        SendMessage sendMessage = new SendMessage().setChatId(update.getMessage().getChatId()).setReplyMarkup(orderKeyboard).setText("Выход из меню");
        try {
            thisBotContext.execute(sendMessage);
        } catch (
                TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void sendExtraInfoAboutOrder(Update update, OrderDetails orderDetails) {
        StringBuilder info = new StringBuilder();
        info.append(EmojiParser.parseToUnicode(":outbox_tray:")).append(" Откуда: ").append(orderDetails.getFrom()).append('\n')
                .append(EmojiParser.parseToUnicode(":inbox_tray:")).append("Куда : ").append(orderDetails.getTo()).append('\n')
                .append(EmojiParser.parseToUnicode(":office:")).append("Службы(а) доставки : ").append(orderDetails.getOrderService()).append('\n')
                .append(EmojiParser.parseToUnicode(":o:")).append("Вес : ").append(orderDetails.getWeight()).append('\n')
                .append(EmojiParser.parseToUnicode(":hourglass:")).append("В пути : ").append(orderDetails.getOnTheWay()).append(" дней").append('\n')
                .append(EmojiParser.parseToUnicode(":airplane_arriving:")).append("Время прибытия : ").append(orderDetails.getArrivalTime());
        MessageSender.sendMessage(update.getMessage(), info.toString());
    }

    public static void sendOrderPath(Update update, OrderDetails orderDetails) {
        MessageSender.sendMessage(update.getMessage(), new DataParser().toStringPath(orderDetails.getPathList()));
    }

    private static void sendInlineKeyboardWithOrdersList(Update update, StringBuilder menu, InlineKeyboardMarkup keyboardListOrders) {
        SendMessage message = new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(menu.toString())
                .setReplyMarkup(keyboardListOrders)
                .enableMarkdown(true);
        try {
            thisBotContext.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

   public static void sendUsersOrdersList(Update update, User currentUser) {
        if (currentUser.getOrders().isEmpty()) {
            MessageSender.sendMessage(update.getMessage(), EmojiParser.parseToUnicode(":clipboard:") + "*Трек лист* пустой!");
            return;
        }
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        InlineKeyboardMarkup keyboardWithOrders = new InlineKeyboardMarkup();
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
        keyboardWithOrders.setKeyboard(rowsInline);
        MessageSender.sendInlineKeyboardWithOrdersList(update, usersOrdersMenu, keyboardWithOrders);
    }

}
