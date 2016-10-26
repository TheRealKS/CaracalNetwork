
package com.koens.caracalnetwork.info;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ViewHolder> {

    private List<PlayerWrapper> data;
    private Context context;
    private static final String d = "-";


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
        holder.uuid.setText(formatUUID(playerData.getUuid()));
        holder.world.setText(playerData.getWorld());
        HeadInformation h = new HeadInformation(playerData.getName(), holder.head);
        getImages(h);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView head;
        private TextView name;
        private TextView uuid;
        private TextView world;
        public ViewHolder(View v) {
            super(v);
            this.head = (ImageView) v.findViewById(R.id.player_head);
            this.name = (TextView) v.findViewById(R.id.player_name);
            this.uuid = (TextView) v.findViewById(R.id.player_uuid);
            this.world = (TextView) v.findViewById(R.id.player_world);
        }
    }

    private String formatUUID(String rawUUID) {
        if (rawUUID.length() < 32) {
            return rawUUID;
        } else {
            String s = rawUUID.substring(0,8) + d + rawUUID.substring(8,12) + d + rawUUID.substring(12, 16) + d + rawUUID.substring(16, 20) + d + rawUUID.substring(20, rawUUID.length());
            return s;
        }
    }

    private void getImages(final HeadInformation data) {
        AsyncHttpClient client = new AsyncHttpClient();
            client.get("http://www.caracalnetwork.pe.hu/get_skin.php?name=" + data.getName(), new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    data.getHead().setImageBitmap(image);
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                }
            });
    }

    private class HeadInformation {
        private String name;
        private ImageView head;
        public HeadInformation(String n, ImageView h) {
            this.name = n;
            this.head = h;
        }

        public String getName() {
            return name;
        }

        public ImageView getHead() {
            return head;
        }
    }
}