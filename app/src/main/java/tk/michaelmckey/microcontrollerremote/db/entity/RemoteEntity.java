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

package tk.michaelmckey.microcontrollerremote.db.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import tk.michaelmckey.microcontrollerremote.db.Layout;
import tk.michaelmckey.microcontrollerremote.connection.ConnectionMethod;
import tk.michaelmckey.microcontrollerremote.recyclerview.RecyclerElement;

/**
 * Stores the title, layout type, connection method, connection info and the time of creation
 * for a given RemoteEntity
 * @author Michael McKey
 * @version 1.0.0
 */
@Entity(tableName = "remote_table")
public class RemoteEntity implements RecyclerElement{
    /**
     * A unique autogenerated id
     * @see #getId()
     */
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    public int id;

    /**
     * The title of the RemoteEntity
     * @see #getTitle()
     * @see #setTitle(String)
     */
    @NonNull
    @ColumnInfo(name = "title")
    private String mTitle;

    /**
     * The {@link Layout} used by the RemoteEntity.
     * @see #getLayoutType()
     * @see #setLayoutType(String)
     */
    @NonNull
    @ColumnInfo(name = "mLayoutType")
    private String mLayoutType;

    /**
     * The {@link ConnectionMethod} used by the RemoteEntity.
     * @see #getConnectionMethod()
     * @see #setConnectionMethod(String)
     */
    @NonNull
    @ColumnInfo(name = "mConnectionMethod")
    private String mConnectionMethod;

    /**
     * The connection info for the remote.
     * The Bluetooth MAC address, the serial baud rate or the URL
     * (depending on the {@link #mConnectionMethod})
     * @see #setConnectionInfo(String)
     * @see #getConnectionInfo()
     */
    @NonNull
    @ColumnInfo(name = "mConnectionInfo")
    private String mConnectionInfo;

    /**
     * The creation time of the RemoteEntity
     * (the number of milliseconds between January 1, 1970, 00:00:00 GMT)
     * Cannot be changed once the RemoteEntity has been created.
     * @see #getTime()
     */
    @ColumnInfo(name = "time")
    private final long mTime;

    /**
     * Constructs the RemoteEntity
     * @param title            the title of the remote. See {@link #mTitle}
     * @param layoutType       the {@link Layout}
     * @param connectionMethod the {@link ConnectionMethod}
     * @param connectionInfo   the information necessary to establish the connection.
     *                         See {@link #mConnectionInfo}
     * @param time             the time of creation. See {@link #mTime}
     */
    public RemoteEntity(@NonNull String title,
                        @NonNull String layoutType,
                        @NonNull String connectionMethod,
                        @NonNull String connectionInfo,
                        long time) {
        Layout.valueOf(layoutType);//deliberately throws an error if a layout hasn't been passed
        ConnectionMethod.valueOf(connectionMethod);
        mTitle = title;
        mLayoutType = layoutType;
        mConnectionMethod = connectionMethod;
        mConnectionInfo = connectionInfo;
        mTime = time;
    }

    /**
     * Returns the {@link Layout} of the RemoteEntity
     * @return the layout type
     * @see #mLayoutType
     */
    @NonNull
    public String getLayoutType() {
        return mLayoutType;
    }

    /**
     * Sets the {@link Layout} of the RemoteEntity
     * @param layoutType the new layout type(must be a value in the Enum Layout).
     * @see Layout
     * @see #mLayoutType
     */
    public void setLayoutType(@NonNull String layoutType) {
        Layout.valueOf(layoutType);
        mLayoutType = layoutType;
    }

    /**
     * Returns the {@link ConnectionMethod} of the RemoteEntity
     * @return the {@link ConnectionMethod}.
     * @see #setConnectionMethod(String)
     * @see #mConnectionMethod
     */
    @NonNull
    public String getConnectionMethod() {
        return mConnectionMethod;
    }

    /**
     * Sets the ConnectionMethod of the RemoteEntity
     * @param connectionMethod the new {@link ConnectionMethod}
     * @see #getConnectionMethod()
     * @see #mConnectionMethod
     */
    public void setConnectionMethod(@NonNull String connectionMethod) {
        ConnectionMethod.valueOf(connectionMethod);//throws an error if the input isn't valid
        mConnectionMethod = connectionMethod;
    }


    /**
     * Returns the title of this RemoteEntity/RecyclerElement.
     * Used by the RecyclerViewAdapter to sort its elements.
     * @return the title
     * @see #mTitle
     * @see #setTitle(String)
     */
    @NonNull
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns the contents of this RecyclerElement(the {@link ConnectionMethod}).
     * Used by the RecyclerViewAdapter to sort its elements.
     * @return the contents(the {@link ConnectionMethod} in this case)
     * @see #getConnectionMethod()
     * @see #mConnectionMethod
     */
    @NonNull
    @Override
    public String getContents() {
        return mConnectionMethod;
    }

    /**
     * Returns the connection info necessary to create a connection
     *
     * @return the connection info
     * @see #setConnectionInfo(String) ()
     * @see #mConnectionInfo
     */
    @NonNull
    public String getConnectionInfo() {
        return mConnectionInfo;
    }

    /**
     * Returns the time of creation of this RemoteEntity.
     * Used by the RecyclerViewAdapter to sort its RecyclerViewElements.
     * The time is the number of milliseconds since the Epoch(January 1, 1970, 00:00:00 GMT)
     *
     * @return the time of creation
     * @see #mTime
     */
    public long getTime() {
        return mTime;
    }

    /**
     * Sets the connection info for the remote.
     *
     * @see #getConnectionInfo()
     * @see #mConnectionInfo
     */
    public void setConnectionInfo(@NonNull String connectionInfo) {
        mConnectionInfo = connectionInfo;
    }

    /**
     * Returns the unique Id associated with this RemoteEntity on creation
     *
     * @return the Id
     * @see #getId()
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the title associated with this RemoteEntity
     *
     * @param title the new title
     * @see #mTitle
     * @see #getTitle()
     */
    public void setTitle(@NonNull String title) {
        mTitle = title;
    }

    /**
     * Returns the text representation of the RemoteEntity
     * @return the String which represents the RemoteEntity
     */
    @NonNull
    @Override
    public String toString() {
        return "RemoteEntity{" +
                "id=" + id +
                ", mTitle='" + mTitle + '\'' +
                ", mLayoutType='" + mLayoutType + '\'' +
                ", mConnectionMethod='" + mConnectionMethod + '\'' +
                ", mConnectionInfo='" + mConnectionInfo + '\'' +
                ", mTime=" + mTime +
                '}';
    }

    /**
     * Compares this Remote to the given Remote to see if their data is the same(excluding id's)
     * @param obj the remote to compare to this remote
     * @return true if they are the same
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof RemoteEntity){
            RemoteEntity remote = (RemoteEntity) obj;
            return remote.getTitle().equals(this.getTitle())
                    && remote.getLayoutType().equals(this.getLayoutType())
                    && remote.getConnectionMethod().equals(this.getConnectionMethod())
                    && remote.getConnectionInfo().equals(this.getConnectionInfo())
                    && remote.getTime() == this.getTime();
        }else{
            return false;
        }
    }
}