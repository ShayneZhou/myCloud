package com.example.ramsey.myCloud;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import android.app.ProgressDialog;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Register extends AppCompatActivity {
    private static final String TAG = Register.class.getSimpleName();
    private EditText inputFullName;
    private EditText inputEmail;                        //用户名编辑
    private EditText inputPassword;                            //密码编辑
    private EditText inputPasswordCheck;                       //密码编辑
    private Button mRegisterButton;                       //确定按钮
    private TextView mCancelButton;                     //取消按钮
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private Spinner authoritySpinner;
    private Spinner sectionSpinner;
    private String spSelected_authority;
    private String spSelected_section;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.register_tb);
        setSupportActionBar(toolbar);

        ImageView image = (ImageView) findViewById(R.id.logo_register);             //使用ImageView显示logo
        image.setImageResource(R.drawable.login_logo);

        inputFullName = (EditText) findViewById(R.id.register_edit_fullName);
        inputEmail = (EditText) findViewById(R.id.register_edit_email);
        inputPassword = (EditText) findViewById(R.id.register_edit_pwd);
        inputPasswordCheck = (EditText) findViewById(R.id.register_check_pwd);


        mRegisterButton = (Button) findViewById(R.id.register_btn_sure);
        mCancelButton = (Button) findViewById(R.id.register_btn_cancel);

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        Login.class);
                startActivity(i);
                finish();
            }
        });

        authoritySpinner = (Spinner) findViewById(R.id.spinner1);
        List<String> objects = new ArrayList<>();
        objects.add("操作员");
        objects.add("技术工");
        // add hint as last item
        objects.add("请选择您的身份：");
        //设置样式
        simpleArrayAdapter adapter = new simpleArrayAdapter(this, android.R.layout.simple_spinner_item, objects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        authoritySpinner.setAdapter(adapter);
        authoritySpinner.setSelection(objects.size() - 1, true);

        sectionSpinner = (Spinner) findViewById(R.id.spinner2);
        List<String> objects2 = new ArrayList<>();
        objects2.add("车头");
        objects2.add("车身");
        objects2.add("车尾");
        // add hint as last item
        objects2.add("请选择您的工段：");
        //设置样式
        simpleArrayAdapter adapter2 = new simpleArrayAdapter(this, android.R.layout.simple_spinner_item, objects2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        sectionSpinner.setAdapter(adapter2);
        sectionSpinner.setSelection(objects2.size() - 1, true);

//        spSelected_authority = (String) authoritySpinner.getSelectedItem();
//        spSelected_section = (String) sectionSpinner.getSelectedItem();

        spSelected_authority = "null";
        spSelected_section = "null";

        authoritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spSelected_authority = (String) authoritySpinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spSelected_section = (String) sectionSpinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // Check if user is already logged in or not

        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(Register.this,
                    User.class);
            startActivity(intent);
            finish();
        }
        // Register Button Click event
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (spinnerValidate()) {
                    String name = inputFullName.getText().toString().trim();
                    String email = inputEmail.getText().toString().trim();
                    String password = inputPassword.getText().toString().trim();
                    String passwordCheck = inputPasswordCheck.getText().toString().trim();
                    String authority = spSelected_authority.trim();
                    String section = spSelected_section.trim();

                    Log.d(TAG, "onClick: " + name + email + password);

                    if (!section.isEmpty() && !name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !passwordCheck.isEmpty() && (password.equals(passwordCheck))) {
                        registerUser(name, email, password, authority, section);
                    }
                    if (!section.isEmpty() && !name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !passwordCheck.isEmpty() && !(password.equals(passwordCheck))) {
                        Toast.makeText(getApplicationContext(),
                                "两次密码不一致，请重新输入！", Toast.LENGTH_LONG)
                                .show();
                    }
                    if (password.isEmpty() | passwordCheck.isEmpty() | name.isEmpty() | email.isEmpty()){
                        Toast.makeText(getApplicationContext(),
                                "请确认已经输入全部信息！", Toast.LENGTH_LONG)
                                .show();
                    }
                }
                else{
                    Toast.makeText(Register.this, "请确认身份以及条线已经选择", Toast.LENGTH_SHORT).show();
                }
            }
        });
}

    private boolean spinnerValidate() {
        if (!spSelected_section.equals("null") && !spSelected_authority.equals("null")) {
            return true;
        }
        else
            return false;
    }


    private void registerUser(final String name, final String email,
                              final String password, final String authority, final String section) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user
                                .getString("created_at");
                        String authority = user.getString("authority");
                        String section = user.getString("section");

                        Log.d(TAG, "onResponse: "+user);

                        // Inserting row in users table
                        db.addUser(name, email, uid, created_at, authority, section);

                        Toast.makeText(getApplicationContext(), "注册成功，尝试登录吧！", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                Register.this,
                                Login.class);
                        startActivity(intent);
                        finish();
                    }else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("authority", authority);
                params.put("section", section);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void onBackPressed(){
        Intent reg_to_login=new Intent(Register.this,Login.class);
        startActivity(reg_to_login);
        finish();
    }
}
