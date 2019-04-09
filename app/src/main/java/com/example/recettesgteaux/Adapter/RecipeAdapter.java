package com.example.recettesgteaux.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.recettesgteaux.R;
import com.example.recettesgteaux.remote_data_source.models.Recipe;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeViewHolder> {

    private static String TAG = RecipeAdapter.class.getSimpleName();
    private List<Recipe> mRecipeList;
    private VideoPlayerRecyclerView mVideoPlayerRecyclerView;


    public RecipeAdapter(VideoPlayerRecyclerView videoPlayerRecyclerView, List<Recipe> recipeList) {
        mRecipeList = recipeList;
        mVideoPlayerRecyclerView = videoPlayerRecyclerView;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        return new RecipeViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_recipe_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        if(mRecipeList.isEmpty())
            return;

        holder.bind(mVideoPlayerRecyclerView,mRecipeList.get(position));

//        if (position+1 == getItemCount() && mVideoPlayerRecyclerView.isPlayWhenReady()){
//            mVideoPlayerRecyclerView.resetPlayer();
//        }
    }


    @Override
    public int getItemCount() {
        if(mRecipeList == null)
            return 0;

        return mRecipeList.size();
    }


    @Override
    public void onViewRecycled(@NonNull RecipeViewHolder holder) {
//        int position = holder.getAdapterPosition();
//        holder.bind(mRecipeList.get(position));

        super.onViewRecycled(holder);
    }



}
