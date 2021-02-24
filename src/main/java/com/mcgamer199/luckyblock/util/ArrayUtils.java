package com.mcgamer199.luckyblock.util;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public class ArrayUtils {

    @SafeVarargs
    public static <T> T[] concat(@NotNull T[] first, T[]... others) {
        if(others == null || others.length == 0) {
            return first;
        }

        int sumLength = Arrays.stream(others).filter(Objects::nonNull).mapToInt(array -> array.length).sum();
        T[] result = Arrays.copyOf(first, first.length + sumLength);

        int nextPos = 0;
        boolean firstPlacement = true;

        for (T[] other : others) {
            nextPos += other.length;
            System.arraycopy(other, 0, result, firstPlacement ? first.length : nextPos, other.length);
            firstPlacement = false;
        }

        return result;
    }

    public static <T> T[] concat(@NotNull T[] first, @NotNull T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static <T> T getLast(@NotNull T[] array) {
        if(array.length == 0) {
            return null;
        }

        return array[array.length - 1];
    }
}
