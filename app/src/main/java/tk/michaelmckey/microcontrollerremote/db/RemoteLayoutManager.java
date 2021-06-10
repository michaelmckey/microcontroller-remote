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

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import tk.michaelmckey.microcontrollerremote.db.entity.CodeEntity;
import tk.michaelmckey.microcontrollerremote.db.entity.RemoteEntity;

/**
 * Manages the all of the remote Layouts.
 * Manages creating, retrieving, modifying and deleting layouts.
 * @author Michael Mckey
 * @version 1.2.2
 */
class RemoteLayoutManager {
    @NonNull private static final String TAG = "RemoteLayoutManager";
    @NonNull
    private final String mFileName;
    /**
     * Map representing the layout. In the format: {Remote_id, {resource_name, Code_id}}
     */
    @NonNull
    private  Map<Long, HashMap<String, Long>> mLayouts = new HashMap<>();
    @NonNull
    private final Context mContext;
    @NonNull
    private final Resources mResources;

    /**
     * Initialises the RemoteLayoutManager
     * @param context the context for accessing files
     * @param fileName the author_name_text_view of the file that contains the layouts
     */
    public RemoteLayoutManager(@NonNull Context context, @NonNull String fileName){
        mContext = context;
        mResources = context.getResources();
        mFileName = fileName;
    }

    /**
     * Saves the current Layout Map to the associated json file.
     */
    public void saveCodeLayouts() {
        HashMap<String, HashMap<String, String>> stringLayouts = new HashMap<>();
        for (Map.Entry<Long, HashMap<String, Long>> entry1 : mLayouts.entrySet()) {
            Map<String, Long> layoutValue = entry1.getValue();
            HashMap<String, String> stringLayout = new HashMap<>();
            assert layoutValue != null;
            for (Map.Entry<String, Long> entry2 : layoutValue.entrySet()) {
                stringLayout.put(entry2.getKey(), String.valueOf(entry2.getValue()));
            }
            stringLayouts.put(String.valueOf((long) entry1.getKey()), stringLayout);
        }
        JSONObject jsonObject = new JSONObject(stringLayouts);
        String jsonString = jsonObject.toString();
        if(!jsonString.isEmpty()) {//&& (jsonString != null)
            FileManager.writeFile(mContext, mFileName, jsonString);
        }
    }

    /**
     * Loads the layout data from the JSON file to the Layouts Map,
     * for easy access and modification.
     */
    public void loadLayoutData(){
        try {
            String jsonString = FileManager.readFile(mContext, mFileName);
            mLayouts = new HashMap<>();
            JSONObject jsonObject = new JSONObject(jsonString);
            for(Iterator<String> remoteIds = jsonObject.keys(); remoteIds.hasNext();){
                String key = remoteIds.next();
                Long remoteId = Long.parseLong(key);
                HashMap<String, Long> layout = new HashMap<>();

                JSONObject layoutJson = jsonObject.getJSONObject(key);
                Iterator<String> resourceNames = layoutJson.keys();
                while(resourceNames.hasNext()){
                    String resourceName = resourceNames.next();
                    Long codeId = Long.parseLong(layoutJson.getString(resourceName));
                    layout.put(resourceName, codeId);
                }
                mLayouts.put(remoteId, layout);
            }
        } catch (@NonNull IOException | JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Error reading from file");
        }
    }

    /**
     * Gets the Id of the code associated with the give view
     * @param remote the selected remote to get the layout for
     * @param viewId the view to get the code for
     * @return the code in the specified remote that is associated with the given view
     */
    @Nullable
    public Long getCorrespondingCodeId(@NonNull RemoteEntity remote, int viewId){
        HashMap<String, Long> layout = getLayout(remote);
        String resourceName = mResources.getResourceName(viewId);
        if(layout.containsKey(resourceName)) {
            return layout.get(resourceName);
            //id is >=1 normally and can't be a string(except null due to json)
        }else{
            return null;
        }
    }

    /**
     * Sets the Code(codeId) associated with the given view(resource author_name_text_view)
     * @param remote the selected remote
     * @param resourceName the resource author_name_text_view of the view the code Id is associated with
     * @param code the code to associate with the view
     */
    public void setCorrespondingCode(@NonNull RemoteEntity remote,
                                     @NonNull String resourceName,
                                     @Nullable CodeEntity code){//if code is null deletes it
        Map<String, Long> layout = getLayout(remote);
        if(code!=null) {
            layout.put(resourceName, code.getId());
        }else{
            layout.remove(resourceName);
        }
        saveCodeLayouts();
    }

    /**
     * Gets the layout that is associated with the given remote
     * @param remote the remote to get the layout for
     * @return the Layout HashMap(buttonResourceName(String), codeId(long))
     */
    @NonNull
    public HashMap<String, Long> getLayout(@NonNull RemoteEntity remote){
        if(mLayouts.containsKey(remote.getId())){
            HashMap<String, Long> layout = mLayouts.get(remote.getId());
            if(layout!=null){
                return layout;
            }else{
                //this should never be called (if it is tries to prevent a crash)
                Log.e(TAG, "mLayouts has a null layout for remote:" + remote);
                //if the remote layout is null it creates a blank layout

                HashMap<String, Long> blankLayout = new HashMap<>();
                mLayouts.put(remote.getId(), blankLayout);
                return blankLayout;
            }
        }else{
            //this should never be called (if it is tries to prevent a crash)
            Log.e(TAG, "mLayouts doesn't have a layout for the remote:" + remote);
            //if there is no remote with that id
            HashMap<String, Long> blankLayout = new HashMap<>();
            mLayouts.put(remote.getId(), blankLayout);
            return blankLayout;
        }
    }

    /**
     * Inserts a new remote layout into the database
     * @param remoteId the id of the remote the layout is associated with
     * @param layout the layout Map(buttonResourceName(String), codeId(long))
     */
    public void createLayout(long remoteId, @NonNull HashMap<String, Long> layout){
        mLayouts.put(remoteId, layout);
    }

    /**
     * Deletes a remote layout from the database
     * @param remoteId the remote id the layout is associated with
     */
    public void deleteLayout(long remoteId){
        mLayouts.remove(remoteId);
    }
}