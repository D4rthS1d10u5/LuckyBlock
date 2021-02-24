package com.mcgamer199.luckyblock.util;

import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Удобные таски
 * @author Lokha
 */
public class Scheduler {

    private static final Plugin plugin = LuckyBlockPlugin.instance;
    private static final BukkitScheduler bukkitScheduler = Bukkit.getScheduler();
    private final SchedulerRunnable schedule;

    public Scheduler(Runnable runnable) {
        this.schedule = new SchedulerRunnable(runnable);
    }

    public Scheduler(Consumer<Scheduler> consumer) {
        this.schedule = new SchedulerRunnable(() -> consumer.accept(this));
    }

    /**
     * Создать таск
     *
     * @param runnable код
     * @return таск
     */
    public static Scheduler create(Runnable runnable) {
        return new Scheduler(runnable);
    }

    /**
     * Создать таск
     *
     * @param consumer код
     * @return таск
     */
    public static Scheduler create(Consumer<Scheduler> consumer) {
        return new Scheduler(consumer);
    }

    /**
     * Запустить таск
     *
     * @param runnable таск
     * @return bukkit task
     */
    public static BukkitTask run(Runnable runnable) {
        return bukkitScheduler.runTask(plugin, runnable);
    }

    /**
     * Запустить таск асинхронно
     *
     * @param runnable таск
     * @return bukkit task
     */
    public static BukkitTask runAsync(Runnable runnable) {
        return bukkitScheduler.runTaskAsynchronously(plugin, runnable);
    }

    /**
     * Выполнить позже
     *
     * @param runnable таск
     * @param delay    через сколько выполнить
     * @param time     тип времени
     * @return bukkit task
     */
    public static BukkitTask later(Runnable runnable, long delay, TimeUnit time) {
        return bukkitScheduler.runTaskLater(plugin, runnable, time.toSeconds(delay) * 20);
    }

    /**
     * Запустить таймер
     *
     * @param runnable таск
     * @param delay    через сколько выполнить
     * @param period   через сколько повторять
     * @param time     тип времени
     * @return bukkit task
     */
    public static BukkitTask timer(Runnable runnable, long delay, long period, TimeUnit time) {
        return bukkitScheduler.runTaskTimer(plugin, runnable, time.toSeconds(delay) * 20, time.toSeconds(period) * 20);
    }

    /**
     * Выполнить позже
     *
     * @param runnable таск
     * @param delay    через сколько выполнить
     * @return bukkit task
     */
    public static BukkitTask later(Runnable runnable, long delay) {
        return bukkitScheduler.runTaskLater(plugin, runnable, delay);
    }

    /**
     * Запустить таймер
     *
     * @param runnable таск
     * @param delay    через сколько выполнить
     * @param period   через сколько повторять тиков
     * @return bukkit task
     */
    public static BukkitTask timer(Runnable runnable, long delay, long period) {
        return bukkitScheduler.runTaskTimer(plugin, runnable, delay, period);
    }

    /**
     * Запустить таймер
     *
     * @param runnable таск
     * @param delay    через сколько выполнить
     * @param period   через сколько повторять тиков
     * @return bukkit task
     */
    public static BukkitTask timerAsync(Runnable runnable, long delay, long period) {
        return bukkitScheduler.runTaskTimerAsynchronously(plugin, runnable, delay, period);
    }

    /**
     * Останавливает таймер.
     *
     * @param runnableID - айди таймера, который будем останавливать.
     */
    public static void stopTimer(int runnableID) {
        bukkitScheduler.cancelTask(runnableID);
    }

    /**
     * Установить условие выполенния
     *
     * @param predicate условие выполнения
     * @return this
     */
    public Scheduler predicate(PredicateZero predicate) {
        this.schedule.predicate = predicate;
        return this;
    }

    /**
     * Установить количество срабатываний
     *
     * @param count сколько раз нужно таймеру сработать
     * @return this
     */
    public Scheduler count(int count) {
        this.schedule.count = count;
        return this;
    }

    /**
     * Задать логику отмены таска
     * @param command код, который должен выполнится,
     *                когда таск был завершен
     */
    public Scheduler onCancel(Runnable command) {
        this.schedule.onCancel = command;
        return this;
    }

