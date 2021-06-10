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

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import tk.michaelmckey.microcontrollerremote.R;
import tk.michaelmckey.microcontrollerremote.connection.ConnectionListener;
import tk.michaelmckey.microcontrollerremote.databinding.LayoutLargeBinding;
import tk.michaelmckey.microcontrollerremote.databinding.LayoutLightsBinding;
import tk.michaelmckey.microcontrollerremote.databinding.LayoutMediumBinding;
import tk.michaelmckey.microcontrollerremote.databinding.LayoutSmallBinding;
import tk.michaelmckey.microcontrollerremote.databinding.LayoutToyBinding;
import tk.michaelmckey.microcontrollerremote.db.Layout;
import tk.michaelmckey.microcontrollerremote.db.entity.CodeEntity;
import tk.michaelmckey.microcontrollerremote.db.entity.RemoteEntity;

/**
 * Manages all user interactions within the Current RemoteEntity screen.
 * @author Michael McKey
 * @version 1.2.2
 */
public class CurrentRemoteFragment extends Fragment implements ConnectionListener {
    @NonNull
    private final String TAG = "CurrentRemoteFragment";
    @Nullable
    private CurrentRemoteViewModel mCurrentRemoteViewModel;
    @Nullable
    private ViewGroup mRemoteLayoutContainer;
    @Nullable
    private Toast mConnectingToast;
    @Nullable
    private Toast mReceivedMessageToast;

    /**
     * Inflates the correct {@link Layout} for the selected remote.
     * @param inflater the layout inflater
     * @param container the parent view the UI should be attached to
     * @param savedInstanceState information to reconstruct the fragment(if being reconstructed)
     * @return the root View
     */
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mCurrentRemoteViewModel =
                new ViewModelProvider(requireActivity()).get(CurrentRemoteViewModel.class);

        long remoteId = CurrentRemoteFragmentArgs.fromBundle(requireArguments()).getRemoteId();
        if (remoteId != -1) {//-1 is the default
            try {
                RemoteEntity remote = mCurrentRemoteViewModel.getRemote(remoteId);
                mCurrentRemoteViewModel.setSelectedRemote(remote);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Log.e(TAG, "Invalid Remote passed to CurrentRemotesFragment");
            }
        }

        long codeId = CurrentRemoteFragmentArgs.fromBundle(requireArguments()).getCodeId();
        if (codeId != -1) {//-1 is the default
            String buttonResourceName = mCurrentRemoteViewModel.getButtonResourceName();
            if(buttonResourceName != null){
                try{
                    CodeEntity code = mCurrentRemoteViewModel.getCode(codeId);
                    mCurrentRemoteViewModel.setCorrespondingCode(buttonResourceName, code);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Invalid Code passed to CurrentRemotesFragment");
                }
            }
        }

        switch (Layout.valueOf(mCurrentRemoteViewModel.getSelectedRemote().getLayoutType())) {
            case SMALL:
                mRemoteLayoutContainer =
                        LayoutSmallBinding.inflate(getLayoutInflater()).getRoot();
                break;
            case MEDIUM:
                mRemoteLayoutContainer =
                        LayoutMediumBinding.inflate(getLayoutInflater()).getRoot();
                break;
            case LARGE:
                mRemoteLayoutContainer =
                        LayoutLargeBinding.inflate(getLayoutInflater()).getRoot();
                break;
            case TOY:
                mRemoteLayoutContainer =
                        LayoutToyBinding.inflate(getLayoutInflater()).getRoot();
                break;
            case LIGHTS:
                mRemoteLayoutContainer =
                        LayoutLightsBinding.inflate(getLayoutInflater()).getRoot();
                break;
        }

