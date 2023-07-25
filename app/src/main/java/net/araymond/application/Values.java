package net.araymond.application;

import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;
import java.util.Locale;

public class Values {
    static int total, timeScheme = TimeFormat.CLOCK_24H;
    static String language = "en", country = "us", dateFormat = "MM-dd-yyyy", timeFormat = "HH:mm";
    static ArrayList<Account> accounts = new ArrayList<>();
    static ArrayList<String> categories = new ArrayList<>();
    static ArrayList<String> accountsNames = new ArrayList<>();
    static Locale locale = new Locale(language, country);
}
