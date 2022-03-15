package com.instpay.app.Fragments;

import static com.instpay.app.App.ME;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.instpay.app.databinding.FragmentShowCodeBinding;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class ShowCodeFragment extends Fragment {
    FragmentShowCodeBinding binding;
    public ShowCodeFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShowCodeBinding.inflate(inflater, container, false);

        try {
            Bitmap bitmap = new BarcodeEncoder().createBitmap(new MultiFormatWriter().encode(ME.getAccount(), BarcodeFormat.QR_CODE, 500, 500));
            binding.imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return binding.getRoot();
    }
}