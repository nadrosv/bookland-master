package com.nadrowski.sylwester.bookland;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nadrowski.sylwester.bookland.models.LoginRequest;
import com.nadrowski.sylwester.bookland.models.LoginResponse;
import com.nadrowski.sylwester.bookland.models.RegisterRequest;
import com.nadrowski.sylwester.bookland.services.AppData;
import com.nadrowski.sylwester.bookland.services.UserServiceAPI;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by korSt on 04.11.2016.
 */

public class LoginActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "loginFile";
    Retrofit retrofit = new Retrofit.Builder()
//            .baseUrl("http://bookland.azurewebsites.net/")
            .baseUrl("http://192.168.0.102:8080/")
//            .baseUrl("http://ec2-52-28-61-254.eu-central-1.compute.amazonaws.com:8080/booklandApp-0.0.1-SNAPSHOT/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    UserServiceAPI userService = retrofit.create(UserServiceAPI.class);




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);

        if(isNetworkAvailable() != true ) {
            for (int i=0; i < 3; i++)
            {
                Toast.makeText(getBaseContext(), R.string.login_no_connection,
                        Toast.LENGTH_LONG).show();
            }
        }
        else {
            if(hasLoggedIn) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else {
                setContentView(R.layout.activity_login);
                final EditText username = (EditText) findViewById(R.id.et_username);
                final EditText password = (EditText) findViewById(R.id.et_password);
                Button loginButton = (Button) findViewById(R.id.b_login);
                Button registerButton = (Button) findViewById(R.id.b_register);


                final EditText registerUsername = (EditText) findViewById(R.id.et_username_register);
                final EditText registerPassword = (EditText) findViewById(R.id.et_password_register);
                final EditText registerPasswordRepeat = (EditText) findViewById(R.id.et_password_register_repeat);
                final Button createAccButton = (Button) findViewById(R.id.b_create_account);

                createAccButton.setVisibility(View.INVISIBLE);
                registerUsername.setVisibility(View.INVISIBLE);
                registerPassword.setVisibility(View.INVISIBLE);
                registerPasswordRepeat.setVisibility(View.INVISIBLE);

                loginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isLogged(username.getText().toString(), password.getText().toString());
                    }
                });

                createAccButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!registerPassword.getText().toString().equals(registerPasswordRepeat.getText().toString())) {
                            Utilities.displayMessage("Haslo sie nie zgadza", LoginActivity.this);
                            return;
                        }
                        register(registerUsername.getText().toString(),
                                "test@mail.pl",
                                registerPassword.getText().toString());
                    }
                });

                registerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        createAccButton.setVisibility(View.VISIBLE);
                        registerUsername.setVisibility(View.VISIBLE);
                        registerPassword.setVisibility(View.VISIBLE);
                        registerPasswordRepeat.setVisibility(View.VISIBLE);
                    }
                });
            }
        }


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean isLogged(String username, String password) {
        LoginRequest loginRequest = new LoginRequest(username, password);
        Call<LoginResponse> login = userService.login(loginRequest);
        login.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                Log.d("isOK", String.valueOf(response.isSuccessful()));

                if (response.isSuccessful()){
                    Log.d("response", response.body().toString());
                    LoginResponse resp = new LoginResponse(response.body().getUserId(), response.body().getToken());
                    AppData.loggedUser = resp;

                    proceedUser();

                } else {
                    Utilities.displayMessage(getString(R.string.err_no_user), LoginActivity.this);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.d("fail", t.getMessage());

            }
        });
        return false;
    }

    private void proceedUser() {
        setAsLogged();

        try {
            Utilities.saveToFile("userData", AppData.loggedUser, LoginActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
            Utilities.displayMessage(getString(R.string.err_savefile), this);
        }

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();
    }


    private void setAsLogged() {
        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("hasLoggedIn", true);
        editor.apply();
    }

//    private void displayMessage(String text) {
//        Toast.makeText(LoginActivity.this, text,
//                Toast.LENGTH_LONG).show();
//    }

    private void register(String username, String email, String password) {
        RegisterRequest registerRequest = new RegisterRequest(username, email, password);

        Call<ResponseBody> register = userService.register(registerRequest);
        register.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    Utilities.displayMessage(getString(R.string.register_succes), LoginActivity.this);
                } else {
                    Utilities.displayMessage(getString(R.string.register_fail), LoginActivity.this);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("fail", "throw");
            }
        });

    }

}