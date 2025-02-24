package club.ccit.iots.common;

import android.util.Log;

public class LogUtils {
    public static void i(String message) {
        if (message == null) {
            Log.i("LOG111", "null");
        } else {
            Log.i("LOG111", message);
        }
    }

    public static void socket(String message) {
        if (message == null) {
            Log.i("Socket", "null");
        } else {
            Log.i("Socket", message);
        }
    }

    public static void ble(String message) {
        if (message == null) {
            Log.i("IotBle", "null");
        } else {
            Log.i("IotBle", message);
        }
    }

    public static void wifi(String message) {
        if (message == null) {
            Log.i("IotWifi", "null");
        } else {
            Log.i("IotWifi", message);
        }
    }

    public static void udp(String message) {
        if (message == null) {
            Log.i("UDP", "null");
        } else {
            Log.i("UDP", message);
        }
    }

}
