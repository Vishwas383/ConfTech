package com.example.conftech.adapters;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.conftech.fragments.AttendeeEnded;
import com.example.conftech.fragments.AttendeeOnGoing;
import com.example.conftech.fragments.AttendeeUpcoming;

public class CategoryAdapter extends FragmentPagerAdapter {

    public CategoryAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // Implement logic to return the corresponding fragment based on position
        switch (position) {
            case 0:
                return new AttendeeUpcoming();
            case 1:
                return new AttendeeEnded();
            default:
                return new AttendeeOnGoing();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @NonNull
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Upcoming";
            case 1:
                return "Ended";
            default:
                return null;
        }
    }

}
