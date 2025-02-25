package club.ccit.iots.ble;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;



import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import club.ccit.utils.StringUtils;
import club.ccit.iots.common.IotCode;
import club.ccit.iots.common.LogUtils;

/**
 * File: BaseBle
 * Author: swzhang3
 * Date: 2025/2/14 16:38
 * Description: 蓝牙基类：搜索、连接、断开、发送数据、接收数据
 */
public abstract class BaseBle extends ViewModel {
    // 扫描名字或者Mac地址
    private final MutableLiveData<String> scanCharacteristicLiveData = new MutableLiveData<>();
    // 蓝牙状态
    public MutableLiveData<BleState> bluetoothDeviceStatus = new MutableLiveData<>();
    // 蓝牙连接服务状态
    public MutableLiveData<BleGattState> bluetoothGattStatus = new MutableLiveData<>();
    // 蓝牙搜索结果列表
    public MutableLiveData<List<ScanResult>> bluetoothDeviceListLiveData = new MutableLiveData<>();
    // 蓝牙连接对象
    private final MutableLiveData<BluetoothDevice> connectingDevice = new MutableLiveData<>();
    // 发送和接收数据类型
    private MessageType messageType = MessageType.HEX;
    // 蓝牙适配器
    private BluetoothAdapter bluetoothAdapter;
    // 蓝牙搜索器
    private BluetoothLeScanner bluetoothLeScanner;
    // 扫描设置
    private ScanSettings scanSettings;
    // 扫描定时器
    private Handler scanHandler = new Handler();
    // 连接定时器
    private Handler connectionHandler = new Handler();
    // 蓝牙连接器
    private BluetoothGatt bluetoothGatt;

    // 构造器 设置默认值
    public BaseBle() {

    }

    /**
     * 设置消息类型
     *
     * @param messageType 消息类型
     */
    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    /**
     * 初始化
     *
     * @return 是否初始化成功
     */
    private boolean init() {
        try {
            checkContextAndPermission();
            initBluetoothAdapter();
            initScanSettings();
            initBluetoothLeScanner();
            bluetoothDeviceStatus.postValue(BleState.BLE_INIT_SUCCESS);
        } catch (BleInitException e) {
            LogUtils.ble("初始化失败：" + e.getMessage());
            bluetoothDeviceStatus.postValue(BleState.BLE_INIT_FAILURE);
            return false;
        }
        return true;
    }

    //检查上下文
    private void checkContext() throws BleContextNullException {
        if (getContext() == null) {
            bluetoothDeviceStatus.postValue(BleState.BLE_INIT_FAILURE);
            throw new BleContextNullException("Context 为空");
        }
    }

    /**
     * 检查或请求蓝牙权限
     *
     * @return 是否获取到蓝牙权限
     */
    public boolean checkRequestBlePermission() {
        String[] requiredPermissions = getRequiredPermissions();
        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (!permissionsToRequest.isEmpty()) {
            // 申请权限
            ActivityCompat.requestPermissions(getContext(), permissionsToRequest.toArray(new String[0]), IotCode.BLE_PERMISSIONS_CODE);
            return false;
        } else {
            return true;
        }
    }

    // 生成蓝牙权限请求组
    private String[] getRequiredPermissions() {
        String[] requiredPermissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12 及以上版本需要请求蓝牙扫描和蓝牙连接权限
            requiredPermissions = new String[]{
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
        } else {
            // Android 12 以下版本需要请求位置权限
            requiredPermissions = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
        }
        return requiredPermissions;
    }

    /**
     * 权限申请结果
     *
     * @param requestCode  请求码
     * @param permissions  权限组
     * @param grantResults 结果
     * @return
     */
    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        return requestCode == IotCode.BLE_PERMISSIONS_CODE && (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * 检查上下文和蓝牙权限
     */
    private void checkContextAndPermission() {
        try {
            checkContext();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // 1. 检查蓝牙权限
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    bluetoothDeviceStatus.postValue(BleState.BLE_NOT_PERMISSION);
                    throw new BleNotPermissionException("缺少蓝牙扫描权限");
                }

                // 2. 检查蓝牙连接权限
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    bluetoothDeviceStatus.postValue(BleState.BLE_NOT_PERMISSION);
                    throw new BleNotPermissionException("缺少蓝牙连接权限");
                }
            } else {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    bluetoothDeviceStatus.postValue(BleState.BLE_NOT_PERMISSION);
                    throw new BleNotPermissionException("缺少 ACCESS_FINE_LOCATION 权限");
                }

