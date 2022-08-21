package com.varun.gbu_timetables.service;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.varun.gbu_timetables.R;
import com.varun.gbu_timetables.Utility;

/**
 * Created by varun on 8/3/2016.
 */
public class MyFirebaseInstanceIdService extends mFirebaseInstanceIdService {

    public void onStart() {
        String CurrentToken = FirebaseMessaging.getInstance().getToken().toString();

        //Log.d(this.getClass().getSimpleName(),"Inside Instance on onCreate");
        String savedToken = Utility.getFirebaseInstanceId(getApplicationContext());
        String defaultToken = getApplication().getString(R.string.pref_firebase_instance_id_default_key);

        if (CurrentToken != null && !savedToken.equalsIgnoreCase(defaultToken))
        //currentToken is null when app is first installed and token is not available
        //also skip if token is already saved in preferences...
        {
            Utility.setFirebaseInstanceId(getApplicationContext(), CurrentToken);
        }
        super.onCreate();
    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseMessaging.getInstance().getToken().toString();
        Log.d(this.getClass().getSimpleName(), "Refreshed token: " + refreshedToken);

        Utility.setFirebaseInstanceId(getApplicationContext(), refreshedToken);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);
    }
}
