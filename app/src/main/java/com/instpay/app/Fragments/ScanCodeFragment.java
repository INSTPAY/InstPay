package com.instpay.app.Fragments;

import static com.instpay.app.App.ME;
import static com.instpay.app.App.MY_TOKEN;
import static com.instpay.app.App.requestQueue;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.ScanMode;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.instpay.app.Activities.PaymentActivity;
import com.instpay.app.Models.User;
import com.instpay.app.R;
import com.instpay.app.databinding.FragmentScanCodeBinding;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class ScanCodeFragment extends Fragment {
    private static final String TAG = ScanCodeFragment.class.getSimpleName();
    FragmentScanCodeBinding binding;
    CodeScanner scanner;

    public ScanCodeFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentScanCodeBinding.inflate(inflater, container, false);

        scanner = new CodeScanner(requireContext(), binding.scannerView);
        scanner.setCamera(CodeScanner.CAMERA_BACK);
        scanner.setFormats(Collections.singletonList(BarcodeFormat.QR_CODE));
        scanner.setAutoFocusMode(AutoFocusMode.SAFE);
        scanner.setScanMode(ScanMode.SINGLE);
        scanner.setAutoFocusEnabled(true);
        scanner.setFlashEnabled(false);

        scanner.setDecodeCallback(result -> requireActivity().runOnUiThread(()-> {
            JsonObjectRequest receiverRequest = new JsonObjectRequest(Request.Method.POST, getString(R.string.receiver_account_url), null, userResponse -> {
                startActivity(new Intent(requireContext(), PaymentActivity.class).putExtra("PAYEE", Parcels.wrap(new Gson().fromJson(userResponse.toString(), User.class))));
            }, error -> {
                Log.d(TAG, "error: ", error);
                scanner.startPreview();
            }){
                @Override
                public byte[] getBody() {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("receiver", result.getText());
                        object.put("account", ME.getAccount());
                        object.put("token", MY_TOKEN);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return object.toString().getBytes(StandardCharsets.UTF_8);
                }
            };

            requestQueue.add(receiverRequest);
        }));
        scanner.setErrorCallback(error -> {
            Log.e("TAG", "Scanner Error: ", error);
            Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
        });
        return binding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();
        scanner.stopPreview();
    }

    @Override
    public void onResume() {
        super.onResume();
        scanner.startPreview();
    }
}