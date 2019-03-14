package com.example.foodcomposition.utils;

import com.example.foodcomposition.data.FoodNameRepo;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

public class FoodUtils {
    public static final String EXTRA_FOOD_REPO = "FoodUtils.FoodRepo";

    public static class FoodSearchResults implements Serializable {
        public FoodList list;
    }

    public static class FoodList implements Serializable {
        public ArrayList<FoodNameRepo> items;
    }

    public static class FoodRepo implements Serializable {
        public String name;
        public String ndbno;
    }

    public static String buildFoodSearchURL(String query, String max) {
        String url = "https://api.nal.usda.gov/ndb/search/?format=json&q=" + query +
                "&sort=n&max=" + max +
                "&offset=0&api_key=dDv0ux0V3dyzAsiqNcyVuztwngdsspzARn2vZpYu";

        return url;
    }

    public static ArrayList<FoodNameRepo> parseFoodSearchResults(String json) {
        Gson gson = new Gson();
        FoodSearchResults results = gson.fromJson(json, FoodSearchResults.class);
        if (results != null && results.list != null) {
            return results.list.items;
        } else {
            return null;
        }
    }

}
