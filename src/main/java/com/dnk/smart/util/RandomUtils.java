package com.dnk.smart.util;

import java.util.Random;

public abstract class RandomUtils {

    public static int randomInteger(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
        //return (int) (Math.random() * (max - min + 1)) + min;
    }

    public static double randomDouble(int min, int max) {
        return Math.random() * (max - min + 1) + min;
    }

    public static char randomChar() {
        String charStr = "abcdefghijklmnopqrstuvwxyz";
        return charStr.charAt(randomInteger(0, 25));
    }

    public static String randomString(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(randomChar());
        }
        return builder.toString();
    }

}
