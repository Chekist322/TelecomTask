package com.example.batrakov.telecomconnectionmarshmellow;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
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
 *
 * Created by batrakov on 15.09.17.
 */

class TelecomConnection extends Connection {

    private final AudioEntry mAudioEntry;

    private static class AudioEntry {

        private final int mAudioRes;
        private final String mTitle;

        AudioEntry(int aAudioRes, String aTitle) {
            mAudioRes = aAudioRes;
            mTitle = aTitle;
        }
    }

    private AudioEntry[] mAudioEntries = {
            new AudioEntry(R.raw.answer_music, "Singing machine"),
            new AudioEntry(R.raw.britney, "Britney Spears"),
            new AudioEntry(R.raw.kermit, "Kermit"),
            new AudioEntry(R.raw.majesty, "Her Majesty"),
    };

    private static final String L_TAG = "TestConnection";

    private static final int MSG_INITED = 1;
    private static final int MSG_DIALING = 2;
    private static final int MSG_ACTIVE = 3;
    private static final int MSG_ALLOW_HOLD = 4;
    private static final int MSG_DISCONNECT = 5;
    private static final int MSG_START_RECORD = 6;

    private static final int DIALING_DELAY = 2000;
    private static final int INIT_DELAY = 2000;
    private static final int ACTIVE_DELAY = 4000;
    private static final int HOLD_DELAY = 60000;
    private static final int DISCONNECT_DELAY = 1000;
    private static final int RECORD_DELAY = 3000;
    private static final float VOLUME_LEVEL = 0.5f;


    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message aMsg) {
            switch (aMsg.what) {
                case MSG_INITED:
                    Log.i("call", "inited");
                    setInitialized();
                    sendEmptyMessageDelayed(MSG_DIALING, DIALING_DELAY);
                    break;
                case MSG_DIALING:
                    setDialing();
                    setRingbackRequested(true);
                    sendEmptyMessageDelayed(MSG_ACTIVE, ACTIVE_DELAY);
                    break;
                case MSG_ACTIVE:
                    setActive();
                    setConnectionCapabilities(CAPABILITY_MUTE | CAPABILITY_SUPPORT_HOLD);
                    sendEmptyMessageDelayed(MSG_ALLOW_HOLD, HOLD_DELAY);
                    sendEmptyMessageDelayed(MSG_START_RECORD, RECORD_DELAY);
                    break;
                case MSG_ALLOW_HOLD:
                    setConnectionCapabilities(CAPABILITY_HOLD | CAPABILITY_MUTE);
                    sendEmptyMessageDelayed(MSG_DISCONNECT, DISCONNECT_DELAY);
                    break;
                case MSG_DISCONNECT:
                    Log.i("call", "disconnect");
                    disconnect();
                    break;
                case MSG_START_RECORD:
                    startMagnitophone();
                    break;
                default:
                    break;
            }
        }
    };
    private final Context mContext;
    private MediaPlayer mPlayer;

    TelecomConnection(Context aContext, Uri aAddress) {

        mContext = aContext;
        mHandler.sendEmptyMessageDelayed(MSG_INITED, INIT_DELAY);

        int id = Math.abs(aAddress.hashCode() % mAudioEntries.length);

        mAudioEntry = mAudioEntries[id];

        setAddress(Uri.fromParts(PhoneAccount.SCHEME_TEL, mAudioEntry.mTitle, ""), TelecomManager.PRESENTATION_ALLOWED);
        setInitialized();
    }

    private void log(String aMsg) {
        Log.i(L_TAG, aMsg);
    }

    @Override
    public void onStateChanged(int aState) {
        log("onStateChanged, state: " + Connection.stateToString(aState));
    }

    @Override
    public void onPlayDtmfTone(char aChar) {
         log("onPlayDtmfTone");
    }

    @Override
    public void onStopDtmfTone() {
         log("onStopDtmfTone");
    }

    @Override
    public void onDisconnect() {
        log("onDisconnect");
        mHandler.sendEmptyMessageDelayed(MSG_DISCONNECT, DISCONNECT_DELAY);
    }

    @Override
    public void onSeparate() {
        log("onSeparate");
    }

    @Override
    public void onAbort() {
        log("onAbort");
        onDisconnect();
    }

    @Override
    public void onHold() {
        log("onHold");
    }

    @Override
    public void onUnhold() {
        log("onUnhold");
    }

    @Override
    public void onAnswer(int aVideoState) {
        log("onAnswer");
    }

    @Override
    public void onReject() {
        log("onReject");
    }

    private void disconnect() {
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }
        if (getState() != STATE_DISCONNECTED) {
            setDisconnected(new DisconnectCause(DisconnectCause.CANCELED));
            destroy();
        }
    }

    private void startMagnitophone() {
        if (getState() != STATE_ACTIVE) {
            return;
        }
        MediaPlayer player = new MediaPlayer();
        mPlayer = player;
        AssetFileDescriptor afd = mContext.getResources().openRawResourceFd(mAudioEntry.mAudioRes);
        try {
            player.setVolume(VOLUME_LEVEL, VOLUME_LEVEL);
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            player.setLooping(false);
            player.prepare();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer aMp) {
                    disconnect();
                }
            });
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
