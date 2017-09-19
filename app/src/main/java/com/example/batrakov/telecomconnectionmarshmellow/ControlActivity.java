package com.example.batrakov.telecomconnectionmarshmellow;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.view.View;
import android.widget.Button;

/**
 * Created by batrakov on 15.09.17. *
 */
public class ControlActivity extends AppCompatActivity {

    private PhoneAccount mPhoneAccount;
    private Button mToggleAccountButton;

    @Override
    protected void onCreate(Bundle aSavedInstanceState) {
        super.onCreate(aSavedInstanceState);
        setContentView(R.layout.activity_control);

        final PhoneAccountHandle handle = new PhoneAccountHandle(
                new ComponentName(this, TelecomConnectionService.class), "one");
        mPhoneAccount = PhoneAccount.builder(handle, "Test account")
                .setCapabilities(
                        PhoneAccount.CAPABILITY_CONNECTION_MANAGER
                                | PhoneAccount.CAPABILITY_PLACE_EMERGENCY_CALLS
                                | PhoneAccount.CAPABILITY_CALL_PROVIDER
                                | PhoneAccount.CAPABILITY_CALL_SUBJECT
                ).setShortDescription("ShortDescription").build();


        mToggleAccountButton = (Button) findViewById(R.id.togglePhoneAccount);

        if (isPhoneAccountRegistered(handle)) {
            mToggleAccountButton.setText("UnRegister Phone account");
        } else {
            mToggleAccountButton.setText("Register Phone account");
        }

        mToggleAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View aView) {
                TelecomManager tm = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);
                if (isPhoneAccountRegistered(handle)) {
                    tm.unregisterPhoneAccount(mPhoneAccount.getAccountHandle());
                    mToggleAccountButton.setText("Register Phone account");
                } else {
                    tm.registerPhoneAccount(mPhoneAccount);
                    mToggleAccountButton.setText("UnRegister Phone account");
                    Intent intent = new Intent(TelecomManager.ACTION_CHANGE_PHONE_ACCOUNTS);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean isPhoneAccountRegistered(PhoneAccountHandle aHandle) {
        TelecomManager tm = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);
        return tm.getPhoneAccount(aHandle) != null;
    }
}
