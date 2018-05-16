package com.example.ramsey.myCloud;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateActivity extends AppCompatActivity {
    private EditText inputTitle, inputSource, inputPosition, inputDescription;
    private TextInputLayout inputLayoutTitle, inputLayoutSource,  inputLayoutPosition, inputLayoutDescription;
    private FloatingActionButton fab_delete, fab_upload;
    private Button b_photo;
    private SQLiteHandler db;
    private ProgressDialog pDialog;
    private Spinner carTypeSpinner, defectTypeSpinner, defectAssemblySpinner, positionNumberSpinner;
//    private Spinner machineNumberSpinner;
    private String[] str_ct = {"LNF", "GNF", };
    private String[] str_dt = {"表面", "点焊", "匹配", "尺寸", "涂胶", "折边", "弧焊", "激光焊"};
    private String[] str_da = {"UBI", "UBII","SIH","SIV","LTV","LTH","STR","STL","PSAD","SAD","AUFBAU","ZP5" };
    private String[] str_pn = {"A1", "A2", "A3", };
//    private String[] str_mn = {"B1", "B2", "B3", };
    private String spSelected_ct, spSelected_dt, spSelected_da, spSelected_pn;
//    private String spSelected_mn;
    String image_uid;

    private static final String TAG = CreateActivity.class.getSimpleName();

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;

    public static final int MEDIA_TYPE_IMAGE = 1;

    private Uri fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // file url to store image

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create);
        Toolbar toolbar2 = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar2);
        toolbar2.setTitle("新建问题单");


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        carTypeSpinner = (Spinner) findViewById(R.id.spinner_ct);
        defectTypeSpinner = (Spinner) findViewById(R.id.spinner_dt);
        defectAssemblySpinner = (Spinner) findViewById(R.id.spinner_da);
        positionNumberSpinner = (Spinner) findViewById(R.id.spinner_pn);
//        machineNumberSpinner = (Spinner) findViewById(R.id.spinner_mn);

        List<String> array_ct = new ArrayList<String>();
        array_ct.addAll(Arrays.asList(str_ct));
        // add hint as last item
        array_ct.add("请选择车型：");
        //设置样式
        simpleArrayAdapter adapter_ct = new simpleArrayAdapter(this, android.R.layout.simple_spinner_item,array_ct);
        adapter_ct.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        carTypeSpinner.setAdapter(adapter_ct);
        carTypeSpinner.setSelection(array_ct.size() - 1, true);

        List<String> array_dt = new ArrayList<String>();
        array_dt.addAll(Arrays.asList(str_dt));
        // add hint as last item

        array_dt.add("请选择缺陷类型：");
        //设置样式
        simpleArrayAdapter adapter_dt = new simpleArrayAdapter(this, android.R.layout.simple_spinner_item,array_dt);
        adapter_dt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        defectTypeSpinner.setAdapter(adapter_dt);
        defectTypeSpinner.setSelection(array_dt.size() - 1, true);

        List<String> array_da = new ArrayList<String>();
        array_da.addAll(Arrays.asList(str_da));
        // add hint as last item
        array_da.add("请选择缺陷总成：");
        //设置样式
        simpleArrayAdapter adapter_da = new simpleArrayAdapter(this, android.R.layout.simple_spinner_item,array_da);
        adapter_da.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        defectAssemblySpinner.setAdapter(adapter_da);
        defectAssemblySpinner.setSelection(array_da.size() - 1, true);

        List<String> array_pn = new ArrayList<String>();
        array_pn.addAll(Arrays.asList(str_pn));
        // add hint as last item
        array_pn.add("请选择工位号：");
        //设置样式
        simpleArrayAdapter adapter_pn = new simpleArrayAdapter(this, android.R.layout.simple_spinner_item,array_pn);
        adapter_pn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        positionNumberSpinner.setAdapter(adapter_pn);
        positionNumberSpinner.setSelection(array_pn.size() - 1, true);

//        List<String> array_mn = new ArrayList<String>();
//        array_mn.addAll(Arrays.asList(str_mn));
//        // add hint as last item
//        array_mn.add("请选择机器号：");
//        //设置样式
//        simpleArrayAdapter adapter_mn = new simpleArrayAdapter(this, android.R.layout.simple_spinner_item,array_mn);
//        adapter_mn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        //加载适配器
//        machineNumberSpinner.setAdapter(adapter_mn);
//        machineNumberSpinner.setSelection(array_mn.size() - 1, true);



        spSelected_dt = "null";
        spSelected_ct = "null";
        spSelected_pn = "null";
        spSelected_da = "null";
