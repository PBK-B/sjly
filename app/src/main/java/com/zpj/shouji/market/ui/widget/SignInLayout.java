//package com.zpj.shouji.market.ui.widget;
//
//import android.content.Context;
//import androidx.annotation.Nullable;
//import android.text.SpannableString;
//import android.text.Spanned;
//import android.text.method.LinkMovementMethod;
//import android.text.style.URLSpan;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.zpj.shouji.market.R;
//import com.zpj.shouji.market.ui.fragment.WebFragment;
//import com.zpj.utils.ScreenUtils;
//import com.zpj.widget.checkbox.SmoothCheckBox;
//import com.zpj.widget.editor.AccountInputView;
//import com.zpj.widget.editor.EmailInputView;
//import com.zpj.widget.editor.PasswordInputView;
//import com.zpj.widget.editor.validator.EmailValidator;
//import com.zpj.widget.editor.validator.LengthValidator;
//import com.zpj.widget.editor.validator.SameValueValidator;
//
//public class SignInLayout extends LinearLayout {
//
//    private AccountInputView etAccount;
//    private PasswordInputView etPassword;
//    private PasswordInputView etPasswordAgain;
//    private EmailInputView etEmail;
//
//    private SmoothCheckBox cbAgreement;
//    private TextView tvAgreement;
//    private TextView tvSignIn;
//
//    public SignInLayout(Context context) {
//        this(context, null);
//    }
//
//    public SignInLayout(Context context, @Nullable AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public SignInLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init(context);
//    }
//
//    private void init(Context context) {
//        setOrientation(VERTICAL);
//
//        LayoutInflater.from(context).inflate(R.layout.layout_sign_in, this, true);
//
//        int padding = ScreenUtils.dp2pxInt(context, 16);
//        setPadding(padding, padding, padding, padding);
//
//
//        etAccount = findViewById(R.id.et_account);
//        etAccount.addValidator(new LengthValidator("账号长度必须在3-20之间", 3, 20));
//        etPassword = findViewById(R.id.et_password);
//        etPassword.addValidator(new LengthValidator("密码长度不能小于6", 6, Integer.MAX_VALUE));
//        etPasswordAgain = findViewById(R.id.et_password_again);
//        etPasswordAgain.addValidator(new LengthValidator("密码长度不能小于6", 6, Integer.MAX_VALUE));
//        etPasswordAgain.addValidator(new SameValueValidator(etPassword.getEditText(), "两次输入的密码不相同"));
//        etEmail = findViewById(R.id.et_email);
//        etEmail.addValidator(new EmailValidator("邮箱格式有误"));
//        cbAgreement = findViewById(R.id.cb_agreement);
//        tvAgreement = findViewById(R.id.tv_agreement);
//
//
//        String text = "同意《用户协议》和《隐私协议》";
//        SpannableString sp = new SpannableString(text);
//
//
//        int index1 = text.indexOf("用户协议");
//        int index2 = text.indexOf("隐私协议");
//        sp.setSpan(new URLSpan("https://m.shouji.com.cn/sjlyyhxy.html") {
//                       @Override
//                       public void onClick(View widget) {
//                           widget.setTag(true);
//                           Log.d("widget", "widget=" + widget);
//                           WebFragment.start(getURL());
//                       }
//                   }, index1, index1 + 4,
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        sp.setSpan(new URLSpan("https://m.shouji.com.cn/ysxy.html") {
//                       @Override
//                       public void onClick(View widget) {
//                           widget.setTag(true);
//                           Log.d("widget", "widget=" + widget);
//                           WebFragment.start(getURL());
//                       }
//                   }, index2, index2 + 4,
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        tvAgreement.setText(sp);
//        tvAgreement.setMovementMethod(LinkMovementMethod.getInstance());
//        tvAgreement.setTag(false);
//        tvAgreement.setOnClickListener(v -> {
//            if ((boolean)tvAgreement.getTag()) {
//                tvAgreement.setTag(false);
//                return;
//            }
//            cbAgreement.setChecked(!cbAgreement.isChecked(), true);
//            Log.d("widget", "onClick v=" + v);
//        });
//
//        tvSignIn = findViewById(R.id.tv_sign_in);
//        tvSignIn.setOnClickListener(v -> {
////            if (cbAgreement.isChecked()) {
////                if (etAccount.isValid() && etPassword.isValid() && etEmail.isValid()) {
////                    String accountName = etAccount.getText().toString();
////                    String password = etPassword.getText().toString();
////                    String email = etPassword.getText().toString();
////                    ZToast.normal("onClick");
////                    UserManager.getInstance().signUp(accountName, password, email);
////                } else {
////                    ZToast.warning("输入内容有误");
////                }
////            } else {
////                ZToast.warning("请同意《手机乐园协议》");
////            }
//
//        });
//    }
//
//    @Override
//    public void clearFocus() {
//        super.clearFocus();
//        if (etAccount != null) {
//            etAccount.clearFocus();
//        }
//        if (etPassword != null) {
//            etPassword.clearFocus();
//        }
//        if (etPasswordAgain != null) {
//            etPasswordAgain.clearFocus();
//        }
//        if (etEmail != null) {
//            etEmail.clearFocus();
//        }
//    }
//
//    public String getAccountText() {
//        return etAccount.getText().toString();
//    }
//
//    public String getPasswordText() {
//        return etPassword.getText().toString();
//    }
//
//    public boolean isAgree() {
//        return cbAgreement.isChecked();
//    }
//
////    @Override
////    public void onSignUpSuccess() {
////
////    }
////
////    @Override
////    public void onSignUpFailed(String errInfo) {
////        if ("用户名已被注册".equals(errInfo)) {
////            etAccount.requestFocus();
////            etAccount.setError(errInfo);
////        } else {
////            ZToast.error(errInfo);
////        }
////    }
//
//    public interface OnLoginListener {
//        void onSignInSuccess();
//    }
//
//}
