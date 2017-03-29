/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ucai.superwechat.ui;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.db.IUserModel;
import cn.ucai.superwechat.db.OnCompleteListener;
import cn.ucai.superwechat.db.UserModel;
import cn.ucai.superwechat.utils.CommonUtils;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.MD5;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.utils.Result;
import cn.ucai.superwechat.utils.ResultUtils;

/**
 * register screen
 */
public class RegisterActivity extends BaseActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_nickname)
    EditText etNickname;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_confirm_password)
    EditText etConfirmPassword;
    ProgressDialog pd;
    String username, nickname, password;
    IUserModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_register);
        ButterKnife.bind(this);
        model = new UserModel();
        initView();
    }

    private void initView() {
        txtTitle.setVisibility(View.VISIBLE);
        txtTitle.setText(R.string.register);
    }

    private void showDialog() {
        pd = new ProgressDialog(this);
        pd.setMessage(getResources().getString(R.string.Is_the_registered));
        pd.show();
    }

    private boolean checkInput() {
        username = etUsername.getText().toString().trim();
        nickname = etNickname.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        String confirm_pwd = etConfirmPassword.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, getResources().getString(R.string.User_name_cannot_be_empty),
                    Toast.LENGTH_SHORT).show();
            etUsername.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(nickname)) {
            Toast.makeText(this, getResources().getString(R.string.toast_nick_not_isnull),
                    Toast.LENGTH_SHORT).show();
            etPassword.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, getResources().getString(R.string.Password_cannot_be_empty),
                    Toast.LENGTH_SHORT).show();
            etPassword.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(confirm_pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Confirm_password_cannot_be_empty),
                    Toast.LENGTH_SHORT).show();
            etConfirmPassword.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(confirm_pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Two_input_password),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void registerEMServer() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    // call method in SDK
                    EMClient.getInstance().createAccount(username, password);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (!RegisterActivity.this.isFinishing())
                                pd.dismiss();
                            // save current user
                            SuperWeChatHelper.getInstance().setCurrentUserName(username);
                            Toast.makeText(getApplicationContext(), getResources().getString(
                                    R.string.Registered_successfully), Toast.LENGTH_SHORT)
                                    .show();
                            finish();
                        }
                    });
                } catch (final HyphenateException e) {
                    unregister();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (!RegisterActivity.this.isFinishing())
                                pd.dismiss();
                            int errorCode = e.getErrorCode();
                            if (errorCode == EMError.NETWORK_ERROR) {
                                Toast.makeText(getApplicationContext(),
                                        getResources().getString(R.string.network_anomalies),
                                        Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.USER_ALREADY_EXIST) {
                                Toast.makeText(getApplicationContext(),
                                        getResources().getString(R.string.User_already_exists),
                                        Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.USER_AUTHENTICATION_FAILED) {
                                Toast.makeText(getApplicationContext(),
                                        getResources().getString(R.string
                                                .registration_failed_without_permission),
                                        Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.USER_ILLEGAL_ARGUMENT) {
                                Toast.makeText(getApplicationContext(),
                                        getResources().getString(R.string.illegal_user_name),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        getResources().getString(R.string.Registration_failed),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }

    private void unregister() {
        model.unregister(RegisterActivity.this, username, new OnCompleteListener<String>() {
            @Override
            public void onSuccess(String result) {
                L.e(TAG, "result=" + result);
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void back(View view) {
        finish();
    }

    @OnClick({R.id.img_back, R.id.btn_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                MFGT.finish(RegisterActivity.this);
                break;
            case R.id.btn_register:
                registerAppServer();
                break;
        }
    }

    private void registerAppServer() {
        if (checkInput()) {
            showDialog();
            L.e(TAG, "password=" + MD5.getMessageDigest(password));
            model.register(RegisterActivity.this, username, nickname,
                    MD5.getMessageDigest(password), new OnCompleteListener<String>() {
                @Override
                public void onSuccess(String s) {
                    L.e(TAG, "s=" + s);
                    boolean success = false;
                    if (s != null) {
                        Result result = ResultUtils.getResultFromJson(s, String.class);
                        if (result != null) {
                            if (result.isRetMsg()) {
                                success = true;
                                registerEMServer();
                            } else if (result.getRetCode() ==
                                    I.MSG_REGISTER_USERNAME_EXISTS) {
                                CommonUtils.showShortToast(R.string.User_already_exists);
                            } else {
                                CommonUtils.showShortToast(R.string.Registration_failed);
                            }
                        }
                    }
                    if (!success) {
                        pd.dismiss();
                    }
                }

                @Override
                public void onError(String error) {
                    L.e(TAG, "error=" + error);
                    pd.dismiss();
                    CommonUtils.showShortToast(R.string.Registration_failed);
                }
            });
        }
    }
}
