package com.example.ramsey.myCloud;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewCauseActivity extends AppCompatActivity {
    private List<sCause> scauseList=new ArrayList<sCause>();
    private sCauseAdapter adapter;
    private static final String TAG = "ViewCause";
    private FloatingActionButton fab;
    private String prob_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cause);
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar_view_cause);
        toolbar.setTitle("查看原因");//设置Toolbar标题
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setSupportActionBar(toolbar);//使toolbar支持ActionBar的特性
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//导航抽屉
//        getSupportActionBar().setHomeButtonEnabled(true);//返回键可用

        Intent intent=getIntent();
        prob_uid=intent.getStringExtra("prob_uid");


        fab=(FloatingActionButton)findViewById(R.id.fab_view_cause_create);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CreateCauseActivity.CreateCauseActivityStart(ViewCauseActivity.this,prob_uid);
            }
        });

        initsCauses();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_cause);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new sCauseAdapter(scauseList);
        recyclerView.setAdapter(adapter);
    }


    private void initsCauses() {
        final ProgressDialog pDiaglog=new ProgressDialog(this);
        pDiaglog.setMessage("请稍等");
        pDiaglog.show();
        String tag_scause_request = "req_scause";
        scauseList.clear();
        AppController.getInstance().addToRequestQueue(new StringRequest(Request.Method.POST, AppConfig.URL_CheckCauseList,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Cause Response: " + response.toString());
                        pDiaglog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (!error) {
                                try {
                                    JSONArray obj2 = obj.getJSONArray("cause");
                                    for (int i = 0; i < obj2.length(); i++) {
                                        JSONObject obj3 = obj2.getJSONObject(i);
                                        sCause scause = new sCause(obj3.getString("cause_uid"), obj3.getString("cause"), obj3.getString("analysis"),obj3.getString("prob_uid"));
                                        scauseList.add(scause);
                                    }
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(ViewCauseActivity.this,"已刷新",Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                String errorMsg = obj.getString("error_msg");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                                adapter.notifyDataSetChanged();
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

                Log.d(TAG, "getParams: "+prob_uid);
                Map<String, String> params = new HashMap<String, String>();
                params.put("prob_uid",prob_uid);
                return params;
            }
        },tag_scause_request);
    }
    public static void ViewCauseActivityStart(Context context,String prob_uid)
    {
        Intent intent=new Intent(context,ViewCauseActivity.class);
        intent.putExtra("prob_uid",prob_uid);
    }
    @Override
    public void onRestart()
    {
        super.onRestart();
        initsCauses();
    }
}
