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

package tk.michaelmckey.microcontrollerremote.ui.remotes;

import android.os.Bundle;
import android.view.View;

import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import tk.michaelmckey.microcontrollerremote.R;
import tk.michaelmckey.microcontrollerremote.db.entity.RemoteEntity;
import tk.michaelmckey.microcontrollerremote.recyclerview.RecyclerFragment;

/**
 * Manages all user interactions within the Remotes screen.
 * @author Michael McKey
 * @version 1.2.2
 */
public class RemotesFragment extends RecyclerFragment<RemoteEntity> {
    /**
     * Adds a LiveData observer to notify the adapter of changes to the data
     * @param savedInstanceState information to reconstruct the fragment(if being reconstructed)
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assert mMainActivityViewModel != null;
        mMainActivityViewModel.getAllRemotes().observe(getViewLifecycleOwner(), super::updateData);
    }

    /**
     * Navigates to CurrentRemoteFragment and tells it to display the given remote
     * @param remote the selected remote to display
     */
    @Override
    public void normalClick(@NonNull RemoteEntity remote) {
        NavController navController =
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        //makes sure that the nav controller hasn't already navigated away from the fragment
        // (due to another click at the same time - e.g. double clicking the view)
        if (Objects.requireNonNull(navController.getCurrentDestination()).getId()
                == R.id.nav_remotes) {
            RemotesFragmentDirections.ActionNavRemotesToNavCurrentRemote action =
                    RemotesFragmentDirections.actionNavRemotesToNavCurrentRemote();
            action.setRemoteId(remote.getId());
            navController.navigate(action);
        }

    }

    /**
     * Creates a dialog pop up for editing the given RemoteEntity.
     * Creates a new RemoteEntity if null is passed.
     * @param oldRemote the RemoteEntity to edit (if null creates a new remote)
     */
    public void editElement(@Nullable RemoteEntity oldRemote) {
        RemoteEditorDialog remoteEditorDialog = new RemoteEditorDialog(oldRemote);
        remoteEditorDialog.getRemote().observe(remoteEditorDialog, (remote) -> {
            //observer called as soon as setup but this won't change anything
            assert mMainActivityViewModel != null;
            if(oldRemote == null){
                if(remote != null) {
                    mMainActivityViewModel.insert(remote, new HashMap<>());
                }
            }else{
                mMainActivityViewModel.update(remote);
            }
        });
        remoteEditorDialog.show(getChildFragmentManager(), RemoteEditorDialog.TAG);
    }

    /**
     * Deletes the given RemoteEntity
     * @param element the selected RemoteEntity
     */
    @Override
    public void deleteElement(@NonNull RemoteEntity element) {
        assert mMainActivityViewModel != null;
        mMainActivityViewModel.delete(element);
    }

    /**
     * Duplicates the given RemoteEntity
     * @param element the selected RemoteEntity
     */
    @Override
    public void duplicateElement(@NonNull RemoteEntity element) {
        assert mMainActivityViewModel != null;
        mMainActivityViewModel.duplicate(element);
    }
}