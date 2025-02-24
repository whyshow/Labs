package club.ccit.iots.wifi;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// 用于扫描附近 WiFi 的工具类
public class WifiScanner {

    private Context context;
    private WifiManager wifiManager;
    private ConnectivityManager connectivityManager;
    private WifiScanListener listener; // 扫描结果监听器
    private WifiScanReceiver wifiScanReceiver; // 扫描结果广播接收器 (Android 10.0 以下)

    // 扫描结果监听器接口
    public interface WifiScanListener {
        void onWifiScanResults(List<ScanResult> results, List<ScanResult> results24G, List<ScanResult> results5G); // 扫描结果可用回调
    }

    // 构造函数，传入 Context 和扫描结果监听器
    public WifiScanner(AppCompatActivity activity, WifiScanListener listener) {
        this.context = activity;
        this.listener = listener;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        activity.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    stopScan();
                }
            }
        });
    }

    // 构造函数，传入 Context 和扫描结果监听器
    public WifiScanner(Fragment fragment, WifiScanListener listener) {
        this.context = fragment.getContext();
        this.listener = listener;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        fragment.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    stopScan();
                }
            }
        });
    }

    // 开始扫描 WiFi
    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public synchronized void startScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startScanAndroidQAndAbove(); // Android 10.0 及以上版本扫描
        } else {
            startScanBelowAndroidQ(); // Android 10.0 以下版本扫描
        }
    }

    // Android 10.0 及以上版本扫描 WiFi
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void startScanAndroidQAndAbove() {
        wifiScanReceiver = new WifiScanReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(wifiScanReceiver, intentFilter);
        boolean success = wifiManager.startScan();
        if (!success) {
            // scan failure handling
            scanFailure();
        }
    }

    // Android 10.0 以下版本扫描 WiFi
    private void startScanBelowAndroidQ() {
        // 创建 WifiScanReceiver，用于接收扫描结果广播
        wifiScanReceiver = new WifiScanReceiver();
        IntentFilter intentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(wifiScanReceiver, intentFilter);
        // 触发 WiFi 扫描
        wifiManager.startScan();
    }

    // 停止扫描 WiFi
    public void stopScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            stopScanAndroidQAndAbove(); // Android 10.0 及以上版本停止扫描
        } else {
            stopScanBelowAndroidQ(); // Android 10.0 以下版本停止扫描
        }
    }

    // Android 10.0 及以上版本停止扫描 WiFi
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void stopScanAndroidQAndAbove() {
        // 取消注册 NetworkCallback
        if (wifiScanReceiver != null) {
            context.unregisterReceiver(wifiScanReceiver);
            wifiScanReceiver = null;
        }
    }

    // Android 10.0 以下版本停止扫描 WiFi
    private void stopScanBelowAndroidQ() {
        // 取消注册 WifiScanReceiver
        if (wifiScanReceiver != null) {
            context.unregisterReceiver(wifiScanReceiver);
            wifiScanReceiver = null;
        }
    }

    /**
     * 扫描结果广播接收器
     */
    private class WifiScanReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 检查扫描是否成功
            boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
            if (success) {
                scanSuccess();
            } else {
                scanFailure();
            }
        }
    }

    private void scanSuccess() {
        // 获取扫描结果
        if (listener != null) {
            List<ScanResult> results = wifiManager.getScanResults();
            //使用 HashSet 过滤重复结果
            Set<ScanResult> uniqueResults = new HashSet<>(results);
            List<ScanResult> results24G = new ArrayList<>();
            List<ScanResult> results5G = new ArrayList<>();
            for (ScanResult result : uniqueResults) {
                if (is24GHz(result.frequency)) {
                    results24G.add(result);
                } else if (is5GHz(result.frequency)) {
                    results5G.add(result);
                }
            }
            listener.onWifiScanResults(new ArrayList<>(uniqueResults), results24G, results5G);
            stopScan();
        }
    }

    private void scanFailure() {
        // 获取扫描结果
        if (listener != null) {
            List<ScanResult> results = wifiManager.getScanResults();
            //使用 HashSet 过滤重复结果
            Set<ScanResult> uniqueResults = new HashSet<>(results);
            List<ScanResult> results24G = new ArrayList<>();
            List<ScanResult> results5G = new ArrayList<>();
            for (ScanResult result : uniqueResults) {
                if (is24GHz(result.frequency)) {
                    results24G.add(result);
                } else if (is5GHz(result.frequency)) {
                    results5G.add(result);
                }
            }
            listener.onWifiScanResults(new ArrayList<>(uniqueResults), results24G, results5G);
            stopScan();
        }
    }

    // 判断是否是 2.4GHz 频段
    private boolean is24GHz(int frequency) {
        return frequency >= 2400 && frequency <= 2500;
    }

    // 判断是否是 5GHz 频段
    private boolean is5GHz(int frequency) {
        return frequency >= 5000 && frequency <= 6000;
    }
}