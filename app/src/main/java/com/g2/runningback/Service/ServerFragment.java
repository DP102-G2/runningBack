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
import android.widget.Adapter;
import android.widget.TextView;

import com.g2.runningback.Common.Common;
import com.g2.runningback.Common.CommonTask;
import com.g2.runningback.MainActivity;
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

    MainActivity mainActivity;

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
        registerChatReceiver();

        ServiceCommon.connectServer(activity, socket_no);
        mainActivity = (MainActivity) getActivity();


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
        mainActivity.btbar.setVisibility(View.VISIBLE);
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
            holder.tvUserNo.setText("會編: "+message.getUser_no());
            holder.tvNoRead.setText("未讀: "+message.getMsg_read()+"則");

            if (message.msg_read == 0) {
                holder.cardView.setCardBackgroundColor(activity.getColor(R.color.colorBrown));
            }else {
                holder.cardView.setCardBackgroundColor(activity.getColor(R.color.colorPrimary));
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

    private void registerChatReceiver() {
        IntentFilter chatFilter = new IntentFilter("new Message");
        // 攔截chat廣播
        broadcastManager.registerReceiver(chatReceiver, chatFilter);
        // 如果有我們就來做CHATRECEIVER
    }



    private BroadcastReceiver chatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            // 拿到完整的資料
            messageList=getMessageList();
            messageAdaper.setMessages(messageList);
            messageAdaper.notifyDataSetChanged();
            rv.smoothScrollToPosition(1);
            // 解析成需要顯示的字串
            Log.d(TAG, message);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Fragment頁面切換時解除註冊，但不需要關閉WebSocket，
        // 否則回到前頁好友列表，會因為斷線而無法顯示好友
        broadcastManager.unregisterReceiver(chatReceiver);
    }

}
