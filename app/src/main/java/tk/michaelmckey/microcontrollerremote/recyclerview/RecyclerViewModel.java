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

import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Handles the storing and synchronising of the RecyclerView's settings across all of the fragments
 * @author Michael McKey
 * @version 1.0.0
 */
public class RecyclerViewModel extends ViewModel {
    @NonNull
    private final HashMap<Integer, Integer> mRecyclerViewPosition = new HashMap<>();
    @NonNull
    private final MutableLiveData<SortBy> mSortBy = new MutableLiveData<>(SortBy.TIME);
    @NonNull
    private final MutableLiveData<Order> mOrder = new MutableLiveData<>(Order.ASCENDING);

    /**
     * Returns the {@link Order} to sort the list by
     * @return the order
     */
    @NonNull
    public LiveData<Order> getOrder() {
        return mOrder;
    }

    /**
     * Sets the {@link Order} to sort the list by
     * @param order the new {@link Order}
     */
    public void setOrder(@NonNull Order order) {
        mOrder.setValue(order);
    }

    /**
     * Returns what characteristic to sort the list by
     * @return what to {@link SortBy}
     */
    @NonNull
    public LiveData<SortBy> getSortBy() {
        return mSortBy;
    }

    /**
     * Sets what characteristic to sort the list by
     * @param sortBy the new characteristic to {@link SortBy}
     */
    public void setSortBy(@NonNull SortBy sortBy) {
        mSortBy.setValue(sortBy);
    }

    /**
     * Gets the last position of the RecyclerView (in the given fragment)
     * @param fragment the fragment to get the position for
     * @return the position
     */
    @NonNull
    public Integer getRecyclerViewPosition(@NonNull Fragment fragment){
        if (!mRecyclerViewPosition.containsKey(fragment.getId())) {
            setRecyclerViewPosition(fragment, 0);
        }
        return Objects.requireNonNull(mRecyclerViewPosition.get(fragment.getId()));
    }

    /**
     * Sets the last position of the RecyclerView (in the given fragment)
     * @param fragment the fragment to set the position for
     * @param position the position
     */
    public void setRecyclerViewPosition(@NonNull Fragment fragment, @NonNull Integer position) {
        mRecyclerViewPosition.put(fragment.getId(), position);
    }
}
