package com.g2.runningback.User;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.g2.runningback.Common.Common;
import com.g2.runningback.Common.CommonTask;
import com.g2.runningback.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class UserDetailFragment extends Fragment {

    private Activity activity;
    private TextView user_no, user_name, user_id, user_pw, user_email, user_regtime;
    private RecyclerView rvusdetail;
    private List<Order> orders;
    private CommonTask orderGetAllTask;
    private String orderUser_no;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("會員");
        return inflater.inflate(R.layout.fragment_user_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user_no = view.findViewById(R.id.user_no);
        user_name = view.findViewById(R.id.user_name);
        user_id = view.findViewById(R.id.user_id);
        user_pw = view.findViewById(R.id.user_pw);
        user_email = view.findViewById(R.id.user_email);
        user_regtime = view.findViewById(R.id.user_regtime);

        Bundle bundle = getArguments();
        if (bundle == null || bundle.getSerializable("user") == null) {
            Common.showToast(activity, "no employee found");
            return;
        }

        User user= (User) bundle.getSerializable("user");
        orderUser_no = user.getUser_no();
        user_no.setText(user.getUser_no());
        user_name.setText(user.getUser_name());
        user_id.setText(user.getUser_id());
        user_pw.setText(user.getUser_pw());
        user_email.setText(user.getUser_email());
        user_regtime.setText(user.getUser_regtime());

        rvusdetail = view.findViewById(R.id.rvusdetail);
        rvusdetail.setLayoutManager(new LinearLayoutManager(activity));

        orders = getOrders();
        showOrders(orders);

    }

    private List<Order> getOrders(){
        List<Order> orders = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "/UserOrderServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            jsonObject.addProperty("getorder", orderUser_no);
            String jsonOut = jsonObject.toString();
            orderGetAllTask = new CommonTask(url, jsonOut);
            try{
                String jsonIn = orderGetAllTask.execute().get();
                Type listType = new TypeToken<List<Order>>(){
                }.getType();
                Log.d(TAG,"jsonIn: " + jsonIn);
                orders = new Gson().fromJson(jsonIn,listType);
                Log.d(TAG,"orders" + orders);
            }catch(Exception e){
                Log.e(TAG,e.toString());
            }
        } else{
            Common.showToast(activity,"no network connection available");
        }

        return orders;
    }

    private void showOrders(List<Order> orders){
        if(orders == null || orders.isEmpty()){ //判斷是否為空直，如果是就不用顯示
            Common.showToast(activity,"沒有訂單");
            return;
        }

        OrderAdapter orderAdapter = (OrderAdapter) rvusdetail.getAdapter();

        if(orderAdapter == null){
            rvusdetail.setAdapter(new OrderAdapter(activity,orders));
        }else{
            orderAdapter.setOrders(orders); // 重新放入資料
            orderAdapter.notifyDataSetChanged(); // 提醒Adapter更新顯示畫面
        }

    }

    private class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyviewHolder>{

        private LayoutInflater layoutInflater;
        private List<Order> orders;

        OrderAdapter(Context context, List<Order> orders){
            layoutInflater = LayoutInflater.from(context);
            this.orders = orders;
        }


        void setOrders(List<Order> orders) {
            this.orders = orders;
        }

        class MyviewHolder extends RecyclerView.ViewHolder{

            TextView itemproduct_no, itemproduct_state;

            MyviewHolder(View itemView){
                super(itemView);
                itemproduct_no = itemView.findViewById(R.id.itemproduct_no);
                itemproduct_state = itemView.findViewById(R.id.itemproduct_state);
            }
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }

        @NonNull
        @Override
        public OrderAdapter.MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_order,parent,false);
            return new OrderAdapter.MyviewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderAdapter.MyviewHolder myviewHolder, int position) {
            final Order order = orders.get(position);
            myviewHolder.itemproduct_no.setText(order.getOrd_no());
            String order_status = "";
            switch(order.getOrd_status()) {
                case "0":
                order_status = "未完成付款";
                break;
                case "1":
                order_status = "未處理";
                break;
                case "2":
                order_status = "未寄出";
                break;
                case "3":
                order_status = "已寄出";
                break;
                case "4":
                order_status = "已取貨";
                break;
            }
            myviewHolder.itemproduct_state.setText(order_status);

        }
    }



}
