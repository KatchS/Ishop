package com.sk.ishop.main_screen;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sk.ishop.MainActivity;
import com.sk.ishop.server_request.HttpRequest;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by sk on 19/12/2017.
 */

public class ScannerFragment extends Fragment implements ZXingScannerView.ResultHandler {

    private final String TAG = "ScannerFragment";

    private ZXingScannerView scannerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        scannerView = new ZXingScannerView(getActivity());
        scannerView.setResultHandler(this);
        return scannerView;
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        scannerView.startCamera();
    }

    @Override
    public void handleResult(Result result) {
        new HttpRequest(prepareJsonObject(result.getText()),((MainActivity)getActivity()),false).start();
        Log.d(TAG, "handleResult: the barcode is: " + result.getText());
        getFragmentManager().popBackStack("CART",0);
        ((MainActivity)getActivity()).scannerClosed();
    }

    private JSONObject prepareJsonObject(String prod_id){
        JSONObject output = new JSONObject();
        try {
            output.put("prod_id",prod_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return output;
    }
}
