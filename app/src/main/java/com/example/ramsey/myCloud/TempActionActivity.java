package com.example.ramsey.myCloud;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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

public class TempActionActivity extends AppCompatActivity {
    private Spinner locationspinner;
    private final static String TAG = "TempActionActivity";
    private EditText editText_tempsolution_solution;
    private EditText editText_tempsolution_feedback;
    private Spinner isdonespinner;
    private Button button_tempsolution_image;
    private Button button_tempsolution_delete;
    private Button button_tempsolution_edit;
    private ImageView temp_solution_imageView;
    private SessionManager session;
    private SQLiteHandler db;
    private String temp_solution_uid;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private Uri fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // file url to store image
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_action);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_temp_action);
        toolbar.setTitle("编辑临时措施");//设置Toolbar标题
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setSupportActionBar(toolbar);//使toolbar支持ActionBar的特性

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        Intent intent=getIntent();
        temp_solution_uid=intent.getStringExtra("tempsolution_uid");

        locationspinner=(Spinner)findViewById(R.id.temp_action_location_spinner);
        List<String> location=new ArrayList<>();
        location.add("车身");
        location.add("车头");
        location.add("车尾");
        //add hint as last item
        location.add("请填写");
        simpleArrayAdapter adapter=new simpleArrayAdapter(this,android.R.layout.simple_spinner_item,location);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationspinner.setAdapter(adapter);
        locationspinner.setSelection(location.size()-1,true);

        isdonespinner=(Spinner)findViewById(R.id.temp_action_isdone_spinner);
        List<String> isdone=new ArrayList<>();
        isdone.add("0");
        isdone.add("1");
        isdone.add("请选择");
        simpleArrayAdapter adapter2=new simpleArrayAdapter(this,android.R.layout.simple_spinner_item,isdone);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        isdonespinner.setAdapter(adapter2);
        isdonespinner.setSelection(isdone.size()-1,true);

        button_tempsolution_delete=(Button)findViewById(R.id.temp_action_delete_button_1);
        button_tempsolution_edit=(Button)findViewById(R.id.temp_action_button_all);
        button_tempsolution_image=(Button)findViewById(R.id.temp_action_image_button_1);

        temp_solution_imageView=(ImageView)findViewById(R.id.temp_feedback_image);

        editText_tempsolution_solution=(EditText)findViewById(R.id.temp_action_action_1);
        editText_tempsolution_feedback=(EditText)findViewById(R.id.temp_action_response_1);

        gettempactiondetail();

        db = new SQLiteHandler(getApplicationContext());
        final HashMap<String, String> user = db.getUserDetails();
        String authority = user.get("authority");
        if(authority.equals("0"))
        {
            editText_tempsolution_solution.setKeyListener(null);
            editText_tempsolution_solution.setEnabled(false);
            locationspinner.setClickable(false);
            locationspinner.setEnabled(false);
            isdonespinner.setClickable(false);
            locationspinner.setEnabled(false);
            button_tempsolution_delete.setEnabled(false);
            button_tempsolution_delete.setVisibility(View.INVISIBLE);
        }
            button_tempsolution_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edittempaction();
                }
            });
            button_tempsolution_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(TempActionActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(TempActionActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        // capture picture
                        captureImage();
//                Toast.makeText(CreateActivity.this,"拍照",Toast.LENGTH_SHORT).show();
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
            button_tempsolution_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(TempActionActivity.this);
                        normalDialog.setIcon(R.drawable.logo);
                        normalDialog.setTitle("确认删除？");
                        normalDialog.setMessage("是否确认删除该条临时措施?");
                        normalDialog.setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deletetempaction();
                                        finish();
                                    }
                                });
                        normalDialog.setNegativeButton("关闭",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                        // 显示
                        normalDialog.show();
                    }
                });

    }

    private void gettempactiondetail() {
        final ProgressDialog pDiaglog = new ProgressDialog(this);
        pDiaglog.setMessage("请稍等");
        pDiaglog.show();
        String tag_getactiondetail_request = "get_temp_action_detail";
        AppController.getInstance().addToRequestQueue(new StringRequest(Request.Method.POST, AppConfig.URL_TempSolutionDetail,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Get Temp action DetailResponse: " + response.toString());
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
                                editText_tempsolution_solution.setText(obj.getString("tempsolution").trim());
                                editText_tempsolution_feedback.setText(obj.getString("feedback").trim());
                                setSpinnerItemSelectedByValue(locationspinner,obj.getString("section").trim());
                                setSpinnerItemSelectedByValue(isdonespinner,obj.getString("isdone").trim());
                                Glide.with(getApplicationContext()).
                                        load(obj.getString("feedback_image_url"))
                                        .placeholder(R.drawable.ic_loading)
                                        .error(R.drawable.ic_error_black_24dp)
                                        .override(100,75)
                                        .into(temp_solution_imageView);
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
                params.put("tempsolution_uid", temp_solution_uid);
                return params;
            }
        }, tag_getactiondetail_request);
    }
    private void deletetempaction() {
        final ProgressDialog pDiaglog = new ProgressDialog(this);
        pDiaglog.setMessage("请稍等");
        pDiaglog.show();
        String tag_delete_temp_action_detail_request = "delete_temp_action_detail";
        AppController.getInstance().addToRequestQueue(new StringRequest(Request.Method.POST, AppConfig.URL_DeleteTempSolution,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Get Temp action DetailResponse: " + response.toString());
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
                                Toast.makeText(getApplicationContext(),"您已成功删除",Toast.LENGTH_LONG).show();
                                finish();
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
                params.put("tempsolution_uid", temp_solution_uid);
                return params;
            }
        }, tag_delete_temp_action_detail_request);
    }
    private void edittempaction() {
        final ProgressDialog pDiaglog = new ProgressDialog(this);
        pDiaglog.setMessage("请稍等");
        pDiaglog.show();
        String tag_edit_temp_action_detail_request = "edit_temp_action_detail";
        AppController.getInstance().addToRequestQueue(new StringRequest(Request.Method.POST, AppConfig.URL_EditTempSolution,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Edit Temp action DetailResponse: " + response.toString());
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
                                Toast.makeText(getApplicationContext(),"保存成功！",Toast.LENGTH_LONG).show();
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
                params.put("tempsolution_uid", temp_solution_uid);
                params.put("tempsolution",editText_tempsolution_solution.getText().toString().trim());
                params.put("feedback",editText_tempsolution_feedback.getText().toString().trim());
                params.put("section",locationspinner.getSelectedItem().toString().trim());
                params.put("isdone",isdonespinner.getSelectedItem().toString().trim());
                return params;
            }
        }, tag_edit_temp_action_detail_request);
    }
    public  void setSpinnerItemSelectedByValue(Spinner spinner,String value){
        SpinnerAdapter apsAdapter= spinner.getAdapter(); //得到SpinnerAdapter对象
        int k= apsAdapter.getCount();
        for(int i=0;i<k;i++){
            if(value.equals(apsAdapter.getItem(i).toString())){
                spinner.setSelection(i);// 默认选中项
                break;
            }
        }
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

//            接收上传成功界面传过来的image_uid;
            case 10:
                if (resultCode ==200) {
                    String image_uid = data.getStringExtra("image_uid");
                    Log.d(TAG, "onActivityResult: "+image_uid);
                }

        }
    }
    private void launchUploadActivity(boolean isImage){
        Intent i = new Intent(TempActionActivity.this, UploadActivity.class);
        i.putExtra("Mode","4");
        i.putExtra("filePath", fileUri.getPath());
        i.setData(fileUri);
        i.putExtra("isImage", isImage);
        i.putExtra("temp_solution_uid",temp_solution_uid);
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
    public static void TempActionStart(Context context, String temp_solution_uid)
    {
        Intent intent=new Intent(context,TempActionActivity.class);
        intent.putExtra("tempsolution_uid",temp_solution_uid);
        context.startActivity(intent);
    }
}
