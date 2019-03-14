package com.example.foodcomposition.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity(tableName = "repos")
public class FoodNameRepo implements Serializable {
    @NonNull
    @PrimaryKey
    public String FoodName;
    public String ndbno;
}
