package com.example.ramsey.myCloud;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Resetpwd extends AppCompatActivity {
    private static final String TAG = Register.class.getSimpleName();
    private EditText inputEmail;                        //用户名编辑
    private EditText inputPwdOld;                            //密码编辑
    private EditText inputPwdNew;                            //密码编辑
    private EditText inputPwdCheck;                       //密码编辑
    private Button mResetButton;                       //确定按钮
    private Button mCancelButton;                     //取消按钮
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resetpwd);
        Toolbar toolbar=(Toolbar) findViewById(R.id.resetpwd_tb);
        setSupportActionBar(toolbar);

//        layout.setOrientation(RelativeLayout.VERTICAL).
        inputEmail = (EditText) findViewById(R.id.resetpwd_edit_email);
        inputPwdOld = (EditText) findViewById(R.id.resetpwd_edit_pwd_old);
        inputPwdNew = (EditText) findViewById(R.id.resetpwd_edit_pwd_new);
        inputPwdCheck = (EditText) findViewById(R.id.resetpwd_edit_pwd_check);

        mResetButton = (Button) findViewById(R.id.resetpwd_btn_sure);
        mCancelButton = (Button) findViewById(R.id.resetpwd_btn_cancel);


        ImageView image = (ImageView) findViewById(R.id.logo_resetpwd);             //使用ImageView显示logo
        image.setImageResource(R.drawable.login_logo);

        //返回按钮退到登录界面
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        Login.class);
                startActivity(i);
                finish();
            }
        });

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

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
            Intent intent = new Intent(Resetpwd.this,
                    User.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        mResetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String passwordOld = inputPwdOld.getText().toString().trim();
                String passwordNew = inputPwdNew.getText().toString().trim();
                String passwordNewCheck = inputPwdCheck.getText().toString().trim();

                if (!email.isEmpty() && !passwordNew.isEmpty() && !passwordOld.isEmpty() && !passwordNewCheck.isEmpty() && (passwordNew.equals(passwordNewCheck))) {
                    resetUser(email, passwordOld, passwordNew);
                }
                if (!passwordNew.isEmpty() && !email.isEmpty() && !passwordOld.isEmpty() && !passwordNewCheck.isEmpty() && !(passwordNew.equals(passwordNewCheck))) {
                    Toast.makeText(getApplicationContext(),
                            "两次密码不一致，请重新输入！", Toast.LENGTH_LONG)
                            .show();
                }
                if (email.equals("")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.email_empty), Toast.LENGTH_LONG)
                            .show();
                }
                if (passwordOld.equals("")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.pwdOld_empty), Toast.LENGTH_LONG)
                            .show();
                }
                if (passwordNew.equals("")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.pwdNew_empty), Toast.LENGTH_LONG)
                            .show();
                }
                if(passwordNewCheck.equals("")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.pwdCheck_empty), Toast.LENGTH_LONG)
                            .show();
                }
//                else {
//                    Toast.makeText(getApplicationContext(), "请检查其他细节!", Toast.LENGTH_LONG)
//                            .show();
//                }
            }
        });
    }

    private void resetUser(final String email, final String passwordOld,
                           final String passwordNew) {
        // Tag used to cancel the request
        String tag_string_req = "req_resetPassword";

        pDialog.setMessage("Resetting ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_RESET, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Reset Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);//解析为json obj
                    boolean error = jObj.getBoolean("error");
                    if (!error) {


                        Toast.makeText(getApplicationContext(), "成功修改密码，尝试登录吧！", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                Resetpwd.this,
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
                Log.e(TAG, "Reset Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to resetPWd url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("newpassword", passwordNew);
                params.put("password", passwordOld);
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
        Intent pwd_to_login=new Intent(Resetpwd.this,Login.class);
        startActivity(pwd_to_login);
        finish();
    }
}

