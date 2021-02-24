package com.mcgamer199.luckyblock.util;

import com.mcgamer199.luckyblock.api.base.ThrowingPredicate;
import com.mcgamer199.luckyblock.api.base.ThrowingRunnable;
import com.mcgamer199.luckyblock.api.base.ThrowingSupplier;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;

/**
 * Удобная рефлекция объекта.
 * При создании указывайте объект:
 * <p>
 * MyObject object = new MyObject(Object object);
 * Так, теперь это новая версия! 1.1
 */
public class MyObject {

    private final Map<String, Field> fieldCache = new TreeMap<>();

    private static final byte FLAG_ACCESS = 1;
    private static final byte FLAG_FINAL = 1 << 1;

    private static final Field modifiers = unchecked(() -> Field.class.getDeclaredField("modifiers"));

    private final Object object;
    private final Class<?> clazz;

    /**
     * Получаем из готового объекта
     */
    public MyObject(Object object) {
        this.object = object;
        this.clazz = object.getClass();
    }

    public MyObject(Object object, Class<?> clazz) {
        this.object = object;
        this.clazz = clazz;
    }

    /**
     * Получаем из класса
     */
    public MyObject(Class<?> clazz) {
        this.object = null;
        this.clazz = clazz;
    }

    public static MyObject wrap(Object player) {
        return new MyObject(player);
    }

    public static MyObject wrap(Class<?> clazz) {
        return new MyObject(clazz);
    }

