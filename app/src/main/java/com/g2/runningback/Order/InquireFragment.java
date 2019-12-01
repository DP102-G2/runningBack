package com.g2.runningback.Order;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.g2.runningback.Common.Common;
import com.g2.runningback.Common.CommonTask;
import com.g2.runningback.Orderlist;
import com.g2.runningback.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

public class InquireFragment extends Fragment {

    private Activity activity;
    private SearchView searchView;
    private TextView tsrecipient,tsshipping_address,tscontact_number;
    private TextView tsorder_number, tsmember_number, tsorder_time;
    private List<com.g2.runningback.Orderlist> orderlists;
    private static final String TAG = "TAG_SpotListFragment";
    private CommonTask GetAllTask;
    private Spinner spprocessing_situation;
    private int i,k;
    private String j;
    private com.g2.runningback.Orderlist orderlist;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("訂單查詢");
        return inflater.inflate(R.layout.fragment_inquire, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        tsorder_number = view.findViewById(R.id.tsorder_number);
        tsmember_number = view.findViewById(R.id.tsmember_number);
        tsorder_time = view.findViewById(R.id.tsorder_time);
        tsrecipient = view.findViewById(R.id.tsrecipient);
        tsshipping_address = view.findViewById(R.id.tsshipping_address);
        tscontact_number = view.findViewById(R.id.tscontact_number);
        spprocessing_situation = view.findViewById(R.id.spprocessing_situation);
        Button budetermine = view.findViewById(R.id.budetermine);




        final Bundle bundle = getArguments();
        if (bundle != null) {
            com.g2.runningback.Orderlist orderlist = (com.g2.runningback.Orderlist) bundle.getSerializable("orderlist");
            if (orderlist != null) {
                tsorder_number.setText(String.valueOf(orderlist.getOrd_no()));
                tsmember_number.setText(String.valueOf(orderlist.getUser_no()));
                tsorder_time.setText(String.valueOf(orderlist.getOrd_date()));
                tsrecipient.setText(String.valueOf(orderlist.getUser_name()));
                tsshipping_address.setText(String.valueOf(orderlist.getAddress()));
                tscontact_number.setText(String.valueOf(orderlist.getPhone()));
                tscontact_number.setText(String.valueOf(orderlist.getPhone()));
                //若當i等於訂單,處理狀況,顯示訂單情況
                i=orderlist.getOrd_status();
                spprocessing_situation.setSelection(i, true);
                spprocessing_situation.setOnItemSelectedListener(listener);
                j=String.valueOf(orderlist.getord_statustText());

            }

        }



        final com.g2.runningback.Orderlist orderlist = (Orderlist) bundle.getSerializable("orderlist");

        final NavController navController = Navigation.findNavController(view);
        budetermine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getorder_status();

                int ord_status = i;



                int ord_no = Integer.valueOf(orderlist.getOrd_no());

                if (Common.networkConnected(activity)) {
                    String url = Common.URL_SERVER + "OrderlistServlet";

                    orderlist.setFields(ord_status, ord_no);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "orderlistUpdate");
                    jsonObject.addProperty("orderlist", new Gson().toJson(orderlist));

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

    }
    Spinner.OnItemSelectedListener listener = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(
                AdapterView<?> parent, View view, int pos, long id) {

            Log.e(TAG, "123");

            j=(parent.getItemAtPosition(pos).toString());

            Log.e(TAG,j);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public int getorder_status( ){

        switch (j){

            case "未處理" :
                i = 0;
                break;
            case "未出貨":
                i = 1;
                break;
            case "已出貨":
                i = 2;
                break;
            case"未送達":
                i = 3;
                break;
            case "已送達":
                i = 4;
                break;
        }

        return i;
    }


}





