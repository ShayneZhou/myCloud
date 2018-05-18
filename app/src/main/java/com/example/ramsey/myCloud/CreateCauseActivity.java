package com.example.ramsey.myCloud;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateCauseActivity extends AppCompatActivity {

    private EditText createcausecause;
    private EditText createcauseanalysis;
    private Button createcausebutton;
    private static final String TAG = "CreateCause";
    private TextInputLayout textInputLayout1;
    private TextInputLayout textInputLayout2;
    private Spinner MachineNumSpinner;
    private String[] str_mn={"IR01","IR02","IR03","IR04","IR05","IR06","IR07","IR08","IR09","IR1",
            "IR10","IR11","IR12","IR2","IR3","IR4","IR5","IR6","IR7","IR8","R01","R02","R03","R04","R05",
            "R06","R07","R08","R09","R10","R11","R12","R13","R14"};

    private String spSelected_MachineNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_cause);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_create_cause);
        toolbar.setTitle("新建原因");//设置Toolbar标题
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setSupportActionBar(toolbar);//使toolbar支持ActionBar的特性
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//导航抽屉
//        getSupportActionBar().setHomeButtonEnabled(true);//返回键可用

        createcausecause = (EditText) findViewById(R.id.cause_create_cause_1);
        createcauseanalysis = (EditText) findViewById(R.id.cause_create_analysis_1);

        createcausebutton = (Button) findViewById(R.id.cause_create_button_1);

        textInputLayout1=(TextInputLayout)findViewById(R.id.cause_create_text_1);
        textInputLayout2=(TextInputLayout)findViewById(R.id.cause_create_text_2);

        MachineNumSpinner = (Spinner) findViewById(R.id.spinner_MachineNum);

        List<String> array_mn = new ArrayList<String>();
        array_mn.addAll(Arrays.asList(str_mn));

        //设置样式
        ArrayAdapter<String> adapter_mn = new ArrayAdapter(this, android.R.layout.simple_spinner_item, array_mn);
        adapter_mn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        MachineNumSpinner.setAdapter(adapter_mn);

        MachineNumSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spSelected_MachineNum = (String) MachineNumSpinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        createcausebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate())
                {
                    Intent intent=getIntent();
                    String prob_uid=intent.getStringExtra("prob_uid");
                    createcause(prob_uid);
                }
            }
        });
    }

    private void createcause(final String prob_uid) {
        final ProgressDialog pDiaglog=new ProgressDialog(this);
        pDiaglog.setMessage("正在上传");
        pDiaglog.show();
        String tag_createcause_request = "create_cause";
        AppController.getInstance().addToRequestQueue(new StringRequest(Request.Method.POST, AppConfig.URL_ReportCause,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Create Cause Response: " + response.toString());
                        pDiaglog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (error)  {
                                String errorMsg = obj.getString("error_msg");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                ViewCauseActivity.ViewCauseActivityStart(CreateCauseActivity.this,prob_uid);
                                finish();
                            }
                        }catch(JSONException e)
                        {
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
                Map<String, String> params = new HashMap<String, String>();
                params.put("prob_uid", prob_uid);
                params.put("cause",createcausecause.getText().toString().trim());
                params.put("analysis",createcauseanalysis.getText().toString().trim());
                String machineNum = spSelected_MachineNum.trim();
                params.put("machine_num",machineNum);
                return params;
            }
        },tag_createcause_request);
    }
    private boolean validate() {
        if (createcausecause.getText().toString().trim().isEmpty()) {
            textInputLayout1.setError("请输入原因内容");
            return false;
        } else {
            if (createcauseanalysis.getText().toString().isEmpty()) {
                textInputLayout2.setError("请输入分析内容");
                return false;
            }
            else
            {
                return true;
            }
        }
    }
        public static void CreateCauseActivityStart(Context context,String prob_uid)
    {
        Intent intent=new Intent(context,CreateCauseActivity.class);
        intent.putExtra("prob_uid",prob_uid);
        context.startActivity(intent);
    }
}

