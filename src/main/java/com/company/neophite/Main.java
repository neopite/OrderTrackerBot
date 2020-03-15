package com.company.neophite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(Main.class,args);
    }

    @Bean
    public ReplyKeyboardMarkup getKeyboard(){
        return new ReplyKeyboardMarkup().setSelective(true).setResizeKeyboard(true).setOneTimeKeyboard(false);
    }
}
