package example.com.xinyuepleayer.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import example.com.xinyuepleayer.R;
import example.com.xinyuepleayer.view.DialogFactory;


/**
 * Created by caobin on 2016/11/18.
 */
public class BaseActivity extends AppCompatActivity {

    private Dialog mProgressDialog = null;//加载进度提示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * toast任何类型的数据
     */
    public void toast(Object o) {
        Toast.makeText(BaseActivity.this, o.toString(), Toast.LENGTH_SHORT).show();
    }

    /**
     * 跳转activity
     *
     * @param cls    跳转的类
     * @param bundle 携带的数据
     */
    protected void launchActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 跳转activity,通过请求码
     *
     * @param cls
     * @param bundle
     * @param code
     */
    protected void launchActivityByCode(Class<?> cls, Bundle bundle, int code) {
        Intent intent = new Intent(this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, code);
    }

    /**
     * 显示进度提示
     */
    protected void showDLG() {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
            mProgressDialog = null;
        }
        try {
            mProgressDialog = DialogFactory.creatCommonDialog(this,
                    getString(R.string.title_wait));
            //mProgressDialog.setCancelable(false);//点击其他地方不消失
            mProgressDialog.show();
        } catch (Exception e) {
        }
    }

    protected void showDlgWithMsg(String msg) {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        }
        mProgressDialog = DialogFactory.creatCommonDialog(this,
                TextUtils.isEmpty(msg) ? getString(R.string.title_wait) : msg);
        mProgressDialog.show();
    }

    protected void disMissDLG() {
        try {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (Exception e) {
        }
    }

    /**
     * 关闭键盘
     */
    public void closeInput() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    /**
     * 弹出AlertDialog
     *
     * @param msg
     */
    protected void showAlert(String msg) {
        new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher)
                .setTitle("温馨提示").setMessage(msg)
                .setPositiveButton("确定", null).create().show();
    }

    @Override
    protected void onDestroy() {
        disMissDLG();
        closeInput();
        super.onDestroy();
    }
}
