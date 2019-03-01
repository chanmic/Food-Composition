package com.example.foodcomposition.utils;

import com.google.gson.Gson;

public class CompositionUrils {

    public static class CompositionSearchResults {
        public Composition_foods[] foods;
    }

    public static class Composition_foods {
        public Composition_food food;
    }

    public static class Composition_food {
        public CompositionRepo[] nutrients;
    }

    public static class CompositionRepo {
        public String name;
        public String value;
        public String unit;
    }

    public static String buildCompositionSearchURL(String query) {
        String url = "https://api.nal.usda.gov/ndb/V2/reports?ndbno=" + query +
                "&type=f&format=json&api_key=dDv0ux0V3dyzAsiqNcyVuztwngdsspzARn2vZpYu";

        return url;
    }

    public static CompositionRepo[] parseCompositionSearchResults(String json) {
        Gson gson = new Gson();
        CompositionSearchResults results = gson.fromJson(json, CompositionSearchResults.class);
        if (results != null && results.foods != null) {
            return results.foods[0].food.nutrients;
        } else {
            return null;
        }
    }

}
