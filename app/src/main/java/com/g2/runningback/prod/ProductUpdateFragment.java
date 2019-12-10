package com.g2.runningback.prod;


import android.app.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;


import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;

import com.g2.runningback.Common.Common;
import com.g2.runningback.Common.CommonTask;
import com.g2.runningback.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;


public class ProductUpdateFragment extends Fragment implements View.OnClickListener {

    CommonTask commonTask;
    private static final String url = Common.URL_SERVER + "/ProductServlet";

    private ImageView ivMain, ivOne, ivTwo, ivThree;
    private EditText pro_name, pro_stock, pro_desc, pro_price, pro_info;
    private TextView pro_no, cat_no;
    private Switch swSale;
    private byte[] img1, img2, img3;

    Uri contentUri;
    int ImageNum = 1;
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_PICTURE = 1;
    private static final int REQ_CROP_PICTURE = 2;

    private Activity activity;
    private final static String TAG = "TAG_PorductUpdateFragment";
    private Product product;
    private View view;
    private Bundle bundle;
    private Gson gson;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        bundle = getArguments();
        gson = Common.getTimeStampGson();

        if (bundle != null) {
            product = (Product) bundle.getSerializable("product");
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_update, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        activity.setTitle("修改商品");
        getImageList();
        onHoldView();

    }

