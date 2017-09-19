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

    /**
     *
     * @param aMsg message
     */
    private void log(String aMsg) {
        Log.i("TelecomService", aMsg);
    }

    @Override
    public Connection onCreateIncomingConnection(
            PhoneAccountHandle aConnectionManagerPhoneAccount, ConnectionRequest aRequest) {
        log("onCreateIncomingConnection started");
        return super.onCreateIncomingConnection(aConnectionManagerPhoneAccount, aRequest);
    }

    @Override
    public Connection onCreateOutgoingConnection(
            PhoneAccountHandle aConnectionManagerPhoneAccount, ConnectionRequest aRequest) {
        log("onCreateOutgoingConnection started");
        return new TelecomConnection(getBaseContext(), aRequest.getAddress());
    }
}
