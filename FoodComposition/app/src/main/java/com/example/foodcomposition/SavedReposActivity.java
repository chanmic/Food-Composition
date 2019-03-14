package com.example.foodcomposition;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.foodcomposition.data.FoodNameRepo;
import com.example.foodcomposition.utils.FoodUtils;

import java.util.List;

public class SavedReposActivity extends AppCompatActivity implements FoodAdapter.OnSearchItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_search_results);

        RecyclerView savedReposRV = findViewById(R.id.rv_saved_repos);
        savedReposRV.setLayoutManager(new LinearLayoutManager(this));
        savedReposRV.setHasFixedSize(true);

        final FoodAdapter adapter = new FoodAdapter(this);
        savedReposRV.setAdapter(adapter);

        FoodCompositionViewModel viewModel = ViewModelProviders.of(this).get(FoodCompositionViewModel.class);
        viewModel.getAllFoodNameRepos().observe(this, new Observer<List<FoodNameRepo>>() {
            @Override
            public void onChanged(@Nullable List<FoodNameRepo> foodNameRepos) {
                adapter.updateSearchResults(foodNameRepos);
            }
        });
    }

    @Override
    public void onSearchItemClick(FoodNameRepo repo) {
        Intent intent = new Intent(this, FoodCompositionActivity.class);
        intent.putExtra(FoodUtils.EXTRA_FOOD_REPO, repo);
        startActivity(intent);
    }
}
