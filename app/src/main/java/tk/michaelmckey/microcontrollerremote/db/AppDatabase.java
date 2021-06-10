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

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import tk.michaelmckey.microcontrollerremote.db.dao.CodeDao;
import tk.michaelmckey.microcontrollerremote.db.dao.RemoteDao;
import tk.michaelmckey.microcontrollerremote.db.entity.CodeEntity;
import tk.michaelmckey.microcontrollerremote.db.entity.RemoteEntity;

/**
 * Creates an instance of the Room database
 * @author Michael Mckey
 * @version 1.2.2
 */
@Database(entities = {CodeEntity.class, RemoteEntity.class}, version=1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    @Nullable
    private static volatile AppDatabase sInstance;
    private static final int NUMBER_OF_THREADS = 4;
    @NonNull
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    /**
     * Returns an instance of this class. Creates a new instance if no instance has been retrieved
     * @param context the context for the database(typically the application context)
     * @return an instance of the class
     */
    @NonNull
    public static AppDatabase getDatabase(@NonNull Context context) {
        synchronized (AppDatabase.class) {
            if (sInstance == null) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, "microcontroller_remote_database")
                        .build();
            }
            return Objects.requireNonNull(sInstance);
        }
    }

    /**
     * Gets the Code Data Access Object
     * @return the CodeDao
     */
    @Nullable
    public abstract CodeDao codeDao();

    /**
     * Gets the Remote Data Access Object
     * @return the RemoteDao
     */
    @Nullable
    public abstract RemoteDao remoteDao();
}