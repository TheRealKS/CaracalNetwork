package com.koens.caracalnetwork.info;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter {

    private List<PlayerWrapper> data;
    private Context context;

    public PlayerAdapter(List<PlayerWrapper> data, Context applicationContext) {
        this.data = data;
        this.context = applicationContext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

    }

}
