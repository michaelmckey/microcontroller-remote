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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import androidx.annotation.NonNull;

/**
 * Creates and manages WiFi connections
 * @author Michael McKey
 * @version 1.2.2
 */
public class WiFiConnection extends Connection{
    @NonNull
    private String mBaseUrl = "";

    /**
     * Initialises necessary variables but doesn't create a connection.
     * @param context The context of the class which manages the connection
     */
    WiFiConnection(@NonNull Context context) {
        super(context);
    }

    /**
     * Connects to given URL to test the connection
     */
    @Override
    public void connect() {
        sendMessage("");//tests connection
    }

    /**
     * Creates a connection with the given device
     * @param url the url of the device chosen to connect to
     */
    public void connect(@NonNull String url){
        mBaseUrl = url;
        connect();
    }

    /**
     * Disconnects from the remote device. Can reconnect by calling the connect method
     */
    public void disconnect(){
        if(mConnected){
            sendMessage("");//gets any remaining messages
        }
        //don't need to free up any resources with wifi
        disconnected(null);
    }

    /**
     * Sends a message to the connected device
     * @param message the message to send as text
     */
    public void sendMessage(@NonNull String message){
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        String url = mBaseUrl + message;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            //This code is executed if the server responds,
            // whether or not the response contains data.
            onNewData(response);
            connected();
        }, error -> {
            //This code is executed if there is an error;
            disconnected(error.getMessage());
        });
        requestQueue.add(stringRequest);
    }
}
