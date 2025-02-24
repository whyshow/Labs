package club.ccit.iots.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.os.PatternMatcher;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

public class WifiConnector {
    private final String TAG = WifiChanged.class.getSimpleName();
    private Context context;
    private WifiManager wifiManager;
    private ConnectivityManager connectivityManager;
    private WifiConnectionListener listener;
    private WifiConnectionReceiver wifiConnectionReceiver;
    private ConnectivityManager.NetworkCallback networkCallback;// 连接状态监听器接口


    // 构造函数，传入 AppCompatActivity 和连接状态监听器
    public WifiConnector(AppCompatActivity activity, WifiConnectionListener listener) {
        this.context = activity;
        this.listener = listener;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // WiFi网络变化监听器
        networkCallback = new ListenerNetworkChanges();
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
    LifecycleEventObserver lifecycleEventObserver;
    // 构造函数，传入 Fragment 和连接状态监听器
    public WifiConnector(Fragment fragment, WifiConnectionListener listener) {
        this.context = fragment.getContext();
        this.listener = listener;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // WiFi网络变化监听器
        networkCallback = new ListenerNetworkChanges();
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

    // 连接 WiFi，根据 Android 版本选择不同的连接方式
    public void connectToWifi(String ssid, String password) {
        if (context == null){
            throw new RuntimeException("init WifiConnector");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            connectToWifiAndroidQAndAbove(ssid, password);
        } else {
            connectToWifiBelowAndroidQ(ssid, password);
        }
    }

    // Android 10.0 及以上版本连接 WiFi
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void connectToWifiAndroidQAndAbove(String ssid, String password) {
        // 创建 WifiNetworkSpecifier
        WifiNetworkSpecifier specifier = new WifiNetworkSpecifier.Builder()
                .setSsidPattern(new PatternMatcher(ssid, PatternMatcher.PATTERN_PREFIX))
                .setWpa2Passphrase(password)
                .build();

        // 创建 NetworkRequest
        NetworkRequest request = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .setNetworkSpecifier(specifier)
                .build();
        // 请求连接 WiFi
        connectivityManager.requestNetwork(request, networkCallback);
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
    private class ListenerNetworkChanges extends ConnectivityManager.NetworkCallback {
        /**
         * 网络连接
         *
         * @param network The {@link Network} of the satisfying network.
         */
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            // 连接成功
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (listener != null) {
                listener.onWifiConnected(wifiInfo);
            }
        }

        /**
         * 网络断开
         *
         * @param network The {@link Network} lost.
         */
        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            if (listener != null) {
                listener.onWifiDisconnected("连接断开");
            }
        }

        /**
         * 无网络
         */
        @Override
        public void onUnavailable() {
            super.onUnavailable();
            // 连接失败
            if (listener != null) {
                listener.onWifiConnectionFailed("连接失败");
            }
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
                if (listener != null) {
                    // 网络连接更改,切换wifi网络
                    listener.onNetworkTypeChanged(NetworkCapabilities.TRANSPORT_WIFI);
                }

            } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                if (listener != null) {
                    // 网络连接更改,切换移动网络
                    listener.onNetworkTypeChanged(NetworkCapabilities.TRANSPORT_CELLULAR);
                }
            }
        }

        @Override
        public void onLinkPropertiesChanged(@NonNull Network network, @NonNull LinkProperties linkProperties) {
            super.onLinkPropertiesChanged(network, linkProperties);
        }
    }

    // Android 10.0 以下版本连接 WiFi
    private void connectToWifiBelowAndroidQ(String ssid, String password) {
        // 创建 WifiConfiguration
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + ssid + "\"";
        if (password != null) {
            wifiConfig.preSharedKey = "\"" + password + "\"";
        }

        // 添加网络配置
        int netId = wifiManager.addNetwork(wifiConfig);
        if (netId != -1) {
            // 注册连接状态广播接收器
            registerConnectionReceiver();
            // 断开当前连接，启用新网络并重新连接
            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();
        } else {
            // 添加网络配置失败
            if (listener != null) {
                listener.onWifiConnectionFailed("添加网络配置失败");
            }
        }
    }

    // 注册连接状态广播接收器
    private void registerConnectionReceiver() {
        if (wifiConnectionReceiver == null) {
            wifiConnectionReceiver = new WifiConnectionReceiver();
            IntentFilter connectionIntentFilter = new IntentFilter();
            connectionIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            connectionIntentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
            context.registerReceiver(wifiConnectionReceiver, connectionIntentFilter);
        }
    }

    // 取消注册连接状态广播接收器
    private void unregisterConnectionReceiver() {
        if (wifiConnectionReceiver != null) {
            context.unregisterReceiver(wifiConnectionReceiver);
            wifiConnectionReceiver = null;
        }
    }

    // 取消注册 NetworkCallback（Android 10.0 及以上）
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void unregisterNetworkCallback() {
        if (networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
            networkCallback = null;
        }
    }

    // 连接状态广播接收器
    private class WifiConnectionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 网络状态变化事件
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo.getNetworkId() != -1) {
                    if (listener != null) {
                        listener.onWifiConnected(wifiInfo);
                    }
                }
                // Wi-Fi 客户端连接状态变化事件
            } else if (WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION.equals(action)) {
                boolean connected = intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false);
                if (!connected) {
                    String reason = intent.getStringExtra(WifiManager.EXTRA_SUPPLICANT_ERROR);
                    if (listener != null) {
                        listener.onWifiDisconnected(reason != null ? reason : "连接断开");
                    }
                }
            }
        }
    }

    public interface WifiConnectionListener {
        /**
         * 连接成功的回调
         *
         * @param wifiInfo 连接成功的 WiFi 信息
         */
        void onWifiConnected(WifiInfo wifiInfo); // 连接成功回调

        /**
         * 连接断开的回调
         *
         * @param reason 连接断开的原因
         */
        void onWifiDisconnected(String reason); // 连接断开回调

        /**
         * 连接网络类型发生变化的回调
         *
         * @param reason 连接失败原因
         */
        void onWifiConnectionFailed(String reason);

        /**
         * 连接网络类型发生变化的回调
         *
         * @param transportWifi 返回可使用的类型 {@link NetworkCapabilities}
         */
        void onNetworkTypeChanged(int transportWifi);
    }

    public void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            unregisterNetworkCallback();
        } else {
            unregisterConnectionReceiver();
        }
    }
}