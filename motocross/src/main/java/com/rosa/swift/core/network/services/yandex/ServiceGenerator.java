package com.rosa.swift.core.network.services.yandex;


import com.rosa.swift.core.business.utils.AppConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static OkHttpClient.Builder sHttpClient = new OkHttpClient.Builder();
    //пока работаем только с Яндекс API
    private static Retrofit.Builder sBuilder = new Retrofit.Builder()
            .baseUrl(AppConfig.URL_GEOCODE_MAPS_YANDEX)
            .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        //sHttpClient.addInterceptor(new HeaderInterceptor());
        sHttpClient.addInterceptor(loggingInterceptor);
        sHttpClient.connectTimeout(AppConfig.MAX_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
        sHttpClient.readTimeout(AppConfig.MAX_READ_TIMEOUT, TimeUnit.MILLISECONDS);
        //sHttpClient.cache(new Cache(GotApplication.getContext().getCacheDir(),
        //        Integer.MAX_VALUE));
        //sHttpClient.addNetworkInterceptor(new StethoInterceptor());

        Retrofit retrofit = sBuilder
                .client(sHttpClient.build())
                .build();

        return retrofit.create(serviceClass);
    }
}
