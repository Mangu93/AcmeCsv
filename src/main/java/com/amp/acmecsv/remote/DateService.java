package com.amp.acmecsv.remote;

import com.amp.acmecsv.remote.models.CategoryResponse;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DateService {
    @POST("api/dates")
    Call<CategoryResponse> postDates(@Body RequestBody body);
}