        setHasOptionsMenu(true);
        return mRemoteLayoutContainer;
    }

    /**
     * Connects to the external device.
     * Also sets up listeners for click events and the Floating Action Button
     * @param rootView the rootView of the layout
     * @param savedInstanceState information to reconstruct the fragment(if being reconstructed)
     */
    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        assert mCurrentRemoteViewModel != null;
        mConnectingToast = Toast.makeText(getContext(), R.string.connecting, Toast.LENGTH_LONG);
        mConnectingToast.show();
        mCurrentRemoteViewModel.connect();

        //Sets up all of the buttons in the layout

        assert mRemoteLayoutContainer != null;
        int childCount = mRemoteLayoutContainer.getChildCount();
        for (int index = 0; index < childCount; index++) {
            View button = mRemoteLayoutContainer.getChildAt(index);
            setupButton(button);
        }

        View fab = requireActivity().findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
    }

    /**
     * Handles new messages. Notifies the user of them and saves any codes received in them.
     * Messages containing Codes are in the form "received_message:[message]".
     */
    @Override
    public void onReceivedMessages(@NonNull Iterable<String> unreadMessages)  {
        //receives messages from all communication types
        for (String message: unreadMessages) {
            //takes each message and splits it into the corresponding key and value
            String[] messageParts = message.split(":");
            if(messageParts.length == 2) {
                String key = messageParts[0];//the author_name_text_view of the variable sent
                String value = messageParts[1];//the value of that variable
                if (key.equals(getString(R.string.received_message_key))) {
                    //value is the message

                    if(mReceivedMessageToast != null){
                        //gets rid of the last message displayed
                        mReceivedMessageToast.cancel();
                        mReceivedMessageToast = null;
                    }

                    //displays a new message
                    mReceivedMessageToast = Toast.makeText(requireContext(),
                            getString(R.string.received_command_prefix) + value,
                            Toast.LENGTH_SHORT);
                    mReceivedMessageToast.show();

                    try {
                        assert mCurrentRemoteViewModel != null;
                        if (mCurrentRemoteViewModel.getCodesWithMessage(value).isEmpty()) {
                            //if message not recorded in database, add code
                            CodeEntity code = new CodeEntity(value,
                                    value,
                                    Calendar.getInstance().getTime().getTime());
                            mCurrentRemoteViewModel.insert(code);
                        }
                    } catch (@NonNull ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Error calling function getCodesWithMessage");
                    }
                }
            }
        }
    }

    /**
     * Updates the background to show that the device is connected.
     */
    @Override
    public void onConnected() {
        if (!isDetached()) {
            if(mConnectingToast != null){
                mConnectingToast.cancel();
                mConnectingToast = null;
            }
            if(mRemoteLayoutContainer != null) {
                int drawableId = R.drawable.background_green;
                Drawable backgroundDrawable = ResourcesCompat.getDrawable(getResources(),
                        drawableId,
                        null);
                //prevents errors when triggered from background thread
                requireActivity().runOnUiThread(() ->
                        mRemoteLayoutContainer.setBackground(backgroundDrawable));

            }
        }
    }

    /**
     * Updates the background to show that the device is disconnected.
     */
    @Override
    public void onDisconnected(@Nullable String reason) {
        if (!isDetached()) {//for getResources()
            if(reason != null) {
                Toast.makeText(requireActivity(), reason, Toast.LENGTH_SHORT).show();
            }
            if(mRemoteLayoutContainer != null) {
                int drawableId = R.drawable.background_black;
                Drawable backgroundDrawable = ResourcesCompat.getDrawable(getResources(),
                        drawableId,
                        null);
                //prevents errors when triggered from background thread
                requireActivity().runOnUiThread(() ->
                        mRemoteLayoutContainer.setBackground(backgroundDrawable));
            }else{
                Log.e("onDisconnect","mRemoteLayoutContainer is null");
            }
        }
    }

    /**
     * Removes connection listener onPause
     */
    @Override
    public void onPause() {
        assert mCurrentRemoteViewModel != null;
        mCurrentRemoteViewModel.removeConnectionListener(this);

        if(mConnectingToast != null){
            mConnectingToast.cancel();
            mConnectingToast = null;
        }

        if(mReceivedMessageToast != null){
            mReceivedMessageToast.cancel();
            mReceivedMessageToast = null;
        }

        super.onPause();
    }


    /**
     * Adds connection listener onResume
     */
    @Override
    public void onResume() {
        assert mCurrentRemoteViewModel != null;
        mCurrentRemoteViewModel.addConnectionListener(this);
        super.onResume();
    }


    /**
     * Sets up the toolbar.
     * @param menu the options menu
     */
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        assert mCurrentRemoteViewModel != null;
        String prefix = getString(R.string.current_remote_fragment_prefix);
        String remoteTitle = mCurrentRemoteViewModel.getSelectedRemote().getTitle();
        toolbar.setTitle(prefix + remoteTitle);
    }

    /**
     * Sets up the button that has been passed
     * (can either be a {@link Button} or an {@link ImageButton}.
     * @param view the button to set up
     */
    private void setupButton(@NonNull View view) {
        assert mCurrentRemoteViewModel != null;
        if (view instanceof Button || view instanceof ImageButton) {
            CodeEntity code = mCurrentRemoteViewModel.getCorrespondingCode(view.getId());

            boolean assignedCode = (code != null);

            //sets up click listeners
            view.setHapticFeedbackEnabled(true);
            view.setOnClickListener(this::remoteButtonClicked);
            view.setOnLongClickListener(v -> {
                modifyButton(v);
                return true;
            });

            if (view instanceof ImageButton) {
                //if the view is an image button,
                // it changes its opacity depending on if it's assigned a CodeEntity
                ImageButton button = (ImageButton) view;
                if (assignedCode) {
                    button.setImageAlpha(255);//sets button to opaque
                } else {
                    button.setImageAlpha(100);//sets button to transparent
                }
            } else {
                //if the view is a normal button,
                // it changes its typeface depending on if it's assigned a CodeEntity
                Button button = (Button) view;
                if (assignedCode) {
                    button.setTypeface(null, Typeface.BOLD);
                } else {
                    button.setTypeface(null, Typeface.ITALIC);
                }
            }
        }
    }

    /**
     * Sends the code associated with the given button to the external device.
     * Executed every time a button on the virtual remote is clicked.
     * @param button the button that has been clicked
     */
    private void remoteButtonClicked(@NonNull View button) {
        assert mCurrentRemoteViewModel != null;
        CodeEntity code = mCurrentRemoteViewModel.getCorrespondingCode(button.getId());
        if (code != null) {
            String message = code.getMessage();
            mCurrentRemoteViewModel.sendMessage(message);
            button.performHapticFeedback(
                    HapticFeedbackConstants.VIRTUAL_KEY,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
        } else {
            Toast.makeText(getContext(),
                    getString(R.string.button_not_assigned),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Creates a dialog popup which allows the user to assign a code to the button.
     * Called when a button on the remote is held down.
     */
    private void modifyButton(@NonNull View view) {
        assert mCurrentRemoteViewModel != null;
        view.performHapticFeedback(
                HapticFeedbackConstants.LONG_PRESS,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        );

        String resourceName = getResources().getResourceName(view.getId());
        CodeEntity code = mCurrentRemoteViewModel.getCorrespondingCode(view.getId());
        ButtonEditorDialog buttonEditorDialog = new ButtonEditorDialog(code);
        buttonEditorDialog.getChosenOption().observe(buttonEditorDialog, (chosenButton) -> {
            if(chosenButton == ChosenButton.POSITIVE) {
                mCurrentRemoteViewModel.setButtonResourceName(resourceName);

                //navigates to the commands fragment to choose a CodeEntity to assign to the button

                NavController navController =
                        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

                //makes sure that the nav controller hasn't already navigated away from the fragment
                // (due to another click at the same time - e.g. double clicking the view)
                if (Objects.requireNonNull(navController.getCurrentDestination()).getId()
                        == R.id.nav_current_remote) {
                    CurrentRemoteFragmentDirections.ActionNavCurrentRemoteToNavCommands action =
                            CurrentRemoteFragmentDirections.actionNavCurrentRemoteToNavCommands();
                    action.setCalledForResult(true);
                    navController.navigate(action);
                }

            }else if(chosenButton == ChosenButton.NEGATIVE){
                mCurrentRemoteViewModel.setCorrespondingCode(resourceName, null);
                //redraws the physical button which has its reference deleted
                setupButton(view);
            }
        });
        buttonEditorDialog.show(getChildFragmentManager(), ButtonEditorDialog.TAG);
    }
}