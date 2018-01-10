package com.rucksack.automusic.services;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.gracenote.gnsdk.GnDescriptor;
import com.gracenote.gnsdk.GnException;
import com.gracenote.gnsdk.GnLanguage;
import com.gracenote.gnsdk.GnLicenseInputMode;
import com.gracenote.gnsdk.GnLocale;
import com.gracenote.gnsdk.GnLocaleGroup;
import com.gracenote.gnsdk.GnManager;
import com.gracenote.gnsdk.GnRegion;
import com.gracenote.gnsdk.GnStorageSqlite;
import com.gracenote.gnsdk.GnUser;
import com.gracenote.gnsdk.GnUserStore;

/**
 * Created by franco on 5/07/17.
 */

public class GnService{
    private static final java.lang.String TAG = GnService.class.getName();
    //We can set a Context in static field while we call getApplicationContext() to avoid memory leaks, because
    //if we use the activity Context, this activity can remain in memory due is still in use its Context
    private static Context sContext;
    private static volatile GnService sGnService;
    private static volatile GnManager sGnManager;
    public static volatile GnUser sGnUser;
    private static volatile GnLocale sGnLocale;
    public static final String sGnsdkLicenseString = "-- BEGIN LICENSE v1.0 F59BE5BB --\\r\\nname: \\r\\nnotes: Gracenote Open Developer Program\\r\\nstart_date: 0000-00-00\\r\\nclient_id: 165320993\\r\\nmusicid_file: enabled\\r\\nmusicid_text: enabled\\r\\nmusicid_stream: enabled\\r\\nmusicid_cd: enabled\\r\\nplaylist: enabled\\r\\nvideoid: enabled\\r\\nvideo_explore: enabled\\r\\nlocal_images: enabled\\r\\nlocal_mood: enabled\\r\\nvideoid_explore: enabled\\r\\nacr: enabled\\r\\nepg: enabled\\r\\n-- SIGNATURE F59BE5BB --\\r\\nlAADAgAfAaHmkGYZKXFSRpj0k2Y/1fKPFutPA6jk81WSpTIUqwAeZubY9qt1NfhvOLtRQIwiKJ4xjhPHNQSz5P/P6ewf\\r\\n-- END LICENSE F59BE5BB --\\r\\n";
    public static final String sGnsdkClientId = "165320993";//"843162123";
    public static final String sGnsdkClientTag = "29543F79713D00691D1E6F4AAE9F0BA3";//"4E937B773F03BA431014169770593072";
    public static final String sAppString = "AutoMusic";
    public static volatile boolean sApiInitialized = false;
    public static volatile boolean sIsInitializing = false;
    public static final int API_INITIALIZED_FROM_SPLASH = 100;
    public static final int API_INITIALIZED_AFTER_CONNECTED = 101;
    private static AsyncApiInitialization sAsyncApiInitialization;

    /**
     * We don't need instances of this class
     */
    private GnService(Context context){
        if(sContext == null) {
            sContext = context.getApplicationContext();
        }
    }


    /**
     * Set sContext for use in GNSDK methods
     * @param appContext
     */
    public static GnService withContext(Context appContext){
        if(sGnService == null){
            sGnService = new GnService(appContext);
        }
        return sGnService;
    }

    /**
     * This method initializes the API
     */
    public void initializeAPI(final int connectedFrom){
        if(sAsyncApiInitialization == null && !sApiInitialized){
            Log.d("sApiInitialized","initializing api");
            sAsyncApiInitialization = new AsyncApiInitialization();
            sAsyncApiInitialization.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, connectedFrom);
        }
    }


    private static class AsyncApiInitialization extends AsyncTask<Integer,Void,Boolean> {
        private int mConnectedFrom = API_INITIALIZED_FROM_SPLASH;
        @Override
        protected Boolean doInBackground(Integer... code) {
            //We initialize the necessary objects for using the GNSDK API in a different thread for not blocking the UI
            try {
                sGnManager = new GnManager(sContext, sGnsdkLicenseString, GnLicenseInputMode.kLicenseInputModeString);
                sGnUser = new GnUser(new GnUserStore(sContext), sGnsdkClientId, sGnsdkClientTag, sAppString);
                sGnLocale = new GnLocale(GnLocaleGroup.kLocaleGroupMusic, GnLanguage.kLanguageSpanish, GnRegion.kRegionGlobal, GnDescriptor.kDescriptorDetailed, sGnUser);
                sGnLocale.setGroupDefault();
                GnStorageSqlite.enable();
                sApiInitialized = true;
                mConnectedFrom = code[0];
                //When api could not be initialized since SplashActivity,
                //inform to user after MainActivity starts

                return true;
            } catch (GnException e) {
                e.printStackTrace();
                //If could not be established the initialization of Gracenote API, try again
                sIsInitializing = false;
                sApiInitialized = false;
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean res){
            Log.d("res",res+" " + (mConnectedFrom == API_INITIALIZED_AFTER_CONNECTED));
            if(!res){
                Job.scheduleJob(sContext);
            }

            sAsyncApiInitialization = null;
        }

    }
}
