package com.zpj.shouji.market.ui.widget;

import android.content.Context;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.toast.ZToast;
import com.zpj.widget.editor.AccountInputView;
import com.zpj.widget.editor.PasswordInputView;
import com.zpj.widget.editor.SubmitView;
import com.zpj.widget.editor.validator.LengthValidator;

public class SignInLayout3 extends LinearLayout implements View.OnClickListener  {

    private AccountInputView piv_account;
    private PasswordInputView piv_password;
    private SubmitView svLogin;

    public SignInLayout3(Context context) {
        this(context, null);
    }

    public SignInLayout3(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SignInLayout3(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        LayoutInflater.from(context).inflate(R.layout.layout_sign_in, this, true);


        piv_account = findViewById(R.id.piv_login_account);
        piv_account.addValidator(new LengthValidator("账号长度必须在3-20之间", 3, 20));
        piv_password = findViewById(R.id.piv_login_password);
        piv_password.addValidator(new LengthValidator("密码长度不能小于6", 6, Integer.MAX_VALUE));

        svLogin = findViewById(R.id.sv_login);
        svLogin.setOnClickListener(this);

        TextView tvRegist = findViewById(R.id.tv_regist);
//        tvRegist.setOnClickListener(this);

        TextView tvForgetPassword = findViewById(R.id.tv_forget_password);
        tvForgetPassword.setOnClickListener(this);

        findViewById(R.id.ib_login_qq).setOnClickListener(this);
        findViewById(R.id.ib_login_wechat).setOnClickListener(this);
        findViewById(R.id.ib_login_weibo).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.sv_login:
                String userName = piv_account.getText();
                String password = piv_password.getText();
                UserManager.getInstance().signIn(userName, password);
                break;
            case R.id.tv_forget_password:
                ZToast.normal("TODO");
                break;
            case R.id.ib_login_qq:
                ZToast.warning("TODO QQ第三方登录");
//                EventBus.sendQQLoginEvent();
                break;
            case R.id.ib_login_wechat:
                ZToast.warning("TODO 微信第三方登录");
                break;
            case R.id.ib_login_weibo:
                ZToast.warning("TODO 微博第三方登录");
                break;
        }
    }

    public void onSignIn(boolean isSuccess, String errorMsg) {
        if (!isSuccess) {
            piv_password.setError(errorMsg);
        }
    }

    public AccountInputView getEtAccount() {
        return piv_account;
    }

    public PasswordInputView getEtPassword() {
        return piv_password;
    }

    public SubmitView getSubmitView() {
        return svLogin;
    }

}
