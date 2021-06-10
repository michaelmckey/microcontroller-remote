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
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import tk.michaelmckey.microcontrollerremote.R;
import tk.michaelmckey.microcontrollerremote.connection.ConnectionManager;
import tk.michaelmckey.microcontrollerremote.databinding.BluetoothDeviceSelectorBinding;

/**
 * Creates a Alert Dialog which allows the user to select a Bluetooth Device
 * (from the list of paired Bluetooth devices)
 * Selected device returned by an observer on getMacAddress.
 * @author Michael McKey
 * @version 1.2.2
 */
public class BluetoothDeviceDialog extends DialogFragment {
    @NonNull
    public static final String TAG = "BluetoothDeviceDialog";
    @NonNull
    private final MutableLiveData<String> mMacAddress = new MutableLiveData<>();

    /**
     * Creates a Dialog to show to the User
     * @param savedInstanceState information to reconstruct the fragment(if being reconstructed)
     * @return the AlertDialog to show to the user
     */
    @NonNull
    @Override
    @RequiresPermission("android.permission.BLUETOOTH")
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        @NonNull BluetoothDeviceSelectorBinding binding =
                BluetoothDeviceSelectorBinding.inflate(getLayoutInflater());

        //gets a list of bluetooth devices
        List<BluetoothDevice> bluetoothDevices = ConnectionManager.getBluetoothDevices();
        List<String> bluetoothDeviceNames = new ArrayList<>();
        for (BluetoothDevice bluetoothDevice : bluetoothDevices) {
            String name = bluetoothDevice.getName();
            if (name != null) {
                bluetoothDeviceNames.add(bluetoothDevice.getName());
            }
        }

        //initialises the layout
        ArrayAdapter<String> connectionInfoAa = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                bluetoothDeviceNames);
        connectionInfoAa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.connectionInfoSpinner.setAdapter(connectionInfoAa);

        builder.setTitle(R.string.select_device);

        builder.setView(binding.getRoot());

        builder.setPositiveButton(R.string.select, (dialog, which) -> {
            //when select is clicked it updates the LiveData triggering the observer
            String bluetoothDeviceName = (String) binding.connectionInfoSpinner.getSelectedItem();
            BluetoothDevice bluetoothDevice = bluetoothDevices.get(
                    bluetoothDeviceNames.indexOf(bluetoothDeviceName));
            mMacAddress.setValue(bluetoothDevice.getAddress());
            dialog.dismiss();
        });

        builder.setNeutralButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    /**
     * Gets a LiveData container for retrieving the selected MAC address
     * @return the MAC address wrapped in LiveData
     */
    @NonNull
    @RequiresPermission("android.permission.BLUETOOTH")
    public LiveData<String> getMacAddress(){
        return mMacAddress;
    }
}
