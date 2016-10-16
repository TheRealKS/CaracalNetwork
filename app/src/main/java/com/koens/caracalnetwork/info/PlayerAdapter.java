
package com.koens.caracalnetwork.info;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ViewHolder> {

    private List<PlayerWrapper> data;
    private Context context;

    public PlayerAdapter(List<PlayerWrapper> data, Context applicationContext) {
        this.data = data;
        this.context = applicationContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.player_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PlayerWrapper playerData = data.get(position);
        holder.name.setText(playerData.getName());
        holder.uuid.setText(playerData.getUuid());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView head;
        private TextView name;
        private TextView uuid;
        public ViewHolder(View v) {
            super(v);
            this.head = (ImageView) v.findViewById(R.id.player_head);
            this.name = (TextView) v.findViewById(R.id.player_name);
            this.uuid = (TextView) v.findViewById(R.id.player_uuid);
        }
    }


}