//        spSelected_mn = "null";


        carTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spSelected_ct = (String) carTypeSpinner.getSelectedItem();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        defectTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spSelected_dt = (String) defectTypeSpinner.getSelectedItem();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        defectAssemblySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spSelected_da = (String) defectAssemblySpinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        positionNumberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spSelected_pn = (String) positionNumberSpinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        machineNumberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                spSelected_mn = (String) machineNumberSpinner.getSelectedItem();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        inputLayoutTitle = (TextInputLayout) findViewById(R.id.create_layout_title);

        inputTitle = (EditText) findViewById(R.id.create_title);


        fab_delete = (FloatingActionButton) findViewById(R.id.btn_delete);
        fab_upload = (FloatingActionButton) findViewById(R.id.btn_upload);

        // SQLite database handler
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        b_photo=(Button) findViewById(R.id.btn_photo);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();


        final String finder = user.get("uid");
        final String section = user.get("section");
        Log.d(TAG, "onCreate: "+finder+"   "+ section);
        fab_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(spinnerValidate(spSelected_ct, spSelected_dt, spSelected_da, spSelected_pn)){
                    String title = inputTitle.getText().toString().trim();
                    String carType = spSelected_ct.trim();
                    String defectType = spSelected_dt.trim();
                    String defectAssembly = spSelected_da.trim();
                    String positionNumber = spSelected_pn.trim();
//                    String machineNumber = spSelected_mn.trim();

                    if (submitForm()) {
                        Log.d(TAG, "onClick: carType"+carType);
                        Log.d(TAG, "onClick: 发送"+image_uid);
                        Log.d(TAG, "onClick: title"+title);
                        uploadProblem(title, carType, defectType, defectAssembly, positionNumber, section, finder, image_uid);
                    }
                    else{
                        Toast.makeText(CreateActivity.this, "出现错误！", Toast.LENGTH_SHORT).show();
                    }
                }

                else{
                    Toast.makeText(CreateActivity.this, "请确认已全部选择上述信息", Toast.LENGTH_SHORT).show();
                }




            }
        });

        fab_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view,"即将清空所有文本框",Snackbar.LENGTH_SHORT).setAction("确定",new View.OnClickListener(){
                    @Override
                    public void onClick(View v)
                    {
                        delete();
                        Toast.makeText(CreateActivity.this,"已清空所有文本框",Toast.LENGTH_SHORT).show();
                    }
                }).show();
            }
        });

        b_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(CreateActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreateActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    // capture picture
                    captureImage();
//                Toast.makeText(CreateActivity.this,"拍照",Toast.LENGTH_SHORT).show();
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

    private boolean spinnerValidate(String spSelected_ct,String spSelected_dt, String spSelected_da, String spSelected_pn ){
        if(!spSelected_pn.equals("null") && !spSelected_da.equals("null") && !spSelected_ct.equals("null") && !spSelected_dt.equals("null"))
        {
            return true;
        }else
            return false;
    }

    private boolean submitForm() {
        if(validate())
        {
            return true;
        }
        else
            return false;
    }


    private boolean validate() {
        if (inputTitle.getText().toString().trim().isEmpty()) {
            inputLayoutTitle.setError(getString(R.string.err_msg_title));
            return false;
        } else {
            return true;
        }
    }

    private void uploadProblem(final String title, final String carType,
                               final String defectType, final String defectAssembly, final String positionNumber,  final String section,
                               final String finder, final String image_uid){
        // Tag used to cancel the request
        String tag_string_req = "req_upload";

        Log.d(TAG, "uploadProblem: image_uid"+image_uid)
        ;
        pDialog.setMessage("Uploading ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_CREATE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Uploading Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Toast.makeText(getApplicationContext(), "成功提交", Toast.LENGTH_SHORT).show();
                        // Launch user activity
                        Intent intent = new Intent(
                                CreateActivity.this,
                                User.class);
                        startActivity(intent);
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
                Log.e(TAG, "Upload Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap();
                params.put("title", title);
                params.put("car_type", carType);
                params.put("defect_type", defectType);
                params.put("finder", finder);
                params.put("section", section);
                params.put("prob_image_uid", image_uid);
                params.put("position_num", positionNumber);
                params.put("defect_assembly", defectAssembly);
//                params.put("machine_num", machineNumber);
                Log.d(TAG, "getParams: "+" "+ title+" "+image_uid);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    private void delete() {
        inputTitle.setText(null);
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

//            接收上传成功界面传过来的image_uid;
            case 10:
                if (resultCode ==200) {
                    image_uid = data.getStringExtra("image_uid");
                    Log.d(TAG, "onActivityResult: "+image_uid);
                }


        }
    }

    private void launchUploadActivity(boolean isImage){
        Intent i = new Intent(CreateActivity.this, UploadActivity.class);
        i.putExtra("Mode","1");
        i.putExtra("filePath", fileUri.getPath());
        i.setData(fileUri);
        i.putExtra("isImage", isImage);
        startActivityForResult(i,10);
//        startActivity(i);
        Log.d(TAG, "launchUploadActivity: "+fileUri.getPath());
//        finish();
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

    public void onBackPressed(){
        Intent cre_to_user=new Intent(CreateActivity.this,User.class);
        startActivity(cre_to_user);
        finish();
    }
}

