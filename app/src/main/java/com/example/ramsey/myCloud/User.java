package com.example.ramsey.myCloud;

import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.widget.DrawerLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.FloatingActionButton;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.example.ramsey.myCloud.AppConfig.URL_CheckProblemList;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("主界面");//设置Toolbar标题
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setSupportActionBar(toolbar);//使toolbar支持ActionBar的特性
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//导航抽屉
        getSupportActionBar().setHomeButtonEnabled(true);//返回键可用
        navView = (NavigationView) findViewById(R.id.nav_view);//导航栏
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.nav_create:
                        Intent intent_cr =new Intent(User.this,CreateActivity.class);
                        startActivity(intent_cr);
                        finish();
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_search:
                        Toast.makeText(User.this,"查询界面",Toast.LENGTH_LONG).show();
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_close:
                        logoutUser();
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_logout:
                        back_to_login();
                        mDrawerLayout.closeDrawers();
                        break;
                }
                return false;
            }
        });


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
        String authority = user.get("authority");
        String uid = user.get("uid");
        String created_at = user.get("created_at");
        String section = user.get("section");
        Log.d(TAG, "onCreate: name "+name);
        Log.d(TAG, "onCreate: email "+email);
        Log.d(TAG, "onCreate: authority "+authority);
        Log.d(TAG, "onCreate: uid "+uid);
        Log.d(TAG, "onCreate: created_at "+created_at);
        Log.d(TAG, "onCreate: section "+section);

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
            Intent intent_cr =new Intent(User.this,CreateActivity.class);
                startActivity(intent_cr);
                finish();
            }
        });



        initsQuestions();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new sQuestionAdapter(squestionList);
        recyclerView.setAdapter(adapter);

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
                                Log.d(TAG, "Login Response: " + response.toString());
                                pDiaglog.dismiss();
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    boolean error = obj.getBoolean("error");
                                    if (!error) {
                                        try {
                                            JSONArray obj2 = obj.getJSONArray("problemlist");
                                            for (int i = 0; i < obj2.length(); i++) {
                                                JSONObject obj3 = obj2.getJSONObject(i);
                                                sQuestion squestion = new sQuestion(obj3.getString("title"), obj3.getString("prob_uid"), obj3.getString("prob_level"));
                                                squestionList.add(squestion);
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
                params.put("user_uid", "1");
                return params;
            }
                },tag_squestion_request);
    }
    public void back_to_login() {
        //setContentView(R.layout.login);
        Intent intent_login = new Intent(User.this,Login.class) ;
        startActivity(intent_login);
        finish();
    }


    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(User.this, Login.class);
        startActivity(intent);
        finish();
    }

}