    /**
     * Запустить таймер
     *
     * @param delay  через сколько первое срабатывание
     * @param period период между запусками
     * @return this
     */
    public Scheduler timer(long delay, long period, TimeUnit time) {
        this.schedule.runTaskTimer(plugin, toTicks(delay, time), toTicks(period, time));
        return this;
    }

    /**
     * Запустить таймер
     *
     * @param delay  через сколько первое срабатывание
     * @param period период между запусками
     * @return this
     */
    public Scheduler timer(long delay, long period) {
        this.schedule.runTaskTimer(plugin, delay, period);
        return this;
    }

    /**
     * Запустить таймер асинхронно
     *
     * @param delay  через сколько первое срабатывание
     * @param period период между запусками
     * @return this
     */
    public Scheduler timerAsync(long delay, long period, TimeUnit time) {
        this.schedule.runTaskTimerAsynchronously(plugin, toTicks(delay, time), toTicks(period, time));
        return this;
    }

    public Scheduler timerAsync(long delay, long period) {
        this.schedule.runTaskTimerAsynchronously(plugin, delay, period);
        return this;
    }

    /**
     * Запустить таск позже
     *
     * @param delay через сколько срабатывание
     * @return this
     */
    public Scheduler later(long delay, TimeUnit time) {
        this.schedule.runTaskLater(plugin, toTicks(delay, time));
        return this;
    }

    /**
     * Запустить таск позже
     *
     * @param delay через сколько срабатывание
     * @return this
     */
    public Scheduler later(long delay) {
        this.schedule.runTaskLater(plugin, delay);
        return this;
    }

    /**
     * Запустить таск
     *
     * @return this
     */
    public Scheduler run() {
        this.schedule.runTask(plugin);
        return this;
    }

    public static long toTicks(long value, TimeUnit time) {
        return time.toSeconds(value) * 20;
    }

    /**
     * Выключить таск
     */
    public void cancel() {
        if (!this.schedule.isCancelled()) {
           cancelTask(this.schedule);
        }
    }

    /**
     * Остановить таск с try catch IGNORE<br>
     * (Иногда при остановлении task'ка, выдает Exception)
     * @param task таск
     */
    public static void cancelTask(@Nullable BukkitTask task) {
        if(task != null) {
            try {
                task.cancel();
            } catch(Exception ignore) {}
        }
    }

    /**
     * Остановить таск с try catch IGNORE<br>
     * (Иногда при остановлении task'ка, выдает Exception)
     * @param task таск
     */
    public static void cancelTask(@Nullable BukkitRunnable task) {
        if(task != null) {
            try {
                task.cancel();
            } catch(Exception ignore) {}
        }
    }

    /**
     * Остановить таск с try catch IGNORE<br>
     * (Иногда при остановлении task'ка, выдает Exception)
     * @param task таск
     */
    public static void cancelTask(@Nullable Scheduler task) {
        if(task != null) {
            try {
                task.cancel();
            } catch(Exception ignore) {}
        }
    }

    public static void cancelTask(int taskId) {
        Bukkit.getScheduler().cancelTask(taskId);
    }

    public class SchedulerRunnable extends BukkitRunnable {

        private final Runnable runnable;
        private PredicateZero predicate;
        private int count = -1;
        private int i = 0;
        private boolean timer = false;
        private Runnable onCancel;

        SchedulerRunnable(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            this.checkCount();
            if (this.hasPredicate()) {
                runnable.run();
                if (!timer) {
                    Scheduler.this.cancel();
                }
            } else {
                Scheduler.this.cancel();
            }
        }

        private boolean hasPredicate() {
            return predicate == null || predicate.test();
        }

        private void checkCount() {
            if (timer && count != -1 && ++i >= count) {
                Scheduler.this.cancel();
            }
        }

        @Override
        public synchronized BukkitTask runTaskTimer(Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
            this.timer = true;
            return super.runTaskTimer(plugin, delay, period);
        }

        @Override
        public synchronized BukkitTask runTaskTimerAsynchronously(Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
            this.timer = true;
            return super.runTaskTimerAsynchronously(plugin, delay, period);
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            onCancel.run();
            super.cancel();
        }

        public Runnable getRunnable() {
            return runnable;
        }
    }

    public SchedulerRunnable getTask() {
        return schedule;
    }

    /**
     * Predicate без параметров
     */
    public interface PredicateZero {

        boolean test();
    }
}
