package com.example.recettesgteaux;

import android.util.Log;

import com.example.recettesgteaux.remote_data_source.AppClient;
import com.example.recettesgteaux.remote_data_source.WebService;
import com.example.recettesgteaux.remote_data_source.models.Recipe;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeRepository {
    private static final String TAG = RecipeRepository.class.getSimpleName();

    private WebService mWebService;

    public RecipeRepository() {
        mWebService = AppClient.getClient().create(WebService.class);
    }

    public LiveData<List<Recipe>> getRecipeList(){
        final MutableLiveData<List<Recipe>> data = new MutableLiveData<>();
        mWebService.getRecipesList().enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG,response.message()+call.request());
                    return;
                }

                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Log.d(TAG,t.getMessage()+call.request());
            }
        });
        return data;
    }
}
