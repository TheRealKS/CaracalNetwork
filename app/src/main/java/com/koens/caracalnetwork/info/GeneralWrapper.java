package com.koens.caracalnetwork.info;

public class GeneralWrapper {

    private int value;
    private boolean alternateColor;
    private int viewtype;

    public GeneralWrapper(int v, boolean a, int vt) {
        this.value = v;
        this.alternateColor = a;
        this.viewtype = vt;
    }

    public int getValue() {
        return value;
    }
    public boolean getAlternateColor() {
        return alternateColor;
    }
    public int getViewType() {
        return viewtype;
    }
}
