package com.g2.runningback.User;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.g2.runningback.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class UserFragment extends Fragment {

    private Activity activity;
    private RecyclerView rvUser;
    private List<User> users;
    private CommonTask userGetAllTask;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("管理會員");
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SearchView searchView = view.findViewById(R.id.userSearch);
        rvUser = view.findViewById(R.id.rvUser);
        rvUser.setLayoutManager(new LinearLayoutManager(activity));

        users = getUsers();
        showUsers(users);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {

                // 如果搜尋條件為空字串，就顯示原始資料；否則就顯示搜尋後結果
                if (newText.isEmpty()) {
                    showUsers(users);
                } else {
                    List<User> searchUsers = new ArrayList<>();
                    // 搜尋原始資料內有無包含關鍵字(不區別大小寫)
                    for (User user : users) {
                        if (user.getUser_name().toUpperCase().contains(newText.toUpperCase())) {
                            searchUsers.add(user);
                        }
                    }
                    showUsers(searchUsers);
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });
    }



    private List<User> getUsers(){
        List<User> users = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "/UserServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            userGetAllTask = new CommonTask(url, jsonOut);
            try{
                String jsonIn = userGetAllTask.execute().get();
                Type listType = new TypeToken<List<User>>(){
                }.getType();
                Log.d(TAG,"jsonIn: " + jsonIn);
                users = new Gson().fromJson(jsonIn,listType);
                Log.d(TAG,"users" + users);
            }catch(Exception e){
                Log.e(TAG,e.toString());
            }
        } else{
            Common.showToast(activity,"no network connection available");
        }

        return users;
    }

    private void showUsers(List<User> users){
        if(users == null || users.isEmpty()){ //判斷是否為空直，如果是就不用顯示
            Common.showToast(activity,"no data found");
            return;
        }

        UserAdapter userAdapter = (UserAdapter) rvUser.getAdapter();

        if(userAdapter == null){
            rvUser.setAdapter(new UserAdapter(activity,users));
        }else{
            userAdapter.setUsers(users); // 重新放入資料
            userAdapter.notifyDataSetChanged(); // 提醒Adapter更新顯示畫面
        }

    }

    private class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyviewHolder>{

        private LayoutInflater layoutInflater;
        private List<User> users;

        UserAdapter(Context context, List<User> users){
            layoutInflater = LayoutInflater.from(context);
            this.users = users;
        }


        void setUsers(List<User> users) {
            this.users = users;
        }

        class MyviewHolder extends RecyclerView.ViewHolder{

            TextView itemuser_no, itemuser_name;

            MyviewHolder(View itemView){
                super(itemView);
                itemuser_no = itemView.findViewById(R.id.itemuser_no);
                itemuser_name = itemView.findViewById(R.id.itemuser_name);
            }
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        @NonNull
        @Override
        public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_user,parent,false);
            return new MyviewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyviewHolder myviewHolder, int position) {
                final User user = users.get(position);
                myviewHolder.itemuser_name.setText(user.getUser_name());
                myviewHolder.itemuser_no.setText(user.getUser_no());

                myviewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("user", user);

                        Navigation.findNavController(view)
                                .navigate(R.id.action_userFragment_to_userDetailFragment, bundle);
                    }
                });
        }
    }

}