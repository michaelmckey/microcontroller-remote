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

import android.app.Dialog;
import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import tk.michaelmckey.microcontrollerremote.R;
import tk.michaelmckey.microcontrollerremote.databinding.DialogButtonEditorBinding;
import tk.michaelmckey.microcontrollerremote.db.entity.CodeEntity;

/**
 * Creates a Alert Dialog for changing which button is associated with which {@link CodeEntity}
 * @author Michael McKey
 * @version 1.2.2
 */
public class ButtonEditorDialog extends DialogFragment {
    @NonNull
    public static final String TAG = "ButtonEditorDialog";
    @NonNull
    private final MutableLiveData<ChosenButton> mChosenButton;//make the string an Enum
    @Nullable
    private final CodeEntity mCode;

    /**
     * Initialises the necessary variables
     * @param code the old {@link CodeEntity} associated with the button
     */
    public ButtonEditorDialog(@Nullable CodeEntity code){
        mChosenButton = new MutableLiveData<>(null);
        mCode = code;
    }

    /**
     * Creates a Dialog to show to the user
     * @param savedInstanceState information to reconstruct the fragment(if being reconstructed)
     * @return the AlertDialog to show to the user
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.button_info);
        @NonNull DialogButtonEditorBinding binding =
                DialogButtonEditorBinding.inflate(getLayoutInflater());

        boolean assignedCode = (mCode != null);
        if (!assignedCode) {
            binding.titleValue.setText(R.string.deleted);
            binding.messageValue.setText(R.string.deleted);
            binding.dateValue.setText(R.string.deleted);
        } else {
            SimpleDateFormat dateFormat =
                    new SimpleDateFormat(getString(R.string.date_format), Locale.getDefault());
            String readableTime = dateFormat.format(mCode.getTime());

            binding.titleValue.setText(mCode.getTitle());
            binding.messageValue.setText(mCode.getMessage());
            binding.dateValue.setText(readableTime);
        }

        builder.setView(binding.getRoot());

        builder.setPositiveButton(R.string.change_associated_code_text, ((dialog, which) -> {
            mChosenButton.setValue(ChosenButton.POSITIVE);
            dialog.dismiss();
        }));

        builder.setNegativeButton(R.string.delete, (dialog, which) -> {
            mChosenButton.setValue(ChosenButton.NEGATIVE);
            dialog.dismiss();
        });

        builder.setNeutralButton(android.R.string.cancel, (dialog, which) -> {
            mChosenButton.setValue(ChosenButton.NEUTRAL);
            dialog.dismiss();
        });

        return builder.create();
    }

    /**
     * Gets a LiveData container for retrieving which button on the Dialog was selected
     * @return the {@link ChosenButton} wrapped in LiveData
     * @see ChosenButton
     */
    @NonNull
    public LiveData<ChosenButton> getChosenOption(){
        return mChosenButton;
    }
}
