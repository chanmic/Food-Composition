package com.example.foodcomposition;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//import com.example.foodcomposition.utils.FoodUtils;
import com.example.foodcomposition.data.FoodNameRepo;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.SearchResultViewHolder> {

    //private FoodUtils.FoodRepo[] mRepos;
    private List<FoodNameRepo> mRepos;

    OnSearchItemClickListener mSearchItemClickListener;

    public interface OnSearchItemClickListener {
        void onSearchItemClick(FoodNameRepo repo);
    }

    FoodAdapter(OnSearchItemClickListener searchItemClickListener){
        mSearchItemClickListener = searchItemClickListener;
    }

    public void updateSearchResults(List<FoodNameRepo> repos) {
        mRepos = repos;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mRepos != null) {
            return mRepos.size();
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.search_result_item, parent, false);
        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        holder.bind(mRepos.get(position));
    }

    class SearchResultViewHolder extends RecyclerView.ViewHolder {
        private TextView mSearchResultTV;

        public SearchResultViewHolder(View itemView) {
            super(itemView);
            mSearchResultTV = (TextView) itemView.findViewById(R.id.tv_search_result);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FoodNameRepo searchResult = mRepos.get(getAdapterPosition());
                    mSearchItemClickListener.onSearchItemClick(searchResult);
                }
            });
        }

        public void bind(FoodNameRepo repo) {
            mSearchResultTV.setText(repo.FoodName);
        }
    }
}
