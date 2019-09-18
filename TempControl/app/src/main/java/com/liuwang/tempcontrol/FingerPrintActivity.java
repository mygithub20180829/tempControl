package com.liuwang.tempcontrol;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.newland.nle_sdk.responseEntity.SensorInfo;
import cn.com.newland.nle_sdk.responseEntity.User;
import cn.com.newland.nle_sdk.responseEntity.base.BaseResponseEntity;
import cn.com.newland.nle_sdk.util.NCallBack;
import cn.com.newland.nle_sdk.util.NetWorkBusiness;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FingerPrintActivity extends AppCompatActivity {
    private NetWorkBusiness netWorkBusiness;
    private ImageView iv_back;
    private TextView finger_text;
    private int finger;
    private Button btn_chart;
    private int LF_lf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_print);

        //隐藏标题栏
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Bundle bundle = getIntent().getExtras();
        final String accessToken = bundle.getString("accessToken");   //获得传输秘钥
        netWorkBusiness = new NetWorkBusiness(accessToken,"http://api.nlecloud.com:80/");   //进行登录连接


        iv_back=findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        finger_text=findViewById(R.id.finger_text);
        btn_chart=findViewById(R.id.btn_chart);
        btn_chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FingerPrintActivity.this,ChartActivity.class);
                Bundle bundle1=new Bundle();
                bundle1.putString("accessToken",accessToken);
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });

//
//        TimerTask task=new TimerTask() {
//            @Override
//            public void run() {
//                Log.d("OKOKOKOK", "run: ---------");
//            }
//        };
//        Timer timer=new Timer();
//        timer.schedule(task,5000);


