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

package tk.michaelmckey.microcontrollerremote.db;

import android.app.Application;
import android.content.res.Resources;
import android.util.Log;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import tk.michaelmckey.microcontrollerremote.R;
import tk.michaelmckey.microcontrollerremote.db.dao.CodeDao;
import tk.michaelmckey.microcontrollerremote.db.dao.RemoteDao;
import tk.michaelmckey.microcontrollerremote.db.entity.CodeEntity;
import tk.michaelmckey.microcontrollerremote.db.entity.RemoteEntity;

/**
 * This is where all of the global data is stored. <br>
 * It manages retrieving and saving data(to both SQL and JSON)
 * @author Michael McKey
 * @version 1.2.2
 */
public class DataRepository {
    @NonNull
    private final CodeDao mCodeDao;
    @NonNull
    private final RemoteDao mRemoteDao;
    @NonNull
    private final LiveData<List<CodeEntity>> mAllCodes;
    @NonNull
    private final LiveData<List<RemoteEntity>> mAllRemotes;
    @NonNull
    private final Resources mResources;
    @NonNull
    private final RemoteLayoutManager mRemoteLayoutManager;
    @NonNull
    private static final String FILENAME = "code_layouts.json";
    @Nullable
    private static volatile DataRepository sInstance;

    /**
     * Sets up object by initialising all variables and retrieving all data. <br>
     * Populates the database with examples if it hasn't been set up yet(as app never run before)
     * @param application the application used to access the sql database
     */
    private DataRepository(@NonNull Application application) {
        mResources = application.getBaseContext().getResources();
        AppDatabase db = AppDatabase.getDatabase(application);

        //sql returns wrapped in LiveData are run asynchronously so aren't always up to date
        mCodeDao = Objects.requireNonNull(db.codeDao());
        mAllCodes = Objects.requireNonNull(mCodeDao.getChronologicalCodes());

        mRemoteDao = Objects.requireNonNull(db.remoteDao());
        mAllRemotes = Objects.requireNonNull(mRemoteDao.getChronologicalRemotes());

        mRemoteLayoutManager = new RemoteLayoutManager(application.getBaseContext(), FILENAME);


        boolean isFilePresent = FileManager.isFilePresent(application.getBaseContext(), FILENAME);
        if(isFilePresent) {
            //loads layout data
            mRemoteLayoutManager.loadLayoutData();
        }else{
            //if file isn't present adds default codes
            createExampleLayouts();
        }
    }

    /**
     * Returns a DataRepository instance
     *
     * @param application the application used to access the SQL database
     * @return a DataRepository instance
     */
    @NonNull
    public static DataRepository getInstance(@NonNull Application application) {
        synchronized (DataRepository.class) {
            if (sInstance == null) {
                sInstance = new DataRepository(application);
            }
            return Objects.requireNonNull(sInstance);
        }
    }

    /**
     * Creates the example layouts. Their layouts are also populated with newly created codes.
     */
    private void createExampleLayouts() {
        long time;
        RemoteEntity remote;

        //creates the Serial Example
        HashMap<String, Long> serialLayout = createLayoutFromMessages(
                ExampleLayoutGenerator.generateSerialExample());
        time = Calendar.getInstance().getTime().getTime();
        remote = new RemoteEntity("Serial Example",
                "LARGE",
                "SERIAL",
                "9600",
                time);
        insert(remote, serialLayout);

        //creates the Bluetooth Example
        HashMap<String, Long> bluetoothLayout = createLayoutFromMessages(
                ExampleLayoutGenerator.generateBluetoothExample());
        time = Calendar.getInstance().getTime().getTime();
        remote = new RemoteEntity("Bluetooth Example (change MAC address)",
                "SMALL",
                "BLUETOOTH",
                "00:11:22:33:44:55",
                time);
        insert(remote, bluetoothLayout);

        //creates the toy example
        HashMap<String, Long> toyLayout = createLayoutFromMessages(
                ExampleLayoutGenerator.generateToyExample());
        time = Calendar.getInstance().getTime().getTime();
        remote = new RemoteEntity("Toy Example(change MAC address)",
                "TOY",
                "BLUETOOTH",
                "00:11:22:33:44:55",
                time);
        insert(remote, toyLayout);

        //creates the lights example
        HashMap<String, Long> lightsLayout = createLayoutFromMessages(
                ExampleLayoutGenerator.generateLightsExample());
        time = Calendar.getInstance().getTime().getTime();
        remote = new RemoteEntity("Lights Example(change MAC address)",
                "LIGHTS",
                "BLUETOOTH",
                "00:11:22:33:44:55",
                time);
        insert(remote, lightsLayout);
    }

