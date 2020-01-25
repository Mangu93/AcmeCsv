package com.amp.acmecsv.remote;

import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import retrofit2.Retrofit;

import static com.amp.acmecsv.remote.FactoryCommons.makeGson;
import static com.amp.acmecsv.remote.FactoryCommons.makeRetrofit;

@Service
public class FeeServiceFactory {

    private static final String BASE_URL = "http://localhost:8083/services/AcmeFees/";

    @Bean
    public static FeeService makeFeeService() {
        return makeFeeService(makeGson());
    }

    private static FeeService makeFeeService(Gson gson) {
        Retrofit retrofit = makeRetrofit(gson, BASE_URL);
        return retrofit.create(FeeService.class);
    }

}
