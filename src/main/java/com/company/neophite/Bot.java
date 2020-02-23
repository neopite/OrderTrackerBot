package com.company.neophite;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static com.company.neophite.DataParser.getFullPath;

public class Bot extends TelegramLongPollingBot {


    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }


    }

    @Override
    public String getBotUsername() {
        return "remindOrder_bot";
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            sendMsg(update.getMessage());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMsg(Message message) throws TelegramApiException {
        SendMessage mes = new SendMessage();
        mes.enableMarkdown(true);
        mes.setChatId(message.getChatId().toString());
        mes.setText(toStringPath(getFullPath(message.getText(),returnUrl())).toString());
        execute(mes);

    }

    private String returnUrl(){
        return System.getenv("siteUrlFirst");
    }

    @Override
    public String getBotToken() {
        return System.getenv("token");
    }

    private static StringBuilder toStringPath(List<NodeOfPath> nodesOfPath) {
        StringBuilder string = new StringBuilder();
        for (int itter = nodesOfPath.size() - 1; itter > 0; itter--) {
            string.append(":top: " + '\n');
            string.append("Дата : " + nodesOfPath.get(itter).getDate() + '\n' + "Нахождение : " + nodesOfPath.get(itter).getInfo());
        }
        return string;
    }

}