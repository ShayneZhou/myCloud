package com.example.ramsey.myCloud;

import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.widget.DrawerLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.FloatingActionButton;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import com.android.volley.toolbox.StringRequest;

import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class User extends AppCompatActivity {
    private static final String TAG = "User";
    //全局控件

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private SQLiteHandler db;
    private SessionManager session;

    private sQuestion[] squestions;

    private List<sQuestion> squestionList=new ArrayList<sQuestion>();
    private NavigationView navView;
    private sQuestionAdapter adapter;
    private int REQUEST_CODE_SCAN_SOLUTION = 111;
    private int REQUEST_CODE_SCAN_PROBLEM = 222;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem  searchItem = (MenuItem) menu.findItem(R.id.search);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setQueryHint("输入字段查找问题单");
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                //提交按钮的点击事件
                Toast.makeText(User.this, "查找的字段是" + query, Toast.LENGTH_SHORT).show();
                queryProblem(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //当输入框内容改变的时候回调
                Log.i(TAG,"searchView content: " + newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.qrcode_problem:
                AlertDialog.Builder dialog = new AlertDialog.Builder(User.this);
                dialog.setTitle("扫码提示");
                dialog.setMessage("该扫描二维码的功能是通过扫描机器号获取该机器号的历史问题单，而右侧隐藏菜单中的二维码扫描是通过扫描机器号获取与该机器号相关的问题单中所有措施进行一一确认");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        customScanProblem();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.show();
                break;
            case R.id.qrcode_solution:
                AlertDialog.Builder dialog1 = new AlertDialog.Builder(User.this);
                dialog1.setTitle("扫码提示");
                dialog1.setMessage("该扫描二维码的功能是通过扫描机器号获取与该机器号相关的问题单中所有措施进行一一确认");
                dialog1.setCancelable(false);
                dialog1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        customScanSolution();
                    }
                });
                dialog1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog1.show();
                break;
//            case R.id.logout:
//                logoutUser();
//                break;
//            case R.id.settings:
//                Toast.makeText(this, "Click Settings", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.user);
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("主界面");//设置Toolbar标题
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setSupportActionBar(toolbar);//使toolbar支持ActionBar的特性
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//导航抽屉
        getSupportActionBar().setHomeButtonEnabled(true);//返回键可用


        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");
        final String authority = user.get("authority");
        String uid = user.get("uid");
        String created_at = user.get("created_at");
        String section = user.get("section");
        Log.d(TAG, "onCreate: name "+name);
        Log.d(TAG, "onCreate: email "+email);
        Log.d(TAG, "onCreate: authority "+authority);
        Log.d(TAG, "onCreate: uid "+uid);
        Log.d(TAG, "onCreate: created_at "+created_at);
        Log.d(TAG, "onCreate: section "+section);


        navView = (NavigationView) findViewById(R.id.nav_view);//导航栏
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.nav_create:
                        if (authority.equals("0")) {
                            Intent intent_cr =new Intent(User.this,CreateActivity.class);
                            startActivity(intent_cr);
                            finish();
                            mDrawerLayout.closeDrawers();
                        }
                        else
                        {
                            Toast.makeText(User.this,
                                    "您不是操作工！无法新建问题单", Toast.LENGTH_SHORT).show();
                            mDrawerLayout.closeDrawers();
                        }
                        break;
                    case R.id.nav_chart:
                        Toast.makeText(User.this,"查询界面",Toast.LENGTH_LONG).show();
                        Intent intent_ch = new Intent(User.this,Chart.class);
                        startActivity(intent_ch);
                        finish();

                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_close:
                        finish();
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_logout:
                        logoutUser();
                        mDrawerLayout.closeDrawers();
                        break;
                }
                return false;
            }
        });




        View headerLayout = navView.inflateHeaderView(R.layout.nav_header);
        TextView mEmail = (TextView) headerLayout.findViewById(R.id.email);
        TextView mName=(TextView) headerLayout.findViewById(R.id.username);

