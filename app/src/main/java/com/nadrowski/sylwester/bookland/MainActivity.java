package com.nadrowski.sylwester.bookland;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.nadrowski.sylwester.bookland.bus.BusProvider;
import com.nadrowski.sylwester.bookland.events.UserEvent;
import com.nadrowski.sylwester.bookland.fragments.AddBookFragment;
import com.nadrowski.sylwester.bookland.fragments.BookDetailsFragment;
import com.nadrowski.sylwester.bookland.fragments.MainFragment;
import com.nadrowski.sylwester.bookland.fragments.MyBooksFragment;
import com.nadrowski.sylwester.bookland.fragments.OnBookSelectedListener;
import com.nadrowski.sylwester.bookland.fragments.OnTransactionSelectedListener;
import com.nadrowski.sylwester.bookland.fragments.OnUserSelectedListener;
import com.nadrowski.sylwester.bookland.fragments.ProfileFragment;
import com.nadrowski.sylwester.bookland.fragments.ReturnBookFragment;
import com.nadrowski.sylwester.bookland.fragments.SearchFragment;
import com.nadrowski.sylwester.bookland.fragments.HistoryFragment;
import com.nadrowski.sylwester.bookland.fragments.TransactionFragment;
import com.nadrowski.sylwester.bookland.fragments.TransactionsFragment;
import com.nadrowski.sylwester.bookland.fragments.UserFragment;
import com.nadrowski.sylwester.bookland.models.Book;
import com.nadrowski.sylwester.bookland.models.LoginResponse;
import com.nadrowski.sylwester.bookland.models.Transaction;
import com.nadrowski.sylwester.bookland.models.User;
import com.nadrowski.sylwester.bookland.services.AppData;
import com.nadrowski.sylwester.bookland.services.BookService;
import com.nadrowski.sylwester.bookland.services.TransactionService;
import com.nadrowski.sylwester.bookland.services.UserService;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnBookSelectedListener, OnTransactionSelectedListener,
        OnUserSelectedListener {

    Fragment currentFragment;
    DrawerLayout drawer;
    NavigationView navigationView;
    FloatingActionButton fab;
    TextView tvUsername;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentFragment != null) {
            getSupportFragmentManager().putFragment(outState, "CURR_FRAG", currentFragment);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        new UserService().getUser(AppData.loggedUser.getUserId());

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        tvUsername = (TextView) findViewById(R.id.tv_nav_username);
        setSupportActionBar(toolbar);


        if (savedInstanceState != null) {
            Fragment fragment = getSupportFragmentManager().getFragment(savedInstanceState, "CURR_FRAG");
            getSupportFragmentManager().beginTransaction().replace(R.id.flContent,fragment).addToBackStack(null).commit();
            currentFragment = fragment;
        } else {
            navigationView.setCheckedItem(0);
            onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_home));

        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setVisibility(View.INVISIBLE);
                Fragment fragment = null;
                try {
                    fragment = AddBookFragment.class.newInstance();
                    currentFragment = fragment;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.flContent, fragment, "DISP_FRAG").addToBackStack(null);
                transaction.commit();
            }
        });


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
//                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
//                invalidateOptionsMenu();
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        AppData.loggedUser = getUserFromFile();
        NotificationEventReceiver.setupAlarm(getApplicationContext());
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = drawer.isDrawerOpen(navigationView);
//        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);

    }

    private LoginResponse getUserFromFile() {
        LoginResponse user = null;
        try {
            user = (LoginResponse) Utilities.loadFromFile("userData", MainActivity.this);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        currentFragment = getSupportFragmentManager().findFragmentByTag("DISP_FRAG");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = null;
        String title = "Share Book";
        switch (id) {
            case R.id.nav_home:
                fragmentClass = MainFragment.class;
                title = getResources().getString(R.string.home);
                break;
            case R.id.nav_profile:
                fragmentClass = ProfileFragment.class;
                title = getResources().getString(R.string.profile);
                break;
            case R.id.nav_mybooks:
                fragmentClass = MyBooksFragment.class;
                title = getResources().getString(R.string.my_books);
                break;

            case R.id.nav_search:
                fragmentClass = SearchFragment.class;
                title = getResources().getString(R.string.search);
                break;
            case R.id.nav_transactions:
                fragmentClass = TransactionsFragment.class;
                title = getResources().getString(R.string.activity);
                break;
            case R.id.nav_history:
                fragmentClass = HistoryFragment.class;
                title = getResources().getString(R.string.history);
                break;
            case R.id.nav_settings:
                Intent settingsIntent = new Intent(this, MyPreferencesActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.nav_logout:
                logout();
                break;
            default:
                fragmentClass = MainFragment.class;
        }

        try {
            fab.setVisibility(View.VISIBLE);
            fragment = (Fragment) fragmentClass.newInstance();
             currentFragment = fragment;
            Log.d("count ", String.valueOf(getSupportFragmentManager().getBackStackEntryCount()));
//            getSupportFragmentManager().popBackStack();
//            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.flContent, fragment, "DISP_FRAG");
            if (fragmentClass.equals(MainFragment.class)) {
                transaction.commit();
            } else {
                getSupportFragmentManager().popBackStack("Current", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                transaction.addToBackStack("Current").commit();
//                transaction.commit();

            }
//            getSupportFragmentManager().beginTransaction().replace(R.id.flContent, fragment, "DISP_FRAG").addToBackStack(null).commit();

            getSupportActionBar().setTitle(title);
            Log.d("ID", String.valueOf(fragment.getId()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public FloatingActionButton getFloatingActionButton() {
        return fab;
    }

    private void logout() {
        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("hasLoggedIn", false).apply();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


    @Override
    public void onBookSelected(Book book, boolean showRentButton) {
        Fragment fragment = null;
        try {
            fragment = BookDetailsFragment.class.newInstance();
            currentFragment = fragment;
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bundle args = new Bundle();
        args.putSerializable(BookDetailsFragment.ARG_BOOK, book);
        args.putBoolean(BookDetailsFragment.ARG_SHOW_RENT_BUTTON, showRentButton);
//        args.putInt(BookDetailsFragment.ARG_POSITION, position);
        fragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flContent, fragment, "DISP_FRAG").addToBackStack(null);
        transaction.commit();

    }

    @Override
    public void onRentBookSelected(Book book) {
        Utilities.displayMessage(getString(R.string.wait_accept), this);
        new TransactionService().startTransaction(AppData.loggedUser, book);

        Fragment fragment = null;
        try {
            fragment = MainFragment.class.newInstance();
            currentFragment = fragment;
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flContent, fragment, "DISP_FRAG").addToBackStack(null);
        transaction.commit();

//        int i = getSupportFragmentManager().getBackStackEntryCount() - 1;
//        currentFragment = (Fragment) getSupportFragmentManager().getBackStackEntryAt(i);
//        getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onDeleteBookSelected(Long bookId) {
        new BookService().deleteBook(bookId);
        Utilities.displayMessage("Usunieto ksiazke", this);

        Fragment fragment = null;
        try {
            fragment = MainFragment.class.newInstance();
            currentFragment = fragment;
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flContent, fragment, "DISP_FRAG").addToBackStack(null);
        transaction.commit();
    }

    public void changeFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flContent, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onTransactionSelected(Transaction transaction, Boolean closed) {
        Fragment fragment = null;
        fab.setVisibility(View.INVISIBLE);
        try {
            fragment = TransactionFragment.class.newInstance();
            currentFragment = fragment;
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bundle args = new Bundle();
        args.putSerializable(TransactionFragment.ARG_TRANSACTION, transaction);
        args.putBoolean(TransactionFragment.ARG_CLOSED, closed);
        fragment.setArguments(args);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.flContent, fragment, "DISP_FRAG");
        fragmentTransaction.addToBackStack(null).commit();
    }

    @Override
    public void onCloseTransaction(Transaction transaction) {
        Fragment fragment = null;
        try {
            fragment = ReturnBookFragment.class.newInstance();
            currentFragment = fragment;
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bundle args = new Bundle();
        args.putSerializable(ReturnBookFragment.ARG_TRANSACTION, transaction);
        fragment.setArguments(args);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.flContent, fragment, "DISP_FRAG");
        fragmentTransaction.addToBackStack(null).commit();
    }

    @Override
    public void onUserSelected(User user) {
        Fragment fragment = null;
        try {
            fragment = UserFragment.class.newInstance();
            currentFragment = fragment;
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bundle args = new Bundle();
        args.putSerializable(UserFragment.ARG_USER, user);
        fragment.setArguments(args);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.flContent, fragment, "DISP_FRAG");
        fragmentTransaction.addToBackStack(null).commit();
    }

    @Subscribe
    public void onUserEvent(UserEvent event) {
        if (event.result == null)
            return;
        tvUsername = (TextView) findViewById(R.id.tv_nav_username);
        tvUsername.setText(event.result.body().getUsername());
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("lat_preference", String.valueOf(event.result.body().getPrefLocalLat())).apply();
        editor.putString("lon_preference", String.valueOf(event.result.body().getPrefLocalLon())).apply();
        editor.putString("radius_preference", String.valueOf(event.result.body().getPrefLocalRadius())).apply();
    }
}
