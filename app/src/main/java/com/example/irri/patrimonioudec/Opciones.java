package com.example.irri.patrimonioudec;

import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class Opciones extends PreferenceActivity {
    Switch s1,s2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager prefsMgr = getPreferenceManager();
        prefsMgr.setSharedPreferencesName("preferencias");
        addPreferencesFromResource(R.xml.preferencias);
    }
}
