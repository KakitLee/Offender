package com.project.zhi.tigerapp.Fragment;

import android.app.AlertDialog;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.project.zhi.tigerapp.R;
import com.project.zhi.tigerapp.Services.UserPrefs_;

import org.androidannotations.annotations.AfterPreferences;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.PreferenceByKey;
import org.androidannotations.annotations.PreferenceChange;
import org.androidannotations.annotations.PreferenceScreen;
import org.androidannotations.annotations.sharedpreferences.Pref;

@PreferenceScreen(R.xml.pref_general)
@EFragment
public class NotificationFragment extends PreferenceFragment {
    AlertDialog dialog;

    @Pref
    UserPrefs_ userPrefs;

    @PreferenceByKey(R.string.pref_no_burn_days)
    EditTextPreference burnDays;

    @PreferenceChange(R.string.pref_no_burn_days)
    void updateBurnDays(Preference preference, Integer newValue){
        userPrefs.burnDays().put(newValue);
        burnDays.setSummary(newValue.toString());
    }

    @AfterPreferences
    void initPrefs() {
        burnDays.setText(userPrefs.burnDays().get().toString());
        burnDays.setSummary(userPrefs.burnDays().get().toString());
    }

}
