package example.com.xinyuepleayer.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import example.com.xinyuepleayer.R;
import example.com.xinyuepleayer.base.BaseActivity;
import example.com.xinyuepleayer.utils.CacheUtils;

/**
 * 登录
 */
public class LoginActivity extends BaseActivity {
    private EditText mAccountEditText;//用户名
    private EditText mPwdEditText;//密码
    private TextView signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        requestPermission();
        initView();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void initView() {
        mAccountEditText = (EditText) findViewById(R.id.edit_account);
        mPwdEditText = (EditText) findViewById(R.id.edit_password);
        signUp = (TextView) findViewById(R.id.tv_signup);

        if (TextUtils.isEmpty(mAccountEditText.getText())) {
            mAccountEditText.requestFocus();
        }
        //登录按钮
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        //注册
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivity(SignUpActivity.class, null);
            }
        });
    }


    /**
     * 登录
     */

    private void signIn() {
        //finish();
        if (TextUtils.isEmpty(mAccountEditText.getText())) {
            toast("请输入账号");
        } else if (TextUtils.isEmpty(mPwdEditText.getText())) {
            toast("请输入密码");
        }
        if (mAccountEditText.getText().toString().equals(CacheUtils.getString(this, "phone")) &&
                mPwdEditText.getText().toString().equals(CacheUtils.getString(this, "pass"))) {
            toast("登录成功");
            CacheUtils.saveIsLogin(this, "isLogin", true);
            finish();
        } else {
            toast("用户或密码错误");
        }
    }


    public static final int EXTERNAL_STORAGE_REQ_CODE = 110;

    /**
     * 申请权限
     */
    private void requestPermission() {
        // 判断当前Activity是否已经获得了该权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // 如果App的权限申请曾经被用户拒绝过，就需要在这里跟用户做出解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showAlert("请进入设置-应用管理-打开存储权限");
            } else {
                // 进行权限请求
                ActivityCompat
                        .requestPermissions(
                                this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                EXTERNAL_STORAGE_REQ_CODE);
            }
        }
    }


    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_STORAGE_REQ_CODE: {
                // 如果请求被拒绝，那么通常grantResults数组为空
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 申请成功，进行相应操作
                } else {
                    // 申请失败，可以继续向用户解释。
                    showAlert("没有存储读取权限,您可能无法查看保存文件");
                }
                return;
            }
        }
    }
}

