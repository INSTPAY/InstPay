package com.instpay.app.Fragments;

import static android.content.Context.WINDOW_SERVICE;
import static com.instpay.app.App.ME;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.instpay.app.databinding.FragmentShowCodeBinding;

import java.io.ByteArrayOutputStream;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class ShowCodeFragment extends Fragment {
    private static final String TAG =  ShowCodeFragment.class.getSimpleName();
    FragmentShowCodeBinding binding;
    public ShowCodeFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShowCodeBinding.inflate(inflater, container, false);

        Point point = new Point();
        ((WindowManager) requireContext().getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getSize(point);
        QRGEncoder encoder = new QRGEncoder(ME.getAccount(), null, QRGContents.Type.TEXT, Math.min(point.x, point.y));
        Bitmap bitmap = encoder.getBitmap();
        binding.imageView.setImageBitmap(bitmap);

        return binding.getRoot();
    }
}