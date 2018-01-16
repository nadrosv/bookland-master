package com.nadrowski.sylwester.bookland.models;

//import retrofit2.GsonConverterFactory;
import com.nadrowski.sylwester.bookland.services.HeaderInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by korSt on 03.11.2016.
 */

public class Data {
    private static OkHttpClient httpClient = new OkHttpClient.Builder()
            .addInterceptor(new HeaderInterceptor())
            .build();

    private static OkHttpClient httpClientGR = new OkHttpClient.Builder()
            .build();

    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.0.102:8080/api/")
//            .baseUrl("http://ec2-52-28-61-254.eu-central-1.compute.amazonaws.com:8080/booklandApp-0.0.1-SNAPSHOT/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build();

    public static Retrofit retrofitGR = new Retrofit.Builder()
            .client(new OkHttpClient())
            .baseUrl("https://www.goodreads.com/")
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build();
}