    /**
     * Saves the apps data so it can be restored if the app is reopened.
     */
    public void save(){
        mRemoteLayoutManager.saveCodeLayouts();
    }

    /**
     * Gets all of the Codes stored in the database
     * @return the Codes wrapped in LiveData
     */
    @NonNull
    public LiveData<List<CodeEntity>> getAllCodes() {
        return mAllCodes;
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
        Callable<CodeEntity> callable = () -> mCodeDao.getCode(id);
        Future<CodeEntity> future = AppDatabase.databaseWriteExecutor.submit(callable);
        return future.get();
    }

    /**
     * Gets any Code with the specified message
     * @param message the message of the Code
     * @return any Codes that match
     * @throws ExecutionException Exception thrown during execution
     * @throws InterruptedException Exception thrown when a Thread is interrupted
     */
    @NonNull
    public List<CodeEntity> getCodesWithMessage(@NonNull String message)
            throws ExecutionException, InterruptedException {
        Callable<List<CodeEntity>> callable = () -> mCodeDao.getEntriesWithMessage(message);
        Future<List<CodeEntity>> future = AppDatabase.databaseWriteExecutor.submit(callable);
        return future.get();
    }

    /**
     * Gets any Remotes created at the exact time
     * @param time the exact time of creation of the remote (in milliseconds)
     * @return any Remotes that match
     * @throws ExecutionException Exception thrown during execution
     * @throws InterruptedException Exception thrown when a Thread is interrupted
     */
    @NonNull
    private List<RemoteEntity> getRemotesWithTime(long time)
            throws ExecutionException, InterruptedException {
        Callable<List<RemoteEntity>> callable = () -> mRemoteDao.getRemotesWithTime(time);
        Future<List<RemoteEntity>> future = AppDatabase.databaseWriteExecutor.submit(callable);
        return future.get();
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
        Callable<RemoteEntity> callable = () -> mRemoteDao.getRemote(id);
        Future<RemoteEntity> future = AppDatabase.databaseWriteExecutor.submit(callable);
        return future.get();
    }

    /**
     * Inserts the Code into the database on a non-UI thread.
     * This prevents any long running operations on the main thread,
     * as this would block the UI(and trigger exceptions)
     * @param code the Code to insert
     */
    public void insert(@NonNull CodeEntity code) {
        AppDatabase.databaseWriteExecutor.execute(() -> mCodeDao.insert(code));
    }

