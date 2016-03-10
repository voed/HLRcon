package voed.voed.hlrcon;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;


public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    public Preference pref;

    ListPreference listPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        listPreference = (ListPreference) findPreference("prefAutoRefreshInt");
        findPreference("prefAutoRefreshInt").setSummary(listPreference.getEntry());
        changepref(PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("prefAutoRefreshInt")) {
            findPreference("prefAutoRefreshInt").setSummary(listPreference.getEntry());
        }
        else if(key.equals("prefAutoRefresh")) {
            changepref(sharedPreferences);
        }
    }

    public void changepref(SharedPreferences prefs)
    {
        pref = findPreference("prefAutoRefreshInt");
        if(prefs.getBoolean("prefAutoRefresh", false)) {
            pref.setEnabled(true);
        }
        else
        {
            pref.setEnabled(false);
        }
    }



}