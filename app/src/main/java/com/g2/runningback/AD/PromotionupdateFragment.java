package com.g2.runningback.AD;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.g2.runningback.Common.Common;
import com.g2.runningback.Common.CommonTask;
import com.g2.runningback.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class PromotionupdateFragment extends Fragment {
    private Activity activity;
    private final static String TAG = "TAG_SpotUpdateFragment";
    private Promotion promotion;
    private EditText proItem_tvNo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(R.string.AdProductManagment);
        return inflater.inflate(R.layout.fragment_promotionupdate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        proItem_tvNo = view.findViewById(R.id.proItem_tvNo);

        final NavController navController = Navigation.findNavController(view);
        Bundle bundle = getArguments();
        if (bundle == null || bundle.getSerializable("promotion") == null) {
            Common.showToast(activity, R.string.textNoAdproductsFound);
            navController.popBackStack();
            return;
        }
        promotion = (Promotion) bundle.getSerializable("promotion");
        showPromotion();


        Button btFinishUpdate = view.findViewById(R.id.btpromptionupdate);
        btFinishUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String proItem_No = proItem_tvNo.getText().toString();
                if (proItem_No.length() <= 0) {
                    Common.showToast(activity, R.string.textNameIsInvalid);
                    return;
                }
                Bundle bundle = getArguments();
                int prom_no = bundle.getInt("prom_no");
                if (Common.networkConnected(activity)) {
                    String url = Common.URL_SERVER + "promotionServlet";
                    promotion.setFields(proItem_No,prom_no);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "promotionUpdate");
                    jsonObject.addProperty("promotion", new Gson().toJson(promotion));

                    int count = 0;
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        count = Integer.valueOf(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Common.showToast(activity, "修改失敗");
                    } else {
                        Common.showToast(activity, "修改成功");
                    }
                } else {
                    Common.showToast(activity, R.string.textNoNetwork);
                }
                /* 回前一個Fragment */
                navController.popBackStack();
            }
        });

        Button btCancel = view.findViewById(R.id.btpromotioncencal);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 回前一個Fragment */
                navController.popBackStack();
            }
        });
    }

    private void showPromotion() {
        Bundle bundle = getArguments();
        int ad_no = bundle.getInt("ad_no");
        String url = Common.URL_SERVER + "adproductServlet";
        proItem_tvNo.setText(String.valueOf(promotion.getPro_no()));

    }

}