//        control("42149","nl_lamp",0);

        FingerPrint();


    }

    public void control(String id,String apiTag,Object value){
        netWorkBusiness.control(id, apiTag, value, new Callback<BaseResponseEntity>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponseEntity> call, @NonNull Response<BaseResponseEntity> response) {
                BaseResponseEntity<User> baseResponseEntity = response.body();  //获得返回体
                Log.d("客户端向云平台发送数据：", "onResponse: "+baseResponseEntity);
                if (baseResponseEntity==null){
                    Toast.makeText(FingerPrintActivity.this,"请求内容为空",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<BaseResponseEntity> call, Throwable t) {
                Toast.makeText(FingerPrintActivity.this,"请求出错 " + t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void FingerPrint(){
        netWorkBusiness.getSensor("42149", "nl_LF", new NCallBack<BaseResponseEntity<SensorInfo>>() {
            @Override
            public void onResponse(final Call<BaseResponseEntity<SensorInfo>> call, final Response<BaseResponseEntity<SensorInfo>> response) {
                BaseResponseEntity baseResponseEntity = response.body();
                if (baseResponseEntity!=null){
                    //获取到了内容,使用json解析.
                    //JSON 是一种文本形式的数据交换格式，它比XML更轻量、比二进制容易阅读和编写，调式也更加方便;解析和生成的方式很多，Java中最常用的类库有：JSON-Java、Gson、Jackson、FastJson等
                    final Gson gson=new Gson();
                    JSONObject jsonObject=null;
                    String msg=gson.toJson(baseResponseEntity);
                    try {
                        jsonObject = new JSONObject(msg);   //解析数据.
                        Log.d("----------", "onResponse: "+jsonObject);
                        JSONObject resultObj = (JSONObject) jsonObject.get("ResultObj");
                        final int LF=resultObj.getInt("Value");
                        if (LF==1){//LF射频卡验证成功
                            Toast.makeText(FingerPrintActivity.this,"LF射频卡验证成功",Toast.LENGTH_SHORT).show();
                            finger_text.setText("开始录入指纹，请移步至指纹机前");
                            //指纹验证
                            final Timer timer=new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    netWorkBusiness.getSensor("42149", "nl_finger", new NCallBack<BaseResponseEntity<SensorInfo>>() {
                                        @Override
                                        public void onResponse(final Call<BaseResponseEntity<SensorInfo>> call, final Response<BaseResponseEntity<SensorInfo>> response) {
                                            BaseResponseEntity baseResponseEntity = response.body();
                                            if (baseResponseEntity!=null){
                                                Log.d("-------------", "onResponse: ");
                                                //获取到了内容,使用json解析.
                                                //JSON 是一种文本形式的数据交换格式，它比XML更轻量、比二进制容易阅读和编写，调式也更加方便;解析和生成的方式很多，Java中最常用的类库有：JSON-Java、Gson、Jackson、FastJson等
                                                final Gson gson=new Gson();
                                                JSONObject jsonObject=null;
                                                String msg=gson.toJson(baseResponseEntity);
                                                try {
                                                    jsonObject = new JSONObject(msg);   //解析数据.
                                                    Log.d("----------", "onResponse: "+jsonObject);
                                                    JSONObject resultObj = (JSONObject) jsonObject.get("ResultObj");
                                                    int value=resultObj.getInt("Value");
                                                    finger=value;
                                                    Log.d("finger---finger", "onResponse: "+finger);
                                                    if (finger==1){
                                                        //成功录入指纹
//                                                        control("42149","nl_lamp",1);
                                                        Toast.makeText(FingerPrintActivity.this,"指纹录入成功",Toast.LENGTH_SHORT).show();
                                                        Timer timer1=new Timer();
                                                        timer1.schedule(new TimerTask() {
                                                            @Override
                                                            public void run() {
                                                                netWorkBusiness.getSensor("42149", "nl_finger", new NCallBack<BaseResponseEntity<SensorInfo>>() {
                                                                    @Override
                                                                    public void onResponse(final Call<BaseResponseEntity<SensorInfo>> call, final Response<BaseResponseEntity<SensorInfo>> response) {
                                                                        BaseResponseEntity baseResponseEntity = response.body();
                                                                        if (baseResponseEntity!=null){
                                                                            Log.d("------开始进行指纹验证--------", "onResponse: ");
                                                                            //获取到了内容,使用json解析.
                                                                            //JSON 是一种文本形式的数据交换格式，它比XML更轻量、比二进制容易阅读和编写，调式也更加方便;解析和生成的方式很多，Java中最常用的类库有：JSON-Java、Gson、Jackson、FastJson等
                                                                            final Gson gson=new Gson();
                                                                            JSONObject jsonObject=null;
                                                                            String msg=gson.toJson(baseResponseEntity);
                                                                            try {
                                                                                jsonObject = new JSONObject(msg);   //解析数据.
                                                                                Log.d("----------", "onResponse: "+jsonObject);
                                                                                JSONObject resultObj = (JSONObject) jsonObject.get("ResultObj");
                                                                                int value=resultObj.getInt("Value");
                                                                                Log.d("value_value_value_value", "onResponse: "+value);
                                                                                if (value==3){
                                                                                    //指纹验证成功
                                                                                    Toast.makeText(FingerPrintActivity.this,"指纹验证成功",Toast.LENGTH_SHORT).show();
                                                                                    //成功则打开电子锁
                                                                                    control("42149","nl_lock",1);
                                                                                    Toast.makeText(FingerPrintActivity.this,"成功打开电机锁",Toast.LENGTH_SHORT).show();
                                                                                }else if (value==2){
//                                                                                    //指纹验证失败
//                                                                                    //蜂鸣器报警
                                                                                    Toast.makeText(FingerPrintActivity.this,"指纹验证失败",Toast.LENGTH_SHORT).show();
                                                                                    control("42149","nl_buzzer",1);
                                                                                    new Handler().postDelayed(new Runnable() {
                                                                                        @Override
                                                                                        public void run() {
                                                                                            control("42149","nl_lamp",1);
                                                                                        }
                                                                                    },500);
                                                                                    new Handler().postDelayed(new Runnable() {
                                                                                        @Override
                                                                                        public void run() {
                                                                                            getFingerValue();
                                                                                        }
                                                                                    },5000);
//                                                                                    new Handler().postDelayed(new Runnable() {
//                                                                                        @Override
//                                                                                        public void run() {
//                                                                                            getFingerPrint();
//                                                                                        }
//                                                                                    },3000);
                                                                                }
                                                                            } catch (JSONException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                        }
                                                                    }

                                                                    @Override
                                                                    protected void onResponse(BaseResponseEntity<SensorInfo> response) {

                                                                    }

                                                                    public void onFailure(final Call<BaseResponseEntity<SensorInfo>> call, final Throwable t) {
                                                                        Toast.makeText(FingerPrintActivity.this,"指纹数值获取失败", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }
                                                        },10000);
                                                    }else if (finger==4){
                                                        //指纹录入失败
                                                        Toast.makeText(FingerPrintActivity.this,"指纹录入失败",Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                        @Override
                                        protected void onResponse(BaseResponseEntity<SensorInfo> response) {

                                        }

                                        public void onFailure(final Call<BaseResponseEntity<SensorInfo>> call, final Throwable t) {
                                            Toast.makeText(FingerPrintActivity.this,"指纹数值获取失败", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            },15000);
                        }else if (LF==4){
                            //刷卡失败
                            Toast.makeText(FingerPrintActivity.this,"刷卡失败，请重新刷卡",Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            protected void onResponse(BaseResponseEntity<SensorInfo> response) {

            }

            public void onFailure(final Call<BaseResponseEntity<SensorInfo>> call, final Throwable t) {
                Toast.makeText(FingerPrintActivity.this,"数据错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getFingerValue(){
        netWorkBusiness.getSensor("42149", "nl_LF", new NCallBack<BaseResponseEntity<SensorInfo>>() {
            @Override
            public void onResponse(final Call<BaseResponseEntity<SensorInfo>> call, final Response<BaseResponseEntity<SensorInfo>> response) {
                BaseResponseEntity baseResponseEntity = response.body();
                if (baseResponseEntity!=null){
                    Log.d("蜂鸣器报警刷LF卡解除警报", "onResponse: ");
                    //获取到了内容,使用json解析.
                    //JSON 是一种文本形式的数据交换格式，它比XML更轻量、比二进制容易阅读和编写，调式也更加方便;解析和生成的方式很多，Java中最常用的类库有：JSON-Java、Gson、Jackson、FastJson等
                    final Gson gson=new Gson();
                    JSONObject jsonObject=null;
                    String msg=gson.toJson(baseResponseEntity);
                    try {
                        jsonObject = new JSONObject(msg);   //解析数据.
                        JSONObject resultObj = (JSONObject) jsonObject.get("ResultObj");
                        int value=resultObj.getInt("Value");
                        LF_lf=value;
                        if (LF_lf==3){
                            //解除警报
                            Toast.makeText(FingerPrintActivity.this,"LF刷卡成功，警报解除",Toast.LENGTH_SHORT).show();
                            //蜂鸣器解除报警
                            control("42149","nl_buzzer",0);

                            //指示灯灭
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    control("42149","nl_lamp",0);
                                }
                            },500);
                            //开锁
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    control("42149","nl_lock",1);
                                }
                            },00);

                        }else if (LF_lf==2){
                            Toast.makeText(FingerPrintActivity.this,"警报解除失败",Toast.LENGTH_SHORT).show();
                        }
                        Log.d("{____LF_lf______}", "onResponse: "+LF_lf);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            protected void onResponse(BaseResponseEntity<SensorInfo> response) {

            }

            public void onFailure(final Call<BaseResponseEntity<SensorInfo>> call, final Throwable t) {
                Toast.makeText(FingerPrintActivity.this,"指纹数值获取失败", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
