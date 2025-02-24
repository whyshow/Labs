package club.ccit.iots.wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import java.util.List;

/**
 * @author swzhang3
 * name: WifiChangedLiset
 * date: 2024/7/3 10:42
 * 监听网络连接的变化，并提供回调来通知这些变化的监听器。
 * <p>
 * 这个类负责注册一个{@link ConnectivityManager。NetworkCallback}来监控网络
 * 连接变化，特别是WiFi连接的变化。它提供了各种网络相关的回调
 * 事件，例如当新网络可用时，当网络丢失时，当网络功能发生变化时
 * (例如，在WiFi和蜂窝网络之间切换)，以及当链路属性发生变化时。
 * <p>
 * {@link ListenerNetworkChanges}类，扩展了{@link ConnectivityManager。NetworkCallback}，用于
 * 实现网络监控和报告功能。{@link WifiChanged}类提供
 * 方法来启动和停止网络更改监听器，以及方法来设置和检索
 * {@link OnNetworkChangedListener}回调接口。
 * <p>
 * 接口{@link OnNetworkChangedListener}定义了可用于接收通知的回调方法
 * 关于网络连接变化，包括WiFi连接变化、网络丢失、网络类型变化。
 */
public class WifiChanged {
    private final String TAG = WifiChanged.class.getSimpleName();
    private ConnectivityManager connectivityManager;
    private Context context;
    private ListenerNetworkChanges listenerNetworkChanges;
    /**
     * wifi连接状态改变回调
     */
    private OnNetworkChangedListener onNetworkChangedListener;

    /**
     * 设置wifi连接状态改变回调
     *
     * @param onNetworkChangedListener wifi连接状态改变回调接口
     */
    public void setOnNetworkConnectivityChangedListener(OnNetworkChangedListener onNetworkChangedListener) {
        this.onNetworkChangedListener = onNetworkChangedListener;
    }

