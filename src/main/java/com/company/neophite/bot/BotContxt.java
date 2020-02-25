package com.company.neophite.bot;

import com.company.neophite.entity.User;

public class BotContxt {
    private Bot bot;
    private long chatId;
    private User currentUser;

    public BotContxt(Bot bot, long charId) {
        this.bot = bot;
        this.chatId = charId;
    }

    public BotContxt() {
    }


    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public Bot getBot() {
        return bot;
    }

    public void setContext(Bot bot, Long chatId , User currentUser) {
        this.bot = bot;
        this.chatId = chatId;
        this.currentUser = currentUser;
    }

    public long getCharId() {
        return chatId;
    }

    public void setCharId(long charId) {
        this.chatId = charId;
    }

    public BotContxt getBotContext(){
        return this;
    }

}
