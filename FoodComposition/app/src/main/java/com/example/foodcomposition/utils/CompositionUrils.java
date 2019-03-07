package com.example.foodcomposition.utils;

import com.google.gson.Gson;

import java.util.*;

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

    public static CompositionRepo[] parseCompositionSearchResults(String json, String sort) {
        Gson gson = new Gson();
        CompositionSearchResults results = gson.fromJson(json, CompositionSearchResults.class);
        if (results != null && results.foods != null) {
            CompositionRepo[] sortResults = results.foods[0].food.nutrients;
            if(sort.equals("Names")) {
                Arrays.sort(sortResults, new Comparator<CompositionRepo>() { // Sort CompositionRepo object by comparing names
                    public int compare(CompositionRepo c1, CompositionRepo c2) {
                        return c1.name.compareToIgnoreCase(c2.name);
                    }
                });
            }
            else if(sort.equals("Values")) {
                Arrays.sort(sortResults, new Comparator<CompositionRepo>() { // Sort CompositionRepo object by comparing values
                    public int compare(CompositionRepo c1, CompositionRepo c2) {
                        if(Float.parseFloat(c1.value) > Float.parseFloat(c2.value)) { // Parse float and compare
                            return 1; // Return 1 if c1 is larger
                        }
                        else if(Float.parseFloat(c1.value) < Float.parseFloat(c2.value)) { // Parse float and compare
                            return -1; // Return 1 if c2 is larger
                        }
                        return 0;
                    }
                });
            }
            else if(sort.equals("Units")) {
                Arrays.sort(sortResults, new Comparator<CompositionRepo>() { // Sort CompositionRepo object by comparing units
                    public int compare(CompositionRepo c1, CompositionRepo c2) {
                        return c1.unit.compareToIgnoreCase(c2.unit);
                    }
                });
            }
            return sortResults;
        } else {
            return null;
        }
    }

}
