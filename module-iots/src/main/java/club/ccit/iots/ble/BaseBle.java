package club.ccit.iots.ble;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import club.ccit.iots.common.IotCode;

/**
 * 蓝牙操作基类，封装了BLE设备的扫描、连接、数据收发等核心功能
 * <p>
 * 主要功能：
 * 1. BLE设备扫描与过滤
 * 2. 设备连接与状态管理
 * 3. 数据收发处理
 * 4. 生命周期管理
 * <p>
 * 使用方式：
 * 1. 继承此类并实现抽象方法
 * 2. 通过LiveData观察蓝牙状态变化
 * 3. 调用公共方法执行蓝牙操作
 */
public abstract class BaseBle extends ViewModel {
    //  常量定义
    /**
     * 默认扫描超时时间(毫秒)
     */
    private static final long DEFAULT_SCAN_TIMEOUT = 10000;
    /**
     * 默认连接超时时间(毫秒)
     */
    private static final long DEFAULT_CONNECTION_TIMEOUT = 8000;

    //  LiveData状态
    /**
     * 扫描特征值LiveData
     */
    private final MutableLiveData<String> scanCharacteristicLiveData = new MutableLiveData<>();
    /**
     * 蓝牙设备状态LiveData
     */
    public final MutableLiveData<BleState> bluetoothDeviceStatus = new MutableLiveData<>();
    /**
     * GATT服务状态LiveData
     */
    public final MutableLiveData<BleGattState> bluetoothGattStatus = new MutableLiveData<>();
    /**
     * 扫描结果列表LiveData
     */
    public final MutableLiveData<List<ScanResult>> bluetoothDeviceListLiveData = new MutableLiveData<>();
    /**
     * 当前连接设备LiveData
     */
    private final MutableLiveData<BluetoothDevice> connectingDevice = new MutableLiveData<>();


    //  蓝牙核心组件
    /**
     * 蓝牙适配器
     */
    private BluetoothAdapter bluetoothAdapter;
    /**
     * BLE扫描器
     */
    private BluetoothLeScanner bluetoothLeScanner;
    /**
     * 扫描设置
     */
    private ScanSettings scanSettings;
    /**
     * GATT连接对象
     */
    private BluetoothGatt bluetoothGatt;
    //  计时器
    /**
     * 扫描计时器
     */
    private final Handler scanHandler = new Handler();
    /**
     * 连接计时器
     */
    private final Handler connectionHandler = new Handler();
    //  状态变量
    /**
     * 上次点击时间戳(用于防抖)
     */
    private long lastClickTime = 0;
    /**
     * 消息类型(HEX/TEXT)
     */
    private final MessageType messageType = MessageType.HEX;

    /**
     * 初始化蓝牙相关组件
     *
     * @return 初始化是否成功
     */
    private boolean init() {
        try {
            checkContextAndPermission();
            initBluetoothAdapter();
            initScanSettings();
            initBluetoothLeScanner();
            bluetoothDeviceStatus.postValue(BleState.BLE_INIT_SUCCESS);
            return true;
        } catch (BleInitException | BleContextNullException e) {
            bluetoothDeviceStatus.postValue(BleState.BLE_INIT_FAILURE);
            return false;
        }
    }

    /**
     * 初始化蓝牙适配器
     */
    private void initBluetoothAdapter() throws BleInitException {
        if (bluetoothAdapter == null) {
            BluetoothManager manager = (BluetoothManager) getContext().getSystemService(Context.BLUETOOTH_SERVICE);
            if (manager == null) {
                throw new BleInitException("获取系统蓝牙服务失败");
            }
            bluetoothAdapter = manager.getAdapter();
            if (bluetoothAdapter == null) {
                throw new BleInitException("获取蓝牙适配器失败");
            }
        }
    }

    /**
     * 初始化扫描设置
     */
    private void initScanSettings() {
        if (scanSettings == null) {
            scanSettings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                    .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                    .build();
        }
    }

    /**
     * 初始化BLE扫描器
     */
    private void initBluetoothLeScanner() throws BleInitException {
        if (bluetoothLeScanner == null) {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            if (bluetoothLeScanner == null) {
                throw new BleInitException("获取BLE扫描器失败");
            }
        }
    }

    /**
     * 开始扫描所有BLE设备
     */
    public void startScanBle() {
        scanCharacteristicLiveData.setValue(null);
        startScan();
    }

    /**
     * 开始扫描指定特征的BLE设备
     *
     * @param characteristic 设备名称或MAC地址
     */
    public void startScanBle(String characteristic) {
        scanCharacteristicLiveData.setValue(characteristic);
        startScan();
    }

