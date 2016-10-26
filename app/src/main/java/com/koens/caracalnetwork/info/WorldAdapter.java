package com.koens.caracalnetwork.info;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.List;

public class WorldAdapter extends RecyclerView.Adapter<WorldAdapter.ViewHolder> {

    private List<WorldWrapper> data;
    private Context context;
    private ColorGenerator generator = ColorGenerator.MATERIAL;

    public WorldAdapter(List<WorldWrapper> d, Context ax) {
        this.data = d;
        this.context = ax;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView txt;
        public ViewHolder(View v) {
            super(v);
            this.icon = (ImageView) v.findViewById(R.id.world_icon);
            this.txt = (TextView) v.findViewById(R.id.world_text);
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.world_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WorldWrapper dt = data.get(position);
        int color = generator.getRandomColor();
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(Integer.toString(dt.getPlayers()), color);
        holder.icon.setImageDrawable(drawable);
        holder.txt.setText("players in " + dt.getName());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
