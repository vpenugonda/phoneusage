package edu.fairfield.phoneusage.controllers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import edu.fairfield.phoneusage.R;
import edu.fairfield.phoneusage.views.SeekBarPreference;

public class SettingsFragment extends PreferenceFragment implements UpdatableFragment, Preference.OnPreferenceClickListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settingsfragment);
        SeekBarPreference seekBar = (SeekBarPreference) findPreference("PERCENTILE");
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        seekBar.setSummary(prefs.getInt("PERCENTILE", 50) + "%");
        CheckBoxPreference check = (CheckBoxPreference) getPreferenceManager().findPreference("ANTISOCIAL_ALERTS");
        check.setOnPreferenceClickListener(this);
    }

    @Override
    public void updateUI() {
        // update UI elements when tab selected
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        Log.d("SettingsFragment", preference.getKey());
        if (preference.getKey().equals("ANTISOCIAL_ALERTS")) {
            CheckBoxPreference cbPref = (CheckBoxPreference) preference;
            MainActivity mainAct = (MainActivity) getActivity();
            if (cbPref.isChecked()) {
                Log.d("SettingsFragment", "isChecked");
                mainAct.restartVoiceThread();

            } else {
                mainAct.stopVoiceThread();
            }
        }
        return false;
    }
}
