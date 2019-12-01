package com.g2.runningback.AD;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
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

import com.g2.runningback.Common.Common;
import com.g2.runningback.Common.CommonTask;
import com.g2.runningback.Common.ImageTask;
import com.g2.runningback.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

import static android.app.Activity.RESULT_OK;


public class AdupdateFragment extends Fragment {

    private Activity activity;
    private EditText proItem_tvNo;
    private ImageView ad_image;
    private final static String TAG = "TAG_SpotUpdateFragment";
    private Adproduct adproduct;
    private byte[] image;
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_IMAGE = 1;
    private static final int REQ_CROP_PICTURE = 2;
    private Uri contentUri, croppedImageUri;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(R.string.AdProductManagment);
        return inflater.inflate(R.layout.fragment_adupdate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        proItem_tvNo = view.findViewById(R.id.proItem_tvNo);
        ad_image = view.findViewById(R.id.ad_image);

        final NavController navController = Navigation.findNavController(view);
        Bundle bundle = getArguments();
        if (bundle == null || bundle.getSerializable("adproduct") == null) {
            Common.showToast(activity, R.string.textNoAdproductsFound);
            navController.popBackStack();
            return;
        }
        adproduct = (Adproduct) bundle.getSerializable("adproduct");
        showAdproduct();

        ad_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(activity, v, Gravity.END);
                popupMenu.inflate(R.menu.popup_adproduct);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.take:
                                Intent intenttake = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                // 指定存檔路徑
                                File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                                file = new File(file, "picture.jpg");
                                contentUri = FileProvider.getUriForFile(
                                        activity, activity.getPackageName() + ".provider", file);
                                intenttake.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

                                if (intenttake.resolveActivity(activity.getPackageManager()) != null) {
                                    startActivityForResult(intenttake, REQ_TAKE_PICTURE);
                                } else {
                                    Common.showToast(activity, R.string.textNoCameraApp);
                                }

                                break;
                            case R.id.pick:
                                Intent intent = new Intent(Intent.ACTION_PICK,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent, REQ_PICK_IMAGE);
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
//                return true;
            }
        });


        Button btFinishUpdate = view.findViewById(R.id.btadupdate);
        btFinishUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String proItem_No = proItem_tvNo.getText().toString();
                if (proItem_No.length() <= 0) {
                    Common.showToast(activity, R.string.textNameIsInvalid);
                    return;
                }
                Bundle bundle = getArguments();
                int ad_no = bundle.getInt("ad_no");
                if (Common.networkConnected(activity)) {
                    String url = Common.URL_SERVER + "adproductServlet";
                    adproduct.setFields(proItem_No,ad_no);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "adproductUpdate");
                    jsonObject.addProperty("adproduct", new Gson().toJson(adproduct));
                    // 有圖才上傳
                    if (image != null) {
                        jsonObject.addProperty("imageBase64", Base64.encodeToString(image, Base64.DEFAULT));
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
                    Common.showToast(activity, R.string.textNoNetwork);
                }
                /* 回前一個Fragment */
                navController.popBackStack();
            }
        });

        Button btCancel = view.findViewById(R.id.btadupdatecancel);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 回前一個Fragment */
                navController.popBackStack();
            }
        });
    }

    private void showAdproduct() {
        Bundle bundle = getArguments();
        int ad_no = bundle.getInt("ad_no");
        String url = Common.URL_SERVER + "adproductServlet";
        int imageSize = getResources().getDisplayMetrics().widthPixels / 3;
        Bitmap bitmap = null;
        try {
            bitmap = new ImageTask(url, ad_no, imageSize).execute().get();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (bitmap != null) {
            ad_image.setImageBitmap(bitmap);
        } else {
            ad_image.setImageResource(R.drawable.pro_image);
        }

        proItem_tvNo.setText(String.valueOf(adproduct.getPro_no()));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_TAKE_PICTURE:
                    crop(contentUri);
                    break;
                case REQ_PICK_IMAGE:
                    Uri uri = intent.getData();
                    crop(uri);
                    break;
                case REQ_CROP_PICTURE:
                    Log.d(TAG, "REQ_CROP_PICTURE: " + croppedImageUri.toString());
                    try {
                        Bitmap picture = BitmapFactory.decodeStream(
                                activity.getContentResolver().openInputStream(croppedImageUri));
                        ad_image.setImageBitmap(picture);
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        picture.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        image = out.toByteArray();
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, e.toString());
                    }
                    break;
            }
        }
    }

    private void crop(Uri sourceImageUri) {
        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture_cropped.jpg");
        croppedImageUri = Uri.fromFile(file);
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // the recipient of this Intent can read soruceImageUri's data
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // set image source Uri and type
            cropIntent.setDataAndType(sourceImageUri, "image/*");
            // send crop message
            cropIntent.putExtra("crop", "true");
            // aspect ratio of the cropped area, 0 means user define
            cropIntent.putExtra("aspectX", 0); // this sets the max width
            cropIntent.putExtra("aspectY", 0); // this sets the max height
            // output with and height, 0 keeps original size
            cropIntent.putExtra("outputX", 0);
            cropIntent.putExtra("outputY", 0);
            // whether keep original aspect ratio
            cropIntent.putExtra("scale", true);
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, croppedImageUri);
            // whether return data by the intent
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, REQ_CROP_PICTURE);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Common.showToast(activity, "This device doesn't support the crop action!");
        }
    }
}