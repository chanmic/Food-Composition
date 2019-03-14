package com.example.foodcomposition;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.foodcomposition.data.FoodNameRepo;

import java.util.List;

public class FoodRepoAdapter extends RecyclerView.Adapter<FoodRepoAdapter.FoodItemViewHolder> {

    private List<FoodNameRepo> mFoodName;
    private OnFoodNameClickListener mFoodNameClickListener;

    public interface OnFoodNameClickListener {
        void onFoodNameClick(FoodNameRepo foodNameRepo);
    }

    public FoodRepoAdapter(OnFoodNameClickListener clickListener) {
        mFoodNameClickListener = clickListener;
    }

    public void updateFoodName(List<FoodNameRepo> foodNameRepos) {
        mFoodName = foodNameRepos;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(mFoodName != null) {
            return mFoodName.size();
        } else {
            return 0;
        }
    }

    @Override
    public FoodItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.repo_list_item, parent, false);
        return new FoodItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodItemViewHolder locationItemViewHolder, int i) {
        locationItemViewHolder.bind(mFoodName.get(i));
    }

    class FoodItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mLocationTV;

        public FoodItemViewHolder(View itemView) {
            super(itemView);
            mLocationTV = itemView.findViewById(R.id.tv_food_repo);
            itemView.setOnClickListener(this);
        }

        public void bind(FoodNameRepo foodNameRepo) {
            mLocationTV.setText(foodNameRepo.FoodName);
        }

        @Override
        public void onClick(View v) {
            FoodNameRepo foodNameRepo = mFoodName.get(getAdapterPosition());
            mFoodNameClickListener.onFoodNameClick(foodNameRepo);
        }
    }

}
