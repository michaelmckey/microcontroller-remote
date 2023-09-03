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

package tk.michaelmckey.microcontrollerremote.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.material.navigation.NavigationView;

import java.util.Arrays;
import java.util.LinkedList;

import tk.michaelmckey.microcontrollerremote.R;
import tk.michaelmckey.microcontrollerremote.databinding.ActivityMainBinding;

/**
 * Manages navigation and the toolbar
 * @author Michael McKey
 * @version 1.2.2
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    @Nullable
    private MainActivityViewModel mMainActivityViewModel;
    @Nullable
    private DrawerLayout mDrawer;
    @Nullable
    private NavController mNavController;
    private final int BLUETOOTH_SCAN_PERMISSION_REQUEST_CODE = 42;

    /**
     * Creates all fragments, sets up toolbar, and sets up NavController.
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding =
                ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mMainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        mDrawer = binding.drawerLayout;
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);

        setSupportActionBar(binding.include.toolbar);

        //every id passed is considered a lot level destination
        AppBarConfiguration mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_remotes, R.id.nav_commands)
                .setOpenableLayout(mDrawer)
                .build();
        NavigationUI.setupWithNavController(binding.include.toolbar,
                mNavController,
                mAppBarConfiguration);
        binding.navView.setNavigationItemSelectedListener(this);
        binding.navView.bringToFront();
        getBluetoothPermission();
    }


    public void getBluetoothPermission(){
        LinkedList<String> permissionsToRequestDynamic = new LinkedList<>();
        LinkedList<String> permissionsNeeded = new LinkedList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionsNeeded.add(Manifest.permission.BLUETOOTH_SCAN);
            permissionsNeeded.add(Manifest.permission.BLUETOOTH_CONNECT);
            //permissionsNeeded.add(Manifest.permission.BLUETOOTH_ADVERTISE);

            for(String permission : permissionsNeeded){
                if(checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED){
                    if(shouldShowRequestPermissionRationale(permission)){
                        Toast.makeText(this, R.string.bluetooth_permission_rationale, Toast.LENGTH_SHORT).show();
                    }
                    permissionsToRequestDynamic.add(permission);
                }
            }
        }//if api is 30 or below could add permission to request in here(if need extra permissions

        Object[] objectArray = permissionsToRequestDynamic.toArray();
        if(objectArray.length != 0) {
            //there are permission to request
            String[] permissionsToRequest = Arrays.copyOf(objectArray, objectArray.length, String[].class);
            ActivityCompat.requestPermissions(this, permissionsToRequest, BLUETOOTH_SCAN_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == BLUETOOTH_SCAN_PERMISSION_REQUEST_CODE){
            for(int i = 0; i<permissions.length; i++){
                String permission = permissions[i];
                int result = grantResults[i];
                if(result != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Need "+ permission + " to connect", Toast.LENGTH_SHORT).show();
                }
            }
            //permission has been granted
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Manages navigation when an item on the navigation menu has been selected.
     * @param item the selected item/designation
     * @return true to show the selected item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        assert mDrawer != null;
        mDrawer.closeDrawer(GravityCompat.START);
        if(item.getItemId() == R.id.nav_licences){
            //displays the licences in the application
            startActivity(new Intent(this, OssLicensesMenuActivity.class));
        }else if(item.getItemId() == R.id.nav_tutorial){
            //redirects the user to the instructions
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.tutorial_link))));
        }else if(item.getItemId() == R.id.nav_privacy_policy){
            //redirects the user to the instructions
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_link))));
        }
        assert mNavController != null;
        return NavigationUI.onNavDestinationSelected(item, mNavController)
                || onOptionsItemSelected(item);
    }

    /**
     * Handles presses of the back button.
     */
    @Override
    public void onBackPressed() {
        SearchView searchView = findViewById(R.id.app_bar_search);
        assert mDrawer != null;
        if(mDrawer.isDrawerOpen(GravityCompat.START)){
            mDrawer.closeDrawer(GravityCompat.START);
            return;
        }
        if(searchView!=null){
            if(!searchView.isIconified()) {
                searchView.setIconified(true);
                return;
            }
        }
        //only calls super method if none of the other actions are preformed
        super.onBackPressed();
    }

    /**
     * Saves all of the data that should be persistent.
     * Might not be called if app is updated when its open (when running via Android Studio).
     */
    @Override
    public void onPause() {
        assert mMainActivityViewModel != null;
        mMainActivityViewModel.save();
        super.onPause();
    }
}