package com.example.foodcomposition.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.example.foodcomposition.utils.FoodUtils;

import java.util.List;

public class FoodNameRepoRepository {
    private FoodNameRepoDao mFoodNameRepoDao;
    //private FoodUtils.FoodRepo mRepo;

    public FoodNameRepoRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mFoodNameRepoDao = db.foodNameRepoDao();
    }

    public void insertFoodNameRepo(FoodNameRepo repo) {
        new InsertAsyncTask(mFoodNameRepoDao).execute(repo);
    }

    public void deleteFoodNameRepo(FoodNameRepo repo) {
        new DeleteAsyncTask(mFoodNameRepoDao).execute(repo);
    }

    public LiveData<List<FoodNameRepo>> getAllFoodNameRepos() {
        return mFoodNameRepoDao.getAllRepos();
    }

    public LiveData<FoodNameRepo> getFoodNameRepoByName(String fullName) {
        return mFoodNameRepoDao.getRepoByName(fullName);
    }

    private static class InsertAsyncTask extends AsyncTask<FoodNameRepo, Void, Void> {
        private FoodNameRepoDao mAsyncTaskDao;
        InsertAsyncTask(FoodNameRepoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(FoodNameRepo... foodNameRepos) {
            mAsyncTaskDao.insert(foodNameRepos[0]);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<FoodNameRepo, Void, Void> {
        private FoodNameRepoDao mAsyncTaskDao;
        DeleteAsyncTask(FoodNameRepoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(FoodNameRepo... foodNameRepos) {
            mAsyncTaskDao.delete(foodNameRepos[0]);
            return null;
        }
    }

}
