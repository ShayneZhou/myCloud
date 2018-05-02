package com.example.ramsey.myCloud;

import android.app.ProgressDialog;
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

public class SelfCheck extends AppCompatActivity {

    private List<sSolution> solutionList=new ArrayList<sSolution>();
    private sSolutionAdapter adapter;
    private static final String TAG = "SelfCheck";
    private FloatingActionButton fab;
    private String machine_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_check);
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar_view_solution);
        toolbar.setTitle("查看措施");//设置Toolbar标题
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setSupportActionBar(toolbar);//使toolbar支持ActionBar的特性

        Intent intent=getIntent();
        machine_num = intent.getStringExtra("machine_num");
        Log.d(TAG, "onCreate: "+machine_num);

        fab=(FloatingActionButton)findViewById(R.id.fab_view_solution_create);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        initSolution();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_solution);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new sSolutionAdapter(solutionList);
        recyclerView.setAdapter(adapter);
    }

    private void initSolution(){
        final ProgressDialog pDiaglog=new ProgressDialog(this);
        pDiaglog.setMessage("请稍等");
        pDiaglog.show();
        String tag_solution_request = "req_solution";
        solutionList.clear();
        AppController.getInstance().addToRequestQueue(new StringRequest(Request.Method.POST, AppConfig.URL_SelfCheck,
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
                                    JSONArray obj2 = obj.getJSONArray("solutionlist");
                                    for (int i = 0; i < obj2.length(); i++) {
                                        JSONObject obj3 = obj2.getJSONObject(i);
                                        sSolution solution = new sSolution(obj3.getString("solution"));
                                        solutionList.add(solution);
                                    }
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(SelfCheck.this,"已刷新",Toast.LENGTH_SHORT).show();
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

                Log.d(TAG, "getParams: "+machine_num);
                Map<String, String> params = new HashMap<String, String>();
                params.put("machine_num",machine_num);
                return params;
            }
        },tag_solution_request);
    }
}
