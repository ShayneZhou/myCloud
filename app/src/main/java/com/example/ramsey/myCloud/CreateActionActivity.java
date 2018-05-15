package com.example.ramsey.myCloud;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateActionActivity extends AppCompatActivity {
    private Spinner locationspinner;
    private EditText createactionaction;
    private static final String TAG = "CreateAction";
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_action);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_create_action);
        toolbar.setTitle("新建措施");//设置Toolbar标题
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setSupportActionBar(toolbar);//使toolbar支持ActionBar的特性
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//导航抽屉
        getSupportActionBar().setHomeButtonEnabled(true);//返回键可用


        frameLayout=(FrameLayout)findViewById(R.id.FrameLayout_create);
        locationspinner=(Spinner)findViewById(R.id.action_create_location_spinner);
        List<String> location=new ArrayList<>();
        location.add("底板分拼");
        location.add("底板Ⅰ");
        location.add("底板Ⅱ");
        location.add("侧围");
        location.add("总拼");
        location.add("装配");
        location.add("报交");
        //add hint as last item
        location.add("请填写");
        simpleArrayAdapter adapter=new simpleArrayAdapter(this,android.R.layout.simple_spinner_item,location);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationspinner.setAdapter(adapter);
        locationspinner.setSelection(location.size()-1,true);

        createactionaction=(EditText)findViewById(R.id.action_create_action_1);

        Button createactionbutton=(Button)findViewById(R.id.action_create_button_1);
        createactionbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Validate())
                {
                    createaction();
                }
            }
        });
    }

    private void createaction()
    {
        final ProgressDialog pDiaglog=new ProgressDialog(this);
        pDiaglog.setMessage("正在上传");
        pDiaglog.show();
        String tag_createaction_request = "create_action";
        AppController.getInstance().addToRequestQueue(new StringRequest(Request.Method.POST, AppConfig.URL_ReportAction,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Create Action Response: " + response.toString());
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
                                Toast.makeText(CreateActionActivity.this,"创建成功",Toast.LENGTH_SHORT).show();
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
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                Intent intent1=getIntent();
                String cause_uid=intent1.getStringExtra("cause_uid");
                params.put("cause_uid", cause_uid);
                params.put("solution",createactionaction.getText().toString().trim());
                params.put("section",locationspinner.getSelectedItem().toString().trim());
                return params;
            }
        },tag_createaction_request);
    }

    private boolean Validate()
    {
        if(createactionaction.getText().toString().trim().isEmpty()) {
            Snackbar.make(frameLayout, "请填写措施描述", Snackbar.LENGTH_SHORT).setAction("确定", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            }).show();
            return false;
        }
        else
        {
            if(locationspinner.getSelectedItem().toString().trim()=="请填写")
            {
                Snackbar.make(frameLayout, "请选择工段", Snackbar.LENGTH_SHORT).setAction("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
                return false;
            }
            else
            {
                return true;
            }
        }
    }
    public static void CreateActionActivityStart(Context context, String cause_uid)
    {
        Intent intent=new Intent(context,CreateActionActivity.class);
        intent.putExtra("cause_uid",cause_uid);
        context.startActivity(intent);
    }
}
