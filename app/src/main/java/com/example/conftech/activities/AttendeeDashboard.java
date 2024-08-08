package com.example.conftech.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.conftech.R;
import com.example.conftech.databinding.ActivityAttendeeDashboardBinding;
import com.example.conftech.fragments.AttendeeFavFragment;
import com.example.conftech.fragments.AttendeeHomeFragment;
import com.example.conftech.fragments.AttendeeProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class AttendeeDashboard extends AppCompatActivity {

    ActivityAttendeeDashboardBinding binding;

    BottomNavigationView bottomNavbar;

    FrameLayout frameLayout;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttendeeDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.bottomNavbar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.icnHome:
                        selectedFragment = new AttendeeHomeFragment();
                        break;
                    case R.id.icnFav:
                        selectedFragment = new AttendeeFavFragment();
                        break;
                    case R.id.icnProfile:
                        selectedFragment = new AttendeeProfileFragment();
                        break;
                }
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, selectedFragment)
                            .commit();
                }
                return true;
            }
        });

        if (savedInstanceState == null) {
            binding.bottomNavbar.setSelectedItemId(R.id.icnHome);
        }


    }


}