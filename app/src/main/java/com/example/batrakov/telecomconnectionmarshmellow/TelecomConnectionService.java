package com.example.batrakov.telecomconnectionmarshmellow;

import android.telecom.Connection;
import android.telecom.ConnectionRequest;
import android.telecom.ConnectionService;
import android.telecom.PhoneAccountHandle;
import android.util.Log;

/**
 * Created by batrakov on 15.09.17.
 */

public class TelecomConnectionService extends ConnectionService {

    private void log(String msg){
        Log.i("TelecomService", msg);
    }

    @Override
    public Connection onCreateIncomingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        log("onCreateIncomingConnection started");
        return super.onCreateIncomingConnection(connectionManagerPhoneAccount, request);
    }

    @Override
    public Connection onCreateOutgoingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        log("onCreateOutgoingConnection started");
        return new TelecomConnection(getBaseContext(), request.getAddress());
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
