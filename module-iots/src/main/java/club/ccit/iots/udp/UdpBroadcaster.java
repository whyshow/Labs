package club.ccit.iots.udp;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 发送和接收 UDP 广播的基类。
 * 允许发送 3 次广播，每次发送后接收 2 秒。
 * 如果接收到响应，立即关闭剩余的发送次数，并将结果回调给 UI 线程。* 使用观察者观察Activity 关闭事件，并在 Activity 关闭时立即关闭发送和接收，并释放资源。
 * 允许多次启动发送。
 * **新增：确保线程资源正确回收，避免内存泄漏。**
 */
public abstract class UdpBroadcaster {

    private static final String TAG = "UdpBroadcaster";

    private final int port;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private AtomicBoolean isReceiving = new AtomicBoolean(false); // 是否正在接收的标志
    private AtomicBoolean isSending = new AtomicBoolean(false); // 是否正在发送的标志
    private CountDownLatch receiveLatch; // 用于同步发送和接收线程
    private DatagramSocket sendSocket; // 发送广播的 Socket
    private DatagramSocket receiveSocket; // 接收广播的 Socket
    private Thread sendThread; // 发送线程
    private Thread receiveThread; // 接收线程

    public UdpBroadcaster(int port) {
        this.port = port;
    }

    /**
     * 发送 UDP 广播并接收响应。
     * 如果当前正在发送或接收广播，则不会启动新的发送线程。
     *
     * @param message        要发送的消息
     * @param targetAddress  目标广播地址
     * @param lifecycleOwner 用于观察 Activity 生命周期的 LifecycleOwner
     */
    public void sendAndReceive(String message, String targetAddress, LifecycleOwner lifecycleOwner) {
        if (isSending.get() || isReceiving.get()) {
            Log.w(TAG, "当前正在发送或接收广播，无法启动新的发送线程。");
            return;
        }

        // 观察 Activity 生命周期
        lifecycleOwner.getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                close(); // 关闭所有资源
            }
        });

        isSending.set(true); // 标记正在发送
        sendThread = new Thread(() -> {
            try {
                sendSocket = new DatagramSocket();
                byte[] data = message.getBytes();
                DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(targetAddress), port);

                for (int i = 0; i < 3; i++) { // 默认发送三次广播
                    if (isReceiving.get() || Thread.currentThread().isInterrupted()) {
                        break; // 如果已经在接收或线程被中断，中断发送
                    }
                    sendSocket.send(packet);
                    receiveLatch = new CountDownLatch(1); // 初始化接收同步锁
                    startReceiving(); // 开始接收响应
                    if (receiveLatch.await(2, TimeUnit.SECONDS)) { // 等待 2 秒或收到响应
                        break; // 收到响应，中断发送
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "发送广播出错：", e);
            } finally {
                closeSockets(); // 关闭 Socket
                isSending.set(false); // 标记发送结束
            }
        });
        sendThread.start();
    }

    /**
     * 开始接收 UDP 广播。
     */
    private void startReceiving() {
        if (isReceiving.getAndSet(true)) {
            return; // 如果已经在接收，直接返回
        }
        receiveThread = new Thread(() -> {
            try {
                receiveSocket = new DatagramSocket(port);
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                receiveSocket.receive(packet);
                String receivedMessage = new String(packet.getData(), 0, packet.getLength());
                uiHandler.post(() -> {
                    onMessageReceived(receivedMessage); // 在 UI 线程回调接收到的消息
                    receiveLatch.countDown(); // 解除发送线程的阻塞
                    closeThread(); // 收到消息后，关闭发送线程
                });
            } catch (Exception e) {
                Log.e(TAG, "接收广播出错：", e);
            } finally {
                closeReceiveSocket(); // 关闭接收 Socket
                isReceiving.set(false); // 接收结束，重置标志
            }
        });
        receiveThread.start();
    }

    /**
     * 关闭发送线程。
     */
    private void closeThread() {
        if (sendThread != null && sendThread.isAlive()) {
            sendThread.interrupt();
            sendThread = null;
        }
        if (receiveThread != null && receiveThread.isAlive()) {
            receiveThread.interrupt();
            receiveThread = null;
        }

    }

    /**
     * 关闭接收 Socket。
     */
    private void closeReceiveSocket() {
        if (receiveSocket != null) {
            receiveSocket.close();
            receiveSocket = null;
        }
    }

    /**
     * 关闭所有资源。
     */
    public void close() {
        closeThread();
        closeReceiveSocket();
        closeSockets();
    }

    /*** 关闭 Socket。
     */
    private void closeSockets() {
        if (sendSocket != null) {
            sendSocket.close();
            sendSocket = null;
        }
    }

    /**
     * 当接收到 UDP 广播时回调。
     *
     * @param message 接收到的消息
     */
    public abstract void onMessageReceived(String message);
}