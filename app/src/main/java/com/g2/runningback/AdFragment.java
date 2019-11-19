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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.g2.runningback.Common.Common;
import com.g2.runningback.Common.CommonTask;
import com.g2.runningback.Common.ImageTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import static android.content.ContentValues.TAG;


public class AdFragment extends Fragment {

    private Activity activity;
    private RecyclerView recyclerView;
    private List<Adproduct> adproducts;
    private CommonTask adproductGetAllTask;
    private ImageTask adproductImageTask;
    MainActivity mainActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(R.string.AdProductManagment);
        return inflater.inflate(R.layout.fragment_ad, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity.btbar.setVisibility(View.VISIBLE);
        recyclerView = view.findViewById(R.id.rvAdProduct);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(new AdFragment.AdproductAdapter(activity, adproducts));
        adproducts = getAdproducts();
        showAdproducts(adproducts);
        Button btnewproductnext = view.findViewById(R.id.btPromotionProduct);
        btnewproductnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_adFragment_to_promotionFragment);
            }
        });

    }

    private class AdproductAdapter extends RecyclerView.Adapter<AdproductAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        List<Adproduct> adproducts;
        private int imageSize;


        AdproductAdapter(Context context, List<Adproduct> adproducts) {
            layoutInflater = LayoutInflater.from(context);
            //  this.context = context;
            this.adproducts = adproducts;

            /* 螢幕寬度除以4當作將圖的尺寸 */
            imageSize = getResources().getDisplayMetrics().widthPixels;

        }

        void setAdproducts(List<Adproduct> adproducts) {
            this.adproducts = adproducts;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView pro_no;
            ImageView ad_image;

            MyViewHolder(View itemView) {
                super(itemView);
                pro_no = itemView.findViewById(R.id.proItem_tvNo);
                ad_image = itemView.findViewById(R.id.ad_image);
            }
        }

        @Override
        public int getItemCount() { return adproducts.size(); }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = layoutInflater.inflate(R.layout.item_view_adproduct, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder viewHolder, final int postion) {
            final Adproduct adproduct = adproducts.get(postion);
            int ad_no = (postion < 3)? 0+(postion+1) : (postion+1);
            String url = Common.URL_SERVER + "adproductServlet";
            adproductImageTask = new ImageTask(url, ad_no, imageSize, viewHolder.ad_image);
            adproductImageTask.execute();
            viewHolder.pro_no.setText(String.valueOf(adproduct.getPro_no()));

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int ad_no = (postion < 3) ? 0 + (postion + 1) : (postion + 1);

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("adproduct", adproduct);
                    bundle.putSerializable("ad_no", ad_no);
                    Navigation.findNavController(view)
                            .navigate(R.id.action_adFragment_to_adupdateFragment, bundle);
                }
            });

        }
    }

    private List<Adproduct> getAdproducts() {
        List<Adproduct> adproducts = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "/adproductServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            adproductGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = adproductGetAllTask.execute().get();
                Type listType = new TypeToken<List<Adproduct>>() {
                }.getType();
                adproducts = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, String.valueOf((R.string.textNoNetwork)));
        }
        return adproducts;
    }

    // check if the device connect to the network
    private void showAdproducts(List<Adproduct> adproducts) {
        if (adproducts == null || adproducts.isEmpty()) {
            Common.showToast(activity, String.valueOf(R.string.textNoProductsFound));
            return;
        }
        AdproductAdapter adproductAdapter = (AdproductAdapter) recyclerView.getAdapter();
        // 如果spotAdapter不存在就建立新的，否則續用舊有的
        if (adproductAdapter == null) {
            recyclerView.setAdapter(new AdproductAdapter(activity, adproducts));
        } else {
            adproductAdapter.setAdproducts(adproducts);
            adproductAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adproductGetAllTask != null) {
            adproductGetAllTask.cancel(true);
            adproductGetAllTask = null;
        }
        if (adproductImageTask != null) {
            adproductImageTask.cancel(true);
            adproductImageTask = null;
        }


    }
}
