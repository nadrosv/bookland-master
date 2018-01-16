package com.nadrowski.sylwester.bookland.fragments;

import com.nadrowski.sylwester.bookland.models.Book;

/**
 * Created by korSt on 13.11.2016.
 */

public interface OnBookSelectedListener {
    void onBookSelected(Book book, boolean showRentButton);
    void onRentBookSelected(Book book);
    void onDeleteBookSelected(Long bookId);
}
