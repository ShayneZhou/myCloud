package com.example.ramsey.myCloud;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class CauseActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "CauseActivity";
    private EditText edittext_cause;
    private EditText edittext_analysis;
    private String prob_uid;
    private String cause_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cause);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_cause);
        toolbar.setTitle("编辑原因");//设置Toolbar标题
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setSupportActionBar(toolbar);//使toolbar支持ActionBar的特性

        Button button1 = (Button) findViewById(R.id.cause_button_1);
        button1.setOnClickListener(this);
        Button button2=(Button)findViewById(R.id.cause_action_button_1);
        button2.setOnClickListener(this);
        Button button3=(Button)findViewById(R.id.cause_delete_button_1);
        button3.setOnClickListener(this);
        edittext_cause = (EditText) findViewById(R.id.cause_cause_1);
        edittext_analysis = (EditText) findViewById(R.id.cause_analysis_1);
        getcausedetail();
        Intent intent=getIntent();
        prob_uid=intent.getStringExtra("prob_uid");
        cause_uid=intent.getStringExtra("cause_uid");
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cause_button_1:
                uploadcause();
                break;
            case R.id.cause_action_button_1:
                ViewActionActivity.ViewActionActivityStart(CauseActivity.this,cause_uid,prob_uid);
                finish();
                break;
            case R.id.cause_delete_button_1:
                final AlertDialog.Builder normalDialog =new AlertDialog.Builder(CauseActivity.this);
                normalDialog.setIcon(R.drawable.logo);
                normalDialog.setTitle("确认删除？");
                normalDialog.setMessage("是否确认删除该条原因?");
                normalDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deletecause();
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
                break;
            default:
                break;
        }
    }

    private void uploadcause() {
        final ProgressDialog pDiaglog = new ProgressDialog(this);
        pDiaglog.setMessage("请稍等");
        pDiaglog.show();
        String tag_cause_upload = "cause_upload";
        AppController.getInstance().addToRequestQueue(new StringRequest(Request.Method.POST, AppConfig.URL_EditCause,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Edit Cause Response: " + response.toString());
                        pDiaglog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (error) {
                                String errorMsg = obj.getString("error_msg");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(CauseActivity.this,"保存成功",Toast.LENGTH_LONG).show();
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

                String cause = edittext_cause.getText().toString();
                String analysis = edittext_analysis.getText().toString();
                Intent intent1=getIntent();
                String cause_uid=intent1.getStringExtra("cause_uid");
                params.put("cause_uid",cause_uid);
                params.put("cause",cause);
                params.put("analysis",analysis);
                return params;
            }
        }, tag_cause_upload);
    }
    private void getcausedetail() {
        final ProgressDialog pDiaglog = new ProgressDialog(this);
        pDiaglog.setMessage("请稍等");
        pDiaglog.show();
        String tag_getcausedetail_request = "get_cause_detail";
        AppController.getInstance().addToRequestQueue(new StringRequest(Request.Method.POST, AppConfig.URL_CauseDetail,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Get Cause DetailResponse: " + response.toString());
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
                                edittext_cause.setText(obj.getString("cause").trim());
                                edittext_analysis.setText(obj.getString("analysis").trim());
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
                String cause_uid ;
                Intent intent=getIntent();
                cause_uid=intent.getStringExtra("cause_uid");
                params.put("cause_uid", cause_uid);
                return params;
            }
        }, tag_getcausedetail_request);
    }

    private void deletecause() {
        final ProgressDialog pDiaglog = new ProgressDialog(this);
        pDiaglog.setMessage("请稍等");
        pDiaglog.show();
        String tag_cause_delete = "cause_delete";
        AppController.getInstance().addToRequestQueue(new StringRequest(Request.Method.POST, AppConfig.URL_DeleteCause,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Delete Cause Response: " + response.toString());
                        pDiaglog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (error) {
                                String errorMsg = obj.getString("error_msg");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(CauseActivity.this,"删除成功",Toast.LENGTH_LONG).show();
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

                Intent intent1=getIntent();
                String cause_uid=intent1.getStringExtra("cause_uid");
                params.put("cause_uid",cause_uid);
                return params;
            }
        }, tag_cause_delete);
    }

    public static void CauseActivityStart(Context context,String cause_uid, String prob_uid)
    {
        Intent intent=new Intent(context,CauseActivity.class);
        intent.putExtra("cause_uid",cause_uid);
        intent.putExtra("prob_uid",prob_uid);
        context.startActivity(intent);
    }
}

