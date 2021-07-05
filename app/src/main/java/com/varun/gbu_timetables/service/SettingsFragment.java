package com.varun.gbu_timetables.service;

import android.os.Bundle;

import com.varun.gbu_timetables.R;

import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_general, rootKey);
    }
}