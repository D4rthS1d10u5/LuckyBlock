package com.mcgamer199.luckyblock.api.book;

import com.mcgamer199.luckyblock.tellraw.RawText;

public class BookPage {
    private final RawText[] texts;

    public BookPage(RawText... texts) {
        this.texts = texts;
    }

    public RawText[] getTexts() {
        return this.texts;
    }

    public void addText(RawText text) {
        for (int x = 0; x < this.texts.length; ++x) {
            if (this.texts[x] == null) {
                this.texts[x] = text;
                break;
            }
        }

    }

    public void removeText(int place) {
        if (this.texts[place] != null) {
            this.texts[place] = null;
        }

    }
}

