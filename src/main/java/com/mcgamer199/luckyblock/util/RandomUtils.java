package com.mcgamer199.luckyblock.util;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class RandomUtils {

    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    /**
     * Выдать true на рандом с указанной вероятностью
     * @param percent вероятность (0-100)
     * @return true с вероятностью
     */
    public static boolean nextPercent(int percent) {
        return random.nextInt(100) + percent >= 100;
    }

    /**
     * Выдать true на рандом с указанной вероятностью
     * @param percent вероятность (0-100)
     * @return true с вероятностью
     */
    public static boolean nextPercent(double percent) {
        return random.nextInt(100) + percent >= 100;
    }

    public static int nextInt(int bound) {
        return random.nextInt(bound);
    }

    public static <T> T getRandomObject(T[] array) {
        return array.length == 0 ? null : array[nextInt(array.length)];
    }

    public static <T> T getRandomObject(List<T> list) {
        return list.isEmpty() ? null : list.get(nextInt(list.size()));
    }

    public static int nextInt(int min, int max) {
        return random.nextInt(min, max);
    }

    public static boolean nextBoolean() {
        return random.nextBoolean();
    }
}
