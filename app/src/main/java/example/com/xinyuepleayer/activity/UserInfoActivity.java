package example.com.xinyuepleayer.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import example.com.xinyuepleayer.R;
import example.com.xinyuepleayer.base.BaseActivity;

/**
 * 个人信息
 */
public class UserInfoActivity extends BaseActivity {
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        back = (Button) findViewById(R.id.btn_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