    /**
     * Inserts the Code into the database(and retrieves it) on a non-UI thread.
     * This prevents any long running operations on the main thread,
     * as this would block the UI(and trigger exceptions)
     * @param uninitialisedCode the Code to insert
     * @return the matching Code after it has been retrieved from the database
     */
    @Nullable
    public CodeEntity insertAndRetrieve(@NonNull CodeEntity uninitialisedCode){
        Callable<CodeEntity> callable = () -> {
            mCodeDao.insert(uninitialisedCode);
            try {
                // need to retrieve the code as the uninitialisedCode never has an Id
                List<CodeEntity> codesWithMessage =
                        getCodesWithMessage(uninitialisedCode.getMessage());
                for (CodeEntity code: codesWithMessage) {
                    if(uninitialisedCode.equals(code)){
                        return code;
                    }
                }
                //code wasn't found and returned
                Log.e("DataRepository",
                        "The Code just entered into the database cannot be found");

            } catch (@NonNull ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Log.e("DataRepository",
                        "Error when calling getCodesWithMessage. Message = "
                                + uninitialisedCode.getMessage());
            }
            return null;
        };
        Future<CodeEntity> future = AppDatabase.databaseWriteExecutor.submit(callable);
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Deletes the Code from the database on a non-UI thread.
     * This prevents any long running operations on the main thread,
     * as this would block the UI(and trigger exceptions)
     * @param code the Code to delete
     */
    public void delete(@NonNull CodeEntity code) {
        AppDatabase.databaseWriteExecutor.execute(() -> mCodeDao.delete(code));
    }

    /**
     * Updates the given Code in the database, on a non-UI thread.
     * This prevents any long running operations on the main thread,
     * as this would block the UI(and trigger exceptions)
     * @param code the updated Code
     */
    public void update(@NonNull CodeEntity code) {
        AppDatabase.databaseWriteExecutor.execute(() -> mCodeDao.update(code));
    }

    /**
     * Gets all of the Remotes stored in the database
     * @return the Remotes wrapped in LiveData
     */
    @NonNull
    public LiveData<List<RemoteEntity>> getAllRemotes() {
        return mAllRemotes;
    }

    /**
     * Inserts the Remote into the database(and retrieves it) on a non-UI thread.
     * This prevents any long running operations on the main thread,
     * as this would block the UI(and trigger exceptions)
     * @param uninitialisedRemote the Remote to insert
     * @return the matching Remote after it has been retrieved from the database
     */
    @Nullable
    public RemoteEntity insertAndRetrieve(@NonNull RemoteEntity uninitialisedRemote){
        Callable<RemoteEntity> callable = () -> {
            mRemoteDao.insert(uninitialisedRemote);
            try {
                // need to retrieve the remote as the uninitialised_remote never has an Id
                List<RemoteEntity> remotesWithTime = getRemotesWithTime(uninitialisedRemote.getTime());
                for (RemoteEntity remote: remotesWithTime) {
                    if(uninitialisedRemote.equals(remote)){
                        return remote;
                    }
                }
                //remote wasn't found and returned
                Log.e("DataRepository",
                        "The Remote just entered into the database cannot be found");

            } catch (@NonNull ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Log.e("DataRepository",
                        "Error when calling getRemotesWithTime. Time = "
                                + uninitialisedRemote.getTime());
            }
            return null;
        };
        Future<RemoteEntity> future = AppDatabase.databaseWriteExecutor.submit(callable);
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates a remote layout with the given remote
     * @param uninitialisedRemote the remote to insert into the database
     * @param layout the Map layout which dictates which code is assigned to which button
     */
    public void insert(@NonNull RemoteEntity uninitialisedRemote,
                       @NonNull HashMap<String, Long> layout){
        RemoteEntity retrievedRemote = insertAndRetrieve(uninitialisedRemote);
        if(retrievedRemote != null) {
            mRemoteLayoutManager.createLayout(retrievedRemote.getId(), layout);
            save();
            Log.e("create new remote", "created remote" + retrievedRemote);
        }else{
            Log.e("DataRepository", "inserted remote wasn't returned");
        }
    }

    /**
     * Deletes the Remote from the database on a non-UI thread.
     * This prevents any long running operations on the main thread,
     * as this would block the UI(and trigger exceptions)
     * @param remote the Remote to delete
     */
    public void delete(@NonNull RemoteEntity remote) {
        mRemoteLayoutManager.deleteLayout(remote.getId());
        AppDatabase.databaseWriteExecutor.execute(() -> mRemoteDao.delete(remote));
        save();
    }

    /**
     * Updates the given Remote in the database, on a non-UI thread.
     * This prevents any long running operations on the main thread,
     * as this would block the UI(and trigger exceptions)
     * @param remote the updated Remote
     */
    public void update(@NonNull RemoteEntity remote) {
        AppDatabase.databaseWriteExecutor.execute(() -> mRemoteDao.update(remote));
    }

    /**
     * Duplicates the remote. Deep copies the remote layout and duplicates the RemoteEntity.
     * @param oldRemote the remote layout to delete
     */
    public void duplicate(@NonNull RemoteEntity oldRemote){
        Map<String, Long> oldLayout = mRemoteLayoutManager.getLayout(oldRemote);
        HashMap<String, Long> newLayout = new HashMap<>();
        for (Map.Entry<String, Long> entry : oldLayout.entrySet()) {
            newLayout.put(entry.getKey(), entry.getValue());
        }
        long time = Calendar.getInstance().getTime().getTime();
        RemoteEntity remote = new RemoteEntity(
                oldRemote.getTitle() + mResources.getString(R.string.copy_suffix),
                oldRemote.getLayoutType(),
                oldRemote.getConnectionMethod(),
                oldRemote.getConnectionInfo(),
                time);
        insert(remote, newLayout);
    }

    /**
     * Duplicates the Code
     * @param oldCode the Code to duplicate
     */
    public void duplicate(@NonNull CodeEntity oldCode){
        long time = Calendar.getInstance().getTime().getTime();
        CodeEntity newCode = new CodeEntity(
                oldCode.getTitle() + mResources.getString(R.string.copy_suffix),
                oldCode.getMessage(),
                time);
        insert(newCode);
    }

    /**
     * Creates a new remote layout from a Map
     * (which links the View ids to their corresponding message).
     * @param exampleIdToMessage a Map (viewId(Integer), message(string))
     * @return a HashMap (viewResourceName(String), codeId(long))
     */
    @NonNull
    private HashMap<String, Long> createLayoutFromMessages(
            @NonNull Map<Integer, String> exampleIdToMessage){
        HashMap<String, Long> exampleLayout = new HashMap<>();

        //for each key:value pair it creates a CodeEntity object and inserts it into the database
        for (Map.Entry<Integer, String> entry : exampleIdToMessage.entrySet()) {
            //resource names used instead of IDs.
            //Because when the layout changes by adding a new view the ids are all changed
            // (the data stored in json is then permanently remembered with incorrect ids)
            int buttonId = entry.getKey();
            String message = entry.getValue();
            //String shortName = mResources.getResourceEntryName(buttonId);
            String longName = mResources.getResourceName(buttonId);

            String title = message + "*";
            //creates and adds the code
            long time = Calendar.getInstance().getTime().getTime();
            CodeEntity uninitialisedCode = new CodeEntity(title, message, time);
            CodeEntity retrievedCode = insertAndRetrieve(uninitialisedCode);
            if(retrievedCode != null) {
                exampleLayout.put(longName, retrievedCode.getId());
            }else{
                Log.e("DataRepository", "inserted code wasn't returned");
            }
        }
        return exampleLayout;
    }

    /**
     * Gets the Code associated with the given view in the given Remote.
     * @param remote the remote
     * @param viewId the id of the view
     * @return the corresponding Code
     */
    @Nullable
    public CodeEntity getCorrespondingCode(@NonNull RemoteEntity remote, int viewId) {
        Long codeId = mRemoteLayoutManager.getCorrespondingCodeId(remote, viewId);
        if(codeId != null) {
            try {
                return getCode(codeId);
            } catch (@NonNull ExecutionException | InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }else{
            return null;
        }
    }

    /**
     * Sets the Code associated with the given view in the given Remote
     * @param remote the selected remote
     * @param resourceName the resourceName of the view
     * @param code the Code to associate with the button
     *               (if null just deletes the reference)
     */
    public void setCorrespondingCode(@NonNull RemoteEntity remote,
                                     @NonNull String resourceName,
                                     @Nullable CodeEntity code) {
        mRemoteLayoutManager.setCorrespondingCode(remote, resourceName, code);
    }
}