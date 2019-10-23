package com.g2.runningback.Service;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.g2.runningback.Common.Common;
import com.g2.runningback.Common.CommonTask;
import com.g2.runningback.R;
import com.g2.runningback.Service.serviceCommon.ServiceCommon;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ServerFragment extends Fragment {

    String socket_no = "0";

    RecyclerView rv;
    View view;
    Activity activity;
    List<Message> messageList;
    CommonTask commonTask;
    messageAdaper messageAdaper;
    private static final String url = Common.URL_SERVER + "ServerServlet";

    private LocalBroadcastManager broadcastManager;
    private static final String TAG = "TAG_ServerFragment";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

        broadcastManager = LocalBroadcastManager.getInstance(activity);
        registerFriendStateReceiver();

        ServiceCommon.connectServer(activity, socket_no);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_server, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        messageList = getMessageList();
        holdView();
    }

    private void holdView() {

        rv = view.findViewById(R.id.server_ryList);
        rv.setLayoutManager(new LinearLayoutManager(activity));
        rv.setAdapter(new messageAdaper(activity, messageList));
        messageAdaper = (ServerFragment.messageAdaper) rv.getAdapter();

    }


    private class messageAdaper extends RecyclerView.Adapter<messageAdaper.messageViewHolder> {

        List<Message> messages;
        Activity activity;
        LayoutInflater layoutInflater;

        public void setMessages(List<Message> messages) {
            this.messages = messages;
        }

        public messageAdaper(Activity activity, List<Message> messageList) {
            this.activity = activity;
            messages = messageList;
            layoutInflater = LayoutInflater.from(activity);
        }

        @NonNull
        @Override
        public messageAdaper.messageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View itemView = layoutInflater.inflate(R.layout.item_view_server_list, parent, false);

            return new messageViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull messageAdaper.messageViewHolder holder, int position) {

            final Message message = messages.get(position);
            holder.tvUserNo.setText(String.valueOf(message.getUser_no()));
            holder.tvNoRead.setText(String.valueOf(message.getMsg_read()));

            if (message.msg_read == 0) {
                holder.cardView.setCardBackgroundColor(activity.getColor(R.color.colorBrown));
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("user_no", message.getUser_no());
                    Navigation.findNavController(v).navigate(R.id.action_serverFragment_to_serverDetailFragment, bundle);
                }
            });
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        private class messageViewHolder extends RecyclerView.ViewHolder {
            TextView tvUserNo, tvNoRead;
            CardView cardView;

            public messageViewHolder(View itemView) {
                super(itemView);
                tvNoRead = itemView.findViewById(R.id.seritem_tvMessage);
                tvUserNo = itemView.findViewById(R.id.seritem_tvUserNo);
                cardView = itemView.findViewById(R.id.seritem_cardView);
            }
        }
    }

    private List<Message> getMessageList() {
        List<Message> messages = new ArrayList<>();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getServerList");

        commonTask = new CommonTask(url, jsonObject.toString());

        try {

            String messageListStr = commonTask.execute().get();
            Type typeList = new TypeToken<List<Message>>() {
            }.getType();
            messages = new Gson().fromJson(messageListStr, typeList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return messages;
    }

    // 攔截user連線或斷線的Broadcast
    // 廣播接收器
    private void registerFriendStateReceiver() {
        IntentFilter openFilter = new IntentFilter("open");
        IntentFilter closeFilter = new IntentFilter("close");
        // 如果收到"OPEN"或"close"

        broadcastManager.registerReceiver(friendStateReceiver, openFilter);
        broadcastManager.registerReceiver(friendStateReceiver, closeFilter);
        // 接收器，如果收到上面說的東西，就操作friendStateReceiver
    }

    // 自訂的接收器method
    // 攔截user連線或斷線的Broadcast，並在RecyclerView呈現
    private BroadcastReceiver friendStateReceiver = new BroadcastReceiver() {

        // 只要收到廣播就會自動被呼叫
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            // 紀錄轉收的資訊，目前是記錄每個人是否上下線，我應該只要提醒app重新整理recyclerView即可
            // 範例是網端紀錄的每個人上下線狀況，我們不須這麼複雜，只需要提供提醒事項即可

            // 根據得到的TYPE判斷下列要做什麼事情
            switch (message) {
                // 有user連線
                case "newMessage":
                    messageList = getMessageList();
                    messageAdaper.setMessages(messageList);
                    messageAdaper.notifyDataSetChanged();
                    break;
            }


            Log.d(TAG, message);
        }
    };


}