    /**
     * 执行扫描操作
     */
    private void startScan() {
        if (!init()) return;
        List<ScanFilter> filters = null;
        if (!TextUtils.isEmpty(scanCharacteristicLiveData.getValue())) {
            filters = new ArrayList<>();
            filters.add(new ScanFilter.Builder()
                    .setDeviceName(scanCharacteristicLiveData.getValue())
                    .build());
        }
        bluetoothLeScanner.startScan(filters, scanSettings, scanCallback);
        bluetoothDeviceStatus.postValue(BleState.BLE_SCANNING);
        scanHandler.postDelayed(scanRunnable, setScanTime());
    }

    /**
     * 停止扫描
     */
    private void stopScan() {
        if (bluetoothDeviceStatus.getValue() != BleState.BLE_SCAN_STOPPED) {
            bluetoothLeScanner.stopScan(scanCallback);
            bluetoothDeviceStatus.postValue(BleState.BLE_SCAN_STOPPED);
        }
    }

    /**
     * 连接指定设备
     *
     * @param device 蓝牙设备
     */
    public void connectToBleDevice(BluetoothDevice device) {
        connectingDevice.setValue(device);
        bluetoothGatt = device.connectGatt(getContext(), false, gattCallback);
        connectionHandler.postDelayed(connectionRunnable, setConnectionTime());
    }

