package com.project.zhi.tigerapp.Services;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

    @SharedPref(value = SharedPref.Scope.APPLICATION_DEFAULT)
    public interface UserPrefs {
        @DefaultBoolean(false)
        boolean firstTime();
        @DefaultBoolean(true)
        boolean appActive();

        @DefaultBoolean(false)
        boolean isFile();

        @DefaultBoolean(false)
        boolean isFolder();

        @DefaultBoolean(false)
        boolean isUrl();

        @DefaultBoolean(false)
        boolean isValidDataSource();

        String urlAddress();
        String urlImagePath();
        String file();
        String voiceFolder();
        String folder();
        String gender();
        String appId();
        String host();
        String username();

        @DefaultInt(28)
        int burnDays();

        String token();
        String facialEntities();
        String voiceEntities();
        String filteredEntites();
        String allMenu();
        String nameMenu();
        String mainDemoMenu();
        String otherDemoMenu();
        String recordFolder();
    }
