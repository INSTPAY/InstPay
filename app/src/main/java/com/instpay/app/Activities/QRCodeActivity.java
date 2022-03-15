package com.instpay.app.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.instpay.app.Adapters.PagerAdapter;
import com.instpay.app.Fragments.ScanCodeFragment;
import com.instpay.app.Fragments.ShowCodeFragment;
import com.instpay.app.R;
import com.instpay.app.databinding.ActivityQrcodeBinding;

import java.util.ArrayList;

public class QRCodeActivity extends AppCompatActivity {
    ActivityQrcodeBinding binding;
    ArrayList<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrcodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fragments = new ArrayList<>();
        fragments.add(new ScanCodeFragment());
        fragments.add(new ShowCodeFragment());

        binding.QRCodeViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), getLifecycle(), fragments));

        binding.QRCodeTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.QRCodeViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.QRCodeViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.QRCodeTab.selectTab(binding.QRCodeTab.getTabAt(position));
            }
        });
    }
}