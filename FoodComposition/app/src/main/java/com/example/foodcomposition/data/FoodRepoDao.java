package com.example.foodcomposition.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface FoodRepoDao {
    @Insert
    void insert(FoodNameRepo repo);

    @Delete
    void delete(FoodNameRepo repo);

    @Query("SELECT * FROM repos")
    LiveData<List<FoodNameRepo>> getAllRepos();

    @Query("SELECT * FROM repos WHERE FoodName = :foodname LIMIT 1")
    LiveData<FoodNameRepo> getRepoByName(String foodname);
}