    /**
     * 断开当前连接
     */
    public void disconnect() {
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt = null;
        }
    }

    /**
     * 发送数据到指定特征
     *
     * @param message            要发送的消息
     * @param serviceUUID        服务UUID
     * @param characteristicUUID 特征UUID
     */
    public void sendData(String message, UUID serviceUUID, UUID characteristicUUID) {
        if (bluetoothGatt == null) {
            bluetoothDeviceStatus.postValue(BleState.BLE_NOT_CONNECTED);
            return;
        }

        BluetoothGattService service = bluetoothGatt.getService(serviceUUID);
        if (service == null) {
            bluetoothGattStatus.postValue(BleGattState.BLE_NOT_FIND_SERVICE);
            return;
        }

        BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
        if (characteristic == null) {
            bluetoothGattStatus.postValue(BleGattState.BLE_NOT_FIND_CHARACTERISTIC);
            return;
        }

        try {
            byte[] data = messageType == MessageType.HEX ?
                    hexStringToByteArray(message) : message.getBytes(StandardCharsets.UTF_8);
            bluetoothGatt.setCharacteristicNotification(characteristic, true);
            characteristic.setValue(data);
            bluetoothGatt.writeCharacteristic(characteristic);
        } catch (Exception e) {
            bluetoothGattStatus.postValue(BleGattState.MESSAGE_FORMAT_ERROR);
        }
    }

    /**
     * 获取上下文
     */
    protected abstract AppCompatActivity getContext();

    /**
     * 扫描到设备回调
     */
    protected abstract void onBleScanResult(ScanResult device);

    /**
     * 发现服务回调
     */
    protected abstract void onDiscoveredServices(BluetoothGatt gatt);

    /**
     * 发现服务UUID回调
     */
    protected abstract void onServiceDiscovered(UUID serviceUUID);

    /**
     * 特征值变化回调
     */
    protected abstract void onChangedCharacteristic(byte[] value, String stringValue);

    /**
     * 设置扫描超时时间
     */
    protected long setScanTime() {
        return DEFAULT_SCAN_TIMEOUT;
    }

    /**
     * 设置连接超时时间
     */
    protected long setConnectionTime() {
        return DEFAULT_CONNECTION_TIMEOUT;
    }

    /**
     * 消息类型枚举
     */
    public enum MessageType {
        TEXT, HEX
    }

    /**
     * 蓝牙状态枚举
     */
    public enum BleState {
        BLE_INIT_SUCCESS, // 初始化成功
        BLE_INIT_FAILURE, // 初始化失败
        BLE_SCANNING,// 正在扫描
        BLE_SCAN_SUCCESS,// 扫描成功
        BLE_SCAN_FAILURE,// 扫描失败
        BLE_SCAN_STOPPED,// 扫描停止
        BLE_SCAN_COMPLETE,// 扫描完成
        BLE_SCAN_TIMEOUT,// 扫描超时
        BLE_NOT_SUPPORT,// 不支持BLE
        BLE_NOT_OPEN,// 蓝牙未打开
        BLE_NOT_PERMISSION,// 无权限
        BLE_NOT_CONNECTED,// 未连接
        BLE_NOT_FOUND,// 未找到设备
        BLE_CONNECTED,// 已连接
        BLE_CONNECT_FAILURE,// 连接失败
        BLE_CONNECT_TIMEOUT,// 连接超时
        BLE_DISCONNECTED// 断开连接
    }

    /**
     * GATT状态枚举
     */
    public enum BleGattState {
        BLE_NOT_FIND_SERVICE, // 未发现服务
        BLE_FIND_SERVICE_SUCCESS, // 发现服务成功
        BLE_FIND_SERVICE_FAILURE, // 发现服务失败
        BLE_FIND_CHARACTERISTIC,// 发现特征值
        BLE_NOT_FIND_CHARACTERISTIC, // 未发现特征值
        BLE_NOT_FIND_DESCRIPTOR, // 未发现描述符
        BLE_WRITE_FAILURE, // 写入失败
        BLE_READ_FAILURE, // 读取失败
        BLE_NOTIFY_FAILURE,// 通知失败
        MESSAGE_FORMAT_ERROR// 消息格式错误
    }

    /**
     * 初始化异常
     */
    public static class BleInitException extends Exception {
        public BleInitException(String message) {
            super(message);
        }
    }

    // 扫描回调
    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (result == null || result.getDevice() == null) return;

            // 更新设备列表
            List<ScanResult> currentList = bluetoothDeviceListLiveData.getValue();
            if (currentList == null) {
                currentList = new ArrayList<>();
            }

            // 检查是否已存在相同设备
            boolean exists = false;
            for (ScanResult item : currentList) {
                if (item.getDevice().getAddress().equals(result.getDevice().getAddress())) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                currentList.add(result);
                bluetoothDeviceListLiveData.postValue(currentList);
            }

            // 回调子类
            onBleScanResult(result);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            bluetoothDeviceStatus.postValue(BleState.BLE_SCAN_FAILURE);
            stopScan();
        }
    };

    // GATT回调
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // 连接成功，发现服务
                bluetoothGatt.discoverServices();
                connectionHandler.removeCallbacks(connectionRunnable);
                bluetoothDeviceStatus.postValue(BleState.BLE_CONNECTED);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // 断开连接
                bluetoothDeviceStatus.postValue(BleState.BLE_DISCONNECTED);
                cleanupResources();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                bluetoothGattStatus.postValue(BleGattState.BLE_FIND_SERVICE_SUCCESS);
                onDiscoveredServices(gatt);
                // 遍历所有服务
                for (BluetoothGattService service : gatt.getServices()) {
                    onServiceDiscovered(service.getUuid());
                }
            } else {
                bluetoothGattStatus.postValue(BleGattState.BLE_FIND_SERVICE_FAILURE);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] data = characteristic.getValue();
            String stringValue = new String(data, StandardCharsets.UTF_8);
            onChangedCharacteristic(data, stringValue);
        }
    };

    // 定时任务
    private final Runnable scanRunnable = () -> {
        stopScan();
        bluetoothDeviceStatus.postValue(BleState.BLE_SCAN_TIMEOUT);
    };

    private final Runnable connectionRunnable = () -> {
        disconnect();
        bluetoothDeviceStatus.postValue(BleState.BLE_CONNECT_TIMEOUT);
    };

    /**
     * 检查上下文和权限
     */
    private void checkContextAndPermission() throws BleInitException, BleContextNullException {
        checkContext();
        if (!checkRequestBlePermission()) {
            throw new BleInitException("缺少蓝牙权限");
        }
    }

    /**
     * 16进制字符串转字节数组
     */
    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * 获取所需权限列表
     */
    private String[] getRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return new String[]{
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
        } else {
            return new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN
            };
        }
    }

    /**
     * 清理蓝牙相关资源
     * 1. 停止扫描
     * 2. 断开连接
     * 3. 清除计时器任务
     */
    private void cleanupResources() {
        stopScan();
        disconnect();
        scanHandler.removeCallbacksAndMessages(null);
        connectionHandler.removeCallbacksAndMessages(null);
        connectingDevice.setValue(null);
        bluetoothDeviceListLiveData.setValue(null);
    }

    /**
     * 检查上下文是否有效
     */
    private void checkContext() throws BleContextNullException {
        if (getContext() == null || getContext().isFinishing()) {
            throw new BleContextNullException("上下文无效或Activity已销毁");
        }
    }

    /**
     * 上下文为空异常
     */
    public static class BleContextNullException extends Exception {
        public BleContextNullException() {
            super("上下文不能为空");
        }

        public BleContextNullException(String message) {
            super(message);
        }

        public BleContextNullException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * 检查并请求蓝牙权限
     *
     * @return 是否已授予所有必要权限
     */
    private boolean checkRequestBlePermission() {
        String[] requiredPermissions = getRequiredPermissions();
        List<String> ungrantedPermissions = new ArrayList<>();

        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(getContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                ungrantedPermissions.add(permission);
            }
        }

        if (!ungrantedPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    getContext(),
                    ungrantedPermissions.toArray(new String[0]),
                    IotCode.BLE_PERMISSIONS_CODE
            );
            return false;
        }
        return true;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopScan();
        disconnect();
        scanHandler.removeCallbacksAndMessages(null);
        connectionHandler.removeCallbacksAndMessages(null);
    }
}