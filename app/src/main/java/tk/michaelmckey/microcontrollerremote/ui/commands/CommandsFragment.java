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

import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import tk.michaelmckey.microcontrollerremote.R;
import tk.michaelmckey.microcontrollerremote.db.entity.CodeEntity;
import tk.michaelmckey.microcontrollerremote.recyclerview.RecyclerFragment;

/**
 * Manages all user interactions within the Commands screen.
 * @author Michael McKey
 * @version 1.2.2
 */
public class CommandsFragment extends RecyclerFragment<CodeEntity> {
    private boolean mCalledForResult;

    /**
     * Checks if the fragment has been called with a request
     * (e.g. redirected from another fragment to allow the user to select an item).
     * Then sets up the RecyclerView and the FloatingActionButton.
     * @param rootView the rootView of the fragment
     * @param savedInstanceState information to reconstruct the fragment(if being reconstructed)
     */
    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        mCalledForResult = CommandsFragmentArgs.fromBundle(requireArguments()).getCalledForResult();
        setMultiSelectAvailable(!mCalledForResult);

        super.onViewCreated(rootView, savedInstanceState);
        //creates an observer to propagate changes any time the data changes
        assert mMainActivityViewModel != null;
        mMainActivityViewModel.getAllCodes().observe(getViewLifecycleOwner(), super::updateData);
    }


    /**
     * If this fragment was called to select a CodeEntity,
     * it associates the selected code with the ButtonResourceName
     * @param code the code that was clicked
     */
    @Override
    public void normalClick(@NonNull CodeEntity code) {
        if (mCalledForResult) {
            //returns to the selected code to the currentRemotes fragment
            NavController navController = Navigation.findNavController(
                    requireActivity(),
                    R.id.nav_host_fragment);

            //makes sure that the nav controller hasn't already navigated away from the fragment
            // (due to another click at the same time - e.g. double clicking the view)
            if (Objects.requireNonNull(navController.getCurrentDestination()).getId()
                    == R.id.nav_commands) {
                CommandsFragmentDirections.ActionNavCommandsToNavCurrentRemote action =
                        CommandsFragmentDirections.actionNavCommandsToNavCurrentRemote();
                action.setCodeId(code.getId());
                navController.navigate(action);
            }

            SearchView searchView = requireActivity().findViewById(R.id.app_bar_search);
            searchView.clearFocus();
        }
    }

    /**
     * Creates a dialog pop up for editing the given CodeEntity.
     * Creates a new CodeEntity if null is passed.
     * @param oldCode the RemoteEntity to edit
     */
    @Override
    public void editElement(@Nullable CodeEntity oldCode) {
        CodeEditorDialog codeEditorDialog = new CodeEditorDialog(oldCode);
        codeEditorDialog.getCode().observe(codeEditorDialog, (code) -> {
            //observer called as soon as setup but this won't change anything
            assert mMainActivityViewModel != null;
            if(oldCode == null){
                if(code != null) {
                    mMainActivityViewModel.insert(code);
                }
            }else{
                mMainActivityViewModel.update(code);
            }
        });
        codeEditorDialog.show(getChildFragmentManager(), CodeEditorDialog.TAG);
    }

    /**
     * Deletes the given code
     * @param element the selected CodeEntity
     */
    @Override
    public void deleteElement(@NonNull CodeEntity element) {
        //can only be triggered if the adapter has loaded(and initialised mMainActivityViewModel)
        assert mMainActivityViewModel != null;
        mMainActivityViewModel.delete(element);
    }

    /**
     * Duplicates the given code
     * @param element the selected CodeEntity
     */
    @Override
    public void duplicateElement(@NonNull CodeEntity element) {
        //can only be triggered if the adapter has loaded(and initialised mMainActivityViewModel)
        assert mMainActivityViewModel != null;
        mMainActivityViewModel.duplicate(element);
    }
}