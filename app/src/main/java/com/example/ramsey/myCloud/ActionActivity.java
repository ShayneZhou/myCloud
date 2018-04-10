package com.example.ramsey.myCloud;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ActionActivity extends AppCompatActivity {

    private Spinner locationspinner;
    private final static String TAG = "ActionActivity";
    private EditText edittext_action_action;
    private EditText edittext_action_performence;
    private EditText edittext_action_response;
    private Button button_action_edit;
    public String misdone;
    public Button deletebutton;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_action);
        toolbar.setTitle("编辑措施");//设置Toolbar标题
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setSupportActionBar(toolbar);//使toolbar支持ActionBar的特性
        locationspinner=(Spinner)findViewById(R.id.action_location_spinner);
//定义Spinner
        //定义工段数组
        List<String>location=new ArrayList<>();
        location.add("车身");
        location.add("车头");
        location.add("车尾");
        //add hint as last item
        location.add("请填写");
        simpleArrayAdapter adapter=new simpleArrayAdapter(this,android.R.layout.simple_spinner_item,location);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationspinner.setAdapter(adapter);
        locationspinner.setSelection(location.size()-1,true);
//实例化EditText
        edittext_action_action=(EditText)findViewById(R.id.action_action_1);
        edittext_action_performence=(EditText)findViewById(R.id.action_performence_1);
        edittext_action_response=(EditText)findViewById(R.id.action_response_1);

//实例化Button
        button_action_edit=(Button)findViewById(R.id.action_button_1);
        button_action_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editaction();
            }
        });
        deletebutton=(Button)findViewById(R.id.action_delete_button_1);
        deletebutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                deleteaction();
                finish();
            }
        });

        imageView=(ImageView)findViewById(R.id.feedback_image);

        getactiondetail();

    }

    private void editaction() {
        final ProgressDialog pDiaglog = new ProgressDialog(this);
        pDiaglog.setMessage("请稍等");
        pDiaglog.show();
        String tag_action_upload = "action_upload";
        AppController.getInstance().addToRequestQueue(new StringRequest(Request.Method.POST, AppConfig.URL_EditAction,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Action Response: " + response.toString());
                        pDiaglog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (!error) {
                                    Toast.makeText(ActionActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                            } else {
                                String errorMsg = obj.getString("error_msg");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
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
                String solutionuid=intent1.getStringExtra("action_uid");
                String action = edittext_action_action.getText().toString().trim();
                String performence = edittext_action_performence.getText().toString().trim();
                String response=edittext_action_response.getText().toString().trim();
                String location=locationspinner.getSelectedItem().toString().trim();
                params.put("solution_uid",solutionuid);
                params.put("solution",action);
                params.put("performance",performence);
                params.put("feedback",response);
                params.put("section",location);
                params.put("isdone",misdone);
                return params;
            }
        }, tag_action_upload);
    }
    private void getactiondetail() {
        final ProgressDialog pDiaglog = new ProgressDialog(this);
        pDiaglog.setMessage("请稍等");
        pDiaglog.show();
        String tag_getactiondetail_request = "get_action_detail";
        AppController.getInstance().addToRequestQueue(new StringRequest(Request.Method.POST, AppConfig.URL_SolutionDetail,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Get Action DetailResponse: " + response.toString());
                        pDiaglog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (error) {
                                String errorMsg = obj.getString("error_msg");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();}
                            else
                            {
                                edittext_action_action.setText(obj.getString("solution").trim());
                                edittext_action_response.setText(obj.getString("feedback").trim());
                                edittext_action_performence.setText(obj.getString("performance").trim());
                                setSpinnerItemSelectedByValue(locationspinner,obj.getString("section").trim());
                                Glide.with(getApplicationContext()).
                                        load(obj.getString("feedback_image_url"))
                                        .placeholder(R.drawable.ic_loading)
                                        .error(R.drawable.ic_error_black_24dp)
                                        .override(160,100)
                                        .into(imageView);
                                misdone=obj.getString("isdone").trim();
                            }
                        } catch (JSONException e) {
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
                Intent intent=getIntent();
                String solution_uid=intent.getStringExtra("action_uid");
                params.put("solution_uid", solution_uid);
                return params;
            }
        }, tag_getactiondetail_request);
    }
    private void deleteaction() {
        final ProgressDialog pDiaglog = new ProgressDialog(this);
        pDiaglog.setMessage("请稍等");
        pDiaglog.show();
        String tag_action_delete = "action_delete";
        AppController.getInstance().addToRequestQueue(new StringRequest(Request.Method.POST, AppConfig.URL_DeleteAction,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Action Response: " + response.toString());
                        pDiaglog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (!error) {
                                Toast.makeText(ActionActivity.this,"删除成功",Toast.LENGTH_LONG);
                                finish();
                            } else {
                                String errorMsg = obj.getString("error_msg");
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
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                pDiaglog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                Intent intent1=getIntent();
                String solutionuid=intent1.getStringExtra("action_uid");
                params.put("solution_uid",solutionuid);
                return params;
            }
        }, tag_action_delete);
    }
    public  void setSpinnerItemSelectedByValue(Spinner spinner,String value){
        SpinnerAdapter apsAdapter= spinner.getAdapter(); //得到SpinnerAdapter对象
        int k= apsAdapter.getCount();
        for(int i=0;i<k;i++){
            if(value.equals(apsAdapter.getItem(i).toString())){
                spinner.setSelection(i);// 默认选中项
                break;
            }
        }
    }
    public static void ActionActivityStart(Context context,String action_uid)
    {
        Intent intent=new Intent(context,ActionActivity.class);
        intent.putExtra("action_uid",action_uid);
        context.startActivity(intent);
    }
}
