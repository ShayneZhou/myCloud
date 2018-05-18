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

public class ViewTempActionActivity extends AppCompatActivity {
    private List<sTempAction> stempactionList=new ArrayList<sTempAction>();
    private sTempActionAdapter adapter;
    private static final String TAG = "ViewTempAction";
    private FloatingActionButton fab;
    private String prob_uid;
    private String temp_action_uid;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_temp_action);

        Intent i=getIntent();
        prob_uid=i.getStringExtra("prob_uid");

        Log.d(TAG, "onCreate: "+prob_uid);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_view_temp_action);
        toolbar.setTitle("查看临时措施");//设置Toolbar标题
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setSupportActionBar(toolbar);//使toolbar支持ActionBar的特性

        initsTempActions();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_temp_action);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new sTempActionAdapter(stempactionList);
        recyclerView.setAdapter(adapter);

        fab=(FloatingActionButton) findViewById(R.id.fab_view_temp_action_create);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session = new SessionManager(getApplicationContext());
                // SQLite database handler
                db = new SQLiteHandler(getApplicationContext());
                final HashMap<String, String> user = db.getUserDetails();
                String authority = user.get("authority");

                if (authority.equals("1")) {
                    CreateTempAction.CreateTempActionActivityStart(ViewTempActionActivity.this, prob_uid);
                }
                else
                {
                    Toast.makeText(ViewTempActionActivity.this, "您不是技术员，无法新建临时措施！",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void initsTempActions() {
        final ProgressDialog pDiaglog=new ProgressDialog(this);
        pDiaglog.setMessage("请稍等");
        pDiaglog.show();
        String tag_section_request = "req_temp_action";
        stempactionList.clear();
        AppController.getInstance().addToRequestQueue(new StringRequest(Request.Method.POST, AppConfig.URL_CheckTempSolutionList,
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
                                    JSONArray obj2 = obj.getJSONArray("tempsolution");
                                    for (int i = 0; i < obj2.length(); i++) {
                                        JSONObject obj3 = obj2.getJSONObject(i);
                                        sTempAction stempaction = new sTempAction(obj3.getString("tempsolution_uid"),obj3.getString("tempsolution"),obj3.getString("section"),obj3.getString("isdone"),obj3.getString("feedback"),obj3.getString("feedback_image_url"));
                                        stempactionList.add(stempaction);
                                    }
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(ViewTempActionActivity.this,"已刷新",Toast.LENGTH_SHORT).show();
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
                params.put("prob_uid", prob_uid);
                return params;
            }
        },tag_section_request);
    }

    public static void ViewTempActionActivityStart(Context context, String prob_uid)
    {
        Intent intent=new Intent(context,ViewTempActionActivity.class);
        intent.putExtra("prob_uid",prob_uid);
        context.startActivity(intent);
    }
    @Override
    public void onRestart()
    {
        super.onRestart();
        initsTempActions();
    }
}
