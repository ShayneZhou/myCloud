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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import org.w3c.dom.Text;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class ActionActivity extends AppCompatActivity {

    private Spinner locationspinner;
    private final static String TAG = "ActionActivity";
    private EditText edittext_action_action;
    private EditText edittext_action_performence;
    private EditText edittext_action_response;
    private Button button_action_edit;
    public String misdone;
    public Button deletebutton;
    public Button button_action_upload;
    public Button button_action_capture;
    private ImageView imageView;
    private SessionManager session;
    private SQLiteHandler db;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;

    public static final int MEDIA_TYPE_IMAGE = 1;

    private String solution_uid;


    private Uri fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // file url to store image

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_action);
        toolbar.setTitle("编辑措施");//设置Toolbar标题
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setSupportActionBar(toolbar);//使toolbar支持ActionBar的特性

        Intent intent1=getIntent();
        final String solution_uid=intent1.getStringExtra("action_uid");

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();


        locationspinner=(Spinner)findViewById(R.id.action_location_spinner);
        //定义Spinner
        //定义工段数组
        List<String>location=new ArrayList<>();
        location.add("车身");
        location.add("车头");
        location.add("车尾");
        //add hint as last item
        location.add("请填写");
        simpleArrayAdapter adapter=new simpleArrayAdapter(this,android.R.layout.simple_spinner_item,location);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationspinner.setAdapter(adapter);
        locationspinner.setSelection(location.size()-1,true);
        //实例化EditText
        edittext_action_action=(EditText)findViewById(R.id.action_action_1);
        edittext_action_performence=(EditText)findViewById(R.id.action_performance_1);
        edittext_action_response=(EditText)findViewById(R.id.action_response_1);

        //实例化Button
        button_action_edit=(Button)findViewById(R.id.action_button_1);
        button_action_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editaction();
            }
        });
        deletebutton=(Button)findViewById(R.id.action_delete_button_1);
        deletebutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final AlertDialog.Builder normalDialog =new AlertDialog.Builder(ActionActivity.this);
                normalDialog.setIcon(R.drawable.logo);
                normalDialog.setTitle("确认删除？");
                normalDialog.setMessage("是否确认删除该条原因?");
                normalDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteaction();
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

        button_action_capture = (Button)findViewById(R.id.action_btn_capture);

        button_action_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ActionActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ActionActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    // capture picture
                    captureImage();
                }
            }
        });

        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "设备不支持相机！",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }


        button_action_upload = (Button)findViewById(R.id.action_btn_upload);

        button_action_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportFeedback();
            }
        });

        imageView=(ImageView)findViewById(R.id.feedback_image);
        getactiondetail();

        session = new SessionManager(getApplicationContext());
        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
        final HashMap<String, String> user = db.getUserDetails();
        String authority = user.get("authority");

        if (authority.equals("0")) {

            locationspinner.setEnabled(false);

            button_action_edit.setVisibility(View.INVISIBLE);
            deletebutton.setVisibility(View.INVISIBLE);


            edittext_action_action.setFocusable(false);
            edittext_action_action.setFocusableInTouchMode(false);

            edittext_action_performence.setFocusable(false);
            edittext_action_performence.setFocusableInTouchMode(false);

        }
        else{
            button_action_upload.setVisibility(View.INVISIBLE);
            button_action_capture.setVisibility(View.INVISIBLE);

            edittext_action_response.setFocusable(false);
            edittext_action_response.setFocusableInTouchMode(false);
        }

    }

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

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private void reportFeedback(){
        final ProgressDialog pDiaglog = new ProgressDialog(this);
        pDiaglog.setMessage("请稍等");
        pDiaglog.show();
        String tag_action_upload = "action_report_feedback";
        AppController.getInstance().addToRequestQueue(new StringRequest(Request.Method.POST, AppConfig.URL_Report_Feedback,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Report Response: " + response.toString());
                        pDiaglog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (!error) {
                                Toast.makeText(ActionActivity.this,"上传反馈成功",Toast.LENGTH_SHORT).show();
                            } else {
                                String errorMsg = obj.getString("error_msg");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_SHORT).show();
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

                Intent intent1=getIntent();
                String solutionuid=intent1.getStringExtra("action_uid");
                String response=edittext_action_response.getText().toString().trim();
                params.put("solution_uid",solutionuid);
                params.put("feedback",response);
                return params;
            }
        }, tag_action_upload);
    }


    private void editaction() {
        final ProgressDialog pDiaglog = new ProgressDialog(this);
        pDiaglog.setMessage("请稍等");
        pDiaglog.show();
        String tag_action_upload = "action_upload";
        AppController.getInstance().addToRequestQueue(new StringRequest(Request.Method.POST, AppConfig.URL_EditAction,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Action Response: " + response.toString());
                        pDiaglog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (!error) {
                                    Toast.makeText(ActionActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                            } else {
                                String errorMsg = obj.getString("error_msg");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_SHORT).show();
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

                Intent intent1=getIntent();
                String solutionuid=intent1.getStringExtra("action_uid");
                String action = edittext_action_action.getText().toString().trim();
                String performence = edittext_action_performence.getText().toString().trim();
                String response=edittext_action_response.getText().toString().trim();
                String location=locationspinner.getSelectedItem().toString().trim();
                params.put("solution_uid",solutionuid);
                params.put("solution",action);
                params.put("performance",performence);
                params.put("feedback",response);
                params.put("section",location);
                params.put("isdone",misdone);
                return params;
            }
        }, tag_action_upload);
    }


    private void getactiondetail() {
        final ProgressDialog pDiaglog = new ProgressDialog(this);
        pDiaglog.setMessage("请稍等");
        pDiaglog.show();
        String tag_getactiondetail_request = "get_action_detail";
        AppController.getInstance().addToRequestQueue(new StringRequest(Request.Method.POST, AppConfig.URL_SolutionDetail,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Get Action DetailResponse: " + response.toString());
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
                                edittext_action_action.setText(obj.getString("solution").trim());
                                edittext_action_response.setText(obj.getString("feedback").trim());
                                edittext_action_performence.setText(obj.getString("performance").trim());
                                setSpinnerItemSelectedByValue(locationspinner,obj.getString("section").trim());
                                Glide.with(getApplicationContext()).
                                        load(obj.getString("feedback_image_url"))
                                        .placeholder(R.drawable.ic_loading)
                                        .error(R.drawable.ic_error_black_24dp)
                                        .override(100,75)
                                        .into(imageView);
                                misdone=obj.getString("isdone").trim();
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
                String solution_uid=intent.getStringExtra("action_uid");
                params.put("solution_uid", solution_uid);
                return params;
            }
        }, tag_getactiondetail_request);
    }

    private void deleteaction() {
        final ProgressDialog pDiaglog = new ProgressDialog(this);
        pDiaglog.setMessage("请稍等");
        pDiaglog.show();
        String tag_action_delete = "action_delete";
        AppController.getInstance().addToRequestQueue(new StringRequest(Request.Method.POST, AppConfig.URL_DeleteAction,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Action Response: " + response.toString());
                        pDiaglog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (!error) {
                                Toast.makeText(ActionActivity.this,"删除成功",Toast.LENGTH_LONG);
                                finish();
                            } else {
                                String errorMsg = obj.getString("error_msg");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
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
                Intent intent1=getIntent();
                String solutionuid=intent1.getStringExtra("action_uid");
                params.put("solution_uid",solutionuid);
                return params;
            }
        }, tag_action_delete);
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

    public static void ActionActivityStart(Context context,String action_uid)
    {
        Intent intent=new Intent(context,ActionActivity.class);
        intent.putExtra("action_uid",action_uid);
        context.startActivity(intent);
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
        Intent intent=getIntent();
        String solution_uid=intent.getStringExtra("action_uid");
        Log.d(TAG, "onSaveInstanceState: "+solution_uid);
        outState.putString("solution_uid",solution_uid);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
        solution_uid = savedInstanceState.getString("solution_uid");
        Log.d(TAG, "onRestoreInstanceState: "+solution_uid);
    }



    /**
     * Receiving activity result method will be called after closing the camera
     * */
    @Override
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
                            "您取消了拍照", Toast.LENGTH_SHORT)
                            .show();

                } else {
                    // failed to capture image
                    Toast.makeText(getApplicationContext(),
                            "未捕获到照片", Toast.LENGTH_SHORT)
                            .show();
                }

        }
    }

    private void launchUploadActivity(boolean isImage){
        Intent intent1=getIntent();
        final String solution_uid=intent1.getStringExtra("action_uid");
        Intent i = new Intent(ActionActivity.this, UploadActivity.class);
        i.putExtra("filePath", fileUri.getPath());
        i.putExtra("solution_uid",solution_uid);
        Log.d(TAG, "launchUploadActivity: "+solution_uid);
        i.setData(fileUri);
        i.putExtra("isImage", isImage);
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
                Log.d(TAG, "创建 "
                        + AppConfig.IMAGE_DIRECTORY_NAME + " 地址失败");
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
}
