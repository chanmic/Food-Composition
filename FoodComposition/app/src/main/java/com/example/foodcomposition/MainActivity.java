package com.example.foodcomposition;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.foodcomposition.utils.FoodUtils;
import com.example.foodcomposition.utils.NetworkUtils;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements FoodAdapter.OnSearchItemClickListener,
        LoaderManager.LoaderCallbacks<String> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String REPOS_ARRAY_KEY = "foodRepos";
    private static final String SEARCH_URL_KEY = "foodSearchURL";

    private static final int FOOD_SEARCH_LOADER_ID = 0;

    private RecyclerView mFoodListRV;
    private EditText mSearchBoxEt;
    private TextView mLoadingErrorTV;
    private ProgressBar mLoadingPB;
    private FoodAdapter mFoodAdapter;

    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    private FoodUtils.FoodRepo[] mRepos;

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

        if(savedInstanceState != null && savedInstanceState.containsKey(REPOS_ARRAY_KEY)){
            mRepos = (FoodUtils.FoodRepo[]) savedInstanceState.getSerializable(REPOS_ARRAY_KEY);
            mFoodAdapter.updateSearchResults(mRepos);
        }

        getSupportLoaderManager().initLoader(FOOD_SEARCH_LOADER_ID, null, this);

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

    private void doFoodSearch(final String query) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String max = preferences.getString(getString(R.string.pref_food_max),getString(R.string.pref_max_default));

        String url = FoodUtils.buildFoodSearchURL(query,max);
        Log.d(TAG, "querying search URL: " + url);
        //new FoodSearchTask().execute(url);

        Bundle args = new Bundle();
        args.putString(SEARCH_URL_KEY, url);
        mLoadingPB.setVisibility(View.VISIBLE);
        getSupportLoaderManager().restartLoader(FOOD_SEARCH_LOADER_ID, args, this);

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                refreshDisplay(query);
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void refreshDisplay(String query) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String max = preferences.getString(getString(R.string.pref_food_max),getString(R.string.pref_max_default));

        String url = FoodUtils.buildFoodSearchURL(query,max);
        Log.d(TAG, "querying search URL: " + url);
        //new FoodSearchTask().execute(url);

        Bundle args = new Bundle();
        args.putString(SEARCH_URL_KEY, url);
        mLoadingPB.setVisibility(View.VISIBLE);
        getSupportLoaderManager().restartLoader(FOOD_SEARCH_LOADER_ID, args, this);
    }

    @Override
    public void onSearchItemClick(FoodUtils.FoodRepo repo) {
        Log.d(TAG, "click on item");
        Intent intent = new Intent(this, FoodCompositionActivity.class);
        intent.putExtra(FoodUtils.EXTRA_FOOD_REPO, repo);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRepos != null) {
            outState.putSerializable(REPOS_ARRAY_KEY, mRepos);
        }
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int i, @Nullable Bundle bundle) {
        String url = null;
        if (bundle != null) {
            url = bundle.getString(SEARCH_URL_KEY);
        }
        return new FoodCompositionSearchLoader(this, url);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {
        Log.d(TAG, "Got results from the loader");
        if (s != null) {
            mLoadingErrorTV.setVisibility(View.INVISIBLE);
            mFoodListRV.setVisibility(View.VISIBLE);
            mRepos = FoodUtils.parseFoodSearchResults(s);
            mFoodAdapter.updateSearchResults(mRepos);
        } else {
            mLoadingErrorTV.setVisibility(View.VISIBLE);
            mFoodListRV.setVisibility(View.INVISIBLE);
        }
        mLoadingPB.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
        // Nothing to do here...
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_settings:
                Log.d(TAG,"click setting");
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


/*    class FoodSearchTask extends AsyncTask<String, Void, String> {

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
    }*/
}