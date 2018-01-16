package com.nadrowski.sylwester.bookland.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.nadrowski.sylwester.bookland.R;
import com.nadrowski.sylwester.bookland.models.SearchResponse;

import java.util.ArrayList;

/**
 * Created by korSt on 09.12.2016.
 */

public class GoodreadsAdapter extends ArrayAdapter<SearchResponse> {

    public GoodreadsAdapter(Context context, int resource) {
        super(context, resource);
    }

    public GoodreadsAdapter(Context context, ArrayList<SearchResponse> transactions) {
        super(context, 0, transactions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SearchResponse transaction = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.transaction_item, parent, false);
        }

        TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_trans_title);
        TextView tvUser = (TextView) convertView.findViewById(R.id.tv_trans_user);
        ImageView ivStatus = (ImageView) convertView.findViewById(R.id.iv_trans_status);

        tvTitle.setText(transaction.getTitle());
        tvUser.setText(transaction.getAuthor());
        Picasso.with(getContext()).load(transaction.getPath()).into(ivStatus);

        return convertView;
    }
}