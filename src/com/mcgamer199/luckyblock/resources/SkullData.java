package com.mcgamer199.luckyblock.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SkullData {
    public static final List<SkullData> skulls = new ArrayList();
    public static final SkullData DEVIL = new SkullData("c659cdd4-e436-4977-a6a7-d5518ebecfbb", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWFlMzg1NWY5NTJjZDRhMDNjMTQ4YTk0NmUzZjgxMmE1OTU1YWQzNWNiY2I1MjYyN2VhNGFjZDQ3ZDMwODEifX19", true, "BOSS", "Devil");
    public static final SkullData KING = new SkullData("9c64b25f-0d6d-4b88-a84e-b716dc54c1a5", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWUzNDY3NjNiYzJkODU0MjVjM2QyMDhiMzBmY2I1YjdjYmM0NzYzODg2MmYyOGI1YWE3YWYxZWNlNTJjMjIifX19", true, "BOSS", "King");
    public static final SkullData GRU = new SkullData("f55d2fa3-fa47-4c6e-9dc3-1fef4ae2eaf6", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU3MDhjYjc3YjIyNTRlNzk3ZjJiODcxNmI0YjFiZjU4YzJjYjM3YmQzODI2ZGNiMThiZjU0ZGM5Njk5MzYifX19", true, "BOSS", "Gru");
    public static final SkullData DOCTOR = new SkullData("f703e558-96e3-4623-bdb1-cb408f380041", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYThmMTZmZTI4NGU3NDlkMzdiN2ExZTNmZmExYmNmZDMwNTJmM2E1MWM4MjY0MGFjODg3YWRlMTQ1MGQ0In19fQ==", true, "BOSS", "Doctor");
    public static final SkullData MEGATRON = new SkullData("8870f492-7926-48ad-8189-77a420afd237", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjNjODkxYmQ0ZjI3YTFkNDE5OTE0NjFmMzk0NzZhMWJmM2QyZWYyMjY2YTRlZDlkZTJlNjVjMzg5YjgzIn19fQ==", true, "BOSS", "Megatron");
    public static final SkullData KNIGHT = new SkullData("f8336e18-6a34-4442-a718-ce20dfe2b5e0", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDYyNTJhN2RkNDMwMjBlYzI3Y2Y3MTU4ZDFmODgzNzc0NDJlMmRhNGRlN2VhYTU0NWZjMjY1Zjc2YTkzNTkifX19", true, "BOSS", "Knight");
    public static final SkullData FOOTBALL_PLAYER_1 = new SkullData("37f198b8-7bdf-4f39-a350-c82c518be70b", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjg3NTg2NTAyMDNhZWRmMDY4OGZlYjRiZWY1ZjIyNzgyMzZiNWUxN2QyNmRjNTdkMTVhMTk2OTIyODQ3ZjExIn19fQ==", true, "FP", "Player1");
    public static final SkullData FOOTBALL_PLAYER_2 = new SkullData("c33a8b2f-3a67-4ba1-a7f4-a9bbf2b56c96", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzdhY2Q0ZDZlNTU0NzEwMWY2YmJlNTgyZTUzNDhlN2UxNmRjNGU3YjcxNDQ1NjY0OWNkYzIyZTNiNTQ2YiJ9fX0=", true, "FP", "Player2");
    private String tag;
    private String id;
    private String data;
    private String name;

    public SkullData(String id, String data) {
        this(id, data, false);
    }

    public SkullData(String id, String data, boolean save) {
        this(id, data, save, (String)null);
    }

    public SkullData(String id, String data, boolean save, String tag) {
        this(id, data, save, tag, (String)null);
    }

    public SkullData(String id, String data, boolean save, String tag, String name) {
        this.id = id;
        this.data = data;
        this.tag = tag;
        if (save) {
            skulls.add(this);
        }

    }

    public String getId() {
        return this.id;
    }

    public String getData() {
        return this.data;
    }

    public String getName() {
        return this.name;
    }

    public String getTag() {
        return this.tag;
    }

    public static SkullData getRandomSkullData(String tag) {
        Random rnd = new Random();
        List<SkullData> sk = new ArrayList();

        for(int x = 0; x < skulls.size(); ++x) {
            if (tag != null) {
                SkullData s = (SkullData)skulls.get(x);
                if (s.tag != null && tag.equalsIgnoreCase(s.tag)) {
                    sk.add(s);
                }
            } else {
                sk.add((SkullData)skulls.get(x));
            }
        }

        return (SkullData)sk.get(rnd.nextInt(sk.size()));
    }

    public static SkullData getSkullByName(String name) {
        for(int x = 0; x < skulls.size(); ++x) {
            SkullData sd = (SkullData)skulls.get(x);
            if (sd.name != null && sd.name.equalsIgnoreCase(name)) {
                return sd;
            }
        }

        return null;
    }
}
