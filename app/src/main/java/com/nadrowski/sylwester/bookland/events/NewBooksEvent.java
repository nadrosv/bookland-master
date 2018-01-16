package com.nadrowski.sylwester.bookland.events;

import com.nadrowski.sylwester.bookland.models.Book;

import java.util.List;

import retrofit2.Response;

/**
 * Created by korSt on 13.11.2016.
 */

public class NewBooksEvent {
    public Response<List<Book>> result;

    public NewBooksEvent(Response<List<Book>> booksResult){
        this.result = booksResult;
    }
}
