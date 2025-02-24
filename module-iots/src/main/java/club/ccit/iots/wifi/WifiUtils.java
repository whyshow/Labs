package club.ccit.iots.wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

/**
 * @author swzhang3
 * name: WifiUtils
 * date: 2024/7/3 10:28
 * description:
 **/
public class WifiUtils {
    private Context context;
    private WifiManager wifiManager;
    private ConnectivityManager connectivityManager;

    /**
     * 构造函数，传入 Context 对象。
     *
     * @param context 上下文对象
     */
    public WifiUtils(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * 获取当前连接的 WiFi 名称 (SSID)。
     *
     * @return WiFi 名称，如果未连接则返回 null
     */
    public String getWifiName() {
        WifiInfo wifiInfo = getCurrentWifiInfo();
        if (wifiInfo == null) {
            return "";
        }
        String ssid = wifiInfo.getSSID();
        if (ssid == null) {
            return "";
        }
        ssid = ssid.replace("\"", "");
        return ssid;
    }

    /**
     * 获取当前连接的 WiFi 的 BSSID。
     *
     * @return WiFi 的 BSSID，如果未连接则返回 null
     */
    public String getWifiBSSID() {
        WifiInfo wifiInfo = getCurrentWifiInfo();
        if (wifiInfo != null) {
            return wifiInfo.getBSSID();
        }
        return null;
    }

    /**
     * 获取当前 WiFi 连接状态。
     *
     * @return true 表示已连接，false 表示未连接
     */
    public boolean isWifiConnected() {
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                }
            } else {
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                return activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI && activeNetwork.isConnected();
            }
        }
        return false;
    }

    /**
     * 获取当前 WiFi 信号强度。
     *
     * @return信号强度，范围为 0 到 -100，值越大表示信号越强
     */
    public int getWifiSignalStrength() {
        WifiInfo wifiInfo = getCurrentWifiInfo();
        if (wifiInfo != null) {
            return wifiInfo.getRssi();
        }
        return -100; // 返回一个表示无信号的值
    }

    /**
     * 获取当前连接 WiFi 的频率 (MHz)。
     *
     * @return WiFi 频率 (MHz)，如果未连接则返回 0
     */
    public int getFrequency() {
        WifiInfo wifiInfo = getCurrentWifiInfo();
        if (wifiInfo != null) {
            return wifiInfo.getFrequency();
        }
        return 0;
    }

    /**
     * 获取当前连接的 WiFi 信息。
     *
     * @return WifiInfo 对象，包含 WiFi 连接的详细信息
     */
    private WifiInfo getCurrentWifiInfo() {
        return wifiManager.getConnectionInfo();
    }

}
