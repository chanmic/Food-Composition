package com.example.foodcomposition;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.foodcomposition.utils.FoodUtils;
import com.example.foodcomposition.utils.NetworkUtils;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements FoodAdapter.OnSearchItemClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mFoodListRV;
    private EditText mSearchBoxEt;
    private TextView mLoadingErrorTV;
    private ProgressBar mLoadingPB;
    private FoodAdapter mFoodAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFoodListRV = (RecyclerView)findViewById(R.id.rv_food_list);
        mSearchBoxEt = findViewById(R.id.et_search_box);
        mLoadingErrorTV = findViewById(R.id.tv_loading_error);
        mLoadingPB = findViewById(R.id.pb_loading);

        mFoodListRV.setLayoutManager(new LinearLayoutManager(this));
        mFoodListRV.setHasFixedSize(true);

        mFoodAdapter = new FoodAdapter(this);
        mFoodListRV.setAdapter(mFoodAdapter);

        Button searchButton = findViewById(R.id.btn_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = mSearchBoxEt.getText().toString();
                if (!TextUtils.isEmpty(searchQuery)) {
                    doFoodSearch(searchQuery);
                }
            }
        });

    }

    private void doFoodSearch(String query) {
        String url = FoodUtils.buildFoodSearchURL(query);
        Log.d(TAG, "querying search URL: " + url);
        new FoodSearchTask().execute(url);
    }

    @Override
    public void onSearchItemClick(FoodUtils.FoodRepo repo) {
        Log.d(TAG, "click on item");
        Intent intent = new Intent(this, FoodCompositionActivity.class);
        intent.putExtra(FoodUtils.EXTRA_FOOD_REPO, repo);
        startActivity(intent);
    }

    class FoodSearchTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingPB.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... urls) {
            String url = urls[0];
            String results = null;
            try {
                results = NetworkUtils.doHTTPGet(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return results;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                mLoadingErrorTV.setVisibility(View.INVISIBLE);
                mFoodListRV.setVisibility(View.VISIBLE);
                FoodUtils.FoodRepo[] repos = FoodUtils.parseFoodSearchResults(s);
                mFoodAdapter.updateSearchResults(repos);
            } else {
                mLoadingErrorTV.setVisibility(View.VISIBLE);
                mFoodListRV.setVisibility(View.INVISIBLE);
            }
            mLoadingPB.setVisibility(View.INVISIBLE);
        }
    }
}
