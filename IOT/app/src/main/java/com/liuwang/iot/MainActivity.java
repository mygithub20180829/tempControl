package com.liuwang.iot;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.com.newland.nle_sdk.requestEntity.SignIn;
import cn.com.newland.nle_sdk.responseEntity.User;
import cn.com.newland.nle_sdk.responseEntity.base.BaseResponseEntity;
import cn.com.newland.nle_sdk.util.NetWorkBusiness;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText username,password;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login=findViewById(R.id.login);
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void signIn() {
        String platformAddress="http://api.nlecloud.com:80/";
        final String _username=username.getText().toString();//17674738454
        final String _password=password.getText().toString();//123456
        if (_username.equals("") || _password.equals("")){
            Toast.makeText(this,"用户名或密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        final NetWorkBusiness netWorkBusiness=new NetWorkBusiness("",platformAddress);
        netWorkBusiness.signIn(new SignIn(_username, _password),new Callback<BaseResponseEntity<User>>(){

            @Override
            public void onResponse(Call<BaseResponseEntity<User>> call, Response<BaseResponseEntity<User>> response) {
                BaseResponseEntity<User> baseResponseEntity=response.body();//获得响应体
                if (baseResponseEntity!=null){
                    if (baseResponseEntity.getStatus()==0){
                        String accessToken=baseResponseEntity.getResultObj().getAccessToken();
                        Intent intent=new Intent(MainActivity.this,MenuActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putString("accessToken",accessToken);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(MainActivity.this,baseResponseEntity.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponseEntity<User>> call, Throwable t) {
                Toast.makeText(MainActivity.this,"登录失败"+t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
