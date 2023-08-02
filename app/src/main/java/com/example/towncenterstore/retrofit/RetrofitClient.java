package com.example.towncenterstore.retrofit;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static RetrofitClient instance ;
    private RetrofitRequests retrofitRequests;
    private final String BASE_URL = "https://towncenterr.shop/api/";



    private RetrofitClient() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getClient())
                .build();
        retrofitRequests = retrofit.create(RetrofitRequests.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public RetrofitRequests getRetrofitRequests() {
        return retrofitRequests;
    }

    private static OkHttpClient getClient(){
        return new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @NonNull
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request.Builder builder = chain.request().newBuilder();
                builder.addHeader("Accept","application/json");
                builder.addHeader("Content-Type","application/json");
                return chain.proceed(builder.build());
            }
        }).build();
    }

}
