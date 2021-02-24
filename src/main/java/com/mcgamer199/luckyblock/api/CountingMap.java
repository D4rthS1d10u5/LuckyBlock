package com.mcgamer199.luckyblock.api;

import com.google.common.collect.ForwardingMap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

@SuppressWarnings("UnstableApiUsage")
@Log
public class CountingMap<K, V> extends ForwardingMap<K, V> {

    public static final int DEFAULT_BUFFER_LIMIT = 15;
    private int bufferLimit = DEFAULT_BUFFER_LIMIT;
    private final AtomicInteger bufferCounter = new AtomicInteger();
    private final Map<K, V> delegate;
    private BiConsumer<Set<Entry<K, V>>, Boolean> limitReached = (map, forced) -> {};
    private CountingKeySet keys;
    private CountingValues values;
    @Setter @Getter
    private boolean countChanges;

    public CountingMap() {
        this.delegate = new HashMap<>();
    }

    public CountingMap(Map<K, V> delegate) {
        this.delegate = delegate;
    }

    public CountingMap(Map<K, V> delegate, int bufferLimit) {
        this.delegate = delegate;
        this.bufferLimit = bufferLimit;
    }

    public CountingMap(Map<K, V> delegate, int bufferLimit, BiConsumer<Set<Entry<K, V>>, Boolean> limitReached) {
        this.delegate = delegate;
        this.bufferLimit = bufferLimit;
        this.limitReached = limitReached;
    }

    @Override
    protected Map<K, V> delegate() {
        return delegate;
    }

    @Override
    public V put(@NotNull K key, @NotNull V value) {
        V result = super.put(key, value);

        processReachedLimit(false);

        return result;
    }

    @Nullable
    @Override
    public V replace(K key, V value) {
        V result = delegate().replace(key, value);

        processReachedLimit(false);

        return result;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        boolean result = delegate().replace(key, oldValue, newValue);

        if(result) {
            processReachedLimit(false);
        }

        return result;
    }

    @Override
    public V remove(@NotNull Object object) {
        V result = super.remove(object);

        if(result != null) {
            processReachedLimit(false);
        }

        return result;
    }

    @Override
    public @NotNull Set<K> keySet() {
        if(keys == null) {
            keys = new CountingKeySet();
        }

        return keys;
    }

    @Override
    public @NotNull Collection<V> values() {
        if(values == null) {
            values = new CountingValues();
        }
        return values;
    }

    /**
     * Вызвать обработчик карты по достижению лимита
     * @param force принудительно вызвать обработчик, игнорируя счетчик изменений
     */
    public void processReachedLimit(boolean force) {
        if(force || countChanges) {
            if(force || bufferCounter.incrementAndGet() == bufferLimit) {
                bufferCounter.set(0);
                limitReached.accept(Collections.unmodifiableSet(entrySet()), force);
            }
        }
    }

    private final class CountingKeySet extends StandardKeySet {

        @Override
        public boolean removeIf(Predicate<? super K> filter) {
            Objects.requireNonNull(filter);
            boolean removed = false;
            Iterator<K> iterator = iterator();
            while (iterator.hasNext()) {
                if (filter.test(iterator.next())) {
                    iterator.remove();
                    removed = true;
                    processReachedLimit(false);
                }
            }
            return removed;
        }
    }

    private final class CountingValues extends StandardValues {

        @Override
        public boolean removeIf(Predicate<? super V> filter) {
            Objects.requireNonNull(filter);
            boolean removed = false;
            Iterator<V> iterator = iterator();
            while (iterator.hasNext()) {
                if (filter.test(iterator.next())) {
                    iterator.remove();
                    removed = true;
                    processReachedLimit(false);
                }
            }
            return removed;
        }
    }
}
