package com.booking.utils;

import java.util.Random;

public class GenerateToken {
    private static final String digits = "0123456789";
    private static final Random generator = new Random();

    private static int randomNumber(int min, int max){
        return generator.nextInt(max - min + 1) + min;
    }

    public static String randomDigits(int numberLength){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < numberLength; i++){
            int number = randomNumber(0, digits.length() - 1);
            sb.append(number);
        }
        return sb.toString();
    }
}
