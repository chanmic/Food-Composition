package com.example.foodcomposition;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.foodcomposition.data.FoodNameRepo;
import com.example.foodcomposition.data.FoodRepoRepository;

import java.util.List;

public class FoodViewModal extends AndroidViewModel {
    private FoodRepoRepository mFoodRepoRepository;

    public FoodViewModal(Application application) {
        super(application);
        mFoodRepoRepository = new FoodRepoRepository(application);
    }

    public void insertFoodRepo(FoodNameRepo repo) {
        mFoodRepoRepository.insertFoodRepo(repo);
    }

    public void deleteFoodRepo(FoodNameRepo repo) {
        mFoodRepoRepository.deleteFoodRepo(repo);
    }

    public LiveData<List<FoodNameRepo>> getAllFoodRepos() {
        return mFoodRepoRepository.getAllForecastRepos();
    }

    public LiveData<FoodNameRepo> getFoodRepoByName(String name) {
        return mFoodRepoRepository.getFoodRepoByName(name);
    }
}
