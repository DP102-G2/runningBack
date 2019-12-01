package com.g2.runningback.Order;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.g2.runningback.Common.Common;
import com.g2.runningback.Common.CommonTask;
import com.g2.runningback.Orderlist;
import com.g2.runningback.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


public class OrderFragment extends Fragment {


    private Activity activity;
    private RecyclerView recyclerView;
    private List<com.g2.runningback.Orderlist> orderlists;
    private CommonTask orderlistGetAllTask,OrderlistDeleteTask;
    private SearchView searchView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("訂單管理");
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclername);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        searchView = view.findViewById(R.id.searchView);

        recyclerView.setAdapter(new OrderlistAdapter(activity, orderlists));
        orderlists = getOrderlists();
        showOrderlists(orderlists);


        //搜尋
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                OrderlistAdapter adapter = (OrderlistAdapter) recyclerView.getAdapter();
                if (adapter != null) {
                    // 如果搜尋條件為空字串，就顯示原始資料；否則就顯示搜尋後結果
                    if (newText.isEmpty()) {
                        adapter.setOrderlists(orderlists);
                    } else {

                        List<com.g2.runningback.Orderlist> searchOrderlists = new ArrayList<>();
                        // 搜尋原始資料內有無包含關鍵字(不區別大小寫)
                        for (com.g2.runningback.Orderlist orderlist : orderlists) {
                            if (String.valueOf(orderlist.getOrd_no()).equals(newText)) {
                                searchOrderlists.add(orderlist);

                                Bundle bundle = new Bundle();
                                bundle.putSerializable("orderlist", orderlist);

                            }
                        }
                        adapter.setOrderlists(searchOrderlists);
                    }
                    adapter.notifyDataSetChanged();
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });

    }

    private class OrderlistAdapter extends RecyclerView.Adapter<OrderlistAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        List<com.g2.runningback.Orderlist> orderlists;


        OrderlistAdapter(Context context, List<com.g2.runningback.Orderlist> orderlists) {
            layoutInflater = LayoutInflater.from(context);
            //  this.context = context;
            this.orderlists = orderlists;


        }

        void setOrderlists(List<com.g2.runningback.Orderlist> orderlists) {
            this.orderlists = orderlists;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tsproduct_number, tsproduct_name, tstreatment_status;


            MyViewHolder(View itemView) {
                super(itemView);
                tsproduct_number = itemView.findViewById(R.id.tsproduct_number);
                tsproduct_name = itemView.findViewById(R.id.tsproduct_name);
                tstreatment_status = itemView.findViewById(R.id.tstreatment_status);
            }


        }

        @Override
        public int getItemCount() {
            return orderlists.size();
        }

        @NonNull
        @Override
        public OrderlistAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = layoutInflater.inflate(R.layout.item_view_order, viewGroup, false);
            return new OrderlistAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final OrderlistAdapter.MyViewHolder viewHolder, final int postion) {
            final com.g2.runningback.Orderlist orderlist = orderlists.get(postion);
            viewHolder.tsproduct_number.setText(String.valueOf(orderlist.getOrd_no()));
            viewHolder.tsproduct_name.setText(String.valueOf(orderlist.getUser_name()));
            viewHolder.tstreatment_status.setText(String.valueOf(orderlist.getord_statustText()));


            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    PopupMenu popupMenu = new PopupMenu(activity, view, Gravity.END);
                    popupMenu.inflate(R.menu.popup_menu_product);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.update:
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("orderlist", orderlist);
                                    Navigation.findNavController(view)
                                            .navigate(R.id.action_orderFragment_to_inquireFragment, bundle);
                                    break;
                                case R.id.delete:
                                    if (Common.networkConnected(activity)) {
                                        String url = Common.URL_SERVER + "/OrderlistServlet";
                                        JsonObject jsonObject = new JsonObject();
                                        jsonObject.addProperty("action", "orderlistDelete");
                                        jsonObject.addProperty("ord_no", orderlist.getOrd_no());
                                        jsonObject.addProperty("pro_no", orderlist.getPro_no());
                                        int count = 0;
                                        try {
                                            OrderlistDeleteTask = new CommonTask(url, jsonObject.toString());
                                            String result = OrderlistDeleteTask.execute().get();
                                            count = Integer.valueOf(result);
                                        } catch (Exception e) {
                                            Log.e(TAG, e.toString());
                                        }
                                        if (count == 0) {
                                            Common.showToast(activity,"修改失敗");
                                        } else {
                                            orderlists.remove(orderlist);
                                            OrderlistAdapter.this.notifyDataSetChanged();
                                            // 外面spots也必須移除選取的spot
                                        orderlists.remove(orderlist);
                                            Common.showToast(activity, "修改成功");
                                        }
                                    } else {
                                        Common.showToast(activity, R.string.textNoNetwork);
                                    };
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
//                    return true;
                }
            });
        }
    }

    private List<com.g2.runningback.Orderlist> getOrderlists() {
        List<com.g2.runningback.Orderlist> orderlists = null;
        if (Common.networkConnected(activity)) {
            Timestamp t = new Timestamp(System.currentTimeMillis());
            String url = Common.URL_SERVER + "/OrderlistServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            orderlistGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = orderlistGetAllTask.execute().get();
//
                Type listType = new TypeToken<List<com.g2.runningback.Orderlist>>() {
                }.getType();
                orderlists =new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, String.valueOf(R.string.textNoNetwork));
        }
        return orderlists;
    }

    // check if the device connect to the network
    private void showOrderlists(List<Orderlist> orderlists) {
        if (orderlists == null || orderlists.isEmpty()) {
            Common.showToast(activity, String.valueOf(R.string.textNoProductsFound));
            return;
        }
       OrderlistAdapter orderlistAdapter = (OrderlistAdapter) recyclerView.getAdapter();
        // 如果spotAdapter不存在就建立新的，否則續用舊有的
        if (orderlistAdapter == null) {
            recyclerView.setAdapter(new OrderlistAdapter(activity, orderlists));
        } else {
            orderlistAdapter.setOrderlists(orderlists);
            orderlistAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (orderlistGetAllTask != null) {
            orderlistGetAllTask.cancel(true);
            orderlistGetAllTask = null;
        }

        if (OrderlistDeleteTask != null) {
            OrderlistDeleteTask.cancel(true);
            OrderlistDeleteTask = null;
        }


    }



}











