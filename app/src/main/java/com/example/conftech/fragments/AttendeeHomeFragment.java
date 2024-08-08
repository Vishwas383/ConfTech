package com.example.conftech.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.conftech.R;
import com.example.conftech.adapters.CategoryAdapter;
import com.google.android.material.tabs.TabLayout;


public class AttendeeHomeFragment extends Fragment {

    private TabLayout categorytabs;
    private ViewPager viewpager;

    TextView txtFname;

    SharedPreferences sharedPreferences;

    public AttendeeHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendee_home, container, false);

        sharedPreferences = getActivity().getSharedPreferences("Signin", MODE_PRIVATE);


        categorytabs = view.findViewById(R.id.categorytabs);
        viewpager = view.findViewById(R.id.viewpager);
        txtFname=view.findViewById(R.id.txtFname);

        if(!sharedPreferences.getString("fname","").equalsIgnoreCase(""))
        {
            txtFname.setText(sharedPreferences.getString("fname","").toString());
        }



        CategoryAdapter pagerAdapter = new CategoryAdapter(getChildFragmentManager());
        viewpager.setAdapter(pagerAdapter);
        categorytabs.setupWithViewPager(viewpager);

        return  view;
    }
}