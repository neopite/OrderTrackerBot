package com.company.neophite.bot.util;

import com.company.neophite.parser.model.OrderDetails;
import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Map;

public class KeyboardProvider {
    public static ArrayList<KeyboardRow> getFunctionalKeyboard(String trackNumber) {
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

    public static ArrayList<KeyboardRow> returnEmptyKeyboard() {
        ArrayList<KeyboardRow> emptyKeyboard = new ArrayList<>();
        KeyboardRow keyboardButtons = new KeyboardRow();
        keyboardButtons.add(" ");
        emptyKeyboard.add(keyboardButtons);
        return emptyKeyboard;
    }

}

