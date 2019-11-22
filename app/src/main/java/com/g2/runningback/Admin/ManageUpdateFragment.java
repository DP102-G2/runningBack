package com.g2.runningback.Admin;


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
import android.widget.Spinner;
import android.widget.TextView;

import com.g2.runningback.Common.Common;
import com.g2.runningback.Common.CommonTask;
import com.g2.runningback.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import static android.content.ContentValues.TAG;

public class ManageUpdateFragment extends Fragment {
    private Spinner spJob;
    private Activity activity;
    private EditText etName,etPassword;
    private TextView etNo,etId;
    private String emp_no;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("修改帳戶");
        return inflater.inflate(R.layout.fragment_manage_update, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);
        spJob = view.findViewById(R.id.updatespJob);
        spJob.setSelection(0, true);
        etId = view.findViewById(R.id.updateetId);
        etName = view.findViewById(R.id.updateetName);
        etPassword = view.findViewById(R.id.updateetpassword);
        etNo = view.findViewById(R.id.updateetNo);

        Button updatebtDelete = view.findViewById(R.id.updatebtDelete);
        updatebtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 回前一個Fragment */
                navController.popBackStack();
            }
        });

        Bundle bundle = getArguments();
        if (bundle == null || bundle.getSerializable("manage") == null) {
            Common.showToast(activity, "no employee found");
            navController.popBackStack();
            return;
        }

        emp_no =  bundle.getString("manage");
        showManage();


        Button updatebtConfirm = view.findViewById(R.id.updatebtConfirm);
        updatebtConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name =etName.getText().toString().trim();
                String id =etId.getText().toString().trim();
                String password =etPassword.getText().toString().trim();
                String no =etNo.getText().toString().trim();
                String job = spJob.getSelectedItem().toString().trim();
                String job_no="0";
                switch(job){
                    case "管理員":
                        job_no = "1";
                        break;
                    case "客服":
                        job_no = "2";
                        break;
                    case "商品部":
                        job_no = "3";
                        break;
                }

                if(name.length()<=0||password.length()<=0){
                    Common.showToast(getActivity(),"輸入不行空白");
                    return;
                }

                if (Common.networkConnected(activity)) {
                    String url = Common.URL_SERVER + "ManageServlet";
                    Manage manage = new Manage(no, name, id, password, job_no);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "manageUpdate");
                    jsonObject.addProperty("update", new Gson().toJson(manage));

                    int count = 0;
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        count = Integer.valueOf(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Common.showToast(getActivity(), "更新失敗");
                    } else {
                        Common.showToast(getActivity(), "更新成功");
                        /* 回前一個Fragment */
                        navController.popBackStack();
                    }
                } else {
                    Common.showToast(getActivity(), "no network");
                }
            }
        });

    }

    private void showManage() {
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "ManageServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "manageSearch");
            jsonObject.addProperty("search", emp_no);
            Manage manages = null;
            try {
                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                Log.d(TAG, "result is :"+result);
                manages = new Gson().fromJson(result, Manage.class);
                Log.d(TAG, "result is :"+manages);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

            etNo.setText(emp_no);
            etPassword.setText(manages.getEmp_pw());
            etId.setText(manages.getEmp_id());
            etName.setText(manages.getEmp_name());
            int job_no = Integer.parseInt(manages.getJob_no());
            job_no = job_no-1;
            spJob.setSelection(job_no);
        }

    }

}

