package com.joe.broom;

import android.content.pm.IPackageDataObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.joe.cachecleaner.engine.AppCleanEngine;
import com.joe.cachecleaner.model.AppInfo;

import java.util.ArrayList;

public class CleanOneActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private AppCacheAdapter adapter;
    private AppCleanEngine engine;
    private ArrayList<AppInfo> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        results = new ArrayList<>();
        findViewById(R.id.btn_scan).setOnClickListener(this);
        findViewById(R.id.btn_clean).setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        engine = new AppCleanEngine();
        engine.setDataObserver(new IPackageDataObserver.Stub() {
            @Override
            public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                ScanAsyncTask task = new ScanAsyncTask();
                task.execute();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan:
                ScanAsyncTask task = new ScanAsyncTask();
                task.execute();
                break;
            case R.id.btn_clean:
                engine.cleanAllCache();
                break;
        }
    }

    class ScanAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            results = engine.scanAppCache(CleanOneActivity.this);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter = new AppCacheAdapter(CleanOneActivity.this, results);
                    recyclerView.setAdapter(adapter);
                }
            });
            return null;
        }
    }
}