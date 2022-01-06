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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import tk.michaelmckey.microcontrollerremote.R;

/**
 * Creates and manages Bluetooth Classic connections
 * @author Michael McKey
 * @version 1.2.2
 */
public class BluetoothConnection extends Connection{
    @NonNull
    private static final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    @NonNull
    private static final List<BluetoothDevice> mDevices = new ArrayList<>();
    @NonNull
    private static final UUID BLUETOOTH_SPP =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Nullable
    private BluetoothSocket mBluetoothSocket;
    @NonNull
    private String mMacAddress = "";

    /**
     * Initialises necessary variables but doesn't create a connection.
     * Starts scan for Bluetooth Classic devices.
     * @param context The context of the class which manages the connection
     */
    BluetoothConnection(@NonNull Context context){
        super(context);
        //todo add a way to disable bluetooth if it isn't supported on the current platform
        // BluetoothAdapter test = BluetoothAdapter.getDefaultAdapter();
        scanForDevices();
    }

    /**
     * Connects to the device that was chosen last
     */
    @Override
    public void connect() {
        if(!mMacAddress.isEmpty()) {
            if (mBluetoothAdapter.isEnabled()) {//checks if bluetooth is turned ON
                mBluetoothAdapter.cancelDiscovery();
                try {
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mMacAddress);
                    mBluetoothSocket = device.createRfcommSocketToServiceRecord(BLUETOOTH_SPP);
                    mBluetoothSocket.connect();
                    if (mBluetoothSocket.isConnected()) {
                        connected();
                        checkForMessages();
                    } else {
                        disconnected(mContext.getString(R.string.not_connected));
                    }
                } catch (IllegalArgumentException e){
                    e.printStackTrace();
                    disconnected(mContext.getString(R.string.invalid_bluetooth_address));
                }catch (IOException e) {
                    e.printStackTrace();
                    disconnected(mContext.getString(R.string.io_exception));
                }
            } else {
                disconnected(mContext.getString(R.string.turn_on_bluetooth));
            }
        }else{
            disconnected(mContext.getString(R.string.mac_address_is_empty));
        }
    }

    /**
     * Creates a connection with the given device
     * @param macAddress the mac address of the Bluetooth classic device chosen to connect to
     */
    public void connect(@NonNull String macAddress){
        mMacAddress = macAddress;
        connect();
    }

    /**
     * Continuously scans for new messages in the background
     */
    private void checkForMessages() {
        Runnable r = () -> {
            while(mConnected) {//stops checking in background if disconnected
                if (mBluetoothSocket != null) {
                    if (mBluetoothSocket.isConnected()) {
                        try {
                            InputStream inputStream = mBluetoothSocket.getInputStream();
                            if (inputStream.available() > 0) {
                                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                while (inputStream.available() > 0) {
                                    outputStream.write(inputStream.read());
                                }
                                onNewData(outputStream.toString());
                                outputStream.close();
                            }
                        } catch (IOException e) {
                            //logs the exception but continues to loop and scan for messages
                            e.printStackTrace();
                            disconnected(mContext.getString(R.string.io_exception));
                        }
                    } else {
                        disconnected(mContext.getString(R.string.not_connected));
                    }
                }
            }
        };
        Thread thread = new Thread(r);
        thread.start();
    }

    /**
     * Disconnects from the remote device it is currently connected to.
     * Can reconnect by calling the connect method
     */
    public void disconnect(){
        if(mBluetoothSocket !=null){
            try {
                mBluetoothSocket.close();
            } catch (IOException e) {
                //doesn't matter if error thrown as socket won't be used again.
                e.printStackTrace();
            }
        }
        disconnected(null);
    }

    /**
     * Sends a message to the connected device
     * @param message the message to send as text
     */
    public void sendMessage(@NonNull String message){
        if(mConnected) {
            assert mBluetoothSocket != null;
            byte[] data = (message + NEW_LINE).getBytes();

            try {
                mBluetoothSocket.getOutputStream().write(data);
                connected();
            } catch (IOException e) {
                e.printStackTrace();
                disconnected(mContext.getString(R.string.io_exception));
            }
        }else{
            Log.d("BluetoothConnection", "Not connected");
        }
    }

    /**
     * Returns the list of the available Bluetooth Classic devices
     * @return - the Bluetooth Classic Devices
     */
    @NonNull
    public static List<BluetoothDevice> getDevices(){
        return Collections.unmodifiableList(mDevices);
    }

    /**
     * Gets a list of paired Bluetooth Classic devices
     */
    public static void scanForDevices(){
        mDevices.clear();
        for (BluetoothDevice device : mBluetoothAdapter.getBondedDevices()) {
            if(device.getType() == BluetoothDevice.DEVICE_TYPE_CLASSIC
                    || device.getType() == BluetoothDevice.DEVICE_TYPE_DUAL){
                mDevices.add(device);
            }
        }
        Collections.sort(mDevices, (o1, o2) ->
                Collator.getInstance().compare(o1.getName(), o2.getName()));
        mBluetoothAdapter.cancelDiscovery();
    }
}