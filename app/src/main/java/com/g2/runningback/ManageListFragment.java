package com.g2.runningback;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;

import com.g2.runningback.Common.Common;
import com.g2.runningback.Common.CommonTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ManageListFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvManage;
    private Activity activity;
    private List<Manage> manages;
    private CommonTask manageGetAllTask;
    private CommonTask manageDeleteTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("帳戶管理");
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_manage_list, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SearchView searchView = view.findViewById(R.id.fmSearch);
        swipeRefreshLayout = view.findViewById(R.id.fmsR);
        rvManage = view.findViewById(R.id.rvEmployee);

        rvManage.setLayoutManager(new LinearLayoutManager(activity));
        manages = getManagess();
        showManages(manages);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                showManages(manages);
                swipeRefreshLayout.setRefreshing(false);
                Log.d(TAG, "swipeRefreshLayout: " + swipeRefreshLayout);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                // 如果搜尋條件為空字串，就顯示原始資料；否則就顯示搜尋後結果
                if (newText.isEmpty()) {
                    showManages(manages);
                } else {
                    List<Manage> searchSpots = new ArrayList<>();
                    // 搜尋原始資料內有無包含關鍵字(不區別大小寫)
                    for (Manage manage : manages) {
                        if (manage.getEmp_name().toUpperCase().contains(newText.toUpperCase())) {
                            searchSpots.add(manage);
                        }
                    }
                    showManages(searchSpots);
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });

        FloatingActionButton btAdd = view.findViewById(R.id.btAdd);
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view)
                        .navigate(R.id.action_manageListFragment_to_manageInsertFragment);
            }
        });
    }

    private List<Manage> getManagess() {
        List<Manage> manages = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "/ManageServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            manageGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = manageGetAllTask.execute().get();
                Type listType = new TypeToken<List<Manage>>() {
                }.getType();
                Log.d(TAG, "jsonIn: " + jsonIn);
                manages = new Gson().fromJson(jsonIn, listType);
                Log.d(TAG, "manages: " + manages);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, "no network connection available");
        }
        return manages;
    }

    private void showManages(List<Manage> manages) {
        if (manages == null || manages.isEmpty()) {
            Common.showToast(activity, "no data found");
            return;
        }
        ManageAdapter manageAdapter = (ManageAdapter) rvManage.getAdapter();
        // 如果spotAdapter不存在就建立新的，否則續用舊有的
        if (manageAdapter == null) {
            rvManage.setAdapter(new ManageAdapter(activity, manages));
        } else {
            manageAdapter.setSpots(manages);
            manageAdapter.notifyDataSetChanged();
        }
    }

    private class ManageAdapter extends RecyclerView.Adapter<ManageAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Manage> manages;

        ManageAdapter(Context context, List<Manage> manages) {
            layoutInflater = LayoutInflater.from(context);
            this.manages = manages;
        }

        void setSpots(List<Manage> spots) {
            this.manages = spots;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tvEmp_no, tvEmp_name, tvjob_name;

            MyViewHolder(View itemView) {
                super(itemView);
                tvEmp_no = itemView.findViewById(R.id.tvEmp_no);
                tvEmp_name = itemView.findViewById(R.id.tvEmp_name);
                tvjob_name = itemView.findViewById(R.id.tvjob_name);
            }
        }

        @Override
        public int getItemCount() {
            return manages.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_employee, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
            final Manage manage = manages.get(position);
            myViewHolder.tvEmp_no.setText(manage.getEmp_no());
            myViewHolder.tvEmp_name.setText(manage.getEmp_name());
            myViewHolder.tvjob_name.setText(manage.getJob_name());

            myViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View view) {
                    PopupMenu popupMenu = new PopupMenu(activity, view, Gravity.END);
                    popupMenu.inflate(R.menu.popup_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.update:
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("manage", manage.getEmp_no());

                                    Navigation.findNavController(view)
                                            .navigate(R.id.action_manageListFragment_to_manageUpdateFragment, bundle);
                                    break;
                                case R.id.delete:
                                    if (Common.networkConnected(activity)) {
                                        String url = Common.URL_SERVER + "/ManageServlet";
                                        JsonObject jsonObject = new JsonObject();
                                        jsonObject.addProperty("action", "manageDelete");
                                        jsonObject.addProperty("manageEmp_no", manage.getEmp_no());
                                        int count = 0;
                                        try {
                                            manageDeleteTask = new CommonTask(url, jsonObject.toString());
                                            String result = manageDeleteTask.execute().get();
                                            count = Integer.valueOf(result);
                                        } catch (Exception e) {
                                            Log.e(TAG, e.toString());
                                        }
                                        if (count == 0) {
                                            Common.showToast(activity, "text delete fail");
                                        } else {
                                            manages.remove(manage);
                                            ManageAdapter.this.notifyDataSetChanged();
                                            Common.showToast(activity, "text delete success");
                                        }
                                    } else {
                                        Common.showToast(activity, "no network");
                                    }
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                    return true;
                }
            });

        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (manageGetAllTask != null) {
            manageGetAllTask.cancel(true);
            manageGetAllTask = null;
        }

        if (manageDeleteTask != null) {
            manageDeleteTask.cancel(true);
            manageDeleteTask = null;
        }
    }

}
