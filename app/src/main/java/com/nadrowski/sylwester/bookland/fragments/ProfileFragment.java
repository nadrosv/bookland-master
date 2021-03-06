package com.nadrowski.sylwester.bookland.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.nadrowski.sylwester.bookland.MainActivity;
import com.nadrowski.sylwester.bookland.R;
import com.nadrowski.sylwester.bookland.bus.BusProvider;
import com.nadrowski.sylwester.bookland.events.UserEvent;
import com.nadrowski.sylwester.bookland.services.AppData;
import com.nadrowski.sylwester.bookland.services.UserService;

/**
 * Created by korSt on 31.10.2016.
 */

public class ProfileFragment extends Fragment {

    private TextView tvUsername;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        new UserService().getUser(AppData.loggedUser.getUserId());

    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        final View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        tvUsername = (TextView) rootView.findViewById(R.id.tv_profile_username);
        TextView tvReputation = (TextView) rootView.findViewById(R.id.tv_profile_reputation);

        final MainActivity mainActivity = (MainActivity) getContext();
        mainActivity.setTitle("Profile");
        Button myBooks = (Button) rootView.findViewById(R.id.profile_mybooks);
        myBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Fragment fragment = (Fragment) (MyBooksFragment.class).newInstance();
                    mainActivity.changeFragment(fragment);
                } catch (java.lang.InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e){
                    e.printStackTrace();
                }
            }
        });

        Button search = (Button) rootView.findViewById(R.id.profile_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Fragment fragment = (Fragment) (SearchFragment.class).newInstance();
                    mainActivity.changeFragment(fragment);
                } catch (java.lang.InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e){
                    e.printStackTrace();
                }
            }
        });

        Button history = (Button) rootView.findViewById(R.id.profile_history);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Fragment fragment = (Fragment) (HistoryFragment.class).newInstance();
                    mainActivity.changeFragment(fragment);
                } catch (java.lang.InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e){
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Subscribe
    public void onUserEvent(UserEvent event) {
        if(event.result != null)
            tvUsername.setText(event.result.body().getUsername());
    }
}
