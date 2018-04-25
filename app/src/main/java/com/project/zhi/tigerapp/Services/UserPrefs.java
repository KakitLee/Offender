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

        String file();
        String folder();

        String country();
        String dateOfBirth();
        String gender();
        String heightInCm();
        String weightInKg();
        String participantNumber();
        String appActivationDate();
        String appDeactivationDate();
        String participant();
        String appData();

        @DefaultString("08:00")
        String morningNotification();

        @DefaultString("22:00")
        String eveningNotification();

        String appId();

    }
