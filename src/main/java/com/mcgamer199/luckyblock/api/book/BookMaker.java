package com.mcgamer199.luckyblock.api.book;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.mcgamer199.luckyblock.tellraw.EnumTextAction;
import com.mcgamer199.luckyblock.tellraw.ItemText;
import com.mcgamer199.luckyblock.tellraw.RawText;
import com.mcgamer199.luckyblock.tellraw.TextAction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BookMaker {
    public BookMaker() {
    }

    public static BookMaker.IBook createNewBook(String author, String title, List<BookPage> pages) {
        BookMaker.IBook book = new BookMaker.IBook();
        book.pages = pages;
        book.author = author;
        book.title = title;
        return book;
    }

    public static BookMaker.IBook createNewBook(String author, String title, BookPage... pages) {
        BookMaker.IBook book = new BookMaker.IBook();
        BookPage[] var7 = pages;
        int var6 = pages.length;

        for(int var5 = 0; var5 < var6; ++var5) {
            BookPage page = var7[var5];
            if (page != null) {
                book.pages.add(page);
            }
        }

        book.author = author;
        book.title = title;
        return book;
    }

    public static void giveBook(BookMaker.IBook book, Player player) {
        String cmd = "give " + player.getName() + " written_book 1 0 {";
        if (book.author != null) {
            cmd = cmd + "author:" + "\"" + book.author + "\"";
        }

        if (book.title != null) {
            cmd = cmd + ",title:" + "\"" + book.title + "\"";
        }

        if (book.pages != null) {
            cmd = cmd + ",pages:[";

            for(int x = 0; x < book.pages.size(); ++x) {
                BookPage t = (BookPage)book.pages.get(x);
                if (x > 0) {
                    cmd = cmd + ",";
                }

                cmd = cmd + "\"{";

                for(int i = 0; i < t.getTexts().length; ++i) {
                    RawText text = t.getTexts()[i];
                    if (text != null) {
                        if (i == 0) {
                            cmd = cmd + "\\\"text\\\":";
                        } else {
                            if (i == 1) {
                                cmd = cmd + ",\\\"extra\\\":[";
                            }

                            if (i == 1) {
                                cmd = cmd + "{\\\"text\\\":";
                            } else {
                                cmd = cmd + ",{\\\"text\\\":";
                            }
                        }

                        cmd = cmd + "\\\"" + text.getText() + "\\\"";
                        if (text.color != null) {
                            cmd = cmd + ",\\\"color\\\":\\\"" + text.color.name().toLowerCase() + "\\\"";
                        }

                        if (text.bold) {
                            cmd = cmd + ",\\\"bold\\\":\\\"true\\\"";
                        }

                        if (text.magic) {
                            cmd = cmd + ",\\\"obfuscated\\\":\\\"true\\\"";
                        }

                        if (text.strikethrough) {
                            cmd = cmd + ",\\\"strikethrough\\\":\\\"true\\\"";
                        }

                        if (text.underline) {
                            cmd = cmd + ",\\\"underlined\\\":\\\"true\\\"";
                        }

                        if (text.italic) {
                            cmd = cmd + ",\\\"italic\\\":\\\"true\\\"";
                        }

                        if (text.getActions() != null) {
                            for(int a = 0; a < text.getActions().length; ++a) {
                                if (text.getActions()[a] != null) {
                                    TextAction action = text.getActions()[a];
                                    if (action.getAction() == EnumTextAction.SHOW_ITEM) {
                                        if (action.value instanceof ItemStack) {
                                            cmd = cmd + ",\\\"" + action.getEvent().getName() + "\\\":{\\\"action\\\":\\\"" + action.getAction().getName() + "\\\",\\\"value\\\":\\\"" + ItemText.itemToString((ItemStack)action.value) + "\\\"}";
                                        }
                                    } else {
                                        cmd = cmd + ",\\\"" + action.getEvent().getName() + "\\\":{\\\"action\\\":\\\"" + action.getAction().getName() + "\\\",\\\"value\\\":\\\"" + action.value.toString() + "\\\"}";
                                    }
                                }
                            }
                        }

                        if (i > 0) {
                            cmd = cmd + "}";
                        }
                    }
                }

                if (t.getTexts().length > 1 && t.getTexts()[1] != null) {
                    cmd = cmd + "]";
                }

                cmd = cmd + "}\"";
            }

            cmd = cmd + "]";
        }

        cmd = cmd + "}";
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
    }

    public static class IBook {
        List<BookPage> pages = new ArrayList();
        private String author;
        private String title;

        IBook() {
        }

        void setPages(List<BookPage> pages) {
            this.pages = pages;
        }

        void setAuthor(String author) {
            this.author = author;
        }

        void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return this.author;
        }

        public String getTitle() {
            return this.title;
        }

        public List<BookPage> getACopyOfPages() {
            List<BookPage> pgs = new ArrayList();
            if (this.pages != null) {
                Iterator var3 = this.pages.iterator();

                while(var3.hasNext()) {
                    BookPage page = (BookPage)var3.next();
                    if (page != null) {
                        pgs.add(page);
                    }
                }
            }

            return pgs;
        }
    }
}
