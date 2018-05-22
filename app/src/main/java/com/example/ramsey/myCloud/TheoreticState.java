package com.example.ramsey.myCloud;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TheoreticState extends AppCompatActivity {

    private Button theoretic_state_description_button;
    private Button theoretic_state_image_button;
    private EditText theoretic_state_description;
    private ImageView theoretic_state_image;
    private final static String TAG = "Theoretic State";
    private TextInputLayout theoretic_state_description_layout;
    private Button theoretic_state_image_select;

    private String prob_uid;
    private SQLiteHandler db;

    //图片相关
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CHOOSE_PHOTO = 300;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private Uri fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // file url to store image
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    //图片相关

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theoretic_state);

        Toolbar toolbar = (Toolbar) findViewById(R.id.theoretical_state_toolbar);
        toolbar.setTitle("理论状态");//设置Toolbar标题
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setSupportActionBar(toolbar);//使toolbar支持ActionBar的特性
        getSupportActionBar().setHomeButtonEnabled(true);//返回键可用

        //图片相关
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        //图片相关

        theoretic_state_description_button=(Button)findViewById(R.id.theoretical_state_description_button);
        theoretic_state_image_button=(Button)findViewById(R.id.theoretical_state_image_button);
        theoretic_state_image_select=(Button)findViewById(R.id.theoretical_state_image_select);

        theoretic_state_description=(EditText)findViewById(R.id.theoretical_state_description);
        theoretic_state_image=(ImageView) findViewById(R.id.theoretical_state_image);

        theoretic_state_description_layout=(TextInputLayout)findViewById(R.id.theoretical_state_description_Layout);

        Intent i=getIntent();
        prob_uid=i.getStringExtra("prob_uid");

        getTheoreticState();

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        final String authority = user.get("authority");

        if (authority.equals("0")){
            theoretic_state_description.setFocusable(false);
            theoretic_state_description.setFocusableInTouchMode(false);
        }


        theoretic_state_image_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authority.equals("1")){
                    if (ContextCompat.checkSelfPermission(TheoreticState.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(TheoreticState.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
                    } else {
                        openAlbum();
                    }
                }
                else{
                    Toast.makeText(TheoreticState.this, "您不是技术员，没有权限上传照片！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        theoretic_state_description_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (authority.equals("1")) {
                    if (Validate())
                    {
                        EditTheoreticState();
                    }
                }
                else{
                    Toast.makeText(TheoreticState.this, "您不是技术员，没有权限！", Toast.LENGTH_SHORT).show();
                }
            }


            public boolean Validate()
            {
                if(theoretic_state_description.getText().toString().trim().isEmpty()) {
                    theoretic_state_description_layout.setError("请填写理论状态描述");
                    return false;
            }
            else
                {
                    return true;
                }
            }
        });


        theoretic_state_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authority.equals("1")){
                    if (ContextCompat.checkSelfPermission(TheoreticState.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(TheoreticState.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                    // capture picture
                        captureImage();
//                Toast.makeText(CreateActivity.this,"拍照",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(TheoreticState.this, "您不是技术员，没有权限上传照片！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "设备不支持相机！",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }
    }

    private void openAlbum() {
        Intent intent;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        }else{
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        }
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    private void getTheoreticState() {
        final ProgressDialog pDiaglog = new ProgressDialog(this);
        pDiaglog.setMessage("请稍等");
        pDiaglog.show();
        String tag_gettheoreticstate_request = "get_theoretic_state";
        AppController.getInstance().addToRequestQueue(new StringRequest(Request.Method.POST, AppConfig.URL_CheckTheoreticState,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Get Theoretical State Response: " + response.toString());
                        pDiaglog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (error) {
                                String errorMsg = obj.getString("error_msg");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();}
                            else
                            {
                                theoretic_state_description.setText(obj.getString("state_describe").trim());
                                Glide.with(getApplicationContext()).
                                        load(obj.getString("state_image_url"))
                                        .placeholder(R.drawable.ic_loading)
                                        .error(R.drawable.ic_error_black_24dp)
                                        .into(theoretic_state_image);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                pDiaglog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                Intent intent=getIntent();
                String prob_uid=intent.getStringExtra("prob_uid");
                params.put("prob_uid", prob_uid);
                return params;
            }
        }, tag_gettheoreticstate_request);
    }
    private void EditTheoreticState() {
        final ProgressDialog pDiaglog = new ProgressDialog(this);
        pDiaglog.setMessage("请稍等");
        pDiaglog.show();
        String tag_edittheoreticstate_request = "edit_theoretic_state";
        AppController.getInstance().addToRequestQueue(new StringRequest(Request.Method.POST, AppConfig.URL_EditTheoreticState,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Edit Theoretical State Response: " + response.toString());
                        pDiaglog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (error) {
                                String errorMsg = obj.getString("error_msg");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();}
                            else
                            {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                pDiaglog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                Intent intent=getIntent();
                String prob_uid=intent.getStringExtra("prob_uid");
                params.put("prob_uid", prob_uid);
                params.put("state_describe",theoretic_state_description.getText().toString().trim());
                return params;
            }
        }, tag_edittheoreticstate_request);
    }

    public static void TheoreticStateStart(Context context, String prob_uid)
    {
        Intent intent=new Intent(context,TheoreticState.class);
        intent.putExtra("prob_uid",prob_uid);
        context.startActivity(intent);
    }

    //图片相关
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    captureImage();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }
    /**
     * Here we store the file uri as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){

            // if the result is capturing Image
            case CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {

                    // successfully captured the image
                    // launching upload activity
                    launchUploadActivity(true);


                } else if (resultCode == RESULT_CANCELED) {

                    // user cancelled Image capture
                    Toast.makeText(getApplicationContext(),
                            "User cancelled image capture", Toast.LENGTH_SHORT)
                            .show();

                } else {
                    // failed to capture image
                    Toast.makeText(getApplicationContext(),
                            "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                            .show();
                }
                break;

//            接收上传成功界面传过来的image_uid;
            case 10:
                if (resultCode ==200) {
                    String image_uid = data.getStringExtra("image_uid");
                    Log.d(TAG, "onActivityResult: "+image_uid);
                }
                break;

            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        String imagePath = null;
                        final int takeFlags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                        ContentResolver resolver = getApplicationContext().getContentResolver();
                        Uri uri = data.getData();
                        if (DocumentsContract.isDocumentUri(this, uri)) {
                            // 如果是document类型的Uri，则通过document id处理
                            String docId = DocumentsContract.getDocumentId(uri);
                            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                                String id = docId.split(":")[1]; // 解析出数字格式的id
                                String selection = MediaStore.Images.Media._ID + "=" + id;
                                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                                imagePath = getImagePath(contentUri, null);
                            }
                        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                            // 如果是content类型的Uri，则使用普通方式处理
                            imagePath = getImagePath(uri, null);
                        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                            // 如果是file类型的Uri，直接获取图片路径即可
                            imagePath = uri.getPath();
                        }
                        resolver.takePersistableUriPermission(uri, takeFlags);
                        Intent i = new Intent(TheoreticState.this, UploadActivity.class);
                        i.putExtra("Mode","2");
                        i.putExtra("filePath", imagePath);
                        i.setData(uri);
                        i.putExtra("isImage", true);
                        i.putExtra("prob_uid",prob_uid);
                        startActivity(i);
                        Log.d(TAG, "launchUploadActivity: "+imagePath);
                        finish();
                    }
                }
                break;
        }
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void launchUploadActivity(boolean isImage){
        Intent i = new Intent(TheoreticState.this, UploadActivity.class);
        i.putExtra("Mode","2");
        i.putExtra("filePath", fileUri.getPath());
        i.setData(fileUri);
        i.putExtra("isImage", isImage);
        i.putExtra("prob_uid",prob_uid);
//        startActivityForResult(i,10);
        startActivity(i);
        Log.d(TAG, "launchUploadActivity: "+fileUri.getPath());
        finish();
    }


    /**
     * ------------ Helper Methods ----------------------
     * */



    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                AppConfig.IMAGE_DIRECTORY_NAME);

        Log.d(TAG, "getOutputMediaFile: "+ mediaStorageDir);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + AppConfig.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    public void onBackPressed(){
        finish();
    }
}
