package com.example.foodcomposition;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.foodcomposition.utils.NetworkUtils;

import java.io.IOException;

public class FoodCompositionSearchLoader extends AsyncTaskLoader<String> {
    private static final String TAG = FoodCompositionSearchLoader.class.getSimpleName();

    private String mFoodSearchJSON;
    private String mURL;

    FoodCompositionSearchLoader(Context context, String url) {
        super(context);
        mURL = url;
    }

    @Override
    protected void onStartLoading() {
        if (mURL != null) {
            if (mFoodSearchJSON != null) {
                Log.d(TAG, "Delivering cached results");
                deliverResult(mFoodSearchJSON);
            } else {
                forceLoad();
            }
        }
    }

    @Nullable
    @Override
    public String loadInBackground() {
        if (mURL != null) {
            String results = null;
            try {
                Log.d(TAG, "loading results from GitHub with URL: " + mURL);
                results = NetworkUtils.doHTTPGet(mURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return results;
        } else {
            return null;
        }
    }

    @Override
    public void deliverResult(@Nullable String data) {
        mFoodSearchJSON = data;
        super.deliverResult(data);
    }
}
