/**
 * 
 */

package com.cmcc.hyapps.andyou.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.FormatUtils;
import com.cmcc.hyapps.andyou.util.NetUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.SyncListener;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.Reply;
import com.umeng.fb.model.UserInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kuloud
 */
public class FeedbackActivity extends BaseActivity {
    private EditText emailEditText,contentEditText;
    private FeedbackAgent feedbackAgent;
    private Conversation defaultConversation;
    private static final String KEY_UMENG_CONTACT_INFO_PLAIN_TEXT = "plain";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initActionBar();
        initView();
    }

    @Override
    protected void onNewIntent(android.content.Intent intent) {
    }

    private void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(R.string.action_bar_title_feedback);
        actionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        actionBar.getRightTextView().setText("发送");
        actionBar.getRightTextView().setVisibility(View.VISIBLE);
        actionBar.getRightTextView().setOnClickListener(new OnClickListener() {
            @Override
            public void onValidClick(View v) {
                sendFeedBack();
            }
        });
        actionBar.getLeftView().setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                finish();
            }
        });
    }

    private void initView(){
        feedbackAgent = new FeedbackAgent(this);
        defaultConversation = feedbackAgent.getDefaultConversation();
       // sync();
        emailEditText = (EditText)this.findViewById(R.id.feedback_email);
        contentEditText = (EditText)this.findViewById(R.id.feedback_content);
    }

    private void sendFeedBack(){
        String email = emailEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();
        if (TextUtils.isEmpty(content)){
            ToastUtils.show(this,"请输入您宝贵的意见");
         //   contentEditText.setError("请输入您宝贵的意见");
            return;
        }
        if (!FormatUtils.isEmail(email)){
            ToastUtils.show(FeedbackActivity.this, R.string.hint_login_input_ok_email);
            return;
        }
        if (!NetUtils.isNetworkAvailable(this)){
            Toast.makeText(this, R.string.network_unavailable,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (!TextUtils.isEmpty(email)) {
            UserInfo info = feedbackAgent.getUserInfo();
            if (info == null)
                info = new UserInfo();
            Map<String, String> contact = info.getContact();
            if (contact == null)
                contact = new HashMap<String, String>();
            contact.put(KEY_UMENG_CONTACT_INFO_PLAIN_TEXT, email);
            info.setContact(contact);
            feedbackAgent.setUserInfo(info);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean result = feedbackAgent.updateUserInfo();
                }
            }).start();
        }

        defaultConversation.addUserReply(content);
        sync();
        FeedbackActivity.this.finish();
    }

    // 数据同步
    private void sync() {

        defaultConversation.sync(new SyncListener() {

            @Override
            public void onSendUserReply(List<Reply> replyList) {
                ToastUtils.show(FeedbackActivity.this, "反馈发送成功");
            }

            @Override
            public void onReceiveDevReply(List<Reply> replyList) {
            }
        });
    }
}
