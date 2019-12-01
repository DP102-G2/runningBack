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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.g2.runningback.Common.Common;
import com.g2.runningback.Common.CommonTask;
import com.g2.runningback.MainActivity;
import com.g2.runningback.R;
import com.g2.runningback.Service.serviceCommon.ChatWebSocketClient;
import com.g2.runningback.Service.serviceCommon.ServiceCommon;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ServiceDetailFragment extends Fragment {

    private LocalBroadcastManager broadcastManager;
    private String socket_no = "0";

    MainActivity mainActivity;
    Activity activity;
    View view;
    private Gson gson;
    List<Message> messageList;

    mesAdapter adapter;
    RecyclerView rvList;
    EditText etMessage;
    ImageView btSubmit;

    Bundle bundle;
    int user_no;

    CommonTask commonTask;
    private static final String url = Common.URL_SERVER + "ServiceServlet";
    private static final String TAG = "TAG_ChatFragment";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        mainActivity = (MainActivity) getActivity();
        mainActivity.btbar.setVisibility(View.GONE);
        bundle = getArguments();
        user_no = bundle.getInt("user_no");


        broadcastManager = LocalBroadcastManager.getInstance(activity);
        registerChatReceiver();

        ServiceCommon.connectServer(activity, socket_no);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_server_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        activity.setTitle("客服_會員編號： "+user_no);
        gson = Common.getTimeStampGson();
        messageList = getMessageList();
        holdView();
        rvList.scrollToPosition(adapter.getItemCount() - 1);
    }

    @Override
    public void onResume() {
        super.onResume();
        setTextReaded();
    }

    public void setTextReaded(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "setTextReaded");
        jsonObject.addProperty("user_no", user_no);

        try {
            commonTask = new CommonTask(url, jsonObject.toString());
            String countStr = commonTask.execute().get();

        } catch (Exception e) {

        }

    }


    private void holdView() {

        etMessage = view.findViewById(R.id.serDetail_etMessage);
        btSubmit = view.findViewById(R.id.serDetail_btSubmit);
        rvList = view.findViewById(R.id.serDetail_sv);

        rvList.setLayoutManager(new LinearLayoutManager(activity));
        rvList.setAdapter(new mesAdapter(activity, messageList));
        adapter = (mesAdapter) rvList.getAdapter();


        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etMessage.getText().toString().equals("")) {
                    Common.showToast(activity, "請輸入文字訊息");
                } else {
                    Date date = new Date();
                    Timestamp msg_time = new Timestamp(date.getTime());

                    String text = etMessage.getText().toString();
                    Message message = new Message(user_no, 0, text, 0,msg_time);
                    insertMessage(message);
                    pushToSocket(message);
                }


            }
        });

        rvList.smoothScrollToPosition(adapter.getItemCount());

    }


    private class mesAdapter extends RecyclerView.Adapter<mesAdapter.messageViewHolder> {
        Activity activity;
        List<Message> messages;
        LayoutInflater layoutInflater;
        int imageSize;

        public void setMessages(List<Message> messages) {
            this.messages = messages;
        }

        public mesAdapter(Activity activity, List<Message> messageList) {
            this.activity = activity;
            this.messages = messageList;
            layoutInflater = LayoutInflater.from(activity);
            imageSize = getResources().getDisplayMetrics().widthPixels;
        }

        @NonNull
        @Override
        public mesAdapter.messageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_server_message, parent, false);

            return new messageViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull mesAdapter.messageViewHolder holder, int position) {
            final Message message = messages.get(position);
            holder.tvText.setText(message.getMsg_text());
            if (message.getMsg_by() == 1) {
                holder.tvWho.setVisibility(View.VISIBLE);
                holder.tvWho.setText("會員編號: "+message.getUser_no());
                holder.cardView.setCardBackgroundColor(activity.getColor(R.color.colorBrown));
                holder.csLayout.setTranslationX(0);

            } else {
                holder.tvWho.setVisibility(View.GONE);
                holder.cardView.setCardBackgroundColor(activity.getColor(R.color.colorPrimary));
                holder.csLayout.setTranslationX(imageSize - 800);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String formatTS = new SimpleDateFormat("訊息時間： \n yyyy年 MM月 dd日 , HH點 mm分 ss秒").format(message.getMsg_time());
                    Common.showToast(activity, formatTS);
                }
            });

        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        private class messageViewHolder extends RecyclerView.ViewHolder {
            ConstraintLayout csLayout;
            CardView cardView;
            TextView tvText,tvWho;

            public messageViewHolder(View itemView) {
                super(itemView);
                csLayout = itemView.findViewById(R.id.serDetail_item_layout);
                cardView = itemView.findViewById(R.id.serDetail_item_cardView);
                tvText = itemView.findViewById(R.id.serDetail_item_tvText);
                tvWho = itemView.findViewById(R.id.serDetail_item_tvWho);
            }
        }
    }


    private List<Message> getMessageList() {

        List<Message> messageList = new ArrayList<>();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getMessageList");
        jsonObject.addProperty("user_no", user_no);

        commonTask = new CommonTask(url, jsonObject.toString());

        try {
            String messageListStr = commonTask.execute().get();
            Type typeList = new TypeToken<List<Message>>() {
            }.getType();

            messageList = gson.fromJson(messageListStr, typeList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return messageList;
    }

    private void registerChatReceiver() {
        IntentFilter chatFilter = new IntentFilter("new Message");
        // 攔截chat廣播
        broadcastManager.registerReceiver(chatReceiver, chatFilter);
        // 如果有我們就來做CHATRECEIVER
    }

    private BroadcastReceiver chatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String jsStr = intent.getStringExtra("message");
            JsonObject jsonObject = gson.fromJson(jsStr,JsonObject.class);

            String message = jsonObject.get("message").getAsString();
            Message chatMessage = gson.fromJson(message, Message.class);

            if (chatMessage.getUser_no() == user_no) {
                messageList.add(chatMessage);
                adapter.setMessages(messageList);
                adapter.notifyDataSetChanged();
                rvList.smoothScrollToPosition(adapter.getItemCount());
                // 解析成需要顯示的字串
                setTextReaded();
                Log.d(TAG, message);
            }
        }
    };

    private void pushToSocket(Message message) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action","new Message");
        jsonObject.addProperty("message",gson.toJson(message));
        ServiceCommon.chatWebSocketClient.send(jsonObject.toString());
    }

    private void insertMessage(Message message) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "insertMessage");
        jsonObject.addProperty("message", gson.toJson(message));

        commonTask = new CommonTask(url, jsonObject.toString());

        try {
            String messageListStr = commonTask.execute().get();
            Message nMessage = gson.fromJson(messageListStr, Message.class);
            messageList.add(nMessage);
            adapter.setMessages(messageList);
            adapter.notifyDataSetChanged();
            rvList.smoothScrollToPosition(adapter.getItemCount());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Fragment頁面切換時解除註冊，但不需要關閉WebSocket，
        // 否則回到前頁好友列表，會因為斷線而無法顯示好友
        broadcastManager.unregisterReceiver(chatReceiver);
    }

}
