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

package tk.michaelmckey.microcontrollerremote.db.dao;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import tk.michaelmckey.microcontrollerremote.db.entity.RemoteEntity;

/**
 * Defines queries for accessing the RemoteEntity table
 * @author Michael McKey
 * @version 1.2.2
 */
@Dao
public interface RemoteDao {
    /**
    * Inserts a new remote into the table
    */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(@Nullable RemoteEntity remote);

    /**
     * Updates the given remote's message and title
     */
    @Update
    void update(@Nullable RemoteEntity remote);

    /**
     * Deletes the given remote
     */
    @Delete
    void delete(@Nullable RemoteEntity remote);

    /**
     * Returns all of the codes ordered by the time of creation (LiveData version)
     * @return the list of codes wrapped by a LiveData object
     */
    @Nullable
    @Query("SELECT * from remote_table ORDER BY time ASC")
    LiveData<List<RemoteEntity>> getChronologicalRemotes();

    /**
     * Returns a list containing the remote with the given creation time
     * @return a list of the remotes created at this exact time(typically only 1)
     */
    @Nullable
    @Query("SELECT * from remote_table WHERE time=:time")
    List<RemoteEntity> getRemotesWithTime(long time);

    /**
     * Returns the Remote with the given id
     */
    @Nullable
    @Query("SELECT * from remote_table WHERE id=:queryId")
    RemoteEntity getRemote(long queryId);
}
