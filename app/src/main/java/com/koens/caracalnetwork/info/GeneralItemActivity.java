package com.koens.caracalnetwork.info;

import android.content.Context;
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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koens.caracalnetwork.info.util.RecyclerViewDivider;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class GeneralItemActivity extends AppCompatActivity {

    private RecyclerView players;
    private RecyclerView.Adapter playerAdapter;
    private RecyclerView.LayoutManager playerLayoutManager;
    private SwipeRefreshLayout playerRefresh;

    List<PlayerWrapper> data = new ArrayList<>();

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
            }
        } else {
            String s = (String) savedInstanceState.getSerializable("SOURCE");
            viewType = Integer.parseInt(s);
        }
        ActionBar bar = getSupportActionBar();
        if (viewType == 1) {
            bar.setTitle("Players");
        } else {
            bar.setTitle("Worlds");
        }
        PlayerWrapper wrapper = new PlayerWrapper("DUMMY", "uuid", false);
        data.add(wrapper);

        players = (RecyclerView) findViewById(R.id.player_cards);
        players.setHasFixedSize(true);
        playerLayoutManager = new LinearLayoutManager(this);
        players.setLayoutManager(playerLayoutManager);
        players.addItemDecoration(new RecyclerViewDivider(getApplicationContext()));
        playerAdapter = new PlayerAdapter(data, getApplicationContext());
        players.setAdapter(playerAdapter);

        playerRefresh = (SwipeRefreshLayout) findViewById(R.id.players_refresh);
        playerRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initializeData(true);
            }
        });
        playerRefresh.setColorSchemeResources(R.color.orange, R.color.black, R.color.green, R.color.blue, R.color.colorAccent);
    }

    private void initializeData(boolean refresh) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connMgr.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            getDataFromServer(refresh);
        } else {
            findViewById(R.id.networkError).setVisibility(View.VISIBLE);
        }
    }

    private void getDataFromServer(boolean refresh) {
        AsyncHttpClient client = new AsyncHttpClient();
        final boolean r = refresh;
        Log.i("ASYNCHTTPREQUEST", "Downloading 3 items from website...");
        client.get("http://www.caracalnetwork.pe.hu/get_db.php", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                JsonObject obj = new JsonParser().parse(s).getAsJsonObject();
                GeneralWrapper wrapper = new GeneralWrapper(obj.get("UP").getAsInt(), true, 0);
                GeneralWrapper wrapper1 = new GeneralWrapper(obj.get("PLAYERS").getAsInt(), false, 1);
                GeneralWrapper wrapper2 = new GeneralWrapper(obj.get("WORLDS").getAsInt(), false, 2);
                data.clear();
                data.add(wrapper);
                data.add(wrapper1);
                data.add(wrapper2);
                playerAdapter.notifyDataSetChanged();
                if (r) playerRefresh.setRefreshing(false);
                else findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                findViewById(R.id.general_cards).setVisibility(View.VISIBLE);
                Log.i("ASYNCHTTPREQUEST", "Downloaded 3 items from website!");
            }
        });
    }
}
