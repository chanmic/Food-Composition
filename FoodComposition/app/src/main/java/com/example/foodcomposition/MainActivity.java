package com.example.foodcomposition;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.example.foodcomposition.data.FoodNameRepo;
import com.example.foodcomposition.utils.FoodUtils;
import com.example.foodcomposition.utils.NetworkUtils;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements FoodAdapter.OnSearchItemClickListener,
        LoaderManager.LoaderCallbacks<String>, FoodRepoAdapter.OnFoodNameClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String REPOS_ARRAY_KEY = "foodRepos";
    private static final String SEARCH_URL_KEY = "foodSearchURL";

    private static final int FOOD_SEARCH_LOADER_ID = 0;

    private RecyclerView mFoodListRV;
    private EditText mSearchBoxEt;
    private TextView mLoadingErrorTV;
    private ProgressBar mLoadingPB;
    private FoodAdapter mFoodAdapter;
    private DrawerLayout mDrawerLayout;
    private RecyclerView mFoodRepoRV;

    private NavigationView navigationView;
    private FoodViewModal mFoodViewModal;

    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    private FoodUtils.FoodRepo[] mRepos;
    private FoodNameRepo mRepo;
    private boolean mIsSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFoodListRV = (RecyclerView)findViewById(R.id.rv_food_list);
        mFoodRepoRV = (RecyclerView)findViewById(R.id.rv_repo_items);
        mSearchBoxEt = findViewById(R.id.et_search_box);
        mLoadingErrorTV = findViewById(R.id.tv_loading_error);
        mLoadingPB = findViewById(R.id.pb_loading);

        mFoodListRV.setLayoutManager(new LinearLayoutManager(this));
        mFoodListRV.setHasFixedSize(true);

        mFoodAdapter = new FoodAdapter(this);
        mFoodListRV.setAdapter(mFoodAdapter);

        mFoodViewModal = ViewModelProviders.of(this).get(FoodViewModal.class);
        //Nav drawer
        mDrawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nv_nav_drawer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_nav_menu);

        if(savedInstanceState != null && savedInstanceState.containsKey(REPOS_ARRAY_KEY)){
            mRepos = (FoodUtils.FoodRepo[]) savedInstanceState.getSerializable(REPOS_ARRAY_KEY);
            mFoodAdapter.updateSearchResults(mRepos);
        }

        getSupportLoaderManager().initLoader(FOOD_SEARCH_LOADER_ID, null, this);

        mFoodRepoRV.setLayoutManager(new LinearLayoutManager(this));
        mFoodRepoRV.setHasFixedSize(true);
        final FoodRepoAdapter adapter = new FoodRepoAdapter(this);
        mFoodRepoRV.setAdapter(adapter);

        FoodViewModal viewModal = ViewModelProviders.of(this).get(FoodViewModal.class);
        viewModal.getAllFoodRepos().observe(this, new Observer<List<FoodNameRepo>>() {
            @Override
            public void onChanged(@Nullable List<FoodNameRepo> foodNameRepos) {
                adapter.updateFoodName(foodNameRepos);
            }
        });

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

        mRepo = new FoodNameRepo();
        mRepo.FoodName = query;

        String url = FoodUtils.buildFoodSearchURL(mRepo.FoodName,max);
        Log.d(TAG, "querying search URL: " + url);
        //new FoodSearchTask().execute(url);

        mFoodViewModal.getFoodRepoByName(mRepo.FoodName).observe(this, new Observer<FoodNameRepo>() {
            @Override
            public void onChanged(@Nullable FoodNameRepo repo) {
                if(repo != null) {
                    mIsSaved = true;
                } else {
                    mIsSaved = false;
                    mFoodViewModal.insertFoodRepo(mRepo);
                }
            }
        });

        Bundle args = new Bundle();
        args.putString(SEARCH_URL_KEY, url);
        mLoadingPB.setVisibility(View.VISIBLE);
        getSupportLoaderManager().restartLoader(FOOD_SEARCH_LOADER_ID, args, this);

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                refreshDisplay(mRepo.FoodName);
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
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                Log.d(TAG,"click setting");
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFoodNameClick(FoodNameRepo foodNameRepo) {
        refreshDisplay(foodNameRepo.FoodName);
        mSearchBoxEt.setText(foodNameRepo.FoodName);
        mDrawerLayout.closeDrawers();
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
