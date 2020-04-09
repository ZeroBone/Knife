package net.zerobone.knife.utils;

public class StringUtils {

    private StringUtils() {}

    public static boolean isTerminal(String s) {
        return Character.isUpperCase(s.charAt(0));
    }

    public static boolean isNonTerminal(String s) {
        return !isTerminal(s);
    }

}