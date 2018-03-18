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
    private String spSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        Toolbar toolbar=(Toolbar) findViewById(R.id.register_tb);
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

        authoritySpinner= (Spinner) findViewById(R.id.spinner1);
        List<String> objects = new ArrayList<>();
        objects.add("0");
        objects.add("1");
        // add hint as last item
        objects.add("请选择您的身份：");
        //设置样式
        simpleArrayAdapter adapter = new simpleArrayAdapter(this, android.R.layout.simple_spinner_item,objects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        authoritySpinner.setAdapter(adapter);
        authoritySpinner.setSelection(objects.size() - 1, true);

        spSelected = (String) authoritySpinner.getSelectedItem();

        authoritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spSelected = (String) authoritySpinner.getSelectedItem();
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
                String name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String passwordCheck = inputPasswordCheck.getText().toString().trim();
                String authority= spSelected.trim();

                if ((authority=="1" || authority=="0") && !name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !passwordCheck.isEmpty() &&(password.equals(passwordCheck))) {
                    registerUser(name, email, password, authority);
                }
                if ((authority=="1" || authority=="0") && !name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !passwordCheck.isEmpty() && !(password.equals(passwordCheck))) {
                    Toast.makeText(getApplicationContext(),
                            "两次密码不一致，请重新输入！", Toast.LENGTH_LONG)
                            .show();
                }
                if (email.equals("")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.email_empty), Toast.LENGTH_LONG)
                            .show();
                }
                if (name.equals("")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.name_empty), Toast.LENGTH_LONG)
                            .show();
                }
                if (password.equals("")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.pwd_empty), Toast.LENGTH_LONG)
                            .show();
                }
                if (passwordCheck.equals("")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.pwdCheck_empty), Toast.LENGTH_LONG)
                            .show();
                }
                if (!(authority=="1" || authority=="0")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.authority_wrong), Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

    }

    public class simpleArrayAdapter<T> extends ArrayAdapter {
        //构造方法
        public simpleArrayAdapter(Context context, int resource, List<T> objects) {
            super(context, resource, objects);
        }
        //复写这个方法，使返回的数据没有最后一项
        @Override
        public int getCount() {
            // don't display last item. It is used as hint.
            int count = super.getCount();
            return count > 0 ? count - 1 : count;
        }

    }

    private void registerUser(final String name, final String email,
                              final String password, final String authority) {
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

                        // Inserting row in users table
                        db.addUser(name, email, uid, created_at, authority);

                        Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

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
