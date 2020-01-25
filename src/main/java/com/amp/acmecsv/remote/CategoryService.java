package com.amp.acmecsv.remote;

import com.amp.acmecsv.remote.models.CategoryResponse;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import io.reactivex.Observable;

public interface CategoryService {
    @POST("api/products")
    Call<CategoryResponse> postCategories(@Body String body);
}
