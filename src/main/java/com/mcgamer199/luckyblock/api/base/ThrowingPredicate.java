package com.mcgamer199.luckyblock.api.base;

public interface ThrowingPredicate<T> {

    boolean test(T val) throws Exception;
}
