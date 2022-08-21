package com.varun.gbu_timetables.service;

import com.google.firebase.messaging.FirebaseMessagingService;

public abstract class mFirebaseInstanceIdService extends FirebaseMessagingService {
    public abstract void onTokenRefresh();
}
