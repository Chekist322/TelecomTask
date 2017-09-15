package com.example.batrakov.telecomconnectionmarshmellow;

import android.content.ActivityNotFoundException;
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

public class ControlActivity extends AppCompatActivity {

    private PhoneAccount mPhoneAccount;
    private boolean mPhoneAccountRegisterred;
    private Button mToggleAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);


        PhoneAccountHandle handle = new PhoneAccountHandle(new ComponentName(this, TelecomConnectionService.class), "one");
        mPhoneAccount = PhoneAccount.builder(handle, "Test account")
                .setCapabilities(
                        PhoneAccount.CAPABILITY_CONNECTION_MANAGER |
                                PhoneAccount.CAPABILITY_PLACE_EMERGENCY_CALLS |
                                PhoneAccount.CAPABILITY_CALL_PROVIDER |
                                PhoneAccount.CAPABILITY_CALL_SUBJECT
                ).setShortDescription("ShortDescription").build();

        mPhoneAccountRegisterred = false;

        mToggleAccountButton = (Button) findViewById(R.id.togglePhoneAccount);



        mToggleAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TelecomManager tm = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);
                if (mPhoneAccountRegisterred){
                    tm.unregisterPhoneAccount(mPhoneAccount.getAccountHandle());
                    mToggleAccountButton.setText("Register Phone account");
                    mPhoneAccountRegisterred = false;
                } else{
                    tm.registerPhoneAccount(mPhoneAccount);
                    mToggleAccountButton.setText("UnRegister Phone account");
                    mPhoneAccountRegisterred = true;
                    Intent intent = new Intent(TelecomManager.ACTION_CHANGE_PHONE_ACCOUNTS);
                    startActivity(intent);




                    Log.i("Account Enable:", String.valueOf(mPhoneAccount.isEnabled()));
                }
            }
        });

    }



}
