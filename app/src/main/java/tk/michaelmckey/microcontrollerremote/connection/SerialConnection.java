/*
 * MIT License
 *
 * Copyright (c) 2021 Michael McKey (michaelmckey123@protonmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package tk.michaelmckey.microcontrollerremote.connection;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import tk.michaelmckey.microcontrollerremote.R;

/**
 * Creates and manages Serial connections
 * @author Michael McKey
 * @version 1.2.2
 */
public class SerialConnection extends Connection implements SerialInputOutputManager.Listener {
    private static final int WRITE_WAIT_MILLIS = 2000;
    @NonNull
    private static final String INTENT_ACTION_GRANT_USB =
            "com.hoho.android.usbserial.GRANT_USB";
    @NonNull
    private static final String ACTION_USB_DEVICE_DETACHED =
            "android.hardware.usb.action.USB_DEVICE_DETACHED";
    @NonNull
    private static final String ACTION_USB_DEVICE_ATTACHED =
            "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    @Nullable
    private UsbSerialPort mPort;
    @Nullable
    private BroadcastReceiver mBroadcastReceiver;
    private int mBaudRate;
    @Nullable
    private IntentFilter mIntentFilter;

    /**
     * Initialises necessary variables but doesn't create a connection.
     * @param context The context of the class which manages the connection
     */
    SerialConnection(@NonNull Context context) {
        super(context);
        createIntents();
    }

    /**
     * Connects to the device that was chosen last
     */
    @SuppressLint("UnspecifiedImmutableFlag")//can't specify flag in
    @Override
    public void connect() {
        if(mBaudRate > 0) {
            mContext.registerReceiver(mBroadcastReceiver, mIntentFilter);
            UsbManager manager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
            List<UsbSerialDriver> availableDrivers =
                    UsbSerialProber.getDefaultProber().findAllDrivers(manager);
            if (availableDrivers.isEmpty()) {
                disconnected(mContext.getString(R.string.no_device_connected));
                return;
            }

            // Open a connection to the first available driver.
            UsbSerialDriver driver = availableDrivers.get(0);
            UsbDeviceConnection connection;
            if (manager.hasPermission(driver.getDevice())) {
                connection = manager.openDevice(driver.getDevice());
            } else {
                Intent grantUsbIntent = new Intent(INTENT_ACTION_GRANT_USB);
                //notifies app when permission is granted

                PendingIntent pi;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    pi = PendingIntent.getBroadcast(mContext, 0, grantUsbIntent, PendingIntent.FLAG_IMMUTABLE);
                }else{
                    pi = PendingIntent.getBroadcast(mContext, 0, grantUsbIntent, 0);
                }
                manager.requestPermission(driver.getDevice(), pi);
                disconnected(mContext.getString(R.string.serial_permission_not_granted));
                return;
            }

            mPort = driver.getPorts().get(0); // Most devices have just one port (port 0)
            try {
                mPort.open(connection);
                mPort.setParameters(mBaudRate,
                        8,
                        UsbSerialPort.STOPBITS_1,
                        UsbSerialPort.PARITY_NONE);
                connected();
            } catch (IOException e) {
                e.printStackTrace();
                disconnected(mContext.getString(R.string.error_creating_port));
                return;
            }
            SerialInputOutputManager usbIoManager = new SerialInputOutputManager(mPort, this);
            usbIoManager.start();
        }
    }

    /**
     * Connects to the attached serial device
     * @param baudRate the baud rate used to communicate with the device
     */
    public void connect(int baudRate) {
        if (baudRate > 0) {//if baud rate hasn't been set properly it won't connect
            mBaudRate = baudRate;
            connect();
        }
    }

    /**
     * Creates intents to notify the class of hardware changes or if permissions are granted
     */
    private void createIntents() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(@NonNull Context context, @NonNull Intent intent) {
                String action = intent.getAction();
                if (action != null) {
                    switch (action) {
                        case INTENT_ACTION_GRANT_USB: //INTENT_ACTION_DISCONNECT
                            boolean granted = intent.getBooleanExtra(
                                    UsbManager.EXTRA_PERMISSION_GRANTED,false);
                            if (granted) {
                                //USB permission has been granted so attempts to connect to device
                                connect();
                            } else {
                                disconnected(context.getString(
                                        R.string.usb_permission_denied_message));
                            }
                            break;
                        case ACTION_USB_DEVICE_DETACHED:
                            //The device has been disconnected
                            disconnected(context.getString(R.string.device_detached_message));
                            break;
                        case ACTION_USB_DEVICE_ATTACHED:
                            //The device has been attached
                            connect();
                            break;
                    }
                }
            }
        };

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(INTENT_ACTION_GRANT_USB);
        mIntentFilter.addAction(ACTION_USB_DEVICE_DETACHED);
        mIntentFilter.addAction(ACTION_USB_DEVICE_ATTACHED);
        mContext.registerReceiver(mBroadcastReceiver, mIntentFilter);
    }

    /**
     * Sends a messages to the connected device
     * @param message the message to send as text
     */
    public void sendMessage(@NonNull String message) {
        String formattedMessage = message + NEW_LINE;
        if(mConnected){
            assert mPort != null;
            try {
                mPort.write(formattedMessage.getBytes(), WRITE_WAIT_MILLIS);
            } catch (IOException e){
                e.printStackTrace();
                //if error sending message it does nothing
                //as trying to resend message could interrupt future messages
            }
        }
    }

    /**
     * Passes on incoming data for processing
     * @param data data received from the connected device
     */
    @Override
    public void onNewData(@NonNull byte[] data) {
        onNewData(new String(data));
    }

    /**
     * Called when the connection is aborted due to an error
     * @param e the exception thrown
     */
    @Override
    public void onRunError(@NonNull Exception e) {
        e.printStackTrace();
    }

    /**
     * Disconnects from the remote device it is currently connected to.
     * Can reconnect by calling the connect method
     */
    public void disconnect() {
        if (mPort != null) {
            try {
                mPort.close();
            }catch (IOException e) {
                //doesn't matter if error thrown as port won't be used again.
                e.printStackTrace();
            }
        }
        disconnected(null);
    }
}