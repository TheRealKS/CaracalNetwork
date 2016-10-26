package com.koens.caracalnetwork.info;

public class PlayerWrapper {

    private String name;
    private String uuid;
    private boolean AFK;
    private String world;

    public PlayerWrapper(String n, String u, boolean afk, String w) {
        this.name = n;
        this.uuid = u;
        this.AFK = afk;
        this.world = w;
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public boolean isAFK() {
        return AFK;
    }

    public String getWorld() {
        return world;
    }
}
