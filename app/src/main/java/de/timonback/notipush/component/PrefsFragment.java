package de.timonback.notipush.component;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import de.timonback.notipush.R;

public class PrefsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}