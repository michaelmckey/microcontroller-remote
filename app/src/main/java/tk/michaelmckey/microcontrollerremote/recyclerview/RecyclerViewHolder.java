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

import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import tk.michaelmckey.microcontrollerremote.databinding.RecyclerViewItemBinding;

/**
 * Binds the data stored in a {@link RecyclerElement} to its associated view.
 * Also Handles click events.
 * @param <E> the type of the data to display implements RecyclerElement
 * @author Michael McKey
 * @version 1.2.2
 */
class RecyclerViewHolder<E extends RecyclerElement>
        extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
    @NonNull
    public static final String DATE_FORMAT = "d MMM   HH:mma";
    @NonNull
    private final RecyclerAdapter<E> mAdapter;
    @NonNull
    private final RecyclerViewItemBinding mBinding;
    private boolean mSelected = false;
    @Nullable
    private E mObject;

    /**
     * Initialises the ViewHolder
     * @param view the parent view of the RecyclerView element
     * @param adapter the RecyclerView adapter which manages this ViewHolder
     */
    public RecyclerViewHolder(@NonNull View view, @NonNull RecyclerAdapter<E> adapter) {
        super(view);
        mBinding = RecyclerViewItemBinding.bind(view);
        mAdapter = adapter;
        view.setOnLongClickListener(this);
        view.setOnClickListener(this);
    }

    /**
     * Binds the relevant data to this ViewHolder
     * @param object the RecyclerElement for this ViewHolder to display
     */
    public void bind(@NonNull E object) {
        mObject = object;
        //displays the data
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        mBinding.itemTitle.setText(object.getTitle());
        mBinding.itemDate.setText(dateFormat.format(object.getTime()));

        //sets if selected
        setSelected(mAdapter.getSelectionState(mObject));
    }

    /**
     * Toggles if this item is selected or not.
     * Updates the list of selected items in the Adapter.
     */
    private void setSelected(boolean selected) {
        if(mAdapter.getMultiSelect()) {
            mSelected = selected;
        }else{
            mSelected = false;
        }

        mBinding.getRoot().setSelected(mSelected);//causes the xml to change the colour of the view
        assert mObject != null;//if its selection changes it must be assigned an object
        mAdapter.setSelectionState(mObject, mSelected);
    }

    /**
     * {@inheritDoc}
     * If it isn't on multi select mode it will call the adapters normal click method
     * otherwise it will toggle the selection state
     */
    @Override
    public void onClick(@NonNull View v) {
        assert mObject != null;//if it is clicked it must be assigned an object
        if (!mAdapter.getMultiSelect()) {
            mAdapter.normalClick(mObject);
        }else{
            setSelected(!mSelected);
        }
    }

    /**
     * Activates the multi select action mode if possible.{@inheritDoc}
     */
    @Override
    public boolean onLongClick(@NonNull View v) {
        mAdapter.startSupportActionMode();
        setSelected(true);//will only be triggered if in support action mode
        return true;
    }

    /**
     * Gets the String representation of this object
     * @return a String representing this object
     */
    @NonNull
    @Override
    public String toString() {
        return String.valueOf(mBinding.itemTitle.getText());
    }
}
