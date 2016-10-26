package com.koens.caracalnetwork.info;

public class WorldWrapper {

    private String name;
    private int players;

    public WorldWrapper(String n, int p) {
        this.name = n;
        this.players = p;
    }

    public String getName() {
        return name;
    }

    public int getPlayers() {
        return players;
    }
}
