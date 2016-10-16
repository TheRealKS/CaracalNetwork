package com.koens.caracalnetwork.info;

public class PlayerWrapper {

    private String name;
    private String uuid;
    private boolean AFK;

    public PlayerWrapper(String n, String u, boolean afk) {
        this.name = n;
        this.uuid = u;
        this.AFK = afk;
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
}
