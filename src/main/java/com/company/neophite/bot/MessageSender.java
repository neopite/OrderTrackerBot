package com.company.neophite.bot;

import com.company.neophite.bot.Bot;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
class MessageSender {

    private static Bot thisBotContext;

    @Autowired
    public void setThisBotContext(@Lazy Bot bot) {
        thisBotContext = bot;
    }

    static void sendInstruction(Update update) {
        sendMsg(update.getMessage(), "Установить с помощью команды кто `/set` и через Пробел трек-номер которые ты хочешь отслеживать(они будут закреплены за вами). " + '\n' + "С  И кпомощью команды `/orders ` вы можете получить все трек-номера ваших поссылок и отслеживать их онлайн");
    }


    static void sendErrorValiditiTrackNumber(Update update) {
        sendMsg(update.getMessage(), EmojiParser.parseToUnicode(":x:")
                + " Неверный формат трек-номера "
                + '\n'
                + EmojiParser.parseToUnicode(":white_check_mark:") + " Коректный Формат : XX000000000XX");
    }

    static void sendMsg(Message message, String text) {
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

    static void sendKeyboard(Update update, ReplyKeyboardMarkup orderKeyboard) {
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

    static void getInlineKeyboardButtonsAndList(Update update, StringBuilder menu, InlineKeyboardMarkup keyboardListOrders) {
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

}
