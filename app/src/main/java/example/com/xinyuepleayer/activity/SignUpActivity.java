package example.com.xinyuepleayer.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import example.com.xinyuepleayer.R;
import example.com.xinyuepleayer.base.BaseActivity;
import example.com.xinyuepleayer.utils.CacheUtils;

/**
 * 注册
 */
public class SignUpActivity extends BaseActivity {
    private EditText name, phone, newPass, againPass;
    private Button signUp;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initView();
    }

    private void initView() {
        name = (EditText) findViewById(R.id.edit_name);
        phone = (EditText) findViewById(R.id.edit_call_num);
        newPass = (EditText) findViewById(R.id.edit_new_pass);
        againPass = (EditText) findViewById(R.id.edit_again_pass);
        signUp = (Button) findViewById(R.id.btn_submit);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(name.getText())) {
                    toast("请输入昵称");
                } else if (TextUtils.isEmpty(phone.getText())) {
                    toast("请输入手机号");
                } else if (TextUtils.isEmpty(newPass.getText())) {
                    toast("密码不能为空");
                    return;
                } else if (TextUtils.isEmpty(againPass.getText())) {
                    toast("确认密码不能为空");
                    return;
                } else if (!newPass.getText().toString().equals(againPass.getText().toString())) {
                    toast("两次输入的密码不一致");
                    return;
                } else {
                    register();
                }
            }
        });
        back = (Button) findViewById(R.id.btn_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 注册
     */
    private void register() {
        CacheUtils.putString(this, "name", name.getText().toString().trim());
        CacheUtils.putString(this, "pass", againPass.getText().toString());
        CacheUtils.putString(this, "phone", phone.getText().toString().trim());
        toast("注册成功");
        finish();
    }
}
