package com.example.foodcomposition;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.foodcomposition.utils.CompositionUrils;

public class CompositionAdapter extends RecyclerView.Adapter<CompositionAdapter.SearchResultViewHolder> {

    private CompositionUrils.CompositionRepo[] mRepos;
    CompositionAdapter.OnSearchItemClickListener mSearchItemClickListener;

    public interface OnSearchItemClickListener {
        void onSearchItemClick(CompositionUrils.CompositionRepo repo);
    }

    CompositionAdapter(OnSearchItemClickListener searchItemClickListener){
        mSearchItemClickListener = searchItemClickListener;
    }

    public void updateSearchResults(CompositionUrils.CompositionRepo[] repos) {
        mRepos = repos;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mRepos != null) {
            return mRepos.length;
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.composition_result_item, parent, false);
        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        holder.bind(mRepos[position]);
    }

    class SearchResultViewHolder extends RecyclerView.ViewHolder {
        private TextView mSearchResultTV;

        public SearchResultViewHolder(View itemView) {
            super(itemView);
            mSearchResultTV = (TextView) itemView.findViewById(R.id.tv_composition_list);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CompositionUrils.CompositionRepo searchResult = mRepos[getAdapterPosition()];
                    mSearchItemClickListener.onSearchItemClick(searchResult);
                }
            });
        }

        public void bind(CompositionUrils.CompositionRepo repo) {
            String text = repo.name+": " + repo.value + repo.unit;
            mSearchResultTV.setText(text);
        }
    }
}
