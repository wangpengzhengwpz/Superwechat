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
import android.widget.RelativeLayout;

import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseTitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.db.IUserModel;
import cn.ucai.superwechat.db.OnCompleteListener;
import cn.ucai.superwechat.db.UserModel;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.utils.Result;
import cn.ucai.superwechat.utils.ResultUtils;

public class AddContactActivity extends BaseActivity {
    private static final String TAG = AddContactActivity.class.getSimpleName();
    @BindView(R.id.title_bar)
    EaseTitleBar titleBar;
    @BindView(R.id.edit_note)
    EditText editNote;
    @BindView(R.id.ll_user)
    RelativeLayout llUser;
    private String toAddUsername;
    private ProgressDialog progressDialog;
    IUserModel model;
    User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_add_contact);
        ButterKnife.bind(this);
        initView();
        model = new UserModel();
    }

    private void initView() {
        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MFGT.finish(AddContactActivity.this);
            }
        });
    }


    /**
     * search contact
     *
     * @param v
     */
    public void searchContact(View v) {
        final String name = editNote.getText().toString().trim();
        toAddUsername = name;
        if (TextUtils.isEmpty(name)) {
            new EaseAlertDialog(this, R.string.Please_enter_a_username).show();
            return;
        }
        showDiglog();
        searchUser();
    }

    private void showDiglog() {
        progressDialog = new ProgressDialog(AddContactActivity.this);
        progressDialog.setMessage(getString(R.string.addcontact_search));
        progressDialog.show();
    }

    private void searchUser() {
        model.loadUserInfo(AddContactActivity.this, toAddUsername,
                new OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        L.e(TAG, "s=" + s);
                        boolean success = false;
                        if (s != null) {
                            Result result = ResultUtils.getResultFromJson(s, User.class);
                            if (result != null && result.isRetMsg()) {
                                user = (User) result.getRetData();
                                success = true;
                                //跳转到用户详情
                            }
                        }
                        showResult(success);
                    }

                    @Override
                    public void onError(String error) {
                        L.e(TAG, "error=" + error);
                        showResult(false);
                    }
                });
    }

    private void showResult(boolean success) {
        progressDialog.dismiss();
        llUser.setVisibility(success ? View.GONE : View.VISIBLE);
        if (success) {
            //跳转到用户详情
            MFGT.gotoFriend(AddContactActivity.this,user);
        }
    }

    public void back(View v) {
        finish();
    }
}
