package com.company.neophite.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    private static final String regex =  "^[a-zA-Z]{2}\\d{8}\\w[a-zA-Z]{2}";

    public static boolean validate(String trackNumber) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(trackNumber);
        return matcher.matches();
    }

}
