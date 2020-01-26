package com.amp.acmecsv.remote;

import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import retrofit2.Retrofit;

import static com.amp.acmecsv.remote.FactoryCommons.makeGson;
import static com.amp.acmecsv.remote.FactoryCommons.makeRetrofit;

@Service
public class ProductServiceFactory {
    //TODO To change it to proper config
    private static final String BASE_URL = "http://localhost:8082/services/AcmeCategories/";

    @Bean
    public static ProductService makeCategoryService() {
        return makeCategoryService(makeGson());
    }

    private static ProductService makeCategoryService(Gson gson) {
        Retrofit retrofit = makeRetrofit(gson, BASE_URL);
        return retrofit.create(ProductService.class);
    }

}
