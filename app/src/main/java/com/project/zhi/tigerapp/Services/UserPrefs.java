package com.project.zhi.tigerapp.Services;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
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

        @DefaultBoolean(false)
        boolean isUsingUrl();

        String urlAddres();

        String file();
        String folder();

        String gender();

        String appId();

        String host();
        String username();
        String token();

    }
