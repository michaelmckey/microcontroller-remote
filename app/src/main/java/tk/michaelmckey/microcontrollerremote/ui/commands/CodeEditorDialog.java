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

package tk.michaelmckey.microcontrollerremote.ui.commands;

import android.app.Dialog;
import android.os.Bundle;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import tk.michaelmckey.microcontrollerremote.R;
import tk.michaelmckey.microcontrollerremote.databinding.DialogCodeEditorBinding;
import tk.michaelmckey.microcontrollerremote.db.entity.CodeEntity;

/**
 * Creates a Alert Dialog for modifying or creating a {@link CodeEntity}
 * @author Michael McKey
 * @version 1.2.2
 */
public class CodeEditorDialog extends DialogFragment {
    @NonNull
    public static final String TAG = "CodeEditorDialog";
    @NonNull
    private final MutableLiveData<CodeEntity> mCode;

    /**
     * Initialises the necessary variables
     * @param code the {@link CodeEntity} to modify (or null to create a new code)
     */
    public CodeEditorDialog(@Nullable CodeEntity code){
        mCode = new MutableLiveData<>(code);
    }

    /**
     * Creates a Dialog to show to the User
     * @param savedInstanceState information to reconstruct the fragment(if being reconstructed)
     * @return the AlertDialog to show to the user
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        @NonNull DialogCodeEditorBinding binding =
                DialogCodeEditorBinding.inflate(getLayoutInflater());

        builder.setView(binding.getRoot());

        //sets up the layout
        if (mCode.getValue() == null) {
            builder.setTitle(R.string.commands_fragment_add_new_code_dialog_title);
        } else {
            builder.setTitle(R.string.modify_code_dialog);

            binding.titleInput.setText(mCode.getValue().getTitle());
            binding.messageInput.setText(mCode.getValue().getMessage());
        }

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            //when the changes are approved updates the code or creates a new one
            //then updates the LiveData which triggers the observer
            String newTitle = binding.titleInput.getText().toString();
            String newMessage = binding.messageInput.getText().toString();

            if (mCode.getValue() == null) {
                // creating a new remote
                long time = Calendar.getInstance().getTime().getTime();
                CodeEntity code = new CodeEntity(newTitle, newMessage, time);
                mCode.setValue(code);
            } else {
                // modifying existing code
                mCode.getValue().setMessage(newMessage);
                mCode.getValue().setTitle(newTitle);
                //Time stays constant to preserve order
                mCode.setValue(mCode.getValue());
            }
            dialog.dismiss();
        });
        builder.setNeutralButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
        return builder.create();
    }

    /**
     * Gets a LiveData container for retrieving the modified Code
     * @return the up to date Code wrapped in LiveData
     */
    @NonNull
    public LiveData<CodeEntity> getCode(){
        return mCode;
    }
}
