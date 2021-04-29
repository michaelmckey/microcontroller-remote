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
import tk.michaelmckey.microcontrollerremote.db.entity.CodeEntity;

/**
 * Defines queries for accessing the CodeEntity table
 * @author Michael McKey
 * @version 1.0.0
 */
@Dao
public interface CodeDao {
    /**
     * Inserts a new code into the table
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(@Nullable CodeEntity code);

    /**
     * Updates the given code's message and title
     */
    @Update
    void update(@Nullable CodeEntity code);

    /**
     * Deletes the given code
     */
    @Delete
    void delete(@Nullable CodeEntity code);

    /**
     * Returns the codes which have the given message
     */
    @Nullable
    @Query("SELECT * FROM code_table WHERE message=:message")
    List<CodeEntity> getEntriesWithMessage(@Nullable String message);

    /**
     * Returns the code with the given id
     */
    @Nullable
    @Query("SELECT * from code_table WHERE id=:queryId")
    CodeEntity getCode(long queryId);

    /**
     * Returns all of the codes ordered by time of creation.
     */
    @Nullable
    @Query("SELECT * from code_table ORDER BY time ASC")
    LiveData<List<CodeEntity>> getChronologicalCodes();
}
