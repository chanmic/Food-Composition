package com.example.foodcomposition;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.foodcomposition.utils.CompositionUrils;
import com.example.foodcomposition.utils.FoodUtils;
import com.example.foodcomposition.utils.NetworkUtils;

import java.io.IOException;

public class FoodCompositionActivity extends AppCompatActivity
        implements CompositionAdapter.OnSearchItemClickListener{

    private static final String TAG = FoodCompositionActivity.class.getSimpleName();

    private TextView mRepoNameTV;
    private RecyclerView mCompositionListRV;
    private TextView mLoadingErrorTV;
    private ProgressBar mLoadingPB;
    private CompositionAdapter mCompositionAdapter;

    private FoodUtils.FoodRepo mRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_composition);

        mRepoNameTV = findViewById(R.id.tv_repo_name);
        mCompositionListRV = (RecyclerView)findViewById(R.id.rv_composition_list);
        mLoadingErrorTV = findViewById(R.id.repo_loading_error);
        mLoadingPB = findViewById(R.id.repo_pb_loading);

        mCompositionListRV.setLayoutManager(new LinearLayoutManager(this));
        mCompositionListRV.setHasFixedSize(true);

        mCompositionAdapter = new CompositionAdapter(this);
        mCompositionListRV.setAdapter(mCompositionAdapter);

        mRepo = null;
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(FoodUtils.EXTRA_FOOD_REPO)) {
            mRepo = (FoodUtils.FoodRepo) intent.getSerializableExtra(FoodUtils.EXTRA_FOOD_REPO);
            mRepoNameTV.setText(mRepo.name);

            String url = CompositionUrils.buildCompositionSearchURL(mRepo.ndbno);
            new CompositionTask().execute(url);
        }
    }

    @Override
    public void onSearchItemClick(CompositionUrils.CompositionRepo repo){

    }

    class CompositionTask extends AsyncTask<String, Void, String> {
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
                mCompositionListRV.setVisibility(View.VISIBLE);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String sort = preferences.getString(getString(R.string.pref_food_sort), getString(R.string.pref_sort_default));
                CompositionUrils.CompositionRepo[] repos
                        = CompositionUrils.parseCompositionSearchResults(s, sort);
                mCompositionAdapter.updateSearchResults(repos);
            } else {
                mLoadingErrorTV.setVisibility(View.VISIBLE);
                mCompositionListRV.setVisibility(View.INVISIBLE);
            }
            mLoadingPB.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_result_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                shareRepo();
                return true;
            case R.id.action_google:
                searchGoogle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void shareRepo() {
        if (mRepo != null) {
            String shareText = getString(R.string.share_repo_text,mRepo.name,mRepo.ndbno);
            ShareCompat.IntentBuilder.from(this)
                    .setType("text/plain")
                    .setText(shareText)
                    .setChooserTitle(R.string.share_chooser_title)
                    .startChooser();
        }
    }

    public void searchGoogle() {
        if (mRepo != null) {
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, mRepo.name);
            startActivity(intent);
        }
    }
}
