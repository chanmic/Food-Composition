package com.example.foodcomposition.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.example.foodcomposition.utils.FoodUtils;

import java.util.List;

public class FoodRepoRepository {
    private FoodRepoDao mFoodRepoDao;

    public FoodRepoRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        mFoodRepoDao = database.foodRepoDao();
    }

    public void insertFoodRepo(FoodNameRepo repo) {
        new InsertAsyncTask(mFoodRepoDao).execute(repo);
    }

    public void deleteFoodRepo(FoodNameRepo repo) {
        new DeleteAsyncTask(mFoodRepoDao).execute(repo);
    }

    public LiveData<List<FoodNameRepo>> getAllForecastRepos() {
        return mFoodRepoDao.getAllRepos();
    }

    public LiveData<FoodNameRepo> getFoodRepoByName(String name) {
        return mFoodRepoDao.getRepoByName(name);
    }

    private static class InsertAsyncTask extends AsyncTask<FoodNameRepo, Void, Void> {
        private FoodRepoDao mAsyncTaskDao;
        InsertAsyncTask(FoodRepoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(FoodNameRepo... foodRepos) {
            mAsyncTaskDao.insert(foodRepos[0]);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<FoodNameRepo, Void, Void> {
        private FoodRepoDao mAsyncTaskDao;
        DeleteAsyncTask(FoodRepoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(FoodNameRepo... foodRepos) {
            mAsyncTaskDao.delete(foodRepos[0]);
            return null;
        }
    }

}