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

import android.content.Context;
import android.os.Handler;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Defines a generic connection. Is the super class for all connection classes
 * @author Michael McKey
 * @version 1.2.2
 */
public abstract class Connection {
    @NonNull
    private String mStringBuffer = "";
    @NonNull
    private final Collection<String> mUnreadMessages = new LinkedList<>();
    @NonNull
    private final Handler mHandler;
    @NonNull
    static final String NEW_LINE = Objects.requireNonNull(System.getProperty("line.separator"));
    protected boolean mConnected = false;
    @NonNull
    protected final Context mContext;

    @NonNull
    protected final Collection<ConnectionListener> listeners = new LinkedList<>();

    /**
     * Initialises necessary variables but doesn't create a connection.
     * @param context The context of the class which manages the connection
     */
    Connection(@NonNull Context context) {
        mContext = context;
        mHandler = new Handler(context.getMainLooper());
    }
    /**
     * Connects to the device that was chosen last
     */
    public abstract void connect();

    /**
     * Disconnects from the remote device. Can reconnect by calling the connect method
     */
    public abstract void disconnect();//asks to disconnect manually

    /**
     * Sends a message to the connected device
     * @param message the message to send as text
     */
    public abstract void sendMessage(@NonNull String message);

    /**
     * Adds a listener, which will be notified of any changes to the connection.
     * Notifies it of the current connection state
     * @param listener - a ConnectionListener which will be notified
     */
    public void addListener(@NonNull ConnectionListener listener){
        listeners.add(listener);
        if(mConnected){
            listener.onConnected();
        }else{
            listener.onDisconnected(null);
        }
    }

    /**
     * Removes a listener, which will no longer be notified of changes to the connection
     * @param listener - a ConnectionListener which will be removed
     */
    public void removeListener(@NonNull ConnectionListener listener){
        listeners.remove(listener);
    }

    /**
     * Notifies all listeners that the device has become disconnected for any reason.
     * @param reason the reason the device has been disconnected(null if deliberately closed)
     */
    void disconnected(@Nullable String reason){
        mConnected = false;
        for (ConnectionListener listener : listeners) {
            listener.onDisconnected(reason);
        }
    }

    /**
     * Notifies all listeners that the device has been connected to
     */
    void connected(){
        if(!mConnected){//only notifies device if just connected
            mConnected = true;
            for(ConnectionListener listener : listeners){
                listener.onConnected();
            }
        }
    }

    /**
     * Processes the new data and notifies any listeners of the new messages
     * @param newData the new data that hasn't been processed yet
     */
    void onNewData(@NonNull String newData){
        Runnable r = () -> {
            mStringBuffer += newData;
            if(mStringBuffer.contains(NEW_LINE)) {
                while (mStringBuffer.contains(NEW_LINE)) {
                    int newLineIndex = mStringBuffer.indexOf(NEW_LINE);
                    String unreadMessage = mStringBuffer.substring(0, newLineIndex);
                    mUnreadMessages.add(unreadMessage);
                    mStringBuffer = mStringBuffer.substring(newLineIndex + 1);//+1 removes newline
                }

                for(ConnectionListener listener:listeners){
                    //calls each listeners new_message method(first listener triggered first)
                    listener.onReceivedMessages(mUnreadMessages);
                }
                mUnreadMessages.clear();
            }
        };
        mHandler.post(r);
    }
}