    private void onHoldView() {
        ivMain = view.findViewById(R.id.proUpdate_ivMain);
        ivMain.setOnClickListener(this);

        ivOne = view.findViewById(R.id.proUpdate_ivOne);
        ivOne.setOnClickListener(this);

        ivTwo = view.findViewById(R.id.proUpdate_ivTwo);
        ivTwo.setImageResource(R.drawable.pro_image2);
        ivTwo.setOnClickListener(this);

        ivThree = view.findViewById(R.id.proUpdate_ivThree);
        ivThree.setVisibility(View.GONE);
        ivThree.setOnClickListener(this);

        pro_no = view.findViewById(R.id.proUpdate_tvNo);
        pro_name = view.findViewById(R.id.proUpdate_tvName);
        pro_stock = view.findViewById(R.id.proUpdate_etStock);
        pro_desc = view.findViewById(R.id.proUpdate_tvDesc);
        pro_info = view.findViewById(R.id.proUpdate_etInfo);
        pro_price = view.findViewById(R.id.proUpdate_etPrice);
        swSale = view.findViewById(R.id.proUpdate_swSale);
        cat_no = view.findViewById(R.id.proUpdate_tvCateNo);

        if (product != null) {
            pro_no.setText(String.valueOf(product.getPro_no()));
            pro_name.setText(String.valueOf(product.getPro_name()));
            pro_stock.setText(String.valueOf(product.getPro_stock()));
            pro_price.setText(String.valueOf(product.getPro_price()));
            pro_desc.setText(product.getPro_desc());
            pro_info.setText(product.getPro_info());
            cat_no.setText(product.getCat_no());

            if (product.pro_Sale == 1) {
                swSale.setChecked(true);
            } else {
                swSale.setChecked(false);
            }

        }

        Bitmap bmImg1 = BitmapFactory.decodeByteArray(img1, 0, img1.length);
        ivMain.setImageBitmap(bmImg1);
        ivOne.setImageBitmap(bmImg1);

        if (img2 != null) {
            Bitmap bmImg2 = BitmapFactory.decodeByteArray(img2, 0, img2.length);
            ivTwo.setImageBitmap(bmImg2);
            ivThree.setVisibility(View.VISIBLE);
            ivThree.setImageResource(R.drawable.pro_image2);
        }

        if (img3 != null) {
            Bitmap bmImg3 = BitmapFactory.decodeByteArray(img3, 0, img3.length);
            ivThree.setImageBitmap(bmImg3);
        }

        Button btupdateproduct = view.findViewById(R.id.btupdateproduct);
        btupdateproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String no = pro_no.getText().toString();
                if (pro_no.length() <= 0 || pro_name.getText().toString().equals("") | pro_stock.getText().toString().equals("") | pro_price.getText().toString().equals("")
                        | pro_desc.getText().toString().equals("") | pro_info.getText().toString().equals("") | cat_no.getText().toString().equals("")) {
                    Common.showToast(activity, activity.getString(R.string.textNameIsInvalid));
                    return;
                }

                if (Integer.parseInt(pro_stock.getText().toString().trim()) < 1 && swSale.isChecked()) {
                    Common.showToast(activity, "庫存為零，無法上架商品");
                    return;
                }

                if (img1==null){
                    Common.showToast(activity, "請至少上傳一張照片");
                    return;
                }

                String name = pro_name.getText().toString().trim();
                int stock = Integer.parseInt(pro_stock.getText().toString());
                int price = Integer.parseInt(pro_price.getText().toString());
                String desc = pro_desc.getText().toString().trim();
                String info = pro_info.getText().toString().trim();

                if (Common.networkConnected(activity)) {
                    String url = Common.URL_SERVER + "ProductServlet";

                    if (swSale.isChecked()) {
                        product.setPro_Sale(1);
                    } else {
                        product.setPro_Sale(0);
                    }
                    Product nProduct = new Product(product.pro_no, product.cat_no, name, desc, price, stock, product.getPro_Sale(), info);


                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "productUpdate");
                    jsonObject.addProperty("product", new Gson().toJson(nProduct));

                    jsonObject.addProperty("image1", Base64.encodeToString(img1, Base64.DEFAULT));


                    if (img2!=null){
                        jsonObject.addProperty("image2", Base64.encodeToString(img2, Base64.DEFAULT));
                    }

                    if (img3!=null){
                        jsonObject.addProperty("image3", Base64.encodeToString(img3, Base64.DEFAULT));
                    }

                    int count = 0;
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        count = Integer.valueOf(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Common.showToast(activity, "修改失敗");
                    } else {
                        Common.showToast(activity, "修改成功");
                    }
                } else {
                    Common.showToast(activity, activity.getString(R.string.textNoNetwork));
                }
                /* 回前一個Fragment */
                Navigation.findNavController(v).popBackStack();
            }
        });

        Button btcancelproduct = view.findViewById(R.id.btcancelproduct);
        btcancelproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 回前一個Fragment */
                Navigation.findNavController(v).popBackStack();
            }
        });

    }

    private void getImageList() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getImages");
        jsonObject.addProperty("pro_no", product.getPro_no());

        try {
            commonTask = new CommonTask(url, new Gson().toJson(jsonObject));
            String imageJs = commonTask.execute().get();
            JsonObject js = gson.fromJson(imageJs, JsonObject.class);


            String image1Str = js.get("image1").getAsString();
            img1 = Base64.decode(image1Str, Base64.DEFAULT);
            String image2Str = js.get("image2").getAsString();
            img2 = Base64.decode(image2Str, Base64.DEFAULT);
            String image3Str = js.get("image3").getAsString();
            img3 = Base64.decode(image3Str, Base64.DEFAULT);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void takePic() {

        Intent itenet = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture.jpg");
        contentUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", file);
        itenet.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

        if (itenet.resolveActivity(activity.getPackageManager()) != null) {
            startActivityForResult(itenet, REQ_TAKE_PICTURE);
        } else {
            Common.showToast(getActivity(), "No Camera app Found");
        }

    }

    private void pickPic() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ_PICK_PICTURE);
    }


    private void cropPic(Uri sourceImageUri) {
        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture_cropped" + ImageNum + ".jpg");
        Uri uri = Uri.fromFile(file);
        // 開啟截圖功能
        Intent intent = new Intent("com.android.camera.action.CROP");
        // 授權讓截圖程式可以讀取資料
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // 設定圖片來源與類型
        intent.setDataAndType(sourceImageUri, "image/*");
        // 設定要截圖
        intent.putExtra("cropPic", "true");
        // 設定截圖框大小，0代表user任意調整大小
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 設定圖片輸出寬高，0代表維持原尺寸
        intent.putExtra("outputX", 0);
        intent.putExtra("outputY", 0);
        // 是否保持原圖比例
        intent.putExtra("scale", false);
        // 設定截圖後圖片位置
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        // 設定是否要回傳值
        intent.putExtra("return-data", true);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            // 開啟截圖activity
            startActivityForResult(intent, REQ_CROP_PICTURE);
        } else {

            Common.showToast(activity, "No");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case REQ_TAKE_PICTURE:
                    cropPic(contentUri);
                    break;
                case REQ_PICK_PICTURE:
                    cropPic(data.getData());
                    break;
                case REQ_CROP_PICTURE:
                    Uri uri = data.getData();
                    Bitmap bitmap = null;
                    if (uri != null) {
                        try {
                            bitmap = BitmapFactory.decodeStream(
                                    activity.getContentResolver().openInputStream(uri));
                            ivMain.setImageBitmap(bitmap);
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                            switch (ImageNum) {
                                case 1:
                                    img1 = out.toByteArray();
                                    break;
                                case 2:
                                    img2 = out.toByteArray();
                                    break;
                                case 3:
                                    img3 = out.toByteArray();
                                    break;
                            }

                        } catch (FileNotFoundException e) {
                            Log.e("Test", e.toString());
                        }
                    }
                    if (bitmap != null) {
                        ivMain.setImageBitmap(bitmap);

                        switch (ImageNum) {
                            case 1:
                                ivOne.setImageBitmap(bitmap);
                                ivTwo.setVisibility(View.VISIBLE);
                                break;
                            case 2:
                                ivTwo.setImageBitmap(bitmap);
                                ivThree.setVisibility(View.VISIBLE);
                                break;
                            case 3:
                                ivThree.setImageBitmap(bitmap);
                                break;

                        }

                    } else {
                        ivMain.setImageResource(R.drawable.ic_home);
                    }
                    break;
                default:
                    break;
            }

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.proUpdate_ivOne:
                ImageNum = 1;
                ivMain.setImageDrawable(ivOne.getDrawable());
                break;
            case R.id.proUpdate_ivTwo:
                ImageNum = 2;
                ivMain.setImageDrawable(ivTwo.getDrawable());
                break;
            case R.id.proUpdate_ivThree:
                ImageNum = 3;
                ivMain.setImageDrawable(ivThree.getDrawable());
                break;
            case R.id.proUpdate_ivMain:
                PopupMenu popupMenu = new PopupMenu(activity, v, Gravity.END);
                popupMenu.inflate(R.menu.popup_choosepic);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.pmenu_PickPic:
                                pickPic();
                                break;
                            case R.id.pmenu_TakePic:
                                takePic();
                                break;
                            case R.id.pmenu_RemovePic:
                                switch (ImageNum) {
                                    case 1:
                                        if (img1 == null) {
                                            Common.showToast(activity, "沒有照片");
                                            break;
                                        }
                                        if (img2 != null && img3 != null) {
                                            img1 = img2;
                                            img2 = img3;
                                            img3 = null;
                                            ivMain.setImageDrawable(ivTwo.getDrawable());
                                            ivOne.setImageDrawable(ivTwo.getDrawable());
                                            ivTwo.setImageDrawable(ivThree.getDrawable());
                                            ivThree.setImageResource(R.drawable.pro_image2);
                                            break;
                                        }
                                        if (img2 != null) {
                                            img1 = img2;
                                            img2 = null;
                                            ivMain.setImageDrawable(ivTwo.getDrawable());
                                            ivOne.setImageDrawable(ivTwo.getDrawable());
                                            ivTwo.setImageResource(R.drawable.pro_image2);
                                            ivThree.setVisibility(View.GONE);
                                            break;

                                        }

                                        if (img3 == null) {
                                            img1 = null;
                                            ivMain.setImageResource(R.drawable.pro_image2);
                                            ivOne.setImageResource(R.drawable.pro_image2);
                                            ivTwo.setVisibility(View.GONE);
                                            break;
                                        }
                                        break;
                                    case 2:
                                        if (img2 == null) {
                                            Common.showToast(activity, "沒有照片");
                                            break;
                                        }
                                        if (img3 != null) {
                                            img2 = img3;
                                            img3 = null;
                                            ivTwo.setImageDrawable(ivThree.getDrawable());
                                            ivThree.setImageResource(R.drawable.pro_image2);
                                        } else {
                                            img2 = null;
                                            ivTwo.setImageResource(R.drawable.pro_image2);
                                            ivThree.setVisibility(View.GONE);
                                        }
                                        ImageNum = 1;
                                        ivMain.setImageDrawable(ivOne.getDrawable());
                                        break;
                                    case 3:
                                        if (img3 == null) {
                                            Common.showToast(activity, "沒有照片");
                                            break;
                                        } else {
                                            img3 = null;
                                            ivThree.setImageResource(R.drawable.pro_image2);
                                            ivMain.setImageDrawable(ivTwo.getDrawable());
                                        }
                                        ImageNum = 2;
                                        break;
                                }
                        }
                        return true;
                    }
                });
                popupMenu.show();
                break;


        }
    }
}



