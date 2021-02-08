package com.mcgamer199.luckyblock.util;


/**
 * @author Lokha
 */
public class MathUtils {

    public static double ensureRange(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    public static int ensureRange(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    public static boolean inRange(int value, int min, int max) {
        return (value >= min) && (value <= max);
    }

    /**
     * Получить количество процентов, которые составляет value в max
     * @param value значение
     * @param max   миакимальное
     * @return кол-во процентов
     */
    public static int getPercent(int value, int max) {
        return (int) (((double) value / max) * 100);
    }

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    public static boolean isLong(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
}
