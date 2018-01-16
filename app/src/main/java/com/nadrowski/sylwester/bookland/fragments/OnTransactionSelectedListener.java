package com.nadrowski.sylwester.bookland.fragments;

import com.nadrowski.sylwester.bookland.models.Transaction;

/**
 * Created by korSt on 17.11.2016.
 */

public interface OnTransactionSelectedListener {
    void onTransactionSelected(Transaction transaction, Boolean closed);
    void onCloseTransaction(Transaction transaction);
}
