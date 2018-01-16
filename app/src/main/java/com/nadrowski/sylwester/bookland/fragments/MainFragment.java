package com.nadrowski.sylwester.bookland.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.otto.Subscribe;
import com.nadrowski.sylwester.bookland.R;
import com.nadrowski.sylwester.bookland.adapters.NewBooksAdapter;
import com.nadrowski.sylwester.bookland.bus.BusProvider;
import com.nadrowski.sylwester.bookland.events.NewBooksEvent;
import com.nadrowski.sylwester.bookland.events.UserEvent;
import com.nadrowski.sylwester.bookland.models.Book;
import com.nadrowski.sylwester.bookland.models.User;
import com.nadrowski.sylwester.bookland.services.AppData;
import com.nadrowski.sylwester.bookland.services.BookService;
import com.nadrowski.sylwester.bookland.services.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by korSt on 31.10.2016.
 */

public class MainFragment extends Fragment/* implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener */{

    OnBookSelectedListener mCallback;
    OnUserSelectedListener userCallback;
    //disable map for a moment
//    MapView mMapView;
//    private GoogleMap googleMap;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<Book> newBooks;
    private ArrayList<User> nearUsers;

    private BookService bookService;
    private UserService userService;
    Handler h;
    Runnable r;


//    @Override
//    public boolean onMarkerClick(Marker marker) {
//        userCallback.onUserSelected(nearUsers.get((Integer) marker.getTag()));
//        return false;
//    }


    @Override
    public void onResume() {
//        mMapView.onResume();

        super.onResume();
        BusProvider.getInstance().register(this);

        bookService = new BookService();
        userService = new UserService();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String latPref = preferences.getString("lat_preference", "51");
        String lonPref = preferences.getString("lon_preference", "19");
        final LatLng pos = new LatLng(Double.parseDouble(latPref), Double.parseDouble(lonPref));

//        mMapView.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(GoogleMap googleMap) {
//                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 10));
//                googleMap.setOnMarkerClickListener(MainFragment.this);
//
//            }
//        });

        userService.getNearUsers(AppData.loggedUser.getUserId());
        bookService.loadNewBooks();

//        h = new Handler();
//        r = new Runnable() {
//            @Override
//            public void run() {
//                bookService.loadNewBooks();
//                h.postDelayed(this, 5000);
//            }
//        };
//        h.post(r);
    }


    @Override
    public void onStart() {
        super.onStart();
        ((NewBooksAdapter) mAdapter).setOnItemClickListener(new NewBooksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Book newBook = newBooks.get(position);
                mCallback.onBookSelected(newBook, true);
//                LatLng pos = new LatLng(newBooks.get(position).getLocalLat(), newBooks.get(position).getLocalLon());
//                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 10));

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnBookSelectedListener) context;
            userCallback = (OnUserSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement Listeners");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        newBooks = new ArrayList<>();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.books_recycler_view);

        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new NewBooksAdapter(getActivity(), newBooks);
        mRecyclerView.setAdapter(mAdapter);

//        initMap(rootView, savedInstanceState);

        return rootView;
    }

//    public void initMap(View rootView, Bundle savedInstanceState) {
//        mMapView = (MapView) rootView.findViewById(R.id.mapView);
//        try {
//            // Temporary fix for crash issue
//            mMapView.onCreate(savedInstanceState);
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }
////        mMapView.onCreate(savedInstanceState);
//        mMapView.onResume();
////        mMapView.getMapAsync(this);
//        try {
//            MapsInitializer.initialize(getActivity().getApplicationContext());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        mMapView.getMapAsync(this);
//    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//
//        this.googleMap = googleMap;
//        this.googleMap.setOnMarkerClickListener(MainFragment.this);
//
//        if (nearUsers != null)
//            addUserMarkers(nearUsers);
//    }

    @Override
    public void onPause() {
//        mMapView.onPause();
        super.onPause();
        BusProvider.getInstance().unregister(this);
//        h.removeCallbacks(r);
    }


    @Subscribe
    public void onBooksEvent(NewBooksEvent event) {
        setAdapter(event.result.body());
    }

    @Subscribe
    public void onUserEvent(UserEvent event) {
        if (event.results == null)
            return;
        nearUsers = (ArrayList<User>) event.results;
//        addUserMarkers(nearUsers);
    }

    public void setAdapter(List<Book> books) {
        if (newBooks.size() != books.size()) {
//            newBooks = (ArrayList<Book>) books;
            newBooks.clear();
            newBooks.addAll(books);
            mAdapter.notifyItemRangeChanged(0, books.size());
        }
    }

//    public void addUserMarkers(ArrayList<User> users) {
//        for (User u : users) {
//            LatLng pos = new LatLng(u.getPrefLocalLat(), u.getPrefLocalLon());
//            Marker bookMarker = googleMap.addMarker(new MarkerOptions().position(pos)
//                    .title(u.getUsername()).snippet("User"));
//            bookMarker.setTag(users.indexOf(u));
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (mMapView != null)
//        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
//        if (mMapView != null)
//        mMapView.onLowMemory();
    }
}

