package com.g2.runningback.AD;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.g2.runningback.Common.Common;
import com.g2.runningback.Common.CommonTask;
import com.g2.runningback.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import static android.content.ContentValues.TAG;


public class PromotionFragment extends Fragment {

    private Activity activity;
    private RecyclerView recyclerView;
    private List<Promotion> promotions;
    private CommonTask promotionGetAllTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(R.string.AdProductManagment);
        return inflater.inflate(R.layout.fragment_promotion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rvPromotionProduct);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        recyclerView.setAdapter(new PromotionFragment.PromotionAdapter(activity, promotions));
        promotions = getPromotions();
        showPromotions(promotions);

        final NavController navController = Navigation.findNavController(view);

        Button btnewproductnext = view.findViewById(R.id.btAdPproduct);
        btnewproductnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.popBackStack();
            }
        });
    }

    private class PromotionAdapter extends RecyclerView.Adapter<PromotionAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        List<Promotion> promotions;


        PromotionAdapter(Context context, List<Promotion> promotions) {
            layoutInflater = LayoutInflater.from(context);
            //  this.context = context;
            this.promotions = promotions;


        }

        void setPromotions(List<Promotion> promotions) {
            this.promotions = promotions;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView pro_no;
            TextView tvnumber;

            MyViewHolder(View itemView) {
                super(itemView);
                pro_no = itemView.findViewById(R.id.proItem_tvNo);
                tvnumber=itemView.findViewById(R.id.tvnumber);
            }
        }

        @Override
        public int getItemCount() {
            return promotions.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = layoutInflater.inflate(R.layout.item_view_promotionproduct, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder viewHolder, final int postion) {
            final Promotion promotion = promotions.get(postion);
            int prom_no = (postion < 4) ? 0 + (postion + 1) : (postion + 1);
            viewHolder.tvnumber.setText(String.valueOf(prom_no));
            viewHolder.pro_no.setText(String.valueOf(promotion.getPro_no()));

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int prom_no = (postion < 4) ? 0 + (postion + 1) : (postion + 1);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("promotion", promotion);
                    bundle.putSerializable("prom_no", prom_no);
                    Navigation.findNavController(view)
                            .navigate(R.id.action_promotionFragment_to_promotionupdateFragment, bundle);
                }
            });

        }
    }

    private List<Promotion> getPromotions() {
        List<Promotion> promotions = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "/promotionServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            promotionGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = promotionGetAllTask.execute().get();
                Type listType = new TypeToken<List<Promotion>>() {
                }.getType();
                promotions = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, String.valueOf(R.string.textNoNetwork));
        }
        return promotions;
    }

    // check if the device connect to the network
    private void showPromotions(List<Promotion> promotions) {
        if (promotions == null || promotions.isEmpty()) {
            Common.showToast(activity, String.valueOf(R.string.textNoProductsFound));
            return;
        }
        PromotionAdapter promotionAdapter = (PromotionAdapter) recyclerView.getAdapter();
        // 如果spotAdapter不存在就建立新的，否則續用舊有的
        if (promotionAdapter == null) {
            recyclerView.setAdapter(new PromotionAdapter(activity, promotions));
        } else {
            promotionAdapter.setPromotions(promotions);
            promotionAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (promotionGetAllTask != null) {
            promotionGetAllTask.cancel(true);
            promotionGetAllTask = null;
        }


    }
}







