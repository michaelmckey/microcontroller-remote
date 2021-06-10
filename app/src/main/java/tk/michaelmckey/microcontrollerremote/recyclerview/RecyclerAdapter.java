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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.RecyclerView;
import tk.michaelmckey.microcontrollerremote.databinding.RecyclerViewItemBinding;

/**
 * Provides binding of RecyclerElement objects to RecyclerViewHolders.
 * Also manages sorting and filtering of data set.
 * @param <E> the type of the object to display in the RecyclerView
 * @author Michael McKey
 * @version 1.2.2
 */
class RecyclerAdapter<E extends RecyclerElement>
        extends RecyclerView.Adapter<RecyclerViewHolder<E>> {
    @Nullable
    private RecyclerView mRecyclerView;
    @NonNull
    public final List<E> mSelectedItems = new ArrayList<>();
    @NonNull
    private final ActionMode.Callback mActionModeCallbacks;
    @NonNull
    private final RecyclerSelectionListener<E> mRecyclerSelectionListener;

    @NonNull
    private String mQuery = "";
    @NonNull
    private SortBy mSortBy = SortBy.TIME;//Overwritten after created
    @NonNull
    private Order mOrder = Order.ASCENDING;//Overwritten after created

    @SuppressWarnings("CanBeFinal")
    @NonNull
    private List<E> mData = new ArrayList<>();
    @NonNull
    private List<E> mDataFiltered = new ArrayList<>();

    private boolean mMultiSelectAvailable = true;
    private boolean mMultiSelect;

    /**
     * Initialises the adapter
     * @param actionModeCallbacks the callbacks for events in the action mode
     * @param recyclerSelectionListener notified when the state of a ViewHolder has been changed
     */
    public RecyclerAdapter(@NonNull ActionMode.Callback actionModeCallbacks,
                           @NonNull RecyclerSelectionListener<E> recyclerSelectionListener){
        mActionModeCallbacks = actionModeCallbacks;
        mRecyclerSelectionListener = recyclerSelectionListener;
    }

    /**
     * Changes how the RecyclerView is sorted
     * @param sortBy the characteristic to sort the elements by
     */
    public void setSortBy(@NonNull SortBy sortBy){
        mSortBy = sortBy;
        filter();
    }

    /**
     * Changes how the RecyclerView is sorted
     * @param order the order that the elements should be displayed(ascending or descending order)
     */
    public void setOrder(@NonNull Order order){
        mOrder = order;
        filter();
    }

    /**
     * Compares two elements to find out how they should be sorted.
     * Returns pSorts the list using the value of {@link #mSortBy} and {@link #mOrder}
     * @param object1 an element to compare to the second
     * @param object2 an element to compare to the first
     * @return <p>less than zero - if the first object should appear before the second,</p>
     * <p>zero - if the objects determining characteristics match</p>
     * <p>greater than zero - if the first object should appear after the second</p>
     */
    private int compareElements(@NonNull E object1, @NonNull E object2) {
        int difference;
        Collator collator = Collator.getInstance();
        switch (mSortBy) {
            case TITLE:
                //finds out which title is first in alphabetical order(and by how much)
                String title1 = object1.getTitle().toLowerCase(Locale.ENGLISH);
                String title2 = object2.getTitle().toLowerCase(Locale.ENGLISH);
                difference = collator.compare(title1, title2);
                break;
            case CONTENTS:
                //finds out which contents is first in alphabetical order(and by how much)
                String message1 = object1.getContents().toLowerCase(Locale.ENGLISH);
                String message2 = object2.getContents().toLowerCase(Locale.ENGLISH);
                difference = collator.compare(message1, message2);
                break;
            case TIME:
                //compares the times of creation to find out which is first chronologically
                Date date1 = new Date(object1.getTime());
                Date date2 = new Date(object2.getTime());
                difference = date1.compareTo(date2);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + mSortBy);
        }

        switch(mOrder){
            case ASCENDING:
                //the order remains the same
                return difference;
            case DESCENDING:
                //flips the order
                return difference * -1;
            default:
                throw new IllegalStateException("Unexpected value: " + mSortBy);
        }
    }

    /**
     * Filters and sorts the RecyclerView(using the query, the sort by and the order).
     * Uses the {@link #compareElements} function
     */
    private void filter(){
        List<E> filteredList = new ArrayList<>();
        //filters the list
        if (mQuery.isEmpty()) {
            //no query so all items are valid
            filteredList.addAll(mData);
        } else {
            //adds any item that contains the query
            for (E object : mData) {
                if (object.getTitle().toLowerCase(Locale.ENGLISH)
                        .contains(mQuery.toLowerCase(Locale.ENGLISH))) {
                    filteredList.add(object);
                }
            }
        }

        //sorts the list(using the compareElements function
        Collections.sort(filteredList, this::compareElements);
        mDataFiltered = filteredList;
        notifyDataSetChanged();
    }

    /**
     * Filters and sorts the RecyclerView(using the query, the sort by and the order).
     * Uses the {@link #compareElements} function
     * @param query the new query to filter the list by
     */
    public void filter(@NonNull String query){
        mQuery = query;
        filter();
    }

    /**
     * Gets a reference to the RecyclerView this adapter manages
     * @param recyclerView the attached RecyclerView
     */
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * Removes the reference to the RecyclerView. {@inheritDoc}
     */
    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        mRecyclerView = null;
        super.onDetachedFromRecyclerView(recyclerView);
    }

    /**
     * Creates a new RecyclerViewHolder. {@inheritDoc}
     */
    @NonNull
    @Override
    public RecyclerViewHolder<E> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //creates a new view holder to hold the view associated with an element in the list
        @NonNull RecyclerViewItemBinding binding =
                RecyclerViewItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent,
                        false);
        return new RecyclerViewHolder<>(binding.getRoot(), this);
    }

    /**
     * Binds data to a RecyclerViewHolder.{@inheritDoc}
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder<E> holder, int position) {
        //binds the data to the view it is now associated with
        holder.bind(mDataFiltered.get(position));
    }

    /**
     * Returns the number of objects that match the search criteria in the RecyclerView
     */
    @Override
    public int getItemCount() {
        return mDataFiltered.size();
    }

    /**
     * Updates the data displayed in the ViewHolders
     * @param data the new data
     */
    public void updateData(@NonNull List<E> data){
        //used to refresh the list when data is added or removed
        mData = Collections.unmodifiableList(data);
        filter();
    }

    /**
     * Scrolls the recyclerview to a given position
     * @param position the index of the element to scroll to
     *                 (between 0 and {@link #getItemCount()}-1
     */
    public void scrollTo(int position){
        //checks if position is in the valid range
        if (position >= 0 && position <= getItemCount() - 1) {
            assert mRecyclerView != null;
            RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
            assert layoutManager != null;
            layoutManager.scrollToPosition(position);
        }else{
            Log.d("didn't scroll", "position out of range");
            Log.d("didn't scroll",
                    "received position:" + position
                    + ", item_count:" + getItemCount());
        }
    }

    /**
     * Returns the items that have been selected
     * @return all selected items
     */
    @NonNull
    public List<E> getSelectedItems() {
        return Collections.unmodifiableList(mSelectedItems);
    }

    /**
     * Sets if multi-select is available.
     * If it is available the user can enter the ActionMode and select multiple items
     * @param available true if multi-select is available
     */
    public void setMultiSelectAvailable(boolean available){
        mMultiSelectAvailable = available;
    }

    /**
     * Gets if the adapter is is multi select mode
     * @return true if currently multi select mode
     */
    public boolean getMultiSelect(){
        return mMultiSelect;
    }

    /**
     * Sets if the given object is currently selected
     * @param object An object in the data set
     * @param selected the new selection state(true if selected)
     */
    public void setSelectionState(@NonNull E object, boolean selected){
        if(mData.contains(object)){
            if(selected){
                mSelectedItems.add(object);
            }else{
                mSelectedItems.remove(object);
            }
        }
        mRecyclerSelectionListener.selectionChanged();
    }

    /**
     * Gets if the given object is currently selected
     * @param object An object in the data set
     * @return if the object is selected
     */
    public boolean getSelectionState(@NonNull E object){
        return mSelectedItems.contains(object);
    }

    /**
     * Called when a ViewHolder is clicked.
     * Propagates call to the {@link RecyclerSelectionListener}
     * @param object the object which was clicked
     */
    public void normalClick(@NonNull E object){
        mRecyclerSelectionListener.normalClick(object);
    }

    /**
     * Starts the support action mode if possible
     */
    public void startSupportActionMode(){
        if(mMultiSelectAvailable) {
            if (!mMultiSelect) {
                if(mRecyclerView != null) {
                    ((AppCompatActivity) mRecyclerView.getContext())
                            .startSupportActionMode(mActionModeCallbacks);
                }else{
                    Log.e("RecyclerAdapter",
                            "Can't trigger action mode. Adapter detached from RecyclerView");
                }
            }
        }
    }

    /**
     * Toggles if the adapter is currently in multi select action mode
     * @param state true if it should be in multi select mode
     */
    public void setMultiSelect(boolean state){
        //ensures the multi select action mode is available
        if(mMultiSelectAvailable) {
            mMultiSelect = state;
            if (!state) {
                //if not in selection mode clear selection
                mSelectedItems.clear();
                notifyDataSetChanged();
            }
        }else{
            mMultiSelect = false;
        }
    }
}