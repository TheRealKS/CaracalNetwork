package com.koens.caracalnetwork.info;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActionBarContainer;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koens.caracalnetwork.info.util.ActivityMode;
import com.koens.caracalnetwork.info.util.RecyclerViewDivider;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class GeneralItemActivity extends AppCompatActivity {

    private RecyclerView players;
    private RecyclerView.Adapter playerAdapter;
    private RecyclerView.Adapter worldAdapter;
    private RecyclerView.LayoutManager playerLayoutManager;
    private SwipeRefreshLayout playerRefresh;

    List<PlayerWrapper> data = new ArrayList<>();
    List<WorldWrapper> wdata = new ArrayList<>();

    ActivityMode type;
    boolean ServerUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_item);
        int viewType;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                viewType = 1;
            } else {
                viewType = extras.getInt("SOURCE");
                ServerUp = extras.getBoolean("SERVERUP");
            }
        } else {
            String s = (String) savedInstanceState.getSerializable("SOURCE");
            viewType = Integer.parseInt(s);
        }
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        if (viewType == 1) {
            bar.setTitle("Players");
            type = ActivityMode.ACTIVITY_PLAYERS;
        } else {
            bar.setTitle("Worlds");
            type = ActivityMode.ACTIVITY_WORLDS;
        }
        PlayerWrapper wrapper = new PlayerWrapper("DUMMY", "uuid", false, "world");
        data.add(wrapper);

        players = (RecyclerView) findViewById(R.id.player_cards);
        players.setHasFixedSize(true);

        playerLayoutManager = new LinearLayoutManager(this);
        players.setLayoutManager(playerLayoutManager);
        players.addItemDecoration(new RecyclerViewDivider(getApplicationContext()));
        playerAdapter = new PlayerAdapter(data, getApplicationContext());
        worldAdapter = new WorldAdapter(wdata, getApplicationContext());
        players.setAdapter((type == ActivityMode.ACTIVITY_PLAYERS) ? playerAdapter : worldAdapter);

        playerRefresh = (SwipeRefreshLayout) findViewById(R.id.players_refresh);
        playerRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initializeData(true, type);
            }
        });
        playerRefresh.setColorSchemeResources(R.color.orange, R.color.black, R.color.green, R.color.blue, R.color.colorAccent);

        initializeData(false, type);
    }

    private void initializeData(boolean refresh, final ActivityMode mode) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connMgr.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (ServerUp) {
                getDataFromServer(refresh, mode);
            } else {
                //Inflate view
            }
        } else {
            TextView t = (TextView) findViewById(R.id.textView);
            TextView tt = (TextView) findViewById(R.id.textView2);
            Button b = (Button) findViewById(R.id.button);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findViewById(R.id.networkError).setVisibility(View.GONE);
                    findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                    initializeData(false, mode);
                }
            });
            t.setText(R.string.no_internet_main);
            tt.setText(R.string.no_internet_sub);
            if (refresh) playerRefresh.setRefreshing(false);
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            findViewById(R.id.player_cards).setVisibility(View.GONE);
            findViewById(R.id.networkError).setVisibility(View.VISIBLE);
        }
    }

    private void getDataFromServer(boolean refresh, ActivityMode mode) {
        if (mode.equals(ActivityMode.ACTIVITY_PLAYERS)) {
            AsyncHttpClient client = new AsyncHttpClient();
            final boolean r = refresh;
            Log.i("ASYNCHTTPREQUEST", "Downloading 3 items from website...");
            client.get("http://www.caracalnetwork.pe.hu/get_players.php", new TextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    throwable.printStackTrace();
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    if (!s.equals("{\"PLAYERS\":[]})")) {
                        data.clear();
                        JsonObject obj = new JsonParser().parse(s).getAsJsonObject();
                        JsonArray playerarray = obj.getAsJsonArray("PLAYERS");
                        if (playerarray.size() > 1 && !playerarray.get(0).getAsJsonObject().get("name").equals("")) {
                            for (JsonElement player : playerarray) {
                                JsonObject p = player.getAsJsonObject();
                                String n = p.get("name").getAsString();
                                String u = p.get("uuid").getAsString();
                                String w = p.get("world").getAsString();
                                boolean b = (p.get("afk").getAsInt() == 1);
                                PlayerWrapper wr = new PlayerWrapper(n, u, b, w);
                                data.add(wr);
                            }
                        }
                        playerAdapter.notifyDataSetChanged();
                        if (r) playerRefresh.setRefreshing(false);
                        else findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        findViewById(R.id.player_cards).setVisibility(View.VISIBLE);
                        Log.i("ASYNCHTTPREQUEST", "Downloaded 3 items from website!");
                    } else {
                        //View no players are online stuff
                    }
                }
            });
        } else if (mode.equals(ActivityMode.ACTIVITY_WORLDS)) {
            AsyncHttpClient client = new AsyncHttpClient();
            final boolean r = refresh;
            client.get("http://www.caracalnetwork.pe.hu/get_worlds.php", new TextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    throwable.printStackTrace();
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    if (!s.equals("{\"WORLDS\":[]})")) {
                        data.clear();
                        JsonObject obj = new JsonParser().parse(s).getAsJsonObject();
                        JsonArray worldarray = obj.getAsJsonArray("WORLDS");
                        for (JsonElement world : worldarray) {
                            JsonObject w = world.getAsJsonObject();
                            String n = w.get("name").getAsString();
                            int p = w.get("players").getAsInt();
                            WorldWrapper wr = new WorldWrapper(n, p);
                            wdata.add(wr);
                        }
                        playerAdapter.notifyDataSetChanged();
                        if (r) playerRefresh.setRefreshing(false);
                        else findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        findViewById(R.id.player_cards).setVisibility(View.VISIBLE);
                    } else {
                        //No worlds
                    }
                }
            });

        }
    }

}
