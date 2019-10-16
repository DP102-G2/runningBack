package com.g2.runningback;


import android.app.Activity;

import android.graphics.Bitmap;
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
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;


public class UpdateproductFragment extends Fragment {
    private EditText pro_no, pro_name, pro_stock;
    private Activity activity;
    private Product product;
    private final static String TAG = "TAG_PorductUpdateFragment";
    private String productGetAllTask;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(R.string.productmanagment);
        return inflater.inflate(R.layout.fragment_updateproduct, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pro_no = view.findViewById(R.id.pro_no);
        pro_name = view.findViewById(R.id.pro_name);
        pro_stock = view.findViewById(R.id.pro_stock);

        final NavController navController = Navigation.findNavController(view);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Product product = (Product) bundle.getSerializable("product");
            if (product != null) {
                pro_no.setText(String.valueOf(product.getPro_no()));
                pro_name.setText(String.valueOf(product.getPro_name()));
                pro_stock.setText(String.valueOf(product.getPro_stock()));
            }
        }
        product = (Product) bundle.getSerializable("product");

        Button btupdateproduct = view.findViewById(R.id.btupdateproduct);
        btupdateproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String no = pro_no.getText().toString();
                if (pro_no.length() <= 0) {
                    Common.showToast(activity, String.valueOf(R.string.textNameIsInvalid));
                    return;
                }
                String name = pro_name.getText().toString();
                int stock = Integer.parseInt(pro_stock.getText().toString());

                if (Common.networkConnected(activity)) {
                    String url = Common.URL_SERVER + "ProductServlet";
                    product.setFields(no, name, stock);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "productUpdate");
                    jsonObject.addProperty("product", new Gson().toJson(product));

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
                    Common.showToast(activity, String.valueOf(R.string.textNoNetwork));
                }
                /* 回前一個Fragment */
                navController.popBackStack();
            }
        });

        Button btcancelproduct = view.findViewById(R.id.btcancelproduct);
        btcancelproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 回前一個Fragment */
                navController.popBackStack();
            }
        });
    }


}



