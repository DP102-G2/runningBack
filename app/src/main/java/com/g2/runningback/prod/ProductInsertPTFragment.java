package com.g2.runningback.prod;


import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.g2.runningback.Common.Common;
import com.g2.runningback.Common.CommonTask;
import com.g2.runningback.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class ProductInsertPTFragment extends Fragment implements View.OnClickListener {
    Activity activity;
    Button btConfirm, btBack;
    EditText etSotck, etPrice, etProInfo;
    Switch swSale;
    View view;

    SharedPreferences pref;
    private final static String PREFERENCES_NAME = "preferences";

    int stock, price, proSale;
    String proInfo;
    byte[] img1, img2, img3;

    Product product;
    CommonTask commonTask;
    private static final String url = Common.URL_SERVER + "/ProductServlet";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        pref = activity.getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_insert_two, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        activity.setTitle("新增商品");
        onHoldView();
        getPref();

    }

    @Override
    public void onPause() {
        super.onPause();

        saveProData();
        pref.edit().putString("Product", new Gson().toJson(product)).apply();

    }

    public void saveProData() {
        price = Integer.parseInt(etPrice.getText().toString().trim());
        stock = Integer.parseInt(etSotck.getText().toString().trim());
        proInfo = etProInfo.getText().toString().trim();
        proSale =0;
        if (swSale.isChecked()) {
            proSale = 1;
        } else {
            proSale = 0;
        }
        product.savePage2(price, stock, proSale, proInfo);

    }

    private void onHoldView() {
        etPrice = view.findViewById(R.id.ispro2_etProPrice);
        etSotck = view.findViewById(R.id.ispro2_etProStock);
        etProInfo = view.findViewById(R.id.ispro2_etProInfo);
        swSale = view.findViewById(R.id.ispro2_swSale);
        btConfirm = view.findViewById(R.id.ispro2_btConfirm);
        btBack = view.findViewById(R.id.ispro2_btBack);

        btConfirm.setOnClickListener(this);
        btBack.setOnClickListener(this);

            }


    private void getPref() {
        String proStr = pref.getString("Product", "null");
        if (!proStr.equals("null")) {
            product = new Gson().fromJson(proStr, Product.class);

            etProInfo.setText(product.getPro_info());
            etSotck.setText(String.valueOf(product.getPro_stock()));
            etPrice.setText(String.valueOf(product.getPro_price()));

            if (product.getPro_Sale() == 1) {
                swSale.setChecked(true);
            } else {
                swSale.setChecked(false);
            }

        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ispro2_btConfirm:

                saveProData();

                if (etPrice.getText().toString().equals("") | etProInfo.getText().toString().equals("") | etSotck.getText().toString().equals("")) {
                    Common.showToast(activity, "請填寫你的資訊");
                } else if (Integer.valueOf(etPrice.getText().toString().trim()) < 1) {
                    Common.showToast(activity, "售價不可低於零");
                } else if (product.getPro_stock()==0 && product.getPro_Sale()==1) {
                    Common.showToast(activity, "庫存為零，產品無法上架");
                } else {
                    saveProData();
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "insertProduct");

                    if (product.getPro_image() != null) {
                        img1 = product.getPro_image();
                        jsonObject.addProperty("image1", Base64.encodeToString(img1, Base64.DEFAULT));
                    }

                    if (product.getPro_image2() != null) {
                        img2 = product.getPro_image2();
                        jsonObject.addProperty("image2", Base64.encodeToString(img2, Base64.DEFAULT));
                    }

                    if (product.getPro_image3() != null) {
                        img3 = product.getPro_image3();
                        jsonObject.addProperty("image3", Base64.encodeToString(img3, Base64.DEFAULT));
                    }

                    product.clearImage();
                    jsonObject.addProperty("Product", new Gson().toJson(product));

                    commonTask = new CommonTask(url, jsonObject.toString());

                    try {
                        String count = commonTask.execute().get();
                        if (count.equals("1")) {
                            product.Clear();
                            pref.edit().putString("Product", null).apply();
                            Common.showToast(activity, "新增成功");
                        } else {
                            Common.showToast(activity, "新增失敗");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                Navigation.findNavController(v).navigate(R.id.action_productInsertPTFragment_to_productFragment);
                }
                break;
            case R.id.ispro2_btBack:
                saveProData();
                pref.edit().putString("Product", new Gson().toJson(product)).apply();
                Navigation.findNavController(view).popBackStack();
                break;
        }

    }
}
