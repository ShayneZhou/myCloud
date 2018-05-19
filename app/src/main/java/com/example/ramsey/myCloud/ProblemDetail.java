package com.example.ramsey.myCloud;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProblemDetail extends AppCompatActivity {

    private static final String TAG = ProblemDetail.class.getSimpleName();
    private EditText CreatedAt;                             //创立日期
    private EditText Title;                             //缺陷名称
    private EditText Finder;                           //推送人
    private EditText Temp;                          //临时放行标准
    private Button mProblemDetailButton;                       //确定按钮
    private Button mReasonButton;                       //原因按钮
    private Button mImprovedButton;                       //改进按钮

    private Button mTemporaryButton;                       //临时按钮
    private Button mExpectedButton;                       //预期按钮
    private Button mCancelButton;                       //取消按钮

    private Button mExportButton;

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private Spinner CarTypeSpinner;                      //枚举车型
    private Spinner DefectAssemblySpinner;
    private Spinner DefectTypeSpinner;
    private Spinner PositionNumSpinner;
    private TextView url_prob_uid;

    private String[] str_ct = {"LNF", "GNF", };
    private String[] str_dt = {"表面", "点焊", "匹配", "尺寸", "涂胶", "折边", "弧焊", "激光焊"};
    private String[] str_da = {"UBI", "UBII","SIH","SIV","LTV","LTH","STR","STL","PSAD","SAD","AUFBAU","ZP5" };
    private String[] str_pn = {"AFO1020","AFO1030","AFO1040","AFO1050","AFO1070","AFO1100","AFO1110","AFO1120",
            "AFO1122","AFO1130","AFO1132","AFO1160","AFO1170","AFO1180","AFO1190","AFO1200","AFO1210","AFO1240",
            "AFO1250","AFO1260","AFO1270","AFO1310","AFO1312","AFO1316","AFO1320","AFO1328","AFO1338","AFO1344",
            "AFO1348","AFO1375","AFO1377","AFO1382","AFO1386","AFO1400","AFO1402","AFO1410","AFO1415","AFO1425",
            "AFO1432","AFO1435","AFO1440","AFO1457","AFO1458","AFO1460","AFO1465","AFO1470","AFO1475","AFO1480",
            "AFO1500","AFO1505","AFO1510","AFO1515","AFO1520","AFO1525","AFO1530","AFO1535","AFO1550 IR1",
            "AFO1550 IR2","AFO1555","AFO1570","AFO1575","AFO1580","AFO1590","AFO1600","AFO1605","AFO1610",
            "AFO1615","AFO1625","AFO1635","AFO1660","AFO1665","AFO1670","AFO1675","AFO1680","AFO1685","AFO1690",
            "AFO1770","AFO1775","AFO1780","AFO1783","AFO1785","AFO1790","AFO1830","AFO1835","AFO1855","AFO1860",
            "AFO1870","AFO1885","AFO1890","AFO1895","AFO1900","AFO1930","AFO1950","AFO1955","AFO1965","AFO1975",
            "AFO2010","AFO2030","AFO2040","AFO2050","AFO2070","AFO2080","AFO2110","AFO2120","AFO2150","AFO2205",
            "AFO2210","AFO2260","AFO2265","AFO2270","AFO2275","AFO2280","AFO2285","AFO2290","AFO2295","AFO2365",
            "AFO2375","AFO2380","AFO2385","AFO2390","AFO2395","AFO2400","AFO2405","AFO2410","AFO2455","AFO2456",
            "AFO2475","AFO2476","AFO2480","AFO2503","AFO2540","AFO2541","AFO2545","AFO2610","AFO2615","AFO2625",
            "AFO2640","AFO2660","AFO2670","AFO2680","AFO2691","AFO2692","AFO2693","AFO2695","AFO2725","AFO2730",
            "AFO2735","AFO2745","AFO2800","AFO2810","AFO2817","AFO2825","AFO2827","AFO2835","AFO2845","AFO3020",
            "AFO3030","AFO3060","AFO3110","AFO3120","AFO3130","AFO3140","AFO3210","AFO3240","AFO3260","AFO3270",
            "AFO3310","AFO3340","AFO3360","AFO3370","AFO3400","AFO3405","AFO3407","AFO3410","AFO3415","AFO3425",
            "AFO3435","AFO3465","AFO3475","AFO3560","AFO3565","AFO3570","AFO3575","AFO3580","AFO3585","AFO3590",
            "AFO3595","AFO3605","AFO3615","AFO3660","AFO3665","AFO3670","AFO3675","AFO3685","AFO3695","AFO3705",
            "AFO3707","AFO3715","AFO3717","AFO3725","AFO3727","AFO3735","AFO3737","AFO3745","AFO3755","AFO4260",
            "AFO4270","AFO4320","AFO4330","AFO4420","AFO4430","AFO4440","AFO4450","AFO4480","AFO4490","AFO5230",
            "AFO5240","AFO5250","AFO5260","AFO5310","AFO5320","AFO5350","AFO5370","AFO5410","AFO5450","AFO5480",
            "AFO5510","AFO5520","AFO5530","AFO5540","AFO5550","AFO5560","AFO5570","AFO5580","AFO5610","AFO5630",
            "AFO5640","AFO6020","AFO6030","AFO6080","AFO6090","AFO6160","AFO6170","AFO6180","AFO6190","AFO6220",
            "AFO6230","AFO6240","AFO6250","AFO6260","AFO6270","AFO6280","AFO6290","AFO6300","AFO6310","AFO6520",
            "AFO6530","AFO6580","AFO6590","AFO6600","AFO6610","AFO6640","AFO6650","AFO6660","AFO6670","AFO6700",
            "AFO6710","AFO6720","AFO6730","AFO6740","AFO6750","AFO6760","AFO6770","AFO6780","AFO6790","AFO7030",
            "AFO7040","AFO7050","AFO7235","AFO7240","AFO7247","AFO7248","AFO7260","AFO7280","AFO7285","AFO7305",
            "AFO7340","AFO7345","W230","W250","W270","W340","W350"};


    private String spSelected_CarType;
    private String spSelected_DefectAssembly;
    private String spSelected_DefectType;
    private String spSelected_PositionNum;

    private TextInputLayout inputLayoutCreatedAt, inputLayoutTitle, inputLayoutFinder, inputLayoutTemp;
    private ImageView problemImage;
    private ScrollView scrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.problemDetail_tb);
        toolbar.setTitle("问题单详情");//设置Toolbar标题
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setSupportActionBar(toolbar);//使toolbar支持ActionBar的特性

        scrollView = (ScrollView) findViewById(R.id.id_scrollView);

        problemImage = (ImageView) findViewById(R.id.problemDetail_image);



        inputLayoutTitle = (TextInputLayout) findViewById(R.id.content_layout_created_at);
        inputLayoutCreatedAt = (TextInputLayout) findViewById(R.id.content_layout_created_at);
        inputLayoutFinder = (TextInputLayout) findViewById(R.id.content_layout_finder);
        inputLayoutTemp = (TextInputLayout) findViewById(R.id.content_layout_temp);

        CreatedAt = (EditText) findViewById(R.id.problemDetail_edit_CreatedAt);
        Title = (EditText) findViewById(R.id.problemDetail_edit_Title);
        Finder = (EditText) findViewById(R.id.problemDetail_edit_Finder);
        Temp = (EditText) findViewById(R.id.problemDetail_edit_Temp);

        //初始化Temp,因为这个框选填，避免用户不填导致发送时出现空指针错误
        Temp.setText("null");



        CarTypeSpinner = (Spinner) findViewById(R.id.spinner_CarType);
        DefectAssemblySpinner = (Spinner) findViewById(R.id.spinner_DefectAssembly);
        DefectTypeSpinner = (Spinner) findViewById(R.id.spinner_DefectType);
        PositionNumSpinner = (Spinner) findViewById(R.id.spinner_PositionNum);




        //下面的代码请做修改
        // 注意：前两行是可以用的，simpleArrayAdapter不要用，这是我自定义的适配器，adapter是用来加载spinner和传入arraylist的，
        // 你自己定义一个最简单的adapter就行，网上查

        List<String> array_ct = new ArrayList<String>();
        array_ct.addAll(Arrays.asList(str_ct));//这两行可以用，将数组转换为Arraylist

        //设置样式
        ArrayAdapter<String> adapter_ct = new ArrayAdapter(this, android.R.layout.simple_spinner_item, array_ct);
        adapter_ct.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        CarTypeSpinner.setAdapter(adapter_ct);

        //下面同理
        List<String> array_da = new ArrayList<String>();
        array_da.addAll(Arrays.asList(str_da));

        //设置样式
        ArrayAdapter<String> adapter_da = new ArrayAdapter(this, android.R.layout.simple_spinner_item, array_da);
        adapter_da.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        DefectAssemblySpinner.setAdapter(adapter_da);

        List<String> array_dt = new ArrayList<String>();
        array_dt.addAll(Arrays.asList(str_dt));

        //设置样式
        ArrayAdapter<String> adapter_dt = new ArrayAdapter(this, android.R.layout.simple_spinner_item, array_dt);
        adapter_dt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        DefectTypeSpinner.setAdapter(adapter_dt);

        List<String> array_pn = new ArrayList<String>();
        array_pn.addAll(Arrays.asList(str_pn));

        //设置样式
        ArrayAdapter<String> adapter_pn = new ArrayAdapter(this, android.R.layout.simple_spinner_item, array_pn);
        adapter_pn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        PositionNumSpinner.setAdapter(adapter_pn);





        Intent intent = getIntent();                     //通过getIntent获得prob_uid
        final String prob_uid = intent.getStringExtra("prob_uid");



        Log.d("ProblemDetail", prob_uid);
        if (!prob_uid.isEmpty()) {
            problemDetailContent(prob_uid);             //将prob_uid传入请求函数
        }

        //以上是刚进入界面请求的东西

        url_prob_uid =  (TextView) findViewById(R.id.text_prob_uid);

        mExportButton = (Button) findViewById(R.id.problemDetail_btn_export);

        mExportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                export();
            }
        });



        mProblemDetailButton = (Button) findViewById(R.id.problemDetail_btn_sure);
        mReasonButton = (Button) findViewById(R.id.problemDetail_btn_reason);
        mTemporaryButton = (Button) findViewById(R.id.problemDetail_btn_temporary);
        mExpectedButton = (Button) findViewById(R.id.problemDetail_btn_expected);
        mCancelButton = (Button) findViewById(R.id.problemDetail_btn_cancel);
        mImprovedButton=(Button) findViewById(R.id.problemDetail_btn_improved);

        mReasonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {                //后期完善
                Intent i = new Intent(getApplicationContext(),
                        ViewCauseActivity.class);
                i.putExtra("prob_uid",prob_uid);
                startActivity(i);
                finish();
            }
        });


        mTemporaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewTempActionActivity.ViewTempActionActivityStart(ProblemDetail.this,prob_uid);

            }
        });

        mExpectedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TheoreticState.TheoreticStateStart(ProblemDetail.this,prob_uid);
            }
        });

        mImprovedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImproveState.ImproveStateStart(ProblemDetail.this,prob_uid);
            }
        });
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                problemDetailContent(prob_uid);
            }
        });

        CarTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spSelected_CarType = (String) CarTypeSpinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        DefectAssemblySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spSelected_DefectAssembly = (String) DefectAssemblySpinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        DefectTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spSelected_DefectType = (String) DefectTypeSpinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        PositionNumSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spSelected_PositionNum = (String) PositionNumSpinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // Check if user is already logged in or not

        final HashMap<String, String> user = db.getUserDetails();

        String authority = user.get("authority");

        if (authority.equals("0")) {
            CreatedAt.setFocusable(false);
            CreatedAt.setFocusableInTouchMode(false);
            Title.setFocusable(false);
            Title.setFocusableInTouchMode(false);
            Finder.setFocusable(false);
            Finder.setFocusableInTouchMode(false);
            Temp.setFocusable(false);
            Temp.setFocusableInTouchMode(false);

            CarTypeSpinner.setEnabled(false);
            DefectTypeSpinner.setEnabled(false);
            DefectAssemblySpinner.setEnabled(false);
            PositionNumSpinner.setEnabled(false);




//            mCancelButton.setVisibility(View.INVISIBLE);
//            mExpectedButton.setVisibility(View.INVISIBLE);
//            mTemporaryButton.setVisibility(View.INVISIBLE);
//            mProblemDetailButton.setVisibility(View.INVISIBLE);

        }

        mProblemDetailButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                {
                    String date = CreatedAt.getText().toString().trim();
                    String title = Title.getText().toString().trim();
                    String finder = Finder.getText().toString().trim();
                    String temp = Temp.getText().toString().trim();
                    String carType = spSelected_CarType.trim();
                    String defectAssembly = spSelected_DefectAssembly.trim();
                    String defectType = spSelected_DefectType.trim();
                    String positionNum = spSelected_PositionNum.trim();

                    String section = user.get("section");
                    String engineer = user.get("uid");
//                    String prob_describe = inputDescription.getText().toString().trim();
//                    String prob_source = inputSource.getText().toString().trim();
//                    String position = inputPosition.getText().toString().trim();
                    if (submitForm()) {
                        Log.d(TAG, "onClick: carType" + carType);
                        Log.d(TAG, "onClick: 发送" + prob_uid);
                        Log.d(TAG, "onClick: title" + title);
                        confirmProblem(date, title, finder, section, temp, carType, defectAssembly,
                                defectType, positionNum,  engineer, prob_uid);
                    } else {
                        Toast.makeText(ProblemDetail.this, "出现错误！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab1);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "确认生成问题单", Snackbar.LENGTH_LONG)
//                        .setAction("发送", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                {
//                                    String date = CreatedAt.getText().toString().trim();
//                                    String title = Title.getText().toString().trim();
//                                    String finder = Finder.getText().toString().trim();
//                                    String temp = Temp.getText().toString().trim();
//                                    String carType = spSelected_CarType.trim();
//                                    String defectAssembly = spSelected_DefectAssembly.trim();
//                                    String defectType = spSelected_DefectType.trim();
//                                    String positionNum = spSelected_PositionNum.trim();
//                                    String section = user.get("section");
//                                    String engineer = user.get("uid");
//                                    if (submitForm()) {
//                                        Log.d(TAG, "onClick: carType" + carType);
//                                        Log.d(TAG, "onCick: 发送" + prob_uid);
//                                        Log.d(TAG, "onClick: title" + title);
//                                        confirmProblem(date, title, finder, section, temp, carType, defectAssembly,
//                                                defectType, positionNum, engineer);
//                                    } else {
//                                        Toast.makeText(ProblemDetail.this, "出现错误！", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            }
//                        }).show();
//            }
//        });
    }

    private void export(){
        String tag_string_req = "export_request";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_Export, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "export Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        String text_prob_uid = jObj.getString("url");
                        url_prob_uid.setText(text_prob_uid);

                    } else {

                        // Error occurred in request. Get the error
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
                Log.e(TAG, "Request Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap();
                Intent intent = getIntent();                     //通过getIntent获得prob_uid
                final String prob_uid = intent.getStringExtra("prob_uid");
                params.put("prob_uid", prob_uid);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void problemDetailContent(final String prob_uid){
        String tag_string_req = "req_request";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ProblemDetail, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Requesting Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        //显示图片
                        Glide.with(getApplicationContext()).load(jObj.getString("prob_image_url")).error(R.drawable.ic_error_black_24dp)
                                .placeholder(R.drawable.ic_loading).into(problemImage);
                        Log.d(TAG, "onResponse: "+jObj.getString("prob_image_url"));

                        //三个文本框显示在下面，settext方法把取回来的值放入edittext里
                        String created_at = jObj.getString("created_at");
                        CreatedAt.setText(created_at);

                        String title = jObj.getString("title");
                        Title.setText(title);

                        String finder = jObj.getString("finder");
                        Finder.setText(finder);

                        String temp = jObj.getString("temp");
                        if(!temp.isEmpty()){
                            Temp.setText(temp);
                        }


                        //取出了几个下拉框的值；如何设置到下拉框里请你自己完成；
                        String car_type = jObj.getString("car_type");
                        spSelected_CarType = car_type;

                        String defect_type = jObj.getString("defect_type");
                        spSelected_DefectType = defect_type;

                        String defect_assembly = jObj.getString("defect_assembly");
                        spSelected_DefectAssembly = defect_assembly;

                        String position_num = jObj.getString("position_num");
                        spSelected_PositionNum = position_num;



                        //在下面完成设置下拉框的代码
                        setSpinnerItemSelectedByValue(CarTypeSpinner, spSelected_CarType);
                        setSpinnerItemSelectedByValue(DefectAssemblySpinner, spSelected_DefectAssembly);
                        setSpinnerItemSelectedByValue(DefectTypeSpinner, spSelected_DefectType);
                        setSpinnerItemSelectedByValue(PositionNumSpinner, spSelected_PositionNum);


                    } else {

                        // Error occurred in request. Get the error
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
                Log.e(TAG, "Request Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap();
                params.put("prob_uid", prob_uid);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public  void setSpinnerItemSelectedByValue(Spinner spinner,String value){
        SpinnerAdapter apsAdapter= spinner.getAdapter(); //得到SpinnerAdapter对象
        int k= apsAdapter.getCount();
        for(int i=0;i<k;i++){
            if(value.equals(apsAdapter.getItem(i).toString())){
//                spinner.setSelection(i,true);// 默认选中项
                spinner.setSelection(i);// 默认选中项

                break;
            }
        }
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
        if (Title.getText().toString().trim().isEmpty()) {
            inputLayoutTitle.setError(getString(R.string.err_msg_title));
            return false;
        } else {
            inputLayoutTitle.setErrorEnabled(false);
            if (Finder.getText().toString().trim().isEmpty()) {
                inputLayoutFinder.setError(getString(R.string.err_msg_finder));
                return false;
            } else {
                inputLayoutFinder.setErrorEnabled(false);
                if (CreatedAt.getText().toString().trim().isEmpty()) {
                    inputLayoutCreatedAt.setError("请输入发现日期");
                    return false;
                } else {
                    inputLayoutCreatedAt.setErrorEnabled(false);
                }
            }
        }
        return true;
    }

    private void confirmProblem(final String date,final String title,final String finder,final String section, final String temp,
                                final String carType, final String defectAssembly, final String defectType, final String positionNum,
                                final String engineer, final String prob_uid
    ) {
        // Tag used to cancel the request
        String tag_string_req = "req_confirm";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_EditProblemList, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "confirming Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Toast.makeText(getApplicationContext(), "成功确认", Toast.LENGTH_SHORT).show();

                        // Launch user activity
                        Intent intent = new Intent(
                                ProblemDetail.this,
                                User.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in confirming. Get the error
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
                params.put("prob_uid", prob_uid);
                params.put("title", title);
                params.put("car_type", carType);
                params.put("defect_type", defectType);
                params.put("finder", finder);
                params.put("section", section);
                params.put("position_num", positionNum);
                params.put("engineer", engineer);
                params.put("temp", temp);
                params.put("date", date);
                params.put("defect_assembly", defectAssembly);

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


    public void onBackPressed(){
        Intent list_to_user=new Intent(ProblemDetail.this,User.class);
        startActivity(list_to_user);
        finish();
    }
}
