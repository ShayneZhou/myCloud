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

public class ViewActionActivity extends AppCompatActivity {
    private List<sAction> sactionList=new ArrayList<sAction>();
    private sActionAdapter adapter;
    private static final String TAG = "ViewAction";
    private FloatingActionButton fab;
    private String prob_uid;
    private String cause_uid;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_action);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_view_action);
        toolbar.setTitle("查看措施");//设置Toolbar标题
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setSupportActionBar(toolbar);//使toolbar支持ActionBar的特性
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//导航抽屉
//        getSupportActionBar().setHomeButtonEnabled(true);//返回键可用
        Intent intent1=getIntent();
        prob_uid=intent1.getStringExtra("prob_uid");
        cause_uid=intent1.getStringExtra("cause_uid");

        initsActions();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_action);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new sActionAdapter(sactionList);
        recyclerView.setAdapter(adapter);

        fab=(FloatingActionButton) findViewById(R.id.fab_view_action_create);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session = new SessionManager(getApplicationContext());
                // SQLite database handler
                db = new SQLiteHandler(getApplicationContext());
                final HashMap<String, String> user = db.getUserDetails();
                String authority = user.get("authority");

                if (authority.equals("1")) {
                    CreateActionActivity.CreateActionActivityStart(
                            ViewActionActivity.this, cause_uid);
                }
                else
                {
                    Toast.makeText(ViewActionActivity.this, "您不是技术员，无法新建措施！",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initsActions() {
        final ProgressDialog pDiaglog=new ProgressDialog(this);
        pDiaglog.setMessage("请稍等");
        pDiaglog.show();
        String tag_section_request = "req_section";
        sactionList.clear();
        AppController.getInstance().addToRequestQueue(new StringRequest(Request.Method.POST, AppConfig.URL_CheckSolutionList,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Action Response: " + response.toString());
                        pDiaglog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (!error) {
                                try {
                                    JSONArray obj2 = obj.getJSONArray("solution");
                                    for (int i = 0; i < obj2.length(); i++) {
                                        JSONObject obj3 = obj2.getJSONObject(i);
                                        sAction saction = new sAction(obj3.getString("solution_uid"),
                                                obj3.getString("solution"),obj3.getString("section"),
                                                "1","1",obj3.getString("feedback_image_url"),"1");
                                        sactionList.add(saction);
                                    }
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(ViewActionActivity.this,"已刷新",Toast.LENGTH_SHORT).show();
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
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                Intent intent1=getIntent();
                String uid=intent1.getStringExtra("cause_uid");
                params.put("cause_uid", uid);
                return params;
            }
        },tag_section_request);
    }
    public static void ViewActionActivityStart(Context context, String cause_uid,String prob_uid)
    {
        Intent intent=new Intent(context,ViewActionActivity.class);
        intent.putExtra("cause_uid",cause_uid);
        intent.putExtra("prob_uid",prob_uid);
        context.startActivity(intent);
    }
    @Override
    public void onRestart()
    {
        super.onRestart();
        initsActions();
    }
}