//        //在导航栏显示用户名与邮箱
        mEmail.setText(email);
        mName.setText(name);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //以下是实现抽屉菜单栏的代码
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.hello, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerToggle.syncState();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (authority.equals("0")) {
                    Intent intent_cr =new Intent(User.this,CreateActivity.class);
                    startActivity(intent_cr);
                    finish();

                }
                else
                {
                    Toast.makeText(User.this,
                            "您不是操作工！无法新建问题单", Toast.LENGTH_SHORT).show();
                }
            }
        });


        FloatingActionButton fab_refresh = (FloatingActionButton) findViewById(R.id.fab_refresh);
        fab_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(User.this);
                dialog.setTitle("提示");
                dialog.setMessage("重新获取问题单");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        initsQuestions();
                    }
                });
                dialog.show();
            }
        });



        initsQuestions();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new sQuestionAdapter(squestionList);
        recyclerView.setAdapter(adapter);

    }


    public void customScanSolution(){
        Intent intent = new Intent(User.this, CaptureActivity.class);
         /*ZxingConfig是配置类  可以设置是否显示底部布局，闪光灯，相册，是否播放提示音  震动等动能
         * 也可以不传这个参数
         * 不传的话  默认都为默认不震动  其他都为true
         * */

        ZxingConfig config = new ZxingConfig();
        config.setShowbottomLayout(true);//底部布局（包括闪光灯和相册）
        config.setPlayBeep(true);//是否播放提示音
        config.setShake(true);//是否震动
        config.setShowAlbum(true);//是否显示相册
        config.setShowFlashLight(true);//是否显示闪光灯
        intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
        startActivityForResult(intent, REQUEST_CODE_SCAN_SOLUTION);
    }

    public void customScanProblem(){
        Intent intent = new Intent(User.this, CaptureActivity.class);
         /*ZxingConfig是配置类  可以设置是否显示底部布局，闪光灯，相册，是否播放提示音  震动等动能
         * 也可以不传这个参数
         * 不传的话  默认都为默认不震动  其他都为true
         * */

        ZxingConfig config = new ZxingConfig();
        config.setShowbottomLayout(true);//底部布局（包括闪光灯和相册）
        config.setPlayBeep(true);//是否播放提示音
        config.setShake(true);//是否震动
        config.setShowAlbum(true);//是否显示相册
        config.setShowFlashLight(true);//是否显示闪光灯
        intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
        startActivityForResult(intent, REQUEST_CODE_SCAN_PROBLEM);
    }

    private void initsQuestions() {
        final ProgressDialog pDiaglog=new ProgressDialog(this);
        pDiaglog.setMessage("请稍等");
        pDiaglog.show();
        String tag_squestion_request = "req_squestion";
        squestionList.clear();
        AppController.getInstance().addToRequestQueue(new StringRequest(Request.Method.POST, AppConfig.URL_CheckProblemList,
                    new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "question Response: " + response.toString());
                                pDiaglog.dismiss();
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    boolean error = obj.getBoolean("error");
                                    if (!error) {
                                        try {
                                            JSONArray obj2 = obj.getJSONArray("problemlist");
                                            for (int i = 0; i < obj2.length(); i++) {
                                                JSONObject obj3 = obj2.getJSONObject(i);
                                                sQuestion squestion = new sQuestion(obj3.getString("title"), obj3.getString("position_num"),
                                                        obj3.getString("prob_level"), obj3.getString("created_at"), obj3.getString("example_image_url"), obj3.getString("prob_uid"));
                                                squestionList.add(squestion);
                                                Log.d(TAG, "onResponse: "+obj3.getString("example_image_url"));
                                            }
                                            adapter.notifyDataSetChanged();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    else {
                                        String errorMsg = obj.getString("error_msg");
                                        Toast.makeText(getApplicationContext(),
                                                errorMsg, Toast.LENGTH_LONG).show();
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

                HashMap<String, String> user = db.getUserDetails();
                String uid = user.get("uid");
                params.put("user_uid", uid);
                return params;
            }
                },tag_squestion_request);
    }



    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(User.this, Login.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN_SOLUTION && resultCode == RESULT_OK) {
            if (data != null) {

                String content = data.getStringExtra(Constant.CODED_CONTENT);
                Log.d(TAG, "onActivityResult: "+content);
                Toast.makeText(this, "扫描结果是"+content, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(User.this,SelfCheck.class);
                i.putExtra("machine_num",content);
                startActivity(i);
                finish();
            }
        }

        if (requestCode == REQUEST_CODE_SCAN_PROBLEM && resultCode == RESULT_OK) {
            if (data != null) {

                String machine_num = data.getStringExtra(Constant.CODED_CONTENT);
                Log.d(TAG, "onActivityResult: " + machine_num);
                Toast.makeText(this, "扫描结果是" + machine_num, Toast.LENGTH_SHORT).show();
                selfCheckProblem(machine_num);
            }
        }
    }

    private void selfCheckProblem(final String machine_num) {
        final ProgressDialog pDiaglog=new ProgressDialog(this);
        pDiaglog.setMessage("请稍等");
        pDiaglog.show();
        String tag_squestion_request = "req_squestion";
        squestionList.clear();
        AppController.getInstance().addToRequestQueue(new StringRequest(Request.Method.POST, AppConfig.URL_SelfCheck_Problem,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "question Response: " + response.toString());
                        pDiaglog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (!error) {
                                try {
                                    JSONArray obj2 = obj.getJSONArray("problemlist");
                                    for (int i = 0; i < obj2.length(); i++) {
                                        JSONObject obj3 = obj2.getJSONObject(i);
                                        sQuestion squestion = new sQuestion(obj3.getString("title"), obj3.getString("position_num"),
                                                obj3.getString("prob_level"), obj3.getString("created_at"), obj3.getString("example_image_url"), obj3.getString("prob_uid"));
                                        squestionList.add(squestion);
                                        Log.d(TAG, "onResponse: "+obj3.getString("example_image_url"));
                                    }
                                    adapter.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                String errorMsg = obj.getString("error_msg");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
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

                params.put("machine_num", machine_num);
                return params;
            }
        },tag_squestion_request);
    }

    private void queryProblem(final String query) {
        final ProgressDialog pDiaglog=new ProgressDialog(this);
        pDiaglog.setMessage("请稍等");
        pDiaglog.show();
        String tag_squestion_request = "req_squestion";
        squestionList.clear();
        AppController.getInstance().addToRequestQueue(new StringRequest(Request.Method.POST, AppConfig.URL_Query_Problem,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "query Response: " + response.toString());
                        pDiaglog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (!error) {
                                try {
                                    JSONArray obj2 = obj.getJSONArray("problemlist");
                                    for (int i = 0; i < obj2.length(); i++) {
                                        JSONObject obj3 = obj2.getJSONObject(i);
                                        sQuestion squestion = new sQuestion(obj3.getString("title"), obj3.getString("position_num"),
                                                obj3.getString("prob_level"), obj3.getString("created_at"), obj3.getString("example_image_url"), obj3.getString("prob_uid"));
                                        squestionList.add(squestion);
                                        Log.d(TAG, "onResponse: "+obj3.getString("example_image_url"));
                                    }
                                    adapter.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                String errorMsg = obj.getString("error_msg");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
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

                params.put("content", query);
                return params;
            }
        },tag_squestion_request);
    }

    public void onBackPressed(){
        finish();
    }


}
