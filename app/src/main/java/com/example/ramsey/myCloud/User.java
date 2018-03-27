package com.example.ramsey.myCloud;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.design.widget.NavigationView;
import android.support.design.widget.FloatingActionButton;

import java.util.HashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class User extends AppCompatActivity {
    private static final String TAG = "User";
    //全局控件

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private SQLiteHandler db;
    private SessionManager session;


    private Question[] questions={new Question("车头有问题"),new Question("车门有问题"),new Question("车尾有问题"),new Question("到处有问题")};

    private List<Question> questionList=new ArrayList<>();

    private QuestionAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);
        Toolbar toolbar=(Toolbar) findViewById(R.id.tl_custom);
        toolbar.setTitle("Seize The Day！");//设置Toolbar标题
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setSupportActionBar(toolbar);//使toolbar支持ActionBar的特性
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//导航抽屉
        getSupportActionBar().setHomeButtonEnabled(true);//返回键可用
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);//导航栏
        navView.setCheckedItem(R.id.nav_call);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                mDrawerLayout.closeDrawers();
                return true;
            }
        });


        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());


        // session manager
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }
//
        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");
        String authority = user.get("authority");
        String uid = user.get("uid");
        String created_at = user.get("created_at");
        String process = user.get("process");
        Log.d(TAG, "onCreate: name "+name);
        Log.d(TAG, "onCreate: email "+email);
        Log.d(TAG, "onCreate: authority "+authority);
        Log.d(TAG, "onCreate: uid "+uid);
        Log.d(TAG, "onCreate: created_at "+created_at);
        Log.d(TAG, "onCreate: process "+process);

        View headerLayout = navView.inflateHeaderView(R.layout.nav_header);
        TextView mEmail = (TextView) headerLayout.findViewById(R.id.email);
        TextView mName=(TextView) headerLayout.findViewById(R.id.username);

//        //在导航栏显示用户名与邮箱
        mEmail.setText(email);
        mName.setText(name);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_left);
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



        initQuestions();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new QuestionAdapter(questionList);
        recyclerView.setAdapter(adapter);

    }

    private void initQuestions() {
            questionList.clear();
        for (int i = 0; i < 50; i++) {
        Random random = new Random();
        int index = random.nextInt(questions.length);
        questionList.add(questions[index]);
    }
}
    public void back_to_login(View view) {
        //setContentView(R.layout.login);
        Intent intent3 = new Intent(User.this,Login.class) ;
        startActivity(intent3);
        finish();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.logout:
                logoutUser();
                Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show();
                break;
            case R.id.backup:
                Toast.makeText(this, "You clicked Backup", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                Toast.makeText(this, "You clicked Settings", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
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
