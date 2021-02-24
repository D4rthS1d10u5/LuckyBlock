package com.mcgamer199.luckyblock.api.base;

public interface ThrowingSupplier<T> {

    T get() throws Exception;
}
