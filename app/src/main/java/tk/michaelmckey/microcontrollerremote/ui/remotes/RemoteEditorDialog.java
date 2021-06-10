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

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import tk.michaelmckey.microcontrollerremote.R;
import tk.michaelmckey.microcontrollerremote.connection.ConnectionManager;
import tk.michaelmckey.microcontrollerremote.connection.ConnectionMethod;
import tk.michaelmckey.microcontrollerremote.databinding.DialogRemoteEditorBinding;
import tk.michaelmckey.microcontrollerremote.db.Layout;
import tk.michaelmckey.microcontrollerremote.db.entity.RemoteEntity;

/**
 * Creates a Alert Dialog for modifying or creating a {@link RemoteEntity}
 * @author Michael McKey
 * @version 1.2.2
 */
public class RemoteEditorDialog extends DialogFragment {
    @NonNull
    public static final String TAG = "RemoteEditorDialog";
    @NonNull
    private final MutableLiveData<RemoteEntity> mRemote;

    @NonNull
    private String mConnectionInfoSerial = "";
    @NonNull
    private String mConnectionInfoWifi = "";
    @NonNull
    private String mConnectionInfoBluetooth = "";
    @Nullable
    private ConnectionMethod mLastSelectedMethod = null;

    /**
     * Initialises the necessary variables
     * @param remote the {@link RemoteEntity} to modify (or null to create a new code)
     */
    public RemoteEditorDialog(@Nullable RemoteEntity remote){
        mRemote = new MutableLiveData<>(remote);
    }

