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

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import tk.michaelmckey.microcontrollerremote.R;
import tk.michaelmckey.microcontrollerremote.databinding.RecyclerViewItemsListBinding;
import tk.michaelmckey.microcontrollerremote.ui.main.MainActivityViewModel;

/**
 * Manages elements in UI to control the RecyclerView. Handles dialog pop ups and the action bar
 * @param <E> the type of object to display in the RecyclerView
 * @author Michael McKey
 * @version 1.2.2
 */
public abstract class RecyclerFragment<E extends RecyclerElement>
        extends Fragment implements RecyclerSelectionListener<E> {
    /**
     * Callbacks for user interactions with the ActionMode
     */
    @NonNull
    private final ActionMode.Callback mActionModeCallbacks = new ActionMode.Callback() {
        /**
         * {@inheritDoc}.
         * Sets up the Action Mode
         */
        @Override
        public boolean onCreateActionMode(@NonNull ActionMode mode, @NonNull Menu menu) {
            mActionModeMenu = menu;
            mActionMode = mode;
            mAdapter.setMultiSelect(true);

            // Inflate the menu for the CAB
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_recycler_view, menu);

            SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
            searchView.setQueryHint(getString(R.string.searchview_hint));
            searchView.clearFocus();
            return true;
        }

        /**
         * {@inheritDoc}
         * Prepares the ActionMode action bar
         */
        @Override
        public boolean onPrepareActionMode(@NonNull ActionMode mode, @NonNull Menu menu) {
            selectionChanged();
            return true;
        }

        /**
         * {@inheritDoc}
         * Preforms the selected action.
         */
        @Override
        public boolean onActionItemClicked(@NonNull ActionMode mode, @NonNull MenuItem item) {
            mode.setTitleOptionalHint(true);
            if (item.getItemId() == R.id.action_delete) {
                for (E element : mAdapter.getSelectedItems()) {
                    deleteElement(element);
                }
                mode.finish();
            } else if (item.getItemId() == R.id.action_edit) {
                for (E element : mAdapter.getSelectedItems()) {
                    editElement(element);
                }
                mode.finish();
            } else if (item.getItemId() == R.id.action_duplicate) {
                for (E element : mAdapter.getSelectedItems()) {
                    duplicateElement(element);
                }
                mode.finish();
            }
            return true;

        }

        /**
         * {@inheritDoc}
         * Disables multi select mode
         */
        @Override
        public void onDestroyActionMode(@NonNull ActionMode mode) {
            mAdapter.setMultiSelect(false);
        }
    };
    @NonNull
    private final RecyclerAdapter<E> mAdapter = new RecyclerAdapter<>(mActionModeCallbacks, this);
    @Nullable
    protected MainActivityViewModel mMainActivityViewModel;
    @Nullable
    private RecyclerViewModel mRecyclerViewModel;
    @Nullable
    protected RecyclerView mRecyclerView;
    @Nullable
    private Menu mActionModeMenu;
    @Nullable
    private ActionMode mActionMode;
    private boolean mIsRecyclerViewLoaded;
    private boolean mMultiSelectAvailable = true;

    /**
     * Creates a dialog pop up for editing the given RecyclerView element.
     * @param element the selected RecyclerView element. If null creates a new element
     */
    protected abstract void editElement(@Nullable E element);

    /**
     * Deletes the element.
     * @param element the selected RecyclerView element
     */
    protected abstract void deleteElement(@NonNull E element);

    /**
     * Duplicates the element.
     * @param element the selected RecyclerView element
     */
    protected abstract void duplicateElement(@NonNull E element);

    /**
     * Creates a dialog pop up that allows the user to choose how to sort the list
     */
    private void sortBy() {
        assert mRecyclerViewModel != null;
        SortBy oldSortBy = mRecyclerViewModel.getSortBy().getValue();
        Order oldOrder = mRecyclerViewModel.getOrder().getValue();
        assert (oldSortBy != null) && (oldOrder != null);

        SortByDialog sortByDialog = new SortByDialog(oldSortBy, oldOrder);
        sortByDialog.getOrder().observe(sortByDialog, (order) ->
                mRecyclerViewModel.setOrder(order));
        sortByDialog.getSortBy().observe(sortByDialog, (sortBy) ->
                mRecyclerViewModel.setSortBy(sortBy));
        sortByDialog.show(getChildFragmentManager(), SortByDialog.TAG);
    }

    /**
     * Sets if the RecyclerView can ever enter the multi select mode
     * @param available true if it is possible to enter multi select mode
     */
    public void setMultiSelectAvailable(boolean available){
        mMultiSelectAvailable = available;
    }

    /**
     * Updates/initialises the data set of the RecyclerAdapter.
     * If the RecyclerView has just loaded it also sets the position to the last position recorded.
     * @param data the new data to display
     */
    public void updateData(@NonNull List<E> data){
        mAdapter.updateData(data);
        //if the RecyclerView hasn't been loaded yet but the data has loaded
        if (!mIsRecyclerViewLoaded && !data.isEmpty()) {
            //scrolls to the last recorded position
            assert mRecyclerView != null;
            assert mRecyclerViewModel != null;
            mAdapter.scrollTo(mRecyclerViewModel.getRecyclerViewPosition(this));
            mIsRecyclerViewLoaded = true;
        }
    }

    /**
     * Reconstructs the Action Bar to suit the number of items selected.
     * Called when the number of items selected changes(when in multi select mode).
     */
    @Override
    public void selectionChanged() {
        if (mAdapter.getMultiSelect()) {
            assert mActionMode != null;
            assert mActionModeMenu != null;
            int itemsSelected = mAdapter.getSelectedItems().size();
            if (itemsSelected == 0) {
                mActionMode.setTitle(getString(R.string.select_items));
            } else {
                mActionMode.setTitle(String.valueOf(itemsSelected));
            }
            mActionModeMenu.findItem(R.id.app_bar_search).setVisible(false);
            mActionModeMenu.findItem(R.id.action_sort_by).setVisible(false);
            mActionModeMenu.findItem(R.id.action_select).setVisible(false);
            mActionModeMenu.findItem(R.id.action_edit).setVisible(false);
            mActionModeMenu.findItem(R.id.action_duplicate).setVisible(false);
            mActionModeMenu.findItem(R.id.action_delete).setVisible(false);

            if (itemsSelected == 1) {//1 selected enable options for 1 item
                mActionModeMenu.findItem(R.id.action_edit).setVisible(true);
                mActionModeMenu.findItem(R.id.action_duplicate).setVisible(true);
                mActionModeMenu.findItem(R.id.action_delete).setVisible(true);
            } else if (itemsSelected > 1) {//more than 1 selected enable options for multiple items
                mActionModeMenu.findItem(R.id.action_duplicate).setVisible(true);
                mActionModeMenu.findItem(R.id.action_delete).setVisible(true);
            }
        }
    }

    /**
     * Creates the user interface view and enables the option menu
     * @param inflater Layout inflater to initialise layout
     * @param container the view to be the parent of the inflated hierarchy
     * @param savedInstanceState information to reconstruct the fragment(if being reconstructed)
     * @return the root view
     */
    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        tk.michaelmckey.microcontrollerremote.databinding.RecyclerViewItemsListBinding binding =
                RecyclerViewItemsListBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    /**
     * Checks if the fragment has been called with a request
     * (e.g. redirected from another fragment to allow the user to select an item).
     * Then sets up the RecyclerView
     * @param rootView the rootView of the fragment
     * @param savedInstanceState information to reconstruct the fragment(if being reconstructed)
     */
    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        mMainActivityViewModel  =
                new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        mRecyclerViewModel =
                new ViewModelProvider(requireActivity()).get(RecyclerViewModel.class);

        mIsRecyclerViewLoaded = false;

        //checks if the rootView is a RecyclerView(should always be true)
        if (rootView instanceof RecyclerView) {
            //initialises the RecyclerView
            Context context = rootView.getContext();
            mRecyclerView = (RecyclerView) rootView;
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.addItemDecoration(
                    new DividerItemDecoration(context,LinearLayoutManager.VERTICAL));

            //Sets up the Adapter
            mAdapter.setMultiSelectAvailable(mMultiSelectAvailable);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setClickable(true);

            //notifies the adapter of any changes to the ViewModel
            mRecyclerViewModel.getSortBy().observe(getViewLifecycleOwner(), mAdapter::setSortBy);
            mRecyclerViewModel.getOrder().observe(getViewLifecycleOwner(), mAdapter::setOrder);
        }
    }

    /**
     * Sets up the FloatingActionButton(FAB).
     * Can't do it in onViewCreated as the MainActivity Layout isn't initialised then.
     */
    @Override
    public void onStart() {
        super.onStart();

        //Sets up the FAB
        //
        View fab = requireActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(v -> editElement(null));
    }

    /**
     * Inflates the options menu and manages searches in the app bar.
     * @param menu The options menu in which the action icons are placed
     * @param inflater The menu inflater
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_recycler_view, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        SearchManager searchManager =
                (SearchManager) requireContext().getSystemService(Context.SEARCH_SERVICE);

        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(requireActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(@NonNull String query) {
                // filter recycler view when query submitted
                mAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(@NonNull String newQuery) {
                // filter recycler view when text is changed
                mAdapter.filter(newQuery);
                return false;
            }
        });
    }

    /**
     * Selects which items are visible in the options menu.
     * @param menu the options menu
     */
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (!mMultiSelectAvailable) {
            Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
            toolbar.setTitle(R.string.called_for_result_title);
            menu.findItem(R.id.action_select).setVisible(false);
        } else {
            menu.findItem(R.id.action_select).setVisible(true);
        }
        menu.findItem(R.id.app_bar_search).setVisible(true);
        menu.findItem(R.id.action_sort_by).setVisible(true);
        menu.findItem(R.id.action_edit).setVisible(false);
        menu.findItem(R.id.action_duplicate).setVisible(false);
        menu.findItem(R.id.action_delete).setVisible(false);
    }

    /**
     * Handles click events for menu items
     * @param item the item selected
     * @return Return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_select) {
            //launches action mode
            ((AppCompatActivity) requireContext()).startSupportActionMode(mActionModeCallbacks);
            return true;
        } else if (item.getItemId() == R.id.action_sort_by) {
            sortBy();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Saves the last position of the RecyclerView before the app is paused.
     */
    @Override
    public void onPause() {
        //onPause() comes after onAttach() and onViewCreated() so neither variable is null
        assert mRecyclerView != null;
        assert mRecyclerViewModel != null;
        LinearLayoutManager layoutManager =
                (LinearLayoutManager) Objects.requireNonNull(mRecyclerView.getLayoutManager());
        int position = layoutManager.findFirstCompletelyVisibleItemPosition();

        mRecyclerViewModel.setRecyclerViewPosition(this, position);

        super.onPause();
    }
}