    /**
     * 启动网络连接更改监听器，并注册一个生命周期观察者，以便在活动被销毁时清理监听器。
     *
     * @param activity        {@link AppCompatActivity}注册监听器。
     * @param changedListener {@link OnNetworkChangedListener}用于通知WiFi连接变化。
     */
    public void startNetworkChangedListener(AppCompatActivity activity, OnNetworkChangedListener changedListener) {
        setOnNetworkConnectivityChangedListener(changedListener);
        context = activity;
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        NetworkRequest request = builder.build();
        connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        listenerNetworkChanges = new ListenerNetworkChanges();
        connectivityManager.registerNetworkCallback(request, listenerNetworkChanges);
        activity.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    Log.i(TAG, Lifecycle.Event.ON_DESTROY.toString());
                    onDestroy();
                }
            }
        });
    }

    /**
     * 启动WiFi连接更改监听器，并注册一个生命周期观察者，以便在活动被销毁时清理监听器。
     *
     * @param fragment        {@link Fragment}注册监听器。
     * @param changedListener {@link OnNetworkChangedListener}用于通知WiFi连接变化。
     */
    public void startNetworkChangedListener(Fragment fragment, OnNetworkChangedListener changedListener) {
        setOnNetworkConnectivityChangedListener(changedListener);
        context = fragment.getContext();
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        NetworkRequest request = builder.build();
        connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        listenerNetworkChanges = new ListenerNetworkChanges();
        connectivityManager.registerNetworkCallback(request, listenerNetworkChanges);
        fragment.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    Log.i(TAG, Lifecycle.Event.ON_DESTROY.toString());
                    onDestroy();
                }
            }
        });
    }

    /**
     * 网络回调实现监听网络连接的变化，特别是WiFi网络的变化。
     * <p>
     * 这个类扩展了{@link ConnectivityManager。并提供了各种网络相关事件的实现:
     * < ul >
     * <li>{@link #onAvailable(Network)}:当一个新的网络可用时调用。</li>
     * <li>{@link #onLost(Network)}:当网络丢失时调用。</li>
     * <li>{@link #onUnavailable()}:当没有网络可用时调用。</li>
     * <li>{@link #oncapacilitieschanged (Network, NetworkCapabilities)}:当网络的功能发生变化时调用，例如在WiFi和蜂窝网络之间切换。</li>
     * <li>{@link #onLinkPropertiesChanged(Network, LinkProperties)}:当网络的链路属性发生变化时调用。</li>
     * </ul>
     * <p>
     * 这个类被{@link WifiChanged }用来监控和报告WiFi连接的变化。
     */
    public class ListenerNetworkChanges extends ConnectivityManager.NetworkCallback {
        /**
         * 网络连接
         *
         * @param network The {@link Network} of the satisfying network.
         */
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            Log.i(TAG, "onAvailable");
        }

        /**
         * 网络断开
         *
         * @param network The {@link Network} lost.
         */
        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            Log.i(TAG, "onLost");
            if (onNetworkChangedListener != null) {
                onNetworkChangedListener.onNetworkLost();
            }
        }

        /**
         * 无网络
         */
        @Override
        public void onUnavailable() {
            super.onUnavailable();
            Log.i(TAG, "onUnavailable");
        }

        /**
         * 网络更改
         *
         * @param network             The {@link Network} whose capabilities have changed.
         * @param networkCapabilities The new {@link NetworkCapabilities} for this
         *                            network.
         */
        @Override
        public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                // 网络连接更改,切换wifi网络
                onNetworkChangedListener.onNetworkTypeChanged(NetworkCapabilities.TRANSPORT_WIFI);
                refreshWifiConnectivity(context);
            } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                // 网络连接更改,切换移动网络
                onNetworkChangedListener.onNetworkTypeChanged(NetworkCapabilities.TRANSPORT_CELLULAR);
            }
        }

        @Override
        public void onLinkPropertiesChanged(@NonNull Network network, @NonNull LinkProperties linkProperties) {
            super.onLinkPropertiesChanged(network, linkProperties);
        }
    }

    /**
     * 刷新WiFi连接状态，并通知注册{@link OnNetworkChangedListener}任何变化。
     * <p>
     * 该方法获取当前WiFi网络信息，包括SSID和连接状态，然后
     * *调用{@link OnNetworkChangedListener#onWifiConnectivityChanged(boolean, String, WifiInfo)}方法
     * *更新的信息。如果设备没有连接到WiFi网络，该方法将通知侦听器
     * *具有{@code false}连接状态和{@code null} SSID和{@link WifiInfo}。
     */
    public void refreshWifiConnectivity(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI && onNetworkChangedListener != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid = wifiInfo == null ? null : wifiInfo.getSSID();
            if (isEmptySsid(ssid)) {
                ssid = networkInfo.getExtraInfo();
            }
            if (isEmptySsid(ssid) && wifiInfo != null) {
                ssid = getSsid(context, wifiInfo.getNetworkId());
            }
            try {
                onNetworkChangedListener.onWifiConnectivityChanged(true, getPureSsid(ssid), wifiInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (onNetworkChangedListener != null) {
                    onNetworkChangedListener.onWifiConnectivityChanged(false, null, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 监听器接口，用于监控网络连接的变化。
     * <p>
     * onWifiConnectivityChanged 实现此接口会在WiFi连接时发出通知
     * onNetworkLost 状态变化，包括设备连接或断开连接
     * onNetworkTypeChanged 当网络连接类型改变时。
     */
    public interface OnNetworkChangedListener {
        /**
         * 当WiFi连接状态改变时调用的回调方法。
         *
         * @param connected {@code true} 如果设备连接到WiFi网络，
         *                  {@code false} 否则
         * @param ssid      所连接WiFi网络的SSID，或者 {@code null} 如果设备未连接
         * @param wifiInfo  这个 {@link WifiInfo} 对象的详细信息
         *                  *连接WiFi网络，或 {@code null} 如果设备是没有连接
         */
        void onWifiConnectivityChanged(boolean connected, String ssid, WifiInfo wifiInfo);

        /**
         * 网络关闭
         */
        void onNetworkLost();

        /**
         * 当网络连接类型改变时调用的回调方法。
         *
         * @param transportWifi 返回可使用的类型 {@link NetworkCapabilities.TRANSPORT_WIFI }{@link NetworkCapabilities.TRANSPORT_CELLULAR }
         */
        void onNetworkTypeChanged(int transportWifi);
    }


    public static boolean isBlank(String ssid) {
        return TextUtils.isEmpty(ssid) || ssid.trim().isEmpty();
    }

    public static boolean isEmptySsid(String ssid) {

        if (isBlank(ssid)) {
            return true;
        }

        if (getPureSsid(ssid).toLowerCase().contains("<unknown ssid>")) {
            return true;
        }

        return false;
    }

    public static boolean isEmptyBssid(String bssid) {

        if (isBlank(bssid)) {
            return true;
        }

        bssid = bssid.trim();
        if (bssid.equals("000000000000") || bssid.equals("00-00-00-00-00-00") || bssid.equals("00:00:00:00:00:00")) {
            return true;
        }

        return false;
    }

    public static String getSsid(Context context, int networkId) {

        if (networkId != -1) {

            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            List<WifiConfiguration> wifiConfigurations = wifiManager.getConfiguredNetworks();
            if (wifiConfigurations == null) {
                return null;
            }

            for (WifiConfiguration wifiConfiguration : wifiConfigurations) {
                if (wifiConfiguration.networkId == networkId) {
                    return wifiConfiguration.SSID;
                }
            }
        }

        return null;
    }

    public static String getBssid(Context context, int networkId) {

        if (networkId != -1) {

            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            List<WifiConfiguration> wifiConfigurations = wifiManager.getConfiguredNetworks();
            if (wifiConfigurations != null) {
                return null;
            }

            for (WifiConfiguration wifiConfiguration : wifiConfigurations) {
                if (wifiConfiguration.networkId == networkId) {
                    return wifiConfiguration.BSSID;
                }
            }
        }

        return null;
    }

    public static String getPureSsid(String ssid) {

        if (isBlank(ssid)) {
            return ssid;
        }

        if (ssid.startsWith("\"")) {
            ssid = ssid.substring(1);
        }
        if (ssid.endsWith("\"")) {
            ssid = ssid.substring(0, ssid.length() - 1);
        }

        return ssid;
    }


    /**
     * 当对象被销毁时，清除该类使用的资源。
     * 从ConnectivityManager注销网络回调，并设置
     * 各种字段设置为null以允许垃圾收集。
     */
    public void onDestroy() {
        if (context != null) {
            if (connectivityManager != null) {
                connectivityManager.unregisterNetworkCallback(listenerNetworkChanges);
            }
            connectivityManager = null;
            listenerNetworkChanges = null;
            context = null;
        }
        onNetworkChangedListener = null;
    }
}