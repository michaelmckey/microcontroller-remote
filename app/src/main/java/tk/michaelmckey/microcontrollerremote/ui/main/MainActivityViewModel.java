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

package tk.michaelmckey.microcontrollerremote.ui.main;

import android.app.Application;

import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import tk.michaelmckey.microcontrollerremote.db.DataRepository;
import tk.michaelmckey.microcontrollerremote.db.entity.CodeEntity;
import tk.michaelmckey.microcontrollerremote.db.entity.RemoteEntity;

/**
 * Acts as a global ViewModel which manages UI data.
 * Manages layouts and provides access to the {@link DataRepository}.
 * @author Michael McKey
 * @version 1.0.0
 */
public class MainActivityViewModel extends AndroidViewModel {
    @NonNull
    private final DataRepository mDataRepository;

    /**
     * Retrieves all of the data
     * and creates instance of {@link DataRepository}
     */
    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        mDataRepository = DataRepository.getInstance(application);
    }

    /**
     * Saves all data.
     */
    protected void save(){
        mDataRepository.save();
    }

    /**
     * Called when the ViewModel is destroyed
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        save();
    }

    /**
     * Gets all of the Codes stored in the database
     * @return the Codes wrapped in LiveData
     */
    @NonNull
    public LiveData<List<CodeEntity>> getAllCodes() {
        return mDataRepository.getAllCodes();
    }

    /**
     * Gets all of the Remotes stored in the database
     * @return the Remotes wrapped in LiveData
     */
    @NonNull
    public LiveData<List<RemoteEntity>> getAllRemotes() {
        return mDataRepository.getAllRemotes();
    }

    /**
     * Creates a remote layout with the given RemoteEntity
     * @param remote the remote to insert into the database
     * @param layout the HashMap layout which dictates which code is assigned to which button
     */
    public void insert(@NonNull RemoteEntity remote, @NonNull HashMap<String, Long> layout){
        mDataRepository.insert(remote, layout);
    }

    /**
     * Inserts a Code into the database
     * @param code the Code to insert
     */
    public void insert(@NonNull CodeEntity code) {
        mDataRepository.insert(code);
    }

    /**
     * Updates the given Code in the database
     * @param code the Code to update
     */
    public void update(@NonNull CodeEntity code){
        mDataRepository.update(code);
    }

    /**
     * Updates the given Remote in the database
     * @param remote the Remote to update
     */
    public void update(@NonNull RemoteEntity remote){
        mDataRepository.update(remote);
    }

    /**
     * Duplicates the Code in the database.
     * @param oldCode the Code to duplicate
     */
    public void duplicate(@NonNull CodeEntity oldCode){
        mDataRepository.duplicate(oldCode);
    }

    /**
     * Duplicates the Remote and its Layout
     * @param oldRemote the remote to duplicate
     */
    public void duplicate(@NonNull RemoteEntity oldRemote){
        mDataRepository.duplicate(oldRemote);
    }


    /**
     * Deletes the given Code from the database
     * @param code the Code to delete
     */
    public void delete(@NonNull CodeEntity code){
        mDataRepository.delete(code);
    }

    /**
     * Deletes the given Remote from the database
     * @param remote the Remote to delete
     */
    public void delete(@NonNull RemoteEntity remote){
        mDataRepository.delete(remote);
    }
}