    /**
     * Creates a Dialog to show to the User
     * @param savedInstanceState information to reconstruct the fragment(if being reconstructed)
     * @return the AlertDialog to show to the user
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        ConnectionManager.refreshAvailableDevices();
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        @NonNull DialogRemoteEditorBinding binding =
                DialogRemoteEditorBinding.inflate(getLayoutInflater());

        //sets up the layout
        List<Layout> layoutTypes = Arrays.asList(Layout.values());
        ArrayAdapter<Layout> layoutTypesAa = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                layoutTypes);
        layoutTypesAa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.layoutTypeSpinner.setAdapter(layoutTypesAa);

        List<ConnectionMethod> connectionMethods = Arrays.asList(ConnectionMethod.values());
        ArrayAdapter<ConnectionMethod> connectionMethodsAa = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, connectionMethods);
        connectionMethodsAa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.connectionModeSpinner.setAdapter(connectionMethodsAa);

        //Assigns the default values to the fields in the Dialog
        if (mRemote.getValue() == null) {
            builder.setTitle(R.string.new_remote);
        } else {
            builder.setTitle(R.string.modify_remote);

            binding.titleInput.setText(mRemote.getValue().getTitle());
            binding.connectionInfoInput.setText(mRemote.getValue().getConnectionInfo());

            Layout selectedLayout = Layout.valueOf(mRemote.getValue().getLayoutType());
            ConnectionMethod connectionMethod =
                    ConnectionMethod.valueOf(mRemote.getValue().getConnectionMethod());

            switch (connectionMethod) {
                case SERIAL:
                    mConnectionInfoSerial = mRemote.getValue().getConnectionInfo();
                    break;
                case WIFI:
                    mConnectionInfoWifi = mRemote.getValue().getConnectionInfo();
                    break;
                case BLUETOOTH:
                    mConnectionInfoBluetooth = mRemote.getValue().getConnectionInfo();
                    break;
            }

            int spinnerPosition1 = layoutTypes.indexOf(selectedLayout);
            binding.layoutTypeSpinner.setSelection(spinnerPosition1);

            int spinnerPosition2 = connectionMethods.indexOf(connectionMethod);
            binding.connectionModeSpinner.setSelection(spinnerPosition2);
        }

        binding.connectionModeSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view,
                                               int position,
                                               long id) {
                        if (mLastSelectedMethod != null) {
                            //if the selected mode is being changed saves the text input for later
                            switch (mLastSelectedMethod) {
                                case SERIAL:
                                    mConnectionInfoSerial =
                                            binding.connectionInfoInput.getText().toString();
                                    break;
                                case WIFI:
                                    mConnectionInfoWifi =
                                            binding.connectionInfoInput.getText().toString();
                                    break;
                                case BLUETOOTH:
                                    mConnectionInfoBluetooth =
                                            binding.connectionInfoInput.getText().toString();
                                    break;
                            }
                        }

                        //modifies the layout settings to suit the newly selected connection mode
                        ConnectionMethod selectedMethod =
                                (ConnectionMethod) binding.connectionModeSpinner.getSelectedItem();
                        mLastSelectedMethod = selectedMethod;
                        switch (selectedMethod) {
                            case SERIAL:
                                //needs to be a number
                                binding.connectionInfoInput.setText(mConnectionInfoSerial);
                                binding.connectionInfoInput.setHint(R.string.baud_rate);
                                binding.connectionInfoInput.setInputType(
                                        EditorInfo.TYPE_CLASS_NUMBER);
                                binding.connectionInfoButton.setVisibility(View.GONE);
                                break;
                            case WIFI:
                                //needs to be a url
                                binding.connectionInfoInput.setText(mConnectionInfoWifi);
                                binding.connectionInfoInput.setHint(R.string.url);
                                binding.connectionInfoInput.setInputType(
                                        EditorInfo.TYPE_CLASS_TEXT);
                                binding.connectionInfoButton.setVisibility(View.GONE);
                                break;
                            case BLUETOOTH:
                                //needs to be a mac address selected from a drop down list
                                binding.connectionInfoInput.setText(mConnectionInfoBluetooth);
                                binding.connectionInfoInput.setHint(R.string.mac_address);
                                binding.connectionInfoInput.setInputType(
                                        EditorInfo.TYPE_CLASS_TEXT);
                                binding.connectionInfoButton.setVisibility(View.VISIBLE);
                                binding.connectionInfoButton.setOnClickListener(v -> {
                                            BluetoothDeviceDialog bluetoothDeviceDialog =
                                                    new BluetoothDeviceDialog();
                                            bluetoothDeviceDialog.getMacAddress()
                                                    .observe(bluetoothDeviceDialog,
                                                    binding.connectionInfoInput::setText);
                                            bluetoothDeviceDialog.show(
                                                    getChildFragmentManager(),
                                                    BluetoothDeviceDialog.TAG);
                                        });
                                break;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        builder.setView(binding.getRoot());

        //onClickListener set up later.
        //if it was set up here it would disappear even if input wasn't valid
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> { });

        builder.setNeutralButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        //sets up the onClickListener here to prevent the dialog from closing with invalid input
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialog1 -> {
            Button button = ((AlertDialog) dialog1).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(v -> {
                //retrieves the entered data
                String newTitle = binding.titleInput.getText().toString();
                Layout newLayoutType = (Layout) binding.layoutTypeSpinner.getSelectedItem();
                ConnectionMethod newConnectionMethod =
                        (ConnectionMethod) binding.connectionModeSpinner.getSelectedItem();
                String newConnectionInfo = binding.connectionInfoInput.getText().toString();

                //checks if the connection info is valid
                boolean validInfo = false;
                switch(newConnectionMethod){
                    case SERIAL:
                        if(newConnectionInfo.matches("^\\d+$")){
                            if(Integer.parseInt(newConnectionInfo) > 0){
                                validInfo = true;
                            }else{
                                Toast.makeText(getContext(),
                                        R.string.baud_rate_zero_error,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            if(newConnectionInfo.isEmpty()){
                                Toast.makeText(getContext(),
                                        R.string.baud_rate_blank_error,
                                        Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getContext(),
                                        R.string.baud_rate_not_int_error,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    case WIFI:
                        if(newConnectionInfo.matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]$")){
                            validInfo = true;
                        }else{
                            Toast.makeText(getContext(),
                                    R.string.invalid_url_error,
                                    Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case BLUETOOTH:
                        if(newConnectionInfo.matches("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$")){
                            validInfo = true;
                        }else{
                            Toast.makeText(getContext(),
                                    R.string.invalid_mac_address_error,
                                    Toast.LENGTH_SHORT).show();
                        }
                        break;
                }

                if(validInfo) {
                    //if the info is valid creates/modifies the remote and closes the dialog
                    //if it is invalid the dialog isn't closed and the click is ignored
                    if (mRemote.getValue() == null) {
                        // creating a new remote
                        long time = Calendar.getInstance().getTime().getTime();
                        RemoteEntity newRemote = new RemoteEntity(newTitle,
                                newLayoutType.name(),
                                newConnectionMethod.name(),
                                newConnectionInfo,
                                time);
                        mRemote.setValue(newRemote);
                    } else {
                        // modifying existing remote
                        mRemote.getValue().setTitle(newTitle);
                        mRemote.getValue().setLayoutType(newLayoutType.name());
                        mRemote.getValue().setConnectionMethod(newConnectionMethod.name());
                        mRemote.getValue().setConnectionInfo(newConnectionInfo);
                        mRemote.setValue(mRemote.getValue());
                    }
                    dialog1.dismiss();
                }

            });
        });
        return dialog;
    }

    /**
     * Gets a LiveData container for retrieving the modified Remote
     * @return the up to date Remote wrapped in LiveData
     */
    @NonNull
    public LiveData<RemoteEntity> getRemote(){
        return mRemote;
    }
}