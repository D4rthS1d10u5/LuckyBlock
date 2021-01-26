package com.mcgamer199.luckyblock.util;

import com.google.common.collect.Lists;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class ChatComponentUtils {

    private static final HashMap<String, String> mapFormatting = loadFomatting();
    private static final HashMap<String, String> mapColor = loadColor();
    private static final Pattern color = Pattern.compile("[&§].");

    private static HashMap<String, String> loadFomatting() {
        HashMap<String, String> map = new HashMap<>();
        map.put("obfuscated", "k");
        map.put("bold", "l");
        map.put("strikethrough", "m");
        map.put("underlined", "n");
        map.put("italic", "o");
        return map;
    }

    private static HashMap<String, String> loadColor() {
        HashMap<String, String> map = new HashMap<>();
        map.put("black", "0");
        map.put("dark_blue", "1");
        map.put("dark_green", "2");
        map.put("dark_aqua", "3");
        map.put("dark_red", "4");
        map.put("dark_purple", "5");
        map.put("gold", "6");
        map.put("gray", "7");
        map.put("dark_gray", "8");
        map.put("blue", "9");
        map.put("green", "a");
        map.put("aqua", "b");
        map.put("red", "c");
        map.put("light_purple", "d");
        map.put("yellow", "e");
        map.put("white", "f");
        return map;
    }

    /**
     * Достать из серилизированого ChatComponent'a текст сохраняя цвета
     *
     * @param json json дата
     * @return текст
     */
    public static String jsonToText(String json) {
        if (json.length() < 33) {
            return "";
        }
        if (json.contains("{\"extra\":[{\"text\":\"")) {
            json = json.substring(10, json.length() - 12);
        }
        StringBuilder newLine = new StringBuilder();
        char[] value = json.toCharArray();

        int posStart = -1;
        StringBuilder read = new StringBuilder();
        boolean readText = false;
        for (int pos = 0; pos < value.length; pos++) {
            if (value[pos] == '\"') {
                if (posStart == -1) {
                    posStart = pos + 1;
                } else {
                    char[] temp = new char[pos - posStart];
                    System.arraycopy(value, posStart, temp, 0, temp.length);
                    posStart = -1;

                    String text = new String(temp);
                    if (readText) {
                        newLine.append(newLine.length() > 0 ? "§r" : "").append(read).append(text);
                        read = new StringBuilder();
                        readText = false;
                    } else if (text.equals("text")) {
                        readText = true;
                    } else {
                        String color = mapColor.get(text);
                        if (color != null) {
                            read.insert(0, "§" + color);
                        } else {
                            String type = mapFormatting.get(text);
                            if (type != null) {
                                read.append("§").append(type);
                            }
                        }
                    }
                }
            }
        }
        return StringEscapeUtils.unescapeJava(newLine.toString());
    }

    /**
     * Преобразовать текст в json, как бы это сделал ChatComponent
     *
     * @param text текст
     * @return json
     */
    public static String textToJson(String text) {
        return "{\"extra\":[{\"text\":\"" + JSONObject.escape(text) + "\"}],\"text\":\"\"}";
    }

    /**
     * Удалить цвета
     *
     * @param text текст
     * @return текст без цвета
     */
    public static String removeColors(String text) {
        return color.matcher(text).replaceAll("");
    }

    /**
     * Фиксануть компонент на переносы
     *
     * @param component компонент
     */
    public static void fixComponent(BaseComponent component) {
        if (component.getExtra() != null) {
            component.getExtra().forEach(ChatComponentUtils::fixComponent);
        }
        if (component instanceof TextComponent) {
            TextComponent text = (TextComponent) component;
            if (StringUtils.contains(text.getText(), '§')) {
                BaseComponent[] components = TextComponent.fromLegacyText(text.getText());
                List<BaseComponent> extra = text.getExtra();
                if (extra == null) {
                    extra = Lists.newArrayList(components);
                } else {
                    extra.addAll(0, Lists.newArrayList(components));
                }
                text.setText("");
                text.setExtra(extra);
            }
        }
    }

    public static void fixVisibleColors(BaseComponent component) {
        List<BaseComponent> extra = component.getExtra();
        if (extra != null) {
            extra.forEach(ChatComponentUtils::fixVisibleColors);
        }
        HoverEvent hoverEvent = component.getHoverEvent();
        if (hoverEvent != null) {
            Arrays.stream(hoverEvent.getValue()).forEach(ChatComponentUtils::fixVisibleColors);
        }
        if (component instanceof TextComponent) {
            TextComponent textComponent = (TextComponent) component;
            textComponent.setText(ChatComponentUtils.fixVisibleColors(textComponent.getText()));
        }
    }

    public static String fixVisibleColors(String text) {
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length - 1; i++) {
            char c = chars[i];
            if (c == '§') {
                chars[i + 1] = replaceColor0(chars[i + 1]);
            }
        }
        return new String(chars);
    }

    private static char replaceColor0(char c) {
        switch (c) {
            case 'f':
                return '0';
            case 'a':
                return '2';
            case 'b':
                return '3';
            case 'e':
                return '6';
            default:
                return c;
        }
    }
}
