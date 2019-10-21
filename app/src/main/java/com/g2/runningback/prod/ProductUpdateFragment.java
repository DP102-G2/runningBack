package com.g2.runningback.prod;


import android.app.Activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.g2.runningback.Common.Common;
import com.g2.runningback.Common.CommonTask;
import com.g2.runningback.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class ProductUpdateFragment extends Fragment {
    private EditText pro_name, pro_stock,pro_desc,pro_price,pro_info;
    private TextView pro_no,cat_no;
    private Switch swSale;

    private Activity activity;
    private final static String TAG = "TAG_PorductUpdateFragment";
    private Product product;
    private View view;
    private Bundle bundle;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        bundle = getArguments();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(R.string.productmanagment);
        return inflater.inflate(R.layout.fragment_product_update, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        onHoldView();

    }

    private void onHoldView(){
        pro_no = view.findViewById(R.id.proUpdate_tvNo);
        pro_name = view.findViewById(R.id.proUpdate_tvName);
        pro_stock = view.findViewById(R.id.proUpdate_etStock);
        pro_desc = view.findViewById(R.id.proUpdate_tvDesc);
        pro_info = view.findViewById(R.id.proUpdate_etInfo);
        pro_price = view.findViewById(R.id.proUpdate_etPrice);
        swSale = view.findViewById(R.id.proUpdate_swSale);
        cat_no = view.findViewById(R.id.proUpdate_tvCateNo);


        if (bundle != null) {
            product = (Product) bundle.getSerializable("product");
            if (product != null) {
                pro_no.setText(String.valueOf(product.getPro_no()));
                pro_name.setText(String.valueOf(product.getPro_name()));
                pro_stock.setText(String.valueOf(product.getPro_stock()));
                pro_price.setText(String.valueOf(product.getPro_price()));
                pro_desc.setText(product.getPro_desc());
                pro_info.setText(product.getPro_info());
                cat_no.setText(product.getCat_no());

                if (product.pro_Sale==1){
                    swSale.setChecked(true);
                }else {
                    swSale.setChecked(false);
                }

            }
        }

        Button btupdateproduct = view.findViewById(R.id.btupdateproduct);
        btupdateproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String no = pro_no.getText().toString();
                if (pro_no.length() <= 0||pro_name.getText().toString().equals("")|pro_stock.getText().toString().equals("")|pro_price.getText().toString().equals("")
                        |pro_desc.getText().toString().equals("")|pro_info.getText().toString().equals("")|cat_no.getText().toString().equals("")) {
                    Common.showToast(activity,activity.getString(R.string.textNameIsInvalid));
                    return;
                }

                String name = pro_name.getText().toString().trim();
                int stock = Integer.parseInt(pro_stock.getText().toString());
                int price = Integer.parseInt(pro_price.getText().toString());
                String desc = pro_desc.getText().toString().trim();
                String info = pro_info.getText().toString().trim();




                if (Common.networkConnected(activity)) {
                    String url = Common.URL_SERVER + "ProductServlet";

                    if (swSale.isChecked()){
                       product.setPro_Sale(1);
                    }else {
                        product.setPro_Sale(0);
                    }
                    Product nProduct = new Product(product.pro_no,product.cat_no,name,desc,price,stock,product.getPro_Sale(),info);


                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "productUpdate");
                    jsonObject.addProperty("product", new Gson().toJson(nProduct));

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
                    Common.showToast(activity,activity.getString( R.string.textNoNetwork));
                }
                /* 回前一個Fragment */
                Navigation.findNavController(v).popBackStack();
            }
        });

        Button btcancelproduct = view.findViewById(R.id.btcancelproduct);
        btcancelproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 回前一個Fragment */
                Navigation.findNavController(v).popBackStack();
            }
        });

    }


}



