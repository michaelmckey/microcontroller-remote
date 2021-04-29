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

package tk.michaelmckey.microcontrollerremote.recyclerview;

import android.app.Dialog;
import android.os.Bundle;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import tk.michaelmckey.microcontrollerremote.R;
import tk.michaelmckey.microcontrollerremote.databinding.DialogSortByBinding;

/**
 * Creates a Alert Dialog for sorting a RecyclerView.
 * @see SortBy
 * @see Order
 * @author Michael McKey
 * @version 1.0.0
 */
public class SortByDialog extends DialogFragment {
    @NonNull
    public static final String TAG = "SortByDialog";
    @NonNull
    private final MutableLiveData<SortBy> mSortBy;
    @NonNull
    private final MutableLiveData<Order> mOrder;

    /**
     * Gets the initial {@link SortBy} and {@link Order} to sort the RecyclerView by.
     * @param sortBy the old property to sort the list by
     * @param order the old order so sort the list by
     */
    public SortByDialog(@NonNull SortBy sortBy, @NonNull Order order){
        mSortBy = new MutableLiveData<>(sortBy);
        mOrder = new MutableLiveData<>(order);
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
        @NonNull DialogSortByBinding binding =
                DialogSortByBinding.inflate(getLayoutInflater());

        //Sets default values
        switch (Objects.requireNonNull(mSortBy.getValue())) {
            case TITLE:
                binding.sortByRadioGroup.check(R.id.title_radio_button);
                break;
            case CONTENTS:
                binding.sortByRadioGroup.check(R.id.contents_radio_button);
                break;
            case TIME:
                binding.sortByRadioGroup.check(R.id.time_radio_button);
                break;
        }

        switch (Objects.requireNonNull(mOrder.getValue())) {
            case ASCENDING:
                binding.orderRadioGroup.check(R.id.ascending_radio_button);
                break;
            case DESCENDING:
                binding.orderRadioGroup.check(R.id.descending_radio_button);
                break;
        }

        builder.setView(binding.getRoot());

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            int sortBySelectedId = binding.sortByRadioGroup.getCheckedRadioButtonId();
            int orderSelectedId = binding.orderRadioGroup.getCheckedRadioButtonId();

            if (sortBySelectedId == R.id.title_radio_button) {
                mSortBy.setValue(SortBy.TITLE);
            }else if (sortBySelectedId == R.id.contents_radio_button) {
                mSortBy.setValue(SortBy.CONTENTS);
            } else if (sortBySelectedId == R.id.time_radio_button) {
                mSortBy.setValue(SortBy.TIME);
            } else {
                throw new AssertionError("one radio button should have been selected");
            }

            if (orderSelectedId == R.id.ascending_radio_button) {
                mOrder.setValue(Order.ASCENDING);
            } else if (orderSelectedId == R.id.descending_radio_button) {
                mOrder.setValue(Order.DESCENDING);
            } else {
                throw new AssertionError("one radio button should have been selected");
            }
            dialog.dismiss();
        });
        builder.setNeutralButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
        return builder.create();
    }

    /**
     * Gets a LiveData container for retrieving the new property to sort by
     * @return the up to date {@link SortBy} wrapped in LiveData
     */
    @NonNull
    public LiveData<SortBy> getSortBy(){
        return mSortBy;
    }

    /**
     * Gets a LiveData container for retrieving the new order to sort by
     * @return the up to date {@link Order} wrapped in LiveData
     */
    @NonNull
    public LiveData<Order> getOrder(){
        return mOrder;
    }
}
