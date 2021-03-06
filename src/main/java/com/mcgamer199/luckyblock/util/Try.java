package com.mcgamer199.luckyblock.util;

import com.mcgamer199.luckyblock.api.base.ThrowableHandler;
import com.mcgamer199.luckyblock.api.base.ThrowingPredicate;
import com.mcgamer199.luckyblock.api.base.ThrowingRunnable;
import com.mcgamer199.luckyblock.api.base.ThrowingSupplier;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Lokha
 */
public class Try {

    public static ThrowableHandler throwableHandler = Throwable::printStackTrace;

    /**
     * Игрорировать проверяемое исключение (если проверяемое исключение возникнет, оно будет в обертке RuntimeException)
     * @param supplier code
     * @param <T>      type result
     * @return result
     */
    public static <T> T unchecked(ThrowingSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch(Exception e) {
            doThrow0(e);
            throw new AssertionError(); // до сюда код не дойдет
        }
    }

    /**
     * Игрорировать проверяемое исключение (если проверяемое исключение возникнет, оно будет в обертке RuntimeException)
     * @param runnable code
     */
    public static void unchecked(ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch(Exception e) {
            doThrow0(e);
            throw new AssertionError(); // до сюда код не дойдет
        }
    }

    /**
     * Игрорировать проверяемое исключение (если проверяемое исключение возникнет, оно будет в обертке RuntimeException)
     * @param predicate code
     */
    public static <T> Predicate<T> unchecked(ThrowingPredicate<T> predicate) {
        return t -> {
            try {
                return predicate.test(t);
            } catch(Exception e) {
                doThrow0(e);
                throw new AssertionError(); // до сюда код не дойдет
            }
        };
    }

    /**
     * Игнорировать исключения целиком и полностью (если исключение воникнет, тогда просто будет printStackTrace)
     * @param supplier code
     * @param <T>      type result
     * @param def      дефотное значение, если возникнет исключение
     * @return result
     */
    public static <T> T ignore(ThrowingSupplier<T> supplier, T def) {
        try {
            return supplier.get();
        } catch(Exception e) {
            throwableHandler.handle(e);
            return def;
        }
    }

    /**
     * Игнорировать исключения целиком и полностью (если исключение воникнет, тогда исключение передаётся в обработчик)
     * @param runnable code
     */
    public static Optional<Exception> ignore(ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch(Exception e) {
            throwableHandler.handle(e);
            return Optional.of(e);
        }
        return Optional.empty();
    }

    /**
     * Игнорировать исключения целиком и полностью (если исключение воникнет, тогда исключение передаётся в обработчик)
     * @param runnable code
     */
    public static Optional<Exception> ignore(ThrowingRunnable runnable, Consumer<Exception> consumer) {
        try {
            runnable.run();
        } catch(Exception e) {
            consumer.accept(e);
            throwableHandler.handle(e);
            return Optional.of(e);
        }
        return Optional.empty();
    }

    /**
     * Игрорировать проверяемое исключение (если исключение возникнет, оно будет в обертке RuntimeException)
     * @param predicate code
     * @param <T>       type result
     * @param def       дефотное значение, если возникнет исключение
     */
    public static <T> Predicate<T> ignore(ThrowingPredicate<T> predicate, boolean def) {
        return t -> {
            try {
                return predicate.test(t);
            } catch(Exception e) {
                throwableHandler.handle(e);
                return def;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private static <E extends Exception> void doThrow0(Exception e) throws E {
        throw (E) e;
    }
}
