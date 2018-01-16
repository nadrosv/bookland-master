package com.nadrowski.sylwester.bookland;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.nadrowski.sylwester.bookland.services.AppData;
import com.nadrowski.sylwester.bookland.services.UserService;

/**
 * Created by korSt on 05.12.2016.
 */

public class MyPreferencesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            findPreference("lat_preference").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setDefaultValue(newValue);
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

                    new UserService().setPreferences(AppData.loggedUser.getUserId(),
                            Float.parseFloat(preferences.getString("radius_preference", "5")),
                            Float.parseFloat((String) newValue),
                            Float.parseFloat(preferences.getString("lon_preference", "55"))
                    );
                    return true;
                }
            });

            findPreference("lon_preference").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setDefaultValue(newValue);
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

                    new UserService().setPreferences(AppData.loggedUser.getUserId(),
                            Float.parseFloat(preferences.getString("radius_preference", "5")),
                            Float.parseFloat(preferences.getString("lat_preference", "55")),
                            Float.parseFloat((String) newValue)
                    );
                    return true;
                }
            });

            findPreference("radius_preference").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setDefaultValue(newValue);
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

                    new UserService().setPreferences(AppData.loggedUser.getUserId(),
                            Float.parseFloat((String) newValue),
                            Float.parseFloat(preferences.getString("lat_preference", "55")),
                            Float.parseFloat(preferences.getString("lon_preference", "55"))
                    );
                    return true;
                }
            });
        }
    }

}