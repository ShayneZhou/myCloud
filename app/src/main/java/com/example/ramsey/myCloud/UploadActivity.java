package com.example.ramsey.myCloud;

import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;



import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UploadActivity extends AppCompatActivity {

    private static final String TAG = UploadActivity.class.getSimpleName();

    private ProgressBar progressBar;
    private String filePath = null;
    private TextView txtPercentage;
    private Uri fileUri;
    private ImageView imgPreview;
    private Button btnUpload;
    private String prob_uid;
    private String Mode;
    private String temp_solution_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tl_custom);
        toolbar.setTitle("照片上传！");//设置Toolbar标题
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setSupportActionBar(toolbar);//使toolbar支持ActionBar的特性
        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imgPreview = (ImageView) findViewById(R.id.imgPreview);



        // Receiving the data from previous activity
        final Intent i = getIntent();

        if (i.getStringExtra("solution_uid") != null){
            String solution_uid = i.getStringExtra("solution_uid");
        }

        // image or video path that is captured in previous activity
        filePath = i.getStringExtra("filePath");

        fileUri = i.getData();

        Mode=i.getStringExtra("Mode");

        Log.d(TAG, "onCreate: "+filePath);

        // boolean flag to identify the media type, image or video
        boolean isImage = i.getBooleanExtra("isImage", true);

        if (filePath != null) {
            // Displaying the image or video on the screen
            previewMedia(isImage);
        } else {
            Toast.makeText(getApplicationContext(),
                    "文件路径缺失！", Toast.LENGTH_LONG).show();
        }



        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                new UploadFileToServer().execute();

                if (filePath != null) {
                    if(Mode.equals("1")) {
                        imageUpload(filePath, fileUri);
                    }
                    if(Mode.equals("2"))
                    {
                        prob_uid=i.getStringExtra("prob_uid");
                        theoreticstateimageUpload(filePath,fileUri,prob_uid);
                        TheoreticState.TheoreticStateStart(UploadActivity.this,prob_uid);
                    }
                    if(Mode.equals("3"))
                    {
                        prob_uid=i.getStringExtra("prob_uid");
                        improvestateimageUpload(filePath,fileUri,prob_uid);
                        ImproveState.ImproveStateStart(UploadActivity.this,prob_uid);
                    }
                    if(Mode.equals("4"))
                    {
                        temp_solution_uid=i.getStringExtra("temp_solution_uid");
                        tempsolutionimageUpload(filePath,fileUri,temp_solution_uid);
                        TempActionActivity.TempActionStart(UploadActivity.this,temp_solution_uid);
                    }

                Intent i = getIntent();

                if (filePath != null && i.getStringExtra("solution_uid") == null) {
                    imageUpload(filePath,fileUri);
                }
                else if (filePath != null && i.getStringExtra("solution_uid") != null){
                    imageUpload_feedback(filePath,fileUri,i.getStringExtra("solution_uid"));
                }
                else {
                    Toast.makeText(getApplicationContext(), "未选择照片！", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    @Override
    public void onBackPressed(){
        if(Mode.equals("1")) {
            Intent j = new Intent(UploadActivity.this, CreateActivity.class);
            startActivity(j);
            finish();
        }
        if(Mode.equals("2"))
        {
            finish();
        }
        if(Mode.equals("3"))
        {

            finish();
        }
    }

    /**
     * Displaying captured image/video on the screen
     * */
    private void previewMedia(boolean isImage) {
        // Checking whether captured media is image or video
        if (isImage) {
            imgPreview.setVisibility(View.VISIBLE);
            // bitmap factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

            imgPreview.setImageBitmap(bitmap);
        }
    }




    private void imageUpload(final String imagePath,final Uri imageUri) {

        // loading or check internet connection or something...
        // ... then
//        String prob_image_uid = null;

        // Tag used to cancel the request
        String tag_string_req = "req_register";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.FILE_UPLOAD_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Create Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String prob_image_uid = jObj.getString("prob_image_uid");
                        Toast.makeText(UploadActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onResponse: "+prob_image_uid);


                        Intent j = new Intent(UploadActivity.this,CreateActivity.class);
                        Log.d(TAG, "onClick: "+ prob_image_uid);
                        j.putExtra("image_uid",prob_image_uid);
//                        startActivity(j);
                        setResult(200,j);
                        finish();

                    }else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
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
                Log.e(TAG, "Create Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap();

                params.put("filePath",imagePath);
                params.put("imageBase64",AppConfig.getFileDataFromUri(getApplicationContext(),imageUri));
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }
    private void theoreticstateimageUpload(final String imagePath,final Uri imageUri, final String prob_uid) {

        // loading or check internet connection or something...
        // ... then
//        String prob_image_uid = null;

        // Tag used to cancel the request
        String tag_string_req = "req_register";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_TheoreticStateImage, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "TheoreticStateImage Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        Toast.makeText(UploadActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
//                        setResult(200,j);
                        finish();

                    }else {


    private void imageUpload_feedback (final String imagePath,final Uri imageUri,final String solution_uid) {

        // loading or check internet connection or something...
        // ... then
//        String prob_image_uid = null;

        // Tag used to cancel the request
        String tag_string_req = "req_feedback";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_Feedback_Imgae, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Create Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        Toast.makeText(UploadActivity.this, "上传成功", Toast.LENGTH_SHORT).show();


                        Intent j = new Intent(UploadActivity.this,ActionActivity.class);
                        Log.d(TAG, "onClick: "+ solution_uid);
                        j.putExtra("action_uid",solution_uid);
                        startActivity(j);
                        finish();

                    }else {


                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException ) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Feedback Image Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap();
                params.put("solution_uid",solution_uid);
                params.put("filePath",imagePath);
                params.put("imageBase64",AppConfig.getFileDataFromUri(getApplicationContext(),imageUri));
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "TheoreticStateImage Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap();

                params.put("filePath",imagePath);
                params.put("imageBase64",AppConfig.getFileDataFromUri(getApplicationContext(),imageUri));
                params.put("prob_uid",prob_uid);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }
    private void improvestateimageUpload(final String imagePath,final Uri imageUri, final String prob_uid) {

        // loading or check internet connection or something...
        // ... then
//        String prob_image_uid = null;

        // Tag used to cancel the request
        String tag_string_req = "req_register";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ImproveStateImage, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "ImproveStateImage Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        Toast.makeText(UploadActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
//                        setResult(200,j);
                        finish();

                    }else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
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
                Log.e(TAG, "ImproveStateImage Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap();

                params.put("filePath",imagePath);
                params.put("imageBase64",AppConfig.getFileDataFromUri(getApplicationContext(),imageUri));
                params.put("prob_uid",prob_uid);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }
    private void tempsolutionimageUpload(final String imagePath,final Uri imageUri, final String temp_solution_uid) {

        // loading or check internet connection or something...
        // ... then
//        String prob_image_uid = null;

        // Tag used to cancel the request
        String tag_string_req = "req_register";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_TempSolutionImage, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "TempSolutionImage Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        Toast.makeText(UploadActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
//                        setResult(200,j);
                        finish();

                    }else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
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
                Log.e(TAG, "TempSolutionImage Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap();

                params.put("filePath",imagePath);
                params.put("imageBase64",AppConfig.getFileDataFromUri(getApplicationContext(),imageUri));
                params.put("tempsolution_uid",temp_solution_uid);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
