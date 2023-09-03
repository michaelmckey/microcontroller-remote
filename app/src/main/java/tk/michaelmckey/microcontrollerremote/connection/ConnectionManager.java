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

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Creates and manages all connections
 * @author Michael McKey
 * @version 1.2.2
 */
public class ConnectionManager extends Connection implements ConnectionListener {
    @NonNull
    private final SerialConnection mSerialConnection;
    @NonNull
    private final WiFiConnection mWifiConnection;
    @NonNull
    private final BluetoothConnection mBluetoothConnection;
    @NonNull
    private static List<BluetoothDevice> mBluetoothDevices = new LinkedList<>();
    @Nullable
    private ConnectionMethod mConnectionMethod;
    @NonNull
    private String mExtraInfo = "";

    /**
     * Initialises necessary variables/classes but doesn't connect to any devices
     * @param context The context of the class which manages the connections
     */
    public ConnectionManager(@NonNull Context context) {
        super(context);
        mSerialConnection = new SerialConnection(context);
        mWifiConnection = new WiFiConnection(context);
        mBluetoothConnection = new BluetoothConnection(context);

        mSerialConnection.addListener(this);
        mWifiConnection.addListener(this);
        mBluetoothConnection.addListener(this);

        refreshAvailableDevices();
    }

    /**
     * Refreshes the list of devices which the user can connect to
     */
    public void refreshAvailableDevices(){
        mBluetoothConnection.scanForDevices();
        mBluetoothDevices = BluetoothConnection.getDevices();
    }

    /**
     * Gets available bluetooth devices
     * @return - a list of the currently available Bluetooth Classic devices
     */
    @NonNull
    public static List<BluetoothDevice> getBluetoothDevices(){
        return Collections.unmodifiableList(mBluetoothDevices);
    }

    /**
     * Ends all previous connections and connects to the device that was chosen last.
     * Is called by a thread to make connecting asynchronous.
     */
    private void connect_async(){
        disconnect();
        if(mConnectionMethod != null) {
            switch (mConnectionMethod) {
                case SERIAL:
                    int baudRate = Integer.parseInt(mExtraInfo);
                    mSerialConnection.addListener(this);
                    mSerialConnection.connect(baudRate);
                    break;
                case WIFI:
                    mWifiConnection.addListener(this);
                    mWifiConnection.connect(mExtraInfo);
                    break;
                case BLUETOOTH:
                    mBluetoothConnection.addListener(this);
                    mBluetoothConnection.connect(mExtraInfo);
                    break;
            }
        }else{
            Log.d("Connection manager",
                    "Asked to connect when no connection information has been passed");
        }
    }

    /**
     * Ends all previous connections and connects to the device that was chosen last.
     * Runs asynchronously.
     */
    @Override
    public void connect() {
        Thread t  = new Thread(this::connect_async);
        t.start();
    }

    /**
     * Connects to the chosen device
     * @param newConnectionMethod the method which is used to connect to the device
     *                            (e.g. WIFI, SERIAL, BLUETOOTH)
     * @param newExtraInfo the extra information necessary to create the connection
     *                     (e.g. URL, BAUD RATE, MAC ADDRESS)
     */
    public void connect(@NonNull ConnectionMethod newConnectionMethod,
                        @NonNull String newExtraInfo){
        if(newConnectionMethod == mConnectionMethod
                && newExtraInfo.equals(mExtraInfo)
                && mConnected){
            //already connected correctly so does nothing
            connected();
        }else {
            mConnectionMethod = newConnectionMethod;
            mExtraInfo = newExtraInfo;
            connect();
        }
    }

    /**
     * Sends a message to the connected device
     * @param message the message to send as text
     */
    @Override
    public void sendMessage(@NonNull String message){
        if(!mConnected){
            connect();
        }
        if(mConnected) {
            assert mConnectionMethod != null;
            switch (mConnectionMethod) {
                case SERIAL:
                    mSerialConnection.sendMessage(message);
                    break;
                case WIFI:
                    mWifiConnection.sendMessage(message);
                    break;
                case BLUETOOTH:
                    mBluetoothConnection.sendMessage(message);
                    break;
            }
        }
    }

    /**
     * Propagates the onConnected() message to all listeners
     */
    @Override
    public void onConnected() {
        connected();
    }

    /**
     * Propagates the onDisconnected() message to all listeners
     */
    @Override
    public void onDisconnected(@Nullable String reason) {
        disconnected(reason);
    }

    /**
     * Passes on new messages to all listeners
     * @param unreadMessages a list of the new messages that haven't been processed yet
     */
    @Override
    public void onReceivedMessages(@NonNull Iterable<String> unreadMessages) {
        for(ConnectionListener listener : listeners){
            listener.onReceivedMessages(unreadMessages);
        }
    }

    /**
     * Disconnects all connections(and removes listeners).
     * Can reconnect by calling the connect method
     */
    public void disconnect(){
        mSerialConnection.disconnect();
        mWifiConnection.disconnect();
        mBluetoothConnection.disconnect();

        mSerialConnection.removeListener(this);
        mWifiConnection.removeListener(this);
        mBluetoothConnection.removeListener(this);
    }
}