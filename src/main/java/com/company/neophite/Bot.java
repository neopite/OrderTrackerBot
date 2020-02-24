package com.company.neophite;

import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static com.company.neophite.DataParser.getFullPath;

@Component
@PropertySource("classpath:bot.properties")
public class Bot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.tok}")
    private String token;

    @Override
    public void onUpdateReceived(Update update) {
        try {
            sendMsg(update.getMessage());
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