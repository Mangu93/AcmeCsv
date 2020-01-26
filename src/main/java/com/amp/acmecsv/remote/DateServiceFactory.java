package com.amp.acmecsv.remote;

import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import retrofit2.Retrofit;

import static com.amp.acmecsv.remote.FactoryCommons.makeGson;
import static com.amp.acmecsv.remote.FactoryCommons.makeRetrofit;
@Service
public class DateServiceFactory {
    private static final String BASE_URL = "http://localhost:8084/services/AcmeDates/";

    @Bean
    public static DateService makeDateService() {
        return makeDateService(makeGson());
    }

    private static DateService makeDateService(Gson gson) {
        Retrofit retrofit = makeRetrofit(gson, BASE_URL);
        return retrofit.create(DateService.class);
    }

}
