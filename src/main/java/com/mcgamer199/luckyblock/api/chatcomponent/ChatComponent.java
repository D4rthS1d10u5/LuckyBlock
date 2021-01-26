package com.mcgamer199.luckyblock.api.chatcomponent;

import net.md_5.bungee.api.chat.*;
import org.bukkit.command.CommandSender;

public class ChatComponent {

    private BaseComponent component = new TextComponent();

    public ChatComponent() {}

    public ChatComponent(String text) {
        this.addText(text);
    }

    /**
     * Разделить строку, если она превышает лимит символов
     * @param string
     * @param limit  количество символов
     * @return
     */
    public static String[] stack(String string, int limit) {
        if(string == null || string.length() <= limit || limit <= 0) {
            return new String[]{string};
        }

        char[] value = string.toCharArray();
        String[] result = new String[value.length / limit + 1];


        for(int pos = 0; pos < result.length; pos++) {
            int size = Math.min(limit, value.length - pos * limit);
            char[] line = new char[size];
            System.arraycopy(value, pos * limit, line, 0, size);
            result[pos] = new String(line);
        }
        return result;
    }

    /**
     * Дорбавить текст.
     * Если на него навести, то выведется текст (перенос \n)
     * а если нажать, то выполнится команда
     * @param text отображаемый текст
     * @param hover действие при наведении
     * @param command действие при нажатии
     */
    @Deprecated
    public ChatComponent addTextClickAndHover(String text, String hover, String command) {
        return this.addText(text, Hover.show_text, hover, Click.run_command, command);
    }

    /**
     * Добавить текст
     * @param text       текст
     * @param hover      что делать, если навел на текст
     * @param valueHover значение, когда навел на текст
     * @param click      что делать, если кликнул по тексту
     * @param valueClick значение, когда кликгул по тексту
     * @return this
     */
    public ChatComponent addText(String text, Hover hover, Object valueHover, Click click, Object valueClick) {
        this.component.addExtra(this.build(text, hover, valueHover, click, valueClick));
        return this;
    }

    public ChatComponent addLocalizedText(String text) {
        this.component.addExtra(new TranslatableComponent(text));
        return this;
    }

    private BaseComponent build(String text, Hover hover, Object valueHover, Click click, Object valueClick) {
        BaseComponent component = new TextComponent(text);
        if(hover != null) {
            HoverEvent hoverEvent = new HoverEvent(hover.action, new BaseComponent[]{new TextComponent(hover.get(valueHover))});
            component.setHoverEvent(hoverEvent);
        }

        if(click != null) {
            ClickEvent clickEvent = new ClickEvent(click.action, click.get(valueClick));
            component.setClickEvent(clickEvent);
        }
        return component;
    }

    /**
     * Добавить текст в начало
     * @param text       текст
     * @param hover      что делать, если навел на текст
     * @param valueHover значение, когда навел на текст
     * @param click      что делать, если кликнул по тексту
     * @param valueClick значение, когда кликгул по тексту
     * @return this
     */
    public ChatComponent addFirstText(String text, Hover hover, Object valueHover, Click click, Object valueClick) {
        BaseComponent component = this.build(text, hover, valueHover, click, valueClick);
        component.addExtra(this.component);
        this.component = component;
        return this;
    }

    /**
     * Добавить текст в начало
     * @param text текст
     * @return this
     */
    public ChatComponent addFirstText(String text) {
        return this.addFirstText(text, null, null, null, null);
    }

    /**
     * Добавить текст
     * @param text       текст
     * @param hover      что делать, если навел на текст
     * @param valueHover значение, когда навел на текст
     * @return this
     */
    public ChatComponent addText(String text, Hover hover, Object valueHover) {
        this.addText(text, hover, valueHover, null, null);
        return this;
    }

    /**
     * Добавить текст
     * @param text       текст
     * @param click      что делать, если кликнул по тексту
     * @param valueClick значение, когда кликгул по тексту
     * @return this
     */
    public ChatComponent addText(String text, Click click, Object valueClick) {
        this.addText(text, null, null, click, valueClick);
        return this;
    }

    /**
     * Добавить текст
     * @param text текст
     */
    public ChatComponent addText(String text) {
        this.addText(text, null, null, null, null);
        return this;
    }

    /**
     * Отправить сообщение игроку
     * @param player
     */
    public void send(CommandSender player) {
        player.sendMessage(this.component);
    }

    public BaseComponent getBaseComponent() {
        return component;
    }
}

