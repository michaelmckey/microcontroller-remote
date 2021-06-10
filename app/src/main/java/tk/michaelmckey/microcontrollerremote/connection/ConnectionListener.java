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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Listens for changes in the connection state
 * @author Michael McKey
 * @version 1.2.2
 */
public interface ConnectionListener {
    /**
     * Called whenever the connection is initiated
     */
    void onConnected();

    /**
     * Called whenever the connection ends
     * @param reason the reason the device has been disconnected(null disconnected normally)
     */
    void onDisconnected(@Nullable String reason);

    /**
     * Called when messages are received
     * @param unreadMessages a list of the new messages that haven't been processed yet
     */
    void onReceivedMessages(@NonNull Iterable<String> unreadMessages);
}
