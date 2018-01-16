package com.nadrowski.sylwester.bookland.services;

import com.nadrowski.sylwester.bookland.models.LoginRequest;
import com.nadrowski.sylwester.bookland.models.LoginResponse;
import com.nadrowski.sylwester.bookland.models.RegisterRequest;
import com.nadrowski.sylwester.bookland.models.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by korSt on 31.10.2016.
 */

public interface UserServiceAPI {

    @GET("user")
    Call<User> getUser(@Query("id") Long userId);

//    @FormUrlEncoded
    @POST("login")
    @Headers("Content-Type: application/json")
//    Call<User.LoginResponse> login(@Field("username") String username, @Field("password") String password);
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("register")
//    @Headers("Content-Type: application/json")
    Call<ResponseBody> register(@Body RegisterRequest registerRequest);

    @GET("user/near")
    Call<List<User>> getNearUsers(@Query("userId") Long userId);

    @PUT("user/prefs/local")
    Call<Void> setPreferences(@Query("id") Long userId, @Query("radius") float radius, @Query("lat") float lat, @Query("lon") float lon);


}
