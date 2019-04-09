package com.example.recettesgteaux.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;

import com.example.recettesgteaux.Adapter.RecipeAdapter;
import com.example.recettesgteaux.Adapter.VideoPlayerRecyclerView;
import com.example.recettesgteaux.R;
import com.example.recettesgteaux.RecipeViewModel;
import com.example.recettesgteaux.remote_data_source.models.Recipe;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

public class RecipeListFragment extends Fragment {
    private static String TAG = RecipeListFragment.class.getSimpleName();

    private RecipeViewModel mRecipeViewModel;
    public VideoPlayerRecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    public RecipeListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_recipe_list, container,false);

        mRecyclerView = view.findViewById(R.id.rv_recipe);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator(){

//            @Override
//            public void onAnimationFinished(@NonNull RecyclerView.ViewHolder viewHolder) {
//                super.onAnimationFinished(viewHolder);
//
//                mRecyclerView.resetPlayer();
//
//            }
        });


        if(savedInstanceState != null){
            mRecyclerView.setNumberHolder(savedInstanceState.getInt("number"));
            mRecyclerView.setCurrentWindow(savedInstanceState.getInt("window"));
            mRecyclerView.setPlaybackPosition(savedInstanceState.getLong("position"));
            mRecyclerView.setPlayWhenReady(savedInstanceState.getBoolean("play"));

        }


        mProgressBar = view.findViewById(R.id.pb_load_recipe);

        initRecyclerView();

        return view;
    }

    private void initRecyclerView(){

        mRecyclerView.setHasFixedSize(true);
        mRecipeViewModel = ViewModelProviders.of(getActivity()).get(RecipeViewModel.class);
        mRecipeViewModel.getAllRecipes().observe(getActivity(), new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {

                mRecyclerView.setListRecipe(recipes);
                mRecyclerView.setAdapter(new RecipeAdapter(mRecyclerView,recipes));
                mProgressBar.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {

//        if(savedInstanceState != null){
//            mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//
//                    mRecyclerView.resetPlayer();
//
//                    mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//
//                }
//            });
//        }
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//
//        int orientation = newConfig.orientation;
//
//        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
//            mRecyclerView.resetPlayer();
//        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            mRecyclerView.resetPlayer();
//        }
    }

    @Override
    public void onPause() {
        if(mRecyclerView!=null)
            mRecyclerView.releasePlayer();
        super.onPause();
    }

    @Override
    public void onStop() {
        if(mRecyclerView!=null)
            mRecyclerView.releasePlayer();
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("number",mRecyclerView.getNumberHolder());
        outState.putInt("window",mRecyclerView.getCurrentWindow());
        outState.putLong("position",mRecyclerView.getPlaybackPosition());
        outState.putBoolean("play",mRecyclerView.isPlayWhenReady());
    }

}
