package com.instpay.app.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.instpay.app.databinding.FragmentShowCodeBinding;

public class ScanCodeFragment extends Fragment {
    FragmentShowCodeBinding binding;

    public ScanCodeFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShowCodeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }
}