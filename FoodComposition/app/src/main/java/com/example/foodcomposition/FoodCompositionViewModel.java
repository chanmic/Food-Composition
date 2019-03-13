package com.example.foodcomposition;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.foodcomposition.data.FoodNameRepo;
import com.example.foodcomposition.data.FoodNameRepoRepository;

import java.util.List;

public class FoodCompositionViewModel extends AndroidViewModel {
    private FoodNameRepoRepository mFoodNameRepoRepository;

    public FoodCompositionViewModel(Application application) {
        super(application);
        mFoodNameRepoRepository = new FoodNameRepoRepository(application);
    }

    public void insertFoodNameRepo(FoodNameRepo repo) {
        mFoodNameRepoRepository.insertFoodNameRepo(repo);
    }

    public void deleteFoodNameRepo(FoodNameRepo repo) {
        mFoodNameRepoRepository.deleteFoodNameRepo(repo);
    }

    public LiveData<List<FoodNameRepo>> getAllFoodNameRepos() {
        return mFoodNameRepoRepository.getAllFoodNameRepos();
    }

    public LiveData<FoodNameRepo> getFoodNameRepoByName(String fullName) {
        return mFoodNameRepoRepository.getFoodNameRepoByName(fullName);
    }
}
