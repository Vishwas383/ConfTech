package com.example.conftech.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.conftech.R;
import com.example.conftech.databinding.ActivityOrganizerDashboardBinding;
import com.example.conftech.fragments.AttendeeFavFragment;
import com.example.conftech.fragments.AttendeeHomeFragment;
import com.example.conftech.fragments.AttendeeProfileFragment;
import com.example.conftech.fragments.OrganizerAttendees;
import com.example.conftech.fragments.OrganizerEvents;
import com.example.conftech.fragments.OrganizerHome;
import com.example.conftech.fragments.OrganizerProfile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class OrganizerDashboard extends AppCompatActivity {

    BottomNavigationView bottomNavbar;

    FrameLayout frameLayout;

    SharedPreferences sharedPreferences;


    ActivityOrganizerDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrganizerDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.oBottomNavbar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.icnHome:
                        selectedFragment = new OrganizerHome();
                        break;
                    case R.id.icnEvent:
                        selectedFragment = new OrganizerEvents();
                        break;
                    case R.id.icnProfile:
                        selectedFragment = new OrganizerProfile();
                        break;
                }
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.oFrameLayout, selectedFragment)
                            .commit();
                }
                return true;
            }
        });

        if (savedInstanceState == null) {
            binding.oBottomNavbar.setSelectedItemId(R.id.icnHome);
        }
    }
}