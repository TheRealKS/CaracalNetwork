package com.koens.caracalnetwork.info;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koens.caracalnetwork.info.util.RecyclerViewDivider;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpRequest;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.GenericArrayType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private RecyclerView overview;
    private RecyclerView.Adapter overviewAdapter;
    private RecyclerView.LayoutManager overviewLayoutManager;

    SwipeRefreshLayout overviewRefresh;

    private List<GeneralWrapper> data = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        GeneralWrapper wrapper = new GeneralWrapper(1, true, 0);
        GeneralWrapper wrapper1 = new GeneralWrapper(1, false, 1);
        GeneralWrapper wrapper2 = new GeneralWrapper(1, false, 2);
        data.add(wrapper);
        data.add(wrapper1);
        data.add(wrapper2);

        overview = (RecyclerView) findViewById(R.id.general_cards);
        overview.setHasFixedSize(true);
        overviewLayoutManager = new LinearLayoutManager(this);
        overview.setLayoutManager(overviewLayoutManager);
        overview.addItemDecoration(new RecyclerViewDivider(getApplicationContext()));
        overviewAdapter = new OverviewAdapter(data, getApplicationContext());
        overview.setAdapter(overviewAdapter);

        overviewRefresh = (SwipeRefreshLayout) findViewById(R.id.general_cards_refresh);
        overviewRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initializeData(true);
            }
        });
        overviewRefresh.setColorSchemeResources(R.color.orange, R.color.black, R.color.green, R.color.blue, R.color.colorAccent);

        findViewById(R.id.general_cards).setVisibility(RecyclerView.GONE);
        initializeData(false);
    }

    private void initializeData(boolean refresh) {
        overviewRefresh.setEnabled(false);
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connMgr.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            getDataFromServer(refresh);
        } else {
            TextView t = (TextView) findViewById(R.id.textView);
            TextView tt = (TextView) findViewById(R.id.textView2);
            Button b = (Button) findViewById(R.id.button);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findViewById(R.id.networkError).setVisibility(View.GONE);
                    findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                    initializeData(false);
                }
            });
            t.setText(R.string.no_internet_main);
            tt.setText(R.string.no_internet_sub);
            if (refresh) overviewRefresh.setRefreshing(false);
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            findViewById(R.id.general_cards).setVisibility(View.GONE);
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
                overviewAdapter.notifyDataSetChanged();
                if (r) overviewRefresh.setRefreshing(false);
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                findViewById(R.id.general_cards).setVisibility(View.VISIBLE);
                findViewById(R.id.networkError).setVisibility(View.GONE);
                overviewRefresh.setEnabled(true);
                Log.i("ASYNCHTTPREQUEST", "Downloaded 3 items from website!");
            }
        });
    }
}
