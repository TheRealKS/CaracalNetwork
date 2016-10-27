package com.koens.caracalnetwork.info;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.List;

public class OverviewAdapter extends RecyclerView.Adapter<OverviewAdapter.ViewHolder> {

    private List<GeneralWrapper> data;
    private Context context;
    private boolean up;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txt;
        public ImageView img;
        public LinearLayout ll;
        public ViewHolder(View v) {
            super(v);
            this.txt = (TextView) v.findViewById(R.id.general_item_txt);
            this.img = (ImageView) v.findViewById(R.id.general_info_item_icon);
            this.ll = (LinearLayout) v.findViewById(R.id.ll);
        }
    }

    public OverviewAdapter(List<GeneralWrapper> dataset, Context c) {
        this.data = dataset;
        this.context = c;
    }


    @Override
    public OverviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.general_info_item, parent, false);
        return new OverviewAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (data.get(position).getAlternateColor()) {
            if (data.get(position).getValue() == 1) {
                holder.txt.setText("Server is up");
                holder.img.setBackground(context.getDrawable(R.drawable.circle_green));
                holder.img.setImageDrawable(context.getDrawable(R.drawable.ic_done_black_24dp));
                up = true;
            } else {
                holder.txt.setText("Server is down");
                holder.img.setBackground(context.getDrawable(R.drawable.divider_red));
                holder.img.setImageDrawable(context.getDrawable(R.drawable.ic_close_black_24dp));
                up = false;
            }
        } else {
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(Integer.toString(data.get(position).getValue()), R.color.colorAccent);
            holder.img.setImageDrawable(drawable);
            if (position == 1) {
                holder.txt.setText("players are online");
            } else if (position == 2) {
                holder.txt.setText("worlds on this server");
            }
        }
        final GeneralWrapper wrapper = data.get(position);
        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wrapper.getViewType() > 0) {
                    Intent intent = new Intent(context, GeneralItemActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("SOURCE", wrapper.getViewType());
                    intent.putExtra("SERVERUP", up);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
