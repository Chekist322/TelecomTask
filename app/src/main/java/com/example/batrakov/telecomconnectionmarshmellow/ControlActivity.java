package com.example.batrakov.telecomconnectionmarshmellow;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by batrakov on 15.09.17. *
 */
public class ControlActivity extends AppCompatActivity {

    private PhoneAccount mPhoneAccount;
    private boolean mPhoneAccountRegistered;
    private Button mToggleAccountButton;

    @Override
    protected void onCreate(Bundle aSavedInstanceState) {
        super.onCreate(aSavedInstanceState);
        setContentView(R.layout.activity_control);

        PhoneAccountHandle handle = new PhoneAccountHandle(
                new ComponentName(this, TelecomConnectionService.class), "one");
        mPhoneAccount = PhoneAccount.builder(handle, "Test account")
                .setCapabilities(
                        PhoneAccount.CAPABILITY_CONNECTION_MANAGER
                                | PhoneAccount.CAPABILITY_PLACE_EMERGENCY_CALLS
                                | PhoneAccount.CAPABILITY_CALL_PROVIDER
                                | PhoneAccount.CAPABILITY_CALL_SUBJECT
                ).setShortDescription("ShortDescription").build();

        mPhoneAccountRegistered = false;

        mToggleAccountButton = (Button) findViewById(R.id.togglePhoneAccount);

        mToggleAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View aView) {
                TelecomManager tm = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);
                if (mPhoneAccountRegistered) {
                    tm.unregisterPhoneAccount(mPhoneAccount.getAccountHandle());
                    mToggleAccountButton.setText("Register Phone account");
                    mPhoneAccountRegistered = false;
                } else {
                    tm.registerPhoneAccount(mPhoneAccount);
                    mToggleAccountButton.setText("UnRegister Phone account");
                    mPhoneAccountRegistered = true;
                    Intent intent = new Intent(TelecomManager.ACTION_CHANGE_PHONE_ACCOUNTS);
                    startActivity(intent);
                    Log.i("Account Enable:", String.valueOf(mPhoneAccount.isEnabled()));
                }
            }
        });
    }



}
