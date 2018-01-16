package com.nadrowski.sylwester.bookland.services;

import com.nadrowski.sylwester.bookland.models.GoodreadsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by korSt on 08.12.2016.
 */

public interface GoodreadsServiceAPI {

    @GET("search.xml")
    Call<GoodreadsResponse> getBooks(@Query("key") String key, @Query("q") String title);
}