                // 2. 检查蓝牙连接权限
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    bluetoothDeviceStatus.postValue(BleState.BLE_NOT_PERMISSION);
                    throw new BleNotPermissionException("缺少 ACCESS_COARSE_LOCATION 权限");
                }
            }
        } catch (BleNotPermissionException | BleContextNullException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 初始化 BluetoothAdapter
     *
     * @throws BleInitException
     */
    private void initBluetoothAdapter() throws BleInitException {
        if (bluetoothAdapter == null) {
            LogUtils.ble("正在初始化 BluetoothAdapter...");
            BluetoothManager systemBluetoothManager = (BluetoothManager) getContext().getSystemService(Context.BLUETOOTH_SERVICE);
            if (systemBluetoothManager == null) {
                bluetoothDeviceStatus.postValue(BleState.BLE_INIT_FAILURE);
                throw new BleInitException("获取系统蓝牙服务失败");
            }
            bluetoothAdapter = systemBluetoothManager.getAdapter();
            if (bluetoothAdapter == null) {
                bluetoothDeviceStatus.postValue(BleState.BLE_INIT_FAILURE);
                throw new BleInitException("获取蓝牙适配器失败");
            }
            LogUtils.ble("BluetoothAdapter 初始化成功");
        }
    }

    /**
     * 初始化 ScanSettings
     */
    private void initScanSettings() {
        if (scanSettings == null) {
            LogUtils.ble("正在初始化 ScanSettings...");
            scanSettings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                    .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                    .build();
            LogUtils.ble("ScanSettings 初始化成功");
        }
    }

    /**
     * 初始化 BluetoothLeScanner
     *
     * @throws BleInitException
     */
    private void initBluetoothLeScanner() throws BleInitException {
        if (bluetoothLeScanner == null) {
            LogUtils.ble("正在初始化 BluetoothLeScanner...");
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            if (bluetoothLeScanner == null) {
                bluetoothDeviceStatus.postValue(BleState.BLE_INIT_FAILURE);
                throw new BleInitException("获取 BluetoothLeScanner 失败");
            }
            LogUtils.ble("BluetoothLeScanner 初始化成功");
        }
    }

    /**
     * 搜索全部的蓝牙设备
     */
    public void startScanBle() {
        scanCharacteristicLiveData.setValue(null);
        startScan();
    }

    /**
     * 搜索指定特征的蓝牙设备
     *
     * @param characteristic 特征：名字或者Mac地址
     */
    public void startScanBle(String characteristic) {
        scanCharacteristicLiveData.setValue(characteristic);
        startScan();
    }

    /**
     * 开始扫描蓝牙设备
     * 此方法用于启动蓝牙设备的扫描过程。它首先检查是否已经初始化成功，
     * 然后设置一个延迟任务来停止扫描，接着检查是否有权限进行蓝牙扫描，
     * 并根据是否设置了扫描名称来决定是否使用过滤器进行扫描。
     * 最后，它会更新蓝牙扫描状态并记录日志。
     */
    private void startScan() {
        // 检查是否初始化成功
        if (init()) {
            // 检查是否设置了扫描名称
            if (!TextUtils.isEmpty(scanCharacteristicLiveData.getValue())) {
                // 创建过滤器列表
                List<ScanFilter> filters = new ArrayList<>();
                // 添加过滤器，匹配特定的设备名称
                ScanFilter filter = new ScanFilter.Builder()
                        .setDeviceName(scanCharacteristicLiveData.getValue())
                        .build();
                filters.add(filter);
                // 记录日志，开始搜索特定名称的设备
                LogUtils.ble("搜索 ---> " + scanCharacteristicLiveData.getValue() + " 设备");
                // 开始扫描，使用过滤器
                bluetoothLeScanner.startScan(filters, scanSettings, scanCallback);
            } else {
                // 记录日志，开始搜索所有设备
                LogUtils.ble("搜索 ---> 全部设备");
                // 开始扫描，不使用过滤器
                bluetoothLeScanner.startScan(null, scanSettings, scanCallback);
            }
            // 设置蓝牙扫描状态为扫描中
            bluetoothDeviceStatus.postValue(BleState.BLE_SCANNING);
            // 延迟停止扫描
            scanHandler.postDelayed(scanRunnable, setScanTime());
            // 记录日志，搜索状态为搜索中
            LogUtils.ble("搜索状态 ---> 搜索中");
        }
    }

    // 扫描定时器
    /**
     * 扫描定时器任务
     * 此任务用于在蓝牙扫描超时时执行特定的操作。它首先检查是否设置了扫描名称，
     * 然后根据是否设置了扫描名称来更新蓝牙扫描状态并记录日志，最后停止扫描。
     */
    private final Runnable scanRunnable = () -> {
        // 检查权限
        checkContextAndPermission();
        // 检查是否设置了扫描名称
        if (!StringUtils.checkEmpty(scanCharacteristicLiveData.getValue()).isEmpty()) {
            // 设置蓝牙扫描状态为超时
            bluetoothDeviceStatus.postValue(BleState.BLE_SCAN_TIMEOUT);
            // 记录日志，搜索特定名称的设备超时
            LogUtils.ble("搜索 " + scanCharacteristicLiveData.getValue() + "---> " + BleState.BLE_SCAN_TIMEOUT.name() + " 时间： " + setScanTime());
        } else {
            // 设置蓝牙扫描状态为完成
            bluetoothDeviceStatus.postValue(BleState.BLE_SCAN_COMPLETE);
            // 记录日志，搜索所有设备完成
            LogUtils.ble("搜索 ---> " + BleState.BLE_SCAN_TIMEOUT.name() + " 时间： " + setScanTime());
        }
        // 停止扫描
        stopScan();
    };

    /**
     * 蓝牙扫描回调
     * 用于处理蓝牙扫描结果和扫描失败的情况
     */
    private final ScanCallback scanCallback = new ScanCallback() {
        /**
         * 当扫描到设备时调用
         *
         * @param callbackType 回调类型
         * @param result       扫描结果
         */
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            // 调用父类的 onScanResult 方法
            super.onScanResult(callbackType, result);
            // 检查扫描结果中的设备是否为空
            if (result.getDevice() != null) {
                checkContextAndPermission();
                // 记录扫描到的设备信息
                LogUtils.ble("搜索到name：" + result.getDevice().getName() + " 地址：" + result.getDevice().getAddress());
                if (!StringUtils.checkEmpty(scanCharacteristicLiveData.getValue()).isEmpty()) {
                    if (result.getDevice().getName().equals(scanCharacteristicLiveData.getValue()) || result.getDevice().getAddress().equals(scanCharacteristicLiveData.getValue())) {
                        // 设置蓝牙扫描状态为成功
                        bluetoothDeviceStatus.postValue(BleState.BLE_SCAN_SUCCESS);
                        stopScan();
                        scanHandler.removeCallbacks(scanRunnable);
                        // 更新搜索到的设备
                        onBleScanResult(result);
                        putBluetoothDeviceList(result);
                    }
                } else {
                    // 更新搜索到的设备
                    onBleScanResult(result);
                    putBluetoothDeviceList(result);
                }
            }
        }

        /**
         * 当扫描失败时调用
         *
         * @param errorCode 错误码
         */
        @Override
        public void onScanFailed(int errorCode) {
            // 调用父类的 onScanFailed 方法
            super.onScanFailed(errorCode);
            // 设置蓝牙扫描状态为失败
            bluetoothDeviceStatus.postValue(BleState.BLE_SCAN_FAILURE);
            // 移除扫描定时器
            scanHandler.removeCallbacks(scanRunnable);
            // 记录扫描失败的错误码
            LogUtils.ble("BLE扫描失败，错误码: " + errorCode);
        }
    };

    /**
     * 停止扫描
     */
    private void stopScan() {
        // 判断是否扫描停止
        if (bluetoothDeviceStatus.getValue() != BleState.BLE_SCAN_STOPPED) {
            bluetoothLeScanner.stopScan(scanCallback);
            // 设置蓝牙扫描状态为停止
            bluetoothDeviceStatus.postValue(BleState.BLE_SCAN_STOPPED);
            LogUtils.ble("搜索状态 ---> 停止");
        }
    }

    /**
     * 连接蓝牙
     *
     * @param deviceName 蓝牙设备
     */
    public void connectToBleDevice(String deviceName) {
        try {
            checkContextAndPermission();
        } catch (Exception ignored) {
            return;
        }
        if (bluetoothDeviceListLiveData.getValue() == null) {
            return;
        }
        for (ScanResult scanResult : bluetoothDeviceListLiveData.getValue()) {
            if (scanResult.getDevice().getName().equals(deviceName) || scanResult.getDevice().getAddress().equals(deviceName)) {
                connectToBleDevice(scanResult.getDevice());
                break;
            }
        }
        bluetoothDeviceStatus.postValue(BleState.BLE_NOT_FOUND);
        LogUtils.ble("未找到蓝牙设备");
    }

    /**
     * 连接蓝牙
     *
     * @param scanResult 蓝牙设备
     */
    public void connectToBleDevice(ScanResult scanResult) {
        connectToBleDevice(scanResult.getDevice());
    }

    /**
     * 连接蓝牙
     *
     * @param device 蓝牙设备
     */
    public void connectToBleDevice(BluetoothDevice device) {
        LogUtils.ble("正在连接到 " + device.getName() + " mac地址：" + device.getAddress() + "...");
        // 连接到设备
        connectingDevice.setValue(device);
        bluetoothGatt = device.connectGatt(getContext(), false, gattCallback);
        connectionHandler.postDelayed(connectionRunnable, setConnectionTime());
    }

    // 连接定时器
    private final Runnable connectionRunnable = () -> {
        if (bluetoothDeviceStatus.getValue() != BleState.BLE_CONNECTED) {
            // 连接超时
            LogUtils.ble("连接超时");
            onDisconnect();
            bluetoothDeviceStatus.setValue(BleState.BLE_CONNECT_TIMEOUT);
        }
    };

    /**
     * 获取当前连接的蓝牙设备
     *
     * @return 当前连接的蓝牙设备集合
     */
    public Set<BluetoothDevice> getConnectedDevices() {
        checkContextAndPermission();
        BluetoothManager bluetoothManager = (BluetoothManager) getContext().getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            return null;
        }
        return bluetoothAdapter.getBondedDevices();
    }

    /**
     * 发送数据
     *
     * @param message               消息
     * @param bleServiceUUID        服务UUID
     * @param bleCharacteristicUUID 特征UUID
     */
    public void putData(String message, UUID bleServiceUUID, UUID bleCharacteristicUUID) {
        if (bluetoothGatt != null) {
            // 获取指定的服务
            BluetoothGattService service = bluetoothGatt.getService(bleServiceUUID);
            // 检查服务是否存在
            if (service != null) {
                put(service, message, bleServiceUUID, bleCharacteristicUUID);
            } else {
                LogUtils.ble("未发现 " + bleServiceUUID.toString() + " 服务");
                bluetoothGattStatus.postValue(BleGattState.BLE_NOT_FIND_SERVICE);
            }
        } else {
            // 记录日志，蓝牙未连接
            LogUtils.ble("ble未连接");
            // 设置蓝牙扫描状态为未连接
            bluetoothDeviceStatus.setValue(BleState.BLE_NOT_CONNECTED);
        }
    }

    /**
     * 发送数据
     *
     * @param service               蓝牙Gatt服务
     * @param message               要发送的命令
     * @param bleServiceUUID        目标服务的UUID
     * @param bleCharacteristicUUID 目标特征的UUID
     */
    private void put(BluetoothGattService service, String message, UUID bleServiceUUID, UUID bleCharacteristicUUID) {
        LogUtils.ble("发现 " + bleServiceUUID.toString() + " 服务");
        // 检查权限
        checkContextAndPermission();
        // 获取指定的特征
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(bleCharacteristicUUID);
        if (characteristic != null) {
            LogUtils.ble("发现 " + bleCharacteristicUUID.toString() + " 特征");
            bluetoothGattStatus.postValue(BleGattState.BLE_FIND_CHARACTERISTIC);
            // 将命令转换为字节数组
            try {
                byte[] dataToSend;
                if (messageType == MessageType.HEX) {
                    dataToSend = hexStringToByteArray(message);
                } else {
                    dataToSend = message.getBytes(StandardCharsets.UTF_8);
                }
                // 设置通知
                bluetoothGatt.setCharacteristicNotification(characteristic, true);
                // 发送数据
                characteristic.setValue(dataToSend);
                if (bluetoothGatt.writeCharacteristic(characteristic)) {
                    // 记录日志，发送命令成功
                    LogUtils.ble("发送指令成功 " + message);
                } else {
                    // 记录日志，发送命令失败
                    LogUtils.ble("发送命令失败 " + message);
                }
            } catch (Exception e) {
                LogUtils.ble("指令错误 " + message);
                bluetoothGattStatus.postValue(BleGattState.MASSAGE_FORMAT_ERROR);
            }
        } else {
            LogUtils.ble("未发现 " + bleCharacteristicUUID.toString() + " 特征");
            bluetoothGattStatus.postValue(BleGattState.BLE_FIND_CHARACTERISTIC);
        }
    }

    /**
     * 蓝牙Gatt回调
     * 用于处理蓝牙Gatt连接的各种事件，如连接状态改变、服务发现和特征值改变。
     */
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        /**
         * 当连接状态改变时调用
         *
         * @param gatt       蓝牙Gatt对象
         * @param status     连接状态码
         * @param newState   新的连接状态
         */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            // 调用父类的 onConnectionStateChange 方法
            super.onConnectionStateChange(gatt, status, newState);
            // 检查权限
            checkContextAndPermission();
            // 检查新的连接状态是否为已连接
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                // 记录日志，已连接到设备
                LogUtils.ble("已连接到 " + gatt.getDevice().getName());
                // 移除连接定时器
                connectionHandler.removeCallbacks(connectionRunnable);
                // 发现服务
                gatt.discoverServices();
                // 设置蓝牙扫描状态为已连接
                bluetoothDeviceStatus.postValue(BleState.BLE_CONNECTED);
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                // 记录日志，与设备断开连接
                LogUtils.ble("与 " + gatt.getDevice().getName() + " 断开连接");
                // 设置蓝牙扫描状态为已断开连接
                bluetoothDeviceStatus.postValue(BleState.BLE_DISCONNECTED);
                // 调用断开连接方法
                onDisconnect();
            } else {
                // 记录日志，与设备断开连接
                LogUtils.ble("连接 " + gatt.getDevice().getName() + " 失败");
                // 设置蓝牙扫描状态为连接失败
                bluetoothDeviceStatus.postValue(BleState.BLE_CONNECT_FAILURE);
                onDisconnect();
            }
        }

        /**
         * 当服务发现完成时调用
         *
         * @param gatt   蓝牙Gatt对象
         * @param status 服务发现状态码
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            // 调用父类的 onServicesDiscovered 方法
            super.onServicesDiscovered(gatt, status);
            // 检查服务发现状态是否成功
            if (status == BluetoothGatt.GATT_SUCCESS) {
                bluetoothGattStatus.postValue(BleGattState.BLE_FIND_SERVICE_SUCCESS);
                // 遍历发现的服务
                for (BluetoothGattService service : gatt.getServices()) {
                    // 记录日志，已发现服务
                    LogUtils.ble("已发现服务 UUID: " + service.getUuid());
                    // 调用服务发现UUID方法
                    onServicesDiscoveredUUID(service.getUuid());
                }
                // 调用服务发现Gatt方法
                onServicesDiscoveredBluetoothGatt(gatt);
            } else {
                // 记录日志，发现服务失败
                LogUtils.ble("发现服务失败，状态：" + status);
                bluetoothGattStatus.postValue(BleGattState.BLE_FIND_SERVICE_FAILURE);
            }
        }

        /**
         * 当特征值改变时调用
         *
         * @param gatt          蓝牙Gatt对象
         * @param characteristic 改变的特征
         * @param value         特征的新值
         */
        @Override
        public void onCharacteristicChanged(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value) {
            // 调用父类的 onCharacteristicChanged 方法
            super.onCharacteristicChanged(gatt, characteristic, value);
            // 将字节数组转换为字符串
            String valueString = new String(value, StandardCharsets.UTF_8);
            // 调用特征值改变结果方法
            onCharacteristicChangedResult(value, valueString);
        }
    };

    /**
     * 断开蓝牙连接
     */
    public void onDisconnect() {
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
        }
        bluetoothGatt = null;
        LogUtils.ble("释放蓝牙连接资源");
    }

    /**
     * 十六进制字符串转换成byte[]
     *
     * @param message 十六进制字符串
     * @return byte[]
     */
    private byte[] hexStringToByteArray(String message) {
        int len = message.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(message.charAt(i), 16) << 4) + Character.digit(message.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * 获取 上下文 Context
     *
     * @return Context
     */
    protected abstract AppCompatActivity getContext();

    /**
     * 搜索结果通知
     *
     * @param bluetoothDevice 搜索到的蓝牙设备
     */
    protected abstract void onBleScanResult(ScanResult bluetoothDevice);

    /**
     * 发现服务Gatt
     *
     * @param gatt Gatt
     */
    protected abstract void onServicesDiscoveredBluetoothGatt(BluetoothGatt gatt);

    /**
     * 发现服务UUID
     *
     * @param uuid UUID
     */
    protected abstract void onServicesDiscoveredUUID(UUID uuid);

    /**
     * 特性变化通知
     *
     * @param value       值
     * @param valueString
     */
    protected abstract void onCharacteristicChangedResult(byte[] value, String valueString);

    /**
     * 设置连接时间
     *
     * @return 时间
     */
    protected abstract int setConnectionTime();

    /**
     * 设置扫描时间
     *
     * @return 时间
     */
    protected abstract int setScanTime();

    /**
     * 更新搜索设备列表
     *
     * @param newDevice 新设备
     */
    private synchronized void putBluetoothDeviceList(ScanResult newDevice) {
        boolean deviceExists = false;
        List<ScanResult> currentList = bluetoothDeviceListLiveData.getValue();

        if (currentList == null) {
            currentList = new CopyOnWriteArrayList<>();
            bluetoothDeviceListLiveData.postValue(currentList);
        }

        for (ScanResult device : currentList) {
            if (device.getDevice().getAddress().equals(newDevice.getDevice().getAddress())) {
                deviceExists = true;
                break;
            }
        }

        if (!deviceExists) {
            currentList.add(newDevice);
            LogUtils.ble("添加到列表：" + newDevice.getDevice().getName() + " 地址：" + newDevice.getDevice().getAddress());
            // 更新到 LiveData
            bluetoothDeviceListLiveData.postValue(currentList);
        }
    }

    /**
     * 发送消息类型
     */
    public enum MessageType {
        TEXT,// 文本
        HEX,// 十六进制
    }

    /**
     * Gatt蓝牙状态
     */
    public enum BleGattState {
        BLE_NOT_FIND_SERVICE,// 蓝牙未找到服务
        BLE_FIND_SERVICE_SUCCESS,// 蓝牙发现服务成功
        BLE_FIND_SERVICE_FAILURE,// 蓝牙发现服务失败
        BLE_FIND_CHARACTERISTIC,// 蓝牙发现服务特征
        BLE_NOT_CHARACTERISTIC,// 蓝牙未找到特征
        BLE_NOT_DESCRIPTOR,// 蓝牙未找到描述符
        BLE_NOT_WRITE,// 蓝牙写入失败
        BLE_NOT_READ,// 蓝牙读取失败
        BLE_NOT_NOTIFY,// 蓝牙通知失败
        FIND_UUID,// 找到UUID
        MASSAGE_FORMAT_ERROR,// 消息格式错误
    }

    /**
     * 蓝牙状态
     */
    public enum BleState {
        CONTEXT_NULL,// 上下文为空
        BLE_INIT_SUCCESS,// 初始化成功
        BLE_INIT_FAILURE,// 初始化失败
        BLE_SCANNING,// 正在扫描
        BLE_SCAN_SUCCESS,// 扫描成功
        BLE_SCAN_FAILURE,// 扫描失败
        BLE_SCAN_STOPPED,// 扫描停止
        BLE_SCAN_COMPLETE,// 扫描完成
        BLE_SCAN_TIMEOUT,// 扫描超时
        BLE_NOT_SUPPORT,// 蓝牙不支持
        BLE_NOT_OPEN,// 蓝牙未打开
        BLE_NOT_PERMISSION,// 蓝牙未授权
        BLE_NOT_CONNECTED,// 蓝牙未连接
        BLE_NOT_FOUND,// 蓝牙未找到
        BLE_CONNECTED,// 蓝牙连接成功
        BLE_CONNECT_FAILURE,// 蓝牙连接失败
        BLE_CONNECT_TIMEOUT,// 蓝牙连接超时
        BLE_DISCONNECTED,// 蓝牙连接断开
    }

    /**
     * 初始化异常
     */
    public class BleInitException extends Exception {
        public BleInitException(String message) {
            super(message);
        }
    }

    /**
     * 上下文为空异常
     */
    public class BleContextNullException extends Exception {
        public BleContextNullException(String message) {
            super(message);
        }
    }

    /**
     * 没有权限异常
     */
    public class BleNotPermissionException extends Exception {
        public BleNotPermissionException(String message) {
            super(message);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopScan();
        onDisconnect();
        bluetoothLeScanner = null;
        // 释放蓝牙资源
        bluetoothAdapter = null;
        scanSettings = null;
        // 移除所有 Handler 回调
        scanHandler.removeCallbacksAndMessages(null);
        connectionHandler.removeCallbacksAndMessages(null);
        scanHandler = null;
        connectionHandler = null;
        LogUtils.ble("释放蓝牙资源");
    }
}
