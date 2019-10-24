package com.g2.runningback.prod;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;


import com.g2.runningback.Common.Common;
import com.g2.runningback.Common.CommonTask;
import com.g2.runningback.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class ProductInsertFragment extends Fragment implements View.OnClickListener {
    private Activity activity;
    private View view;
    Button btNextPage;
    ImageView ivMain, ivOne, ivTwo, ivThree;
    EditText etProNo, etProName, etProDesc;
    AutoCompleteTextView etProCat;
    String proNo, proCat, proName, proDesc;
    int ImageNum = 1;
    byte[] img1, img2, img3;
    Uri contentUri;
    Product product;

    SharedPreferences pref;
    private final static String PREFERENCES_NAME = "preferences";

    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_PICTURE = 1;
    private static final int REQ_CROP_PICTURE = 2;

    List<Category> cateList = new ArrayList<>();
    List<String> cateStrList = new ArrayList<>();

    CommonTask checkProNameTask;
    CommonTask getCatListTask;
    private static final String url = Common.URL_SERVER + "/ProductServlet";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        pref = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(R.string.productmanagment);
        return inflater.inflate(R.layout.fragment_product_insert, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        getCateList();
        onViewHolder();
        getPref();
    }

    private void getCateList() {

        if (Common.networkConnected(activity)) {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getCateList");
            getCatListTask = new CommonTask(url, jsonObject.toString());
            try {
                String cateListStr = getCatListTask.execute().get();
                Type typeList = new TypeToken<List<Category>>() {
                }.getType();
                cateList = new Gson().fromJson(cateListStr, typeList);

                for (Category c : cateList) {
                    cateStrList.add(c.getCate_name());
                }
            } catch (Exception e) {

            }
        }

    }


    private void onViewHolder() {
        btNextPage = view.findViewById(R.id.ispro_btNextPage);
        ivMain = view.findViewById(R.id.ispro_ivMain);
        ivOne = view.findViewById(R.id.ispro_ivOne);
        ivTwo = view.findViewById(R.id.ispro_ivTwo);
        ivThree = view.findViewById(R.id.ispro_ivThree);
        etProNo = view.findViewById(R.id.ispro_etProNo);
        etProName = view.findViewById(R.id.ispro_etProName);
        etProCat = view.findViewById(R.id.ispro_etProCat);
        etProDesc = view.findViewById(R.id.ispro_etProDesc);

        ivTwo.setVisibility(View.GONE);
        ivThree.setVisibility(View.GONE);

        btNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_productInsertFragment3_to_productInsertPTFragment);
            }
        });

        ivOne.setOnClickListener(this);
        ivTwo.setOnClickListener(this);
        ivThree.setOnClickListener(this);
        ivMain.setOnClickListener(this);
        btNextPage.setOnClickListener(this);

        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>(activity, R.layout.item_view_auto_et, cateStrList);
        //建立調適器，並放入自己使用的item，使用自己做的ViewItem
        etProCat.setAdapter(arrayAdapter);
        //放入你自己所設定的調適器
        etProCat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                //建立字串，在調適器中放入被點選物件的框架在抓取標題
                Toast.makeText(activity, item, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ispro_ivOne:
                ImageNum = 1;
                ivMain.setImageDrawable(ivOne.getDrawable());
                break;
            case R.id.ispro_ivTwo:
                ImageNum = 2;
                ivMain.setImageDrawable(ivTwo.getDrawable());
                break;
            case R.id.ispro_ivThree:
                ImageNum = 3;
                ivMain.setImageDrawable(ivThree.getDrawable());
                break;
            case R.id.ispro_ivMain:
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
                                            ivThree.setImageResource(R.drawable.pro_image);
                                            break;
                                        }
                                        if (img2 != null ){
                                            img1 = img2 ;
                                            img2 =null;
                                            ivMain.setImageDrawable(ivTwo.getDrawable());
                                            ivOne.setImageDrawable(ivTwo.getDrawable());
                                            ivTwo.setImageResource(R.drawable.pro_image);
                                            ivThree.setVisibility(View.GONE);
                                            break;

                                        }

                                        if (img3 == null){
                                            img1 = null ;
                                            ivMain.setImageResource(R.drawable.pro_image);
                                            ivOne.setImageResource(R.drawable.pro_image);
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
                                            ivThree.setImageResource(R.drawable.pro_image);
                                        } else {
                                            img2 = null;
                                            ivTwo.setImageResource(R.drawable.pro_image);
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
                                            ivThree.setImageResource(R.drawable.pro_image);
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

            case R.id.ispro_btNextPage:

                proNo = etProNo.getText().toString();
                proNo = etProNo.getText().toString();
                proCat = etProCat.getText().toString();
                proName = etProName.getText().toString();
                proDesc = etProDesc.getText().toString();
                product = new Product(proNo, proCat, proName, proDesc);

                if (img1 != null) {
                    product.setPro_image(img1);
                }
                if (img2 != null) {
                    product.setPro_image2(img2);
                }
                if (img3 != null) {
                    product.setPro_image3(img3);
                }


                if (etProCat.getText().toString().trim().equals("") || etProDesc.getText().toString().trim().equals("") ||
                        etProName.getText().toString().trim().equals("") || etProNo.getText().toString().trim().equals("") || img1 == null) {
                    Common.showToast(activity, "請完成填寫資料");
                    return;
                }
                if (etProNo.getText().length() > 5) {
                    Common.showToast(activity, "商品編號過長，只能於５個英數字內");
                    return;
                }
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "checkProNo");
                jsonObject.addProperty("ProductNo", proNo);

                checkProNameTask = new CommonTask(url, jsonObject.toString());
                try {
                    String count = checkProNameTask.execute().get();
                    if (count.equals("1")) {
                        Common.showToast(activity, "商品編號重複");
                    } else {

                        pref.edit().putString("Product", new Gson().toJson(product)).apply();
                        Navigation.findNavController(view).navigate(R.id.action_productInsertFragment3_to_productInsertPTFragment);
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }


                break;

        }


    }


    @Override
    public void onPause() {
        super.onPause();


        proNo = etProNo.getText().toString();
        proCat = etProCat.getText().toString();
        proName = etProName.getText().toString();
        proDesc = etProDesc.getText().toString();
        product = new Product(proNo, proCat, proName, proDesc);


        if (img1 != null) {
            product.setPro_image(img1);
        }
        if (img2 != null) {
            product.setPro_image2(img2);
        }
        if (img3 != null) {
            product.setPro_image3(img3);
        }


        pref.edit().putString("Product", new Gson().toJson(product)).apply();


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

    private void getPref() {
        String proStr = pref.getString("Product", "null");
        if (!proStr.equals("null")) {
            Product product = new Gson().fromJson(proStr, Product.class);

            etProNo.setText(product.getPro_no());
            etProName.setText(product.getPro_name());
            etProDesc.setText(product.getPro_desc());
            etProCat.setText(product.getCat_no());
            if (product.pro_image != null) {
                img1 = product.pro_image;
                ivMain.setImageBitmap(BitmapFactory.decodeByteArray(product.pro_image, 0, product.pro_image.length));
                ivOne.setImageBitmap(BitmapFactory.decodeByteArray(product.pro_image, 0, product.pro_image.length));
                ivTwo.setVisibility(View.VISIBLE);
            }
            if (product.getPro_image2() != null) {
                img2 = product.getPro_image2();
                ivTwo.setImageBitmap(BitmapFactory.decodeByteArray(product.pro_image, 0, product.pro_image.length));
                ivThree.setVisibility(View.VISIBLE);

            }

            if (product.getPro_image3() != null) {
                img3 = product.getPro_image3();
                ivThree.setImageBitmap(BitmapFactory.decodeByteArray(product.pro_image, 0, product.pro_image.length));

            }
        }

    }


}