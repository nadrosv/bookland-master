package com.nadrowski.sylwester.bookland.events;

import com.nadrowski.sylwester.bookland.models.User;

import java.util.List;

import retrofit2.Response;

/**
 * Created by korSt on 18.11.2016.
 */
public class UserEvent {

    public Response<User> result;
    public List<User> results;


    public UserEvent(Response<User> response) {
        this.result = response;
    }
    public UserEvent(List<User> responses) {
        this.results = responses;
    }


}
