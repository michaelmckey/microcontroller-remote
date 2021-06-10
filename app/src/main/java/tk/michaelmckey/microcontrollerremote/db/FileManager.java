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
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import androidx.annotation.NonNull;

/**
 * Manages accessing files
 * @author Michael Mckey
 * @version 1.2.2
 */
class FileManager {
    @NonNull
    public static final String FILE_SEPARATOR =
            Objects.requireNonNull(System.getProperty("file.separator"));

    /**
     * Retrieves the JSON data from a specified file
     * @param context Context for accessing the file
     * @param fileName the author_name_text_view of the file to read from
     * @return the JSON string the file contains
     * @throws IOException exceptions thrown when accessing the file
     */
    @NonNull
    public static String readFile(@NonNull Context context, @NonNull String fileName)
            throws IOException {
        FileInputStream fileInputStream = context.openFileInput(fileName);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            return stringBuilder.toString();
        }
    }

    /**
     * Writes a JSON string to a specified fileF
     * @param context Context for accessing the file
     * @param fileName the author_name_text_view of the file to write to
     * @param jsonString the JSON string to write to the file
     */
    public static void writeFile(@NonNull Context context,
                                 @NonNull String fileName,
                                 @NonNull String jsonString){
        try {
            FileOutputStream fileOutputStream =
                    context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fileOutputStream.write(jsonString.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("FileManager", "Error writing to file");
        }
    }

    /**
     * Checks if a file exists with the given author_name_text_view
     * @param context Context for getting the path of the directory holding application files.
     * @param fileName the author_name_text_view of the file to check
     * @return true if the file exists
     */
    public static boolean isFilePresent(@NonNull Context context, @NonNull String fileName) {
        String path = context.getFilesDir().getAbsolutePath() + FILE_SEPARATOR + fileName;
        File file = new File(path);
        return file.exists();
    }
}