package com.yain.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.chat.EMGroupOptions;
import com.yain.R;
import com.yain.model.Model;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.exceptions.HyphenateException;

public class NewGroupActivity  extends Activity {

    private EditText et_newgroup_name;
    private EditText et_newgroup_desc;
    private CheckBox cb_newgroup_public;
    private CheckBox cb_newgroup_invite;
    private Button bt_newgroup_create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        initView();
        initListener();
    }

    private void initListener() {
        // 创建按钮的点击事件处理
        bt_newgroup_create.setOnClickListener(v -> {
            // 跳转到选择联系人页面
            Intent intent = new Intent(NewGroupActivity.this, PickContactActivity.class);
            startActivityForResult(intent, 1);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 成功获取到联系人
        if (resultCode == RESULT_OK) {
            // 创建群
            createGroup(data.getStringArrayExtra("members"));
        }
    }

    // 创建群
    private void createGroup(final String[] memberses) {
        // 群名称
        final String groupName = et_newgroup_name.getText().toString();
        // 群描述
        final String groupDesc = et_newgroup_desc.getText().toString();
        Model.getInstance().getGlobalThreadPool().execute(() -> {
            // 去环信服务器创建群
            // 参数一：群名称；参数二：群描述；参数三：群成员；参数四：原因；参数五：参数设置
            EMGroupOptions options = new EMGroupOptions();

            options.maxUsers = 200;//群最多容纳多少人
            EMGroupManager.EMGroupStyle groupStyle = null;

            if (cb_newgroup_public.isChecked()) {//公开
                if (cb_newgroup_invite.isChecked()) {// 开放群邀请
                    groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
                } else {
                    groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;
                }
            } else {
                if (cb_newgroup_invite.isChecked()) {// 开放群邀请
                    groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
                } else {
                    groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
                }
            }
            options.style = groupStyle; // 创建群的类型
            try {
                EMClient.getInstance().groupManager().createGroup(groupName, groupDesc, memberses, "申请加入群", options);
                runOnUiThread(() -> {
                    Toast.makeText(NewGroupActivity.this, "创建群成功", Toast.LENGTH_SHORT).show();
                    // 结束当前页面
                    finish();
                });
            } catch (HyphenateException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(NewGroupActivity.this, "创建群失败", Toast.LENGTH_SHORT).show());
            }
        });
    }

    // 初始化view
    private void initView() {
        et_newgroup_name = findViewById(R.id.et_newgroup_name);
        et_newgroup_desc = findViewById(R.id.et_newgroup_desc);
        cb_newgroup_public = findViewById(R.id.cb_newgroup_public);
        cb_newgroup_invite = findViewById(R.id.cb_newgroup_invite);
        bt_newgroup_create = findViewById(R.id.bt_newgroup_create);
    }

}
