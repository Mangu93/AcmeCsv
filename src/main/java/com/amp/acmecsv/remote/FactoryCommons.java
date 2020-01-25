package com.amp.acmecsv.remote;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class FactoryCommons {

    @NotNull
    public static Retrofit makeRetrofit(Gson gson, String baseUrl) {
        return new Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(makeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
    }

    @NonNull
    public static OkHttpClient makeOkHttpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.addInterceptor(loggingInterceptor);
        httpClientBuilder.addInterceptor(chain -> {
            Request original = chain.request();
            HttpUrl originalHttpUrl = original.url();
            HttpUrl url = originalHttpUrl.newBuilder().build();
            Request.Builder requestBuilder = original.newBuilder().url(url);
            return chain.proceed(requestBuilder.build());
        });
        return httpClientBuilder.build();
    }

    @NonNull
    public static Gson makeGson() {
        return new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();
    }
}
