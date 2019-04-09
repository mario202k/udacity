package com.example.recettesgteaux;

import android.app.Application;

import com.example.recettesgteaux.remote_data_source.models.Recipe;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class RecipeViewModel extends AndroidViewModel {

    private static final String TAG = RecipeViewModel.class.getSimpleName();
    private RecipeRepository mRecipeRepository;

    public RecipeViewModel(@NonNull Application application) {
        super(application);
        mRecipeRepository = new RecipeRepository();
    }

    public LiveData<List<Recipe>> getAllRecipes(){
        return mRecipeRepository.getRecipeList();
    }
}
