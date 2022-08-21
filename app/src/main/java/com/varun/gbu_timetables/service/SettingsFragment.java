package com.varun.gbu_timetables.service;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.varun.gbu_timetables.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_general, rootKey);
    }
}