    private Field findField(String name) throws NoSuchFieldException {
        Field field = fieldCache.get(name);

        if(field != null) {
            return field;
        }

        Class<?> c = this.clazz;

        do {
            try {
                field = c.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {}
            if (field != null) {
                break;
            }
        }
        while ((c = c.getSuperclass()) != null);

        if (field == null) {
            throw new NoSuchFieldException(name);
        }

        fieldCache.put(name, field);

        return field;
    }

    /**
     * Получай переменные.
     * Не нужно беспокоиться о приватности пеменной!
     * Использование:
     * <p>
     * object.getField("temp");
     * где:
     * * "temp" - название переменной
     *
     * @param name название переменной
     * @return MyObject этой переменной (ее можно получить через .getObject())
     */
    public MyObject getField(String name) {
        return unchecked(() -> {
            final Field field = this.findField(name);

            final byte flags = this.unaccessField(field, false);

            Object get;
            try {
                get = field.get(this.object);
            } finally {
                this.accessField(field, flags);
            }


            return new MyObject(get, get != null ? get.getClass() : field.getType());
        });
    }

    private void accessField(Field field, byte flags) throws IllegalAccessException {
        this.accessObject(field, flags);

        if ((flags & FLAG_FINAL) != 0 && (field.getModifiers() & Modifier.FINAL) == 0) {
            final byte flagsModifiers = this.unaccessObject(modifiers);
            modifiers.setInt(field, field.getModifiers() | Modifier.FINAL);
            this.accessObject(modifiers, flagsModifiers);
        }
    }

    private byte unaccessField(Field field, boolean unfinal) throws IllegalAccessException {
        byte flags = this.unaccessObject(field);

        if (unfinal && (field.getModifiers() & Modifier.FINAL) != 0) {
            final byte flagsModifiers = this.unaccessObject(modifiers);
            modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            this.accessObject(modifiers, flagsModifiers);
            flags |= FLAG_FINAL;
        }

        return flags;
    }

    private void accessObject(AccessibleObject object, byte flags) {
        if ((flags & FLAG_ACCESS) != 0 && object.isAccessible()) {
            object.setAccessible(false);
        }
    }

    private byte unaccessObject(AccessibleObject object) {
        byte flags = 0;
        if (!object.isAccessible()) {
            object.setAccessible(true);
            flags |= FLAG_ACCESS;
        }
        return flags;
    }

    /**
     * Установить значение переменной
     *
     * @param name  имя переменной
     * @param value значение
     */
    public void setField(String name, Object value) {
        unchecked(() -> {
            final Field field = this.findField(name);

            final byte flags = this.unaccessField(field, true);

            try {
                field.set(this.object, value instanceof MyObject ? ((MyObject) value).getObject() : value);
            } finally {
                this.accessField(field, flags);
            }
        });
    }

    /**
     * Вызывай методы.
     * Не нужно беспокоится о приватности метода!
     * Не нужно беспокоится о аргументах метода,
     * моя система сама все кастит и т.п.
     * Если этот метод функция, то вернет значение, иначе null.
     * Использование:
     * <p>
     * object.invokeMethod("exetute", true, Integer.forNumber(10));
     * где:
     * * "exetute" - название метода
     * * true - (первый аргумент метода, boolean значение)
     * * Integer.forNumber(10) - (второй аргумент метода) пример того, что неважно, что указывать (int или Integer)
     *
     * @param name название метода
     * @param args аргументы (Можно указывать сразу MyObject, оно само достанет)
     * @return если этот метод функция, то вернет что-то, иначе null
     */
    public MyObject invokeMethod(String name, Object... args) {
        return unchecked(() -> {
            this.fixArgs(args);
            Method method = this.findMethod(name, args);

            final byte flags = this.unaccessObject(method);

            Object returnObject;
            try {
                returnObject = method.invoke(this.object, args);
            } finally {
                this.accessObject(method, flags);
            }

            return new MyObject(returnObject, returnObject != null ? returnObject.getClass() : method.getReturnType());
        });
    }

    @SuppressWarnings("ConstantConditions")
    public MyObject newInstance(Object... args) {
        return unchecked(() -> {
            this.fixArgs(args);
            Constructor<?> target = findConstructor(args);
            final byte flags = this.unaccessObject(target);

            Object instance;
            try{
                instance = target.newInstance(args);
            } finally {
                this.accessObject(target, flags);
            }

            return new MyObject(instance, instance != null ? instance.getClass() : target.getDeclaringClass());
        });
    }

    private Method findMethod(String name, Object[] args) throws NoSuchMethodException {
        Class<?> c = this.clazz;
        Method method = null;

        one:
        do {
            two:
            for (Method m : c.getDeclaredMethods()) {
                if (!m.getName().equals(name)) {
                    continue;
                }
                if (m.getParameterCount() != args.length) {
                    continue;
                }

                for (int i = 0; i < m.getParameterCount(); i++) {
                    if (args[i] != null) {
                        if (!m.getParameterTypes()[i].isInstance(args[i])) {
                            continue two;
                        }
                    }
                }
                method = m;
                break one;
            }
        }
        while ((c = c.getSuperclass()) != null);

        if (method == null) {
            throw new NoSuchMethodException(name + " (" + Arrays.toString(args) + ")");
        }

        return method;
    }

    private Constructor<?> findConstructor(Object[] args) throws NoSuchMethodException {
        Class<?> current = this.clazz;
        Constructor<?> constructor = null;

        do {
            constructorSkip:
            for(Constructor<?> declaredConstructor : current.getDeclaredConstructors()){
                if(declaredConstructor.getParameterTypes().length != args.length) continue;

                for(int i = 0; i < declaredConstructor.getParameterCount(); i++){
                    if(!declaredConstructor.getParameterTypes()[i].isInstance(args[i])) continue constructorSkip;
                }

                constructor = declaredConstructor;
            }
        } while((current = current.getSuperclass()) != null);

        if(constructor == null) {
            throw new NoSuchMethodException(String.format("%s.<init> (%s)", this.clazz.getName(), Arrays.toString(args)));
        }

        return constructor;
    }

    /**
     * Получить объект, с которым работаем
     */
    @SuppressWarnings("unchecked")
    public <T> T getObject(Class<T> cast) {
        return (T) object;
    }

    /**
     * Получить объект, с которым работаем
     */
    @SuppressWarnings("unchecked")
    public <T> T getObject() {
        return (T) object;
    }

    private void fixArgs(Object[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof MyObject) {
                args[i] = ((MyObject) args[i]).getObject();
            }
        }
    }


    /**
     * Игрорировать проверяемое исключение (если проверяемое исключение возникнет, оно будет в обертке RuntimeException)
     *
     * @param supplier code
     * @param <T>      type result
     * @return result
     */
    public static <T> T unchecked(ThrowingSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            doThrow0(e);
            throw new AssertionError(); // до сюда код не дойдет
        }
    }

    /**
     * Игрорировать проверяемое исключение (если проверяемое исключение возникнет, оно будет в обертке RuntimeException)
     *
     * @param runnable code
     */
    public static void unchecked(ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            doThrow0(e);
            throw new AssertionError(); // до сюда код не дойдет
        }
    }

    /**
     * Игрорировать проверяемое исключение (если проверяемое исключение возникнет, оно будет в обертке RuntimeException)
     *
     * @param predicate code
     */
    public static <T> Predicate<T> unchecked(ThrowingPredicate<T> predicate) {
        return t -> {
            try {
                return predicate.test(t);
            } catch (Exception e) {
                doThrow0(e);
                throw new AssertionError(); // до сюда код не дойдет
            }
        };
    }

    @SuppressWarnings("unchecked")
    private static <E extends Exception> void doThrow0(Exception e) throws E {
        throw (E) e;
    }
}
