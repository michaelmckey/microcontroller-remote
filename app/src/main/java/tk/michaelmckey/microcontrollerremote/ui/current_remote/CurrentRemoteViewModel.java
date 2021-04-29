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

package tk.michaelmckey.microcontrollerremote.ui.current_remote;

import android.app.Application;

import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import tk.michaelmckey.microcontrollerremote.connection.ConnectionListener;
import tk.michaelmckey.microcontrollerremote.connection.ConnectionManager;
import tk.michaelmckey.microcontrollerremote.connection.ConnectionMethod;
import tk.michaelmckey.microcontrollerremote.db.DataRepository;
import tk.michaelmckey.microcontrollerremote.db.entity.CodeEntity;
import tk.michaelmckey.microcontrollerremote.db.entity.RemoteEntity;
import tk.michaelmckey.microcontrollerremote.ui.remotes.RemotesFragment;

/**
 * Prepares and manages data for the {@link CurrentRemoteFragment}
 * @author Michael McKey
 * @version 1.0.0
 */
public class CurrentRemoteViewModel extends AndroidViewModel {
    //no need to use LiveData as for data to change the fragment has to be reloaded
    //Using LiveData would weaken remove nullity checks
    @NonNull
    private final DataRepository mDataRepository;
    @Nullable
    private RemoteEntity mSelectedRemote;//its the first thing set and is never null again
    @Nullable
    private String mButtonResourceName;
    @NonNull
    private final ConnectionManager mConnectionManager;

    /**
     * Gets an instance of the DataRepository
     *
     * @param application the application to pass to DataRepository
     */
    public CurrentRemoteViewModel(@NonNull Application application) {
        super(application);
        mDataRepository = DataRepository.getInstance(application);
        mConnectionManager = new ConnectionManager(application.getBaseContext());
    }

    public void connect(){
        assert mSelectedRemote != null;
        ConnectionMethod connectionMethod =
                ConnectionMethod.valueOf(mSelectedRemote.getConnectionMethod());
        String connectionInfo = mSelectedRemote.getConnectionInfo();
        mConnectionManager.connect(connectionMethod, connectionInfo);
    }

    public void addConnectionListener(@NonNull ConnectionListener listener){
        mConnectionManager.addListener(listener);
    }

    public void removeConnectionListener(@NonNull ConnectionListener listener){
        mConnectionManager.removeListener(listener);
    }

    public void sendMessage(@NonNull String message){
        mConnectionManager.sendMessage(message);
    }

    /**
     * Gets the resource name of the button that is being assigned to a new code
     *
     * @return the resource name (e.g. tk.michaelmckey.microcontrollerremote:id/button_ok)
     */
    @Nullable
    public String getButtonResourceName() {
        return mButtonResourceName;
    }

    /**
     * Sets which button is being assigned to a new code
     *
     * @param resourceName the resource name of the button
     *                     (e.g. tk.michaelmckey.microcontrollerremote:id/button_ok)
     */
    public void setButtonResourceName(@Nullable String resourceName) {
        mButtonResourceName = resourceName;
    }

    /**
     * Gets the Remote to display
     *
     * @return the selected remote (as chosen by the user in {@link RemotesFragment})
     */
    @NonNull
    RemoteEntity getSelectedRemote() {
        if (mSelectedRemote != null) {
            return mSelectedRemote;
        } else {
            throw new AssertionError("getSelectedRemote() returns null");
        }
    }

    /**
     * Sets which Remote to display (chosen by the user in {@link RemotesFragment})
     * @param remote the selected remote
     */
    public void setSelectedRemote(@NonNull RemoteEntity remote) {
        mSelectedRemote = remote;
    }

    /**
     * Gets the Code associated with the button
     *
     * @param viewId the id of the button
     * @return the Code
     */
    @Nullable
    public CodeEntity getCorrespondingCode(int viewId) {
        assert mSelectedRemote != null;
        return mDataRepository.getCorrespondingCode(mSelectedRemote, viewId);
    }

    /**
     * Sets the code associated with the Button
     *
     * @param resourceName the resource name of the button
     *                     (e.g. tk.michaelmckey.microcontrollerremote:id/button_ok)
     * @param code         the Code to associate with the Button
     *                     (if it is null it just removes the reference)
     */
    public void setCorrespondingCode(@NonNull String resourceName, @Nullable CodeEntity code) {
        assert mSelectedRemote != null;
        mDataRepository.setCorrespondingCode(mSelectedRemote, resourceName, code);
    }

    /**
     * Gets all of the codes with the given message
     *
     * @param value the message
     * @return A list of all of the codes with the given message
     * @throws ExecutionException   thrown when due to an error during execution
     * @throws InterruptedException thrown when a thread has been interrupted
     */
    @NonNull
    public List<CodeEntity> getCodesWithMessage(@NonNull String value)
            throws ExecutionException, InterruptedException {
        return mDataRepository.getCodesWithMessage(value);
    }

    /**
     * Inserts a new Code into the database
     *
     * @param code the Code to insert
     */
    public void insert(@NonNull CodeEntity code) {
        mDataRepository.insert(code);
    }

    /**
     * Gets the Code with the specified id
     * @param id the id of the Code
     * @return the Code
     * @throws ExecutionException Exception thrown during execution
     * @throws InterruptedException Exception thrown when a Thread is interrupted
     */
    @NonNull
    public CodeEntity getCode(long id) throws ExecutionException, InterruptedException {
        return mDataRepository.getCode(id);
    }

    /**
     * Gets the Remote with the specified id
     * @param id the id of the Remote
     * @return the Remote
     * @throws ExecutionException Exception thrown during execution
     * @throws InterruptedException Exception thrown when a Thread is interrupted
     */
    @NonNull
    public RemoteEntity getRemote(long id) throws ExecutionException, InterruptedException {
        return mDataRepository.getRemote(id);
    }
}