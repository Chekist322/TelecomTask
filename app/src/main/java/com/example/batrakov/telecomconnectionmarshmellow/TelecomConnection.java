package com.example.batrakov.telecomconnectionmarshmellow;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.telecom.Connection;
import android.telecom.DisconnectCause;
import android.telecom.PhoneAccount;
import android.telecom.TelecomManager;
import android.util.Log;

import java.io.IOException;

/**
 * Created by batrakov on 15.09.17.
 */

public class TelecomConnection extends Connection {

    private final AudioEntry mAudioEntry;

    private static class AudioEntry {
        private final int mAudioRes;
        private final String mTitle;

        AudioEntry(int aAudioRes, String aTitle) {
            mAudioRes = aAudioRes;
            mTitle = aTitle;
        }
    }

    AudioEntry mAudioEntries[] = {
            new AudioEntry(R.raw.answer_music, "Singing machine"),
            new AudioEntry(R.raw.britney, "Britney Spears"),
            new AudioEntry(R.raw.kermit, "Kermit"),
            new AudioEntry(R.raw.majesty, "Her Majesty"),
    };

    private static final boolean VERBOSE = true;
    private static final String L_TAG = "TestConnection";

    private static final int MSG_INITED = 1;
    private static final int MSG_DIALING = 2;
    private static final int MSG_ACTIVE = 3;
    private static final int MSG_ALLOW_HOLD = 4;
    private static final int MSG_DISCONNECT = 5;
    private static final int MSG_START_RECORD = 6;

    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INITED:
                    Log.i("call", "inited");
                    setAudioModeIsVoip(true);
                    setInitialized();
                    sendEmptyMessageDelayed(MSG_DIALING, 2 * 1000);
                    break;
                case MSG_DIALING:
                    setDialing();
                    setRingbackRequested(true);
                    sendEmptyMessageDelayed(MSG_ACTIVE, 4 * 1000);
                    break;
                case MSG_ACTIVE:
                    setActive();
                    setConnectionCapabilities(CAPABILITY_MUTE|CAPABILITY_SUPPORT_HOLD);
                    sendEmptyMessageDelayed(MSG_ALLOW_HOLD, 60 * 1000);
                    sendEmptyMessageDelayed(MSG_START_RECORD, 1 * 1000);
                    break;
                case MSG_ALLOW_HOLD:
                    setConnectionCapabilities(CAPABILITY_HOLD| CAPABILITY_MUTE);
                    sendEmptyMessageDelayed(MSG_DISCONNECT, 5 * 1000);
                    break;
                case MSG_DISCONNECT:
                    Log.i("call", "disconnect");
                    disconnect();
                    break;
                case MSG_START_RECORD:
                    startMagnitophone();
                    break;
            }
        }
    };
    private final Context mContext;
    private MediaPlayer mPlayer;

    TelecomConnection(Context aContext, Uri aAddress) {

        mContext = aContext;
        mHandler.sendEmptyMessageDelayed(MSG_INITED, 2 * 1000);

        int id = Math.abs(aAddress.hashCode() % mAudioEntries.length);

        mAudioEntry = mAudioEntries[id];

        setAddress(Uri.fromParts(PhoneAccount.SCHEME_TEL, mAudioEntry.mTitle, ""), TelecomManager.PRESENTATION_ALLOWED);
        setInitialized();
    }

    private void log(String msg) {
        Log.i(L_TAG, msg);
    }

    @Override
    public void onStateChanged(int state) {
        if (VERBOSE) log("onStateChanged, state: " + Connection.stateToString(state));
    }

    @Override
    public void onPlayDtmfTone(char c) {
        if (VERBOSE) log("onPlayDtmfTone");
    }

    @Override
    public void onStopDtmfTone() {
        if (VERBOSE) log("onStopDtmfTone");
    }

    @Override
    public void onDisconnect() {
        if (VERBOSE) log("onDisconnect");
        mHandler.sendEmptyMessageDelayed(MSG_DISCONNECT, 2 * 1000);
    }

    @Override
    public void onSeparate() {
        if (VERBOSE) log("onSeparate");
    }

    @Override
    public void onAbort() {
        if (VERBOSE) log("onAbort");
        onDisconnect();
    }

    @Override
    public void onHold() {
        if (VERBOSE) log("onHold");
    }

    @Override
    public void onUnhold() {
        if (VERBOSE) log("onUnhold");
    }

    @Override
    public void onAnswer(int videoState) {
        if (VERBOSE) log("onAnswer");
    }

    @Override
    public void onReject() {
        if (VERBOSE) log("onReject");

    }

    private void disconnect() {
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        if (getState() != STATE_DISCONNECTED) {
            setDisconnected(new DisconnectCause(DisconnectCause.CANCELED));
            destroy();
        }
    }

    private void startMagnitophone() {
        if ( getState() != STATE_ACTIVE ) return;
        MediaPlayer player = new MediaPlayer();
        mPlayer = player;
        player.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        AssetFileDescriptor afd = mContext.getResources().openRawResourceFd(mAudioEntry.mAudioRes);
        try {
            player.setVolume(0.5f, 0.5f);
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            player.setLooping(false);
            player.prepare();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    disconnect();
                }
            });
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
