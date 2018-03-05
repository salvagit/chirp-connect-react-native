package com.chirpconnect.rctchirpconnect;

import java.util.Map;
import java.util.HashMap;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import chirpconnect.Chirpconnect;
import io.chirp.connect.ChirpConnect;
import io.chirp.connect.interfaces.ConnectEventListener;
import io.chirp.connect.interfaces.ConnectAuthenticationStateListener;
import io.chirp.connect.models.ChirpError;
import io.chirp.connect.models.ConnectState;


public class RCTChirpConnectModule extends ReactContextBaseJavaModule {

    private static final String TAG = "ChirpConnect";
    private ChirpConnect chirpConnect;
    private ReactContext context;

    @Override
    public String getName() {
        return TAG;
    }

    public RCTChirpConnectModule(ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext;
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("CHIRP_CONNECT_STATE_STOPPED", 0);
        constants.put("CHIRP_CONNECT_STATE_PAUSED", 1);
        constants.put("CHIRP_CONNECT_STATE_RUNNING", 2);
        constants.put("CHIRP_CONNECT_STATE_SENDING", 3);
        constants.put("CHIRP_CONNECT_STATE_RECEIVING", 4);
        return constants;
    }

    @ReactMethod
    public void init(String key, String secret) {
        chirpConnect = new ChirpConnect(this.getCurrentActivity(), key, secret, new ConnectAuthenticationStateListener() {

            @Override
            public void onAuthenticationSuccess() {}

            @Override
            public void onAuthenticationError(ChirpError chirpError) {
                onError(context, chirpError.getMessage());
            }
        });

        chirpConnect.setListener(new ConnectEventListener() {

            @Override
            public void onSending(byte[] data) {
                String hexData = "";
                if (data != null) {
                    hexData = bytesToHex(data);
                }
                WritableMap params = Arguments.createMap();
                params.putString("data", hexData);
                context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("onSending", params);
            }

            @Override
            public void onSent(byte[] data) {
                String hexData = "";
                if (data != null) {
                    hexData = bytesToHex(data);
                }
                WritableMap params = Arguments.createMap();
                params.putString("data", hexData);
                context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("onSent", params);
            }

            @Override
            public void onReceiving() {
                WritableMap params = Arguments.createMap();
                context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("onReceiving", params);
            }

            @Override
            public void onReceived(byte[] data) {
                String hexData = "";
                if (data != null) {
                    hexData = bytesToHex(data);
                }
                WritableMap params = Arguments.createMap();
                params.putString("data", hexData);
                context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("onReceived", params);
            }

            @Override
            public void onStateChanged(byte oldState, byte newState) {
                WritableMap params = Arguments.createMap();
                params.putInt("status", newState);
                context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("onStateChanged", params);
            }

            @Override
            public void onSystemVolumeChanged(int oldVolume, int newVolume) {
                // TODO: Implement
            }
        });
    }

    @ReactMethod
    public void setLicence(String licence) {
        ChirpError setLicenceError = chirpConnect.setLicenceString(licence);
        if (setLicenceError.getCode() > 0) {
            onError(context, setLicenceError.getMessage());
        }
    }

    @ReactMethod
    public void start() {
        ChirpError error = chirpConnect.start();
        if (error.getCode() > 0) {
            onError(context, error.getMessage());
        }
    }

    @ReactMethod
    public void stop() {
        ChirpError error = chirpConnect.stop();
        if (error.getCode() > 0) {
            onError(context, error.getMessage());
        }
    }

    @ReactMethod
    public void sendRandom() {
        long maxSize = chirpConnect.getMaxPayloadLength();
        byte[] payload = chirpConnect.randomPayload(maxSize);

        if (maxSize < payload.length) {
            onError(context, "Invalid payload");
            return;
        }
        ChirpError error = chirpConnect.send(payload);
        if (error.getCode() > 0) {
            onError(context, error.getMessage());
        }
    }

    private void onError(ReactContext reactContext,
                         String error) {
        WritableMap params = Arguments.createMap();
        params.putString("message", error);
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("onError", params);
    }

    public static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for(byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}
