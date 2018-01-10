package com.rucksack.automusic;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.rucksack.automusic.services.ConnectivityDetector;
import com.rucksack.automusic.services.DetectorInternetConnection;
import com.rucksack.automusic.services.GnService;
import com.rucksack.automusic.services.Job;
import com.rucksack.automusic.utilities.Constants;
import com.rucksack.automusic.utilities.RequiredPermissions;
import com.rucksack.automusic.utilities.Settings;

/**
 * Created by franco on 6/11/16.
 */

public class SplashActivity extends AppCompatActivity{

    public static final String APP_SHARED_PREFERENCES = "AutoMusicTagFixer";
    public static boolean DEBUG_MODE = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ConnectivityDetector.withContext(this).setStartedFrom(GnService.API_INITIALIZED_FROM_SPLASH).startCheckingConnection();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //Get default or saved values of settings
        Settings.SETTING_RENAME_FILE_AUTOMATIC_MODE = preferences.getBoolean("key_rename_file_automatic_mode", true);
        Settings.SETTING_RENAME_FILE_MANUAL_MODE = preferences.getBoolean("key_rename_file_manual_mode", true);
        Settings.SETTING_REPLACE_STRANGE_CHARS_MANUAL_MODE = preferences.getBoolean("key_replace_strange_chars_manual_mode",true);
        Settings.ALL_SELECTED = preferences.getBoolean("allSelected",false);
        Settings.SETTING_OVERWRITE_ALL_TAGS_AUTOMATIC_MODE = preferences.getBoolean("key_overwrite_all_tags_automatic_mode", true);
        Settings.SETTING_RENAME_FILE_SEMI_AUTOMATIC_MODE = preferences.getBoolean("key_rename_file_semi_automatic_mode", true);
        Settings.SETTING_USE_EMBED_PLAYER = preferences.getBoolean("key_use_embed_player",true);
        Settings.BACKGROUND_CORRECTION = preferences.getBoolean("key_background_service", true);
        String imageSizeSaved = preferences.getString("key_size_album_art","1000");
        Settings.SETTING_SIZE_ALBUM_ART = Settings.setValueImageSize(imageSizeSaved);


        preferences = null;

        //Is first use of app?
        preferences =  getSharedPreferences(APP_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        boolean firstTime= preferences.getBoolean("first", true);
        Settings.SETTING_SORT = preferences.getInt(Constants.SORT_KEY, 0);
        //do we have permission to access files?
        RequiredPermissions.ACCESS_GRANTED_FILES = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

            if (firstTime) {
                //Is first app use
                Intent intent = new Intent(this, ScreenSlidePagerActivity.class);
                startActivity(intent);
            } else {

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

            }

        //}
        //else {
        //    Intent intent = new Intent(this, ScreenSlidePagerActivity.class);
        //    startActivity(intent);
        //}
        preferences = null;
        finish();
    }

}
