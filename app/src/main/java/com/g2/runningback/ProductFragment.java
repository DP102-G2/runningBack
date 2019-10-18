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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;

import com.g2.runningback.Common.Common;
import com.g2.runningback.Common.CommonTask;
import com.g2.runningback.Common.ImageTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class ProductFragment extends Fragment {
    private Activity activity;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private List<Product> products;
    private CommonTask productGetAllTask;
    private ImageTask productImageTask;
    private CommonTask productDeleteTask;
    private static final String TAG = "TAG_ProductFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(R.string.productmanagment);
        return inflater.inflate(R.layout.fragment_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchView = view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.rvproduct);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(new ProductAdapter(activity, products));

        products = getProducts();
        showProducts(products);

        //搜尋
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                ProductAdapter adapter = (ProductAdapter) recyclerView.getAdapter();
                if (adapter != null) {
                    // 如果搜尋條件為空字串，就顯示原始資料；否則就顯示搜尋後結果
                    if (newText.isEmpty()) {
                        adapter.setProducts(products);
                    } else {
                        List<Product> searchProducts = new ArrayList<>();
                        // 搜尋原始資料內有無包含關鍵字(不區別大小寫)
                        for (Product product : products) {
                            if (product.getPro_no().toUpperCase().contains(newText.toUpperCase())) {
                                searchProducts.add(product);
                            }
                        }
                        adapter.setProducts(searchProducts);
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

        ImageView ivproductadd = view.findViewById(R.id.ivproductadd);
        ivproductadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_productFragment_to_newproductFragment);
            }
        });
    }

    private class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        //Context context;
        List<Product> products;
        //  private int imageSize;


        ProductAdapter(Context context, List<Product> products) {
            layoutInflater = LayoutInflater.from(context);
            //  this.context = context;
            this.products = products;

            /* 螢幕寬度除以4當作將圖的尺寸 */
            //    imageSize = getResources().getDisplayMetrics().widthPixels / 4;

        }

        void setProducts(List<Product> products) {
            this.products = products;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView pro_no, pro_name, pro_stock;

            public MyViewHolder(View itemView) {
                super(itemView);
                pro_name = itemView.findViewById(R.id.pro_name);
                pro_no = itemView.findViewById(R.id.pro_no);
                pro_stock = itemView.findViewById(R.id.pro_stock);
            }
        }

        @Override
        public int getItemCount() {
            return products.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = layoutInflater.inflate(R.layout.item_view_productmanagement, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder viewHolder, final int postion) {
            final Product product = products.get(postion);
            viewHolder.pro_no.setText(String.valueOf(product.getPro_no()));
            viewHolder.pro_name.setText(String.valueOf(product.getPro_name()));
            viewHolder.pro_stock.setText(String.valueOf(product.getPro_stock()));

            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View view) {
                    PopupMenu popupMenu = new PopupMenu(activity, view, Gravity.END);
                    popupMenu.inflate(R.menu.popup_menu_product);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.update:
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("product", product);
                                    Navigation.findNavController(view)
                                            .navigate(R.id.action_productFragment_to_updateproductFragment, bundle);
                                    break;
                                case R.id.delete:
                                    if (Common.networkConnected(activity)) {
                                        String url = Common.URL_SERVER + "/ProductServlet";
                                        JsonObject jsonObject = new JsonObject();
                                        jsonObject.addProperty("action", "productDelete");
                                        jsonObject.addProperty("productpro_no", product.getPro_no());
                                        int count = 0;
                                        try {
                                            productDeleteTask = new CommonTask(url, jsonObject.toString());
                                            String result = productDeleteTask.execute().get();
                                            count = Integer.valueOf(result);
                                        } catch (Exception e) {
                                            Log.e(TAG, e.toString());
                                        }
                                        if (count == 0) {
                                            Common.showToast(activity, String.valueOf(R.string.textDeleteFail));
                                        } else {
                                            products.remove(product);
                                            ProductAdapter.this.notifyDataSetChanged();
                                            // 外面spots也必須移除選取的spot
                                            ProductFragment.this.products.remove(product);
                                            Common.showToast(activity, String.valueOf(R.string.textDeleteSuccess));
                                        }
                                    } else {
                                        Common.showToast(activity, String.valueOf(R.string.textNoNetwork));
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

    private List<Product> getProducts() {
        // List<Product> products = new ArrayList<>();
        List<Product> products = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "/ProductServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            productGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = productGetAllTask.execute().get();
                Type listType = new TypeToken<List<Product>>() {
                }.getType();
                products = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, String.valueOf(R.string.textNoNetwork));
        }
        return products;
    }

    // check if the device connect to the network
    private void showProducts(List<Product> products) {
        if (products == null || products.isEmpty()) {
            Common.showToast(activity, String.valueOf(R.string.textNoProductsFound));
            return;
        }
        ProductAdapter productAdapter = (ProductAdapter) recyclerView.getAdapter();
        // 如果spotAdapter不存在就建立新的，否則續用舊有的
        if (productAdapter == null) {
            recyclerView.setAdapter(new ProductAdapter(activity, products));
        } else {
            productAdapter.setProducts(products);
            productAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (productGetAllTask != null) {
            productGetAllTask.cancel(true);
            productGetAllTask = null;
        }
        if (productImageTask != null) {
            productImageTask.cancel(true);
            productImageTask = null;
        }


    }
}


