package com.company.neophite;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Bot extends TelegramLongPollingBot {


    public static void main(String[] args)  {

    }

    @Override
    public String getBotUsername() {
        return "remindOrder_bot";
    }

    @Override
    public void onUpdateReceived(Update e) {

    }

    @Override
    public String getBotToken() {
        return System.getenv("token");
    }

}