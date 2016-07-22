package test.grs.com.ims.message;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.littlec.sdk.business.RegisterForm;
import com.littlec.sdk.constants.CMSdkContants;
import com.littlec.sdk.manager.CMIMHelper;
import com.littlec.sdk.utils.CMChatListener;
import com.littlec.sdk.utils.SdkUtils;

import java.util.HashMap;

import test.grs.com.ims.IMBaseActivity;
import test.grs.com.ims.R;

/**
 * Created by gaoruishan on 15/10/26.
 */
public class RegisterActivity  extends IMBaseActivity {
    private ImageView mBack_ImageView;
    private Button mRegister_Button;
    private HashMap<String,String> mRegisterMap=new HashMap<String, String>();
    private RegisterForm form;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }
    private void initView() {
        mBack_ImageView = (ImageView)findViewById(R.id.btn_back);
        mRegister_Button = (Button)findViewById(R.id.btn_register);
        mBack_ImageView.setOnClickListener(mOnClickListener);
        mRegister_Button.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            int view_id = view.getId();

            if(R.id.btn_back == view_id) {
                finish();
            }
            else if(R.id.btn_register == view_id) {
                final String userName = ((EditText)findViewById(R.id.et_username)).getText().toString();
                String nickName = ((EditText)findViewById(R.id.et_nickname)).getText().toString();
//                final String phone = ((EditText)findViewById(R.id.et_phone)).getText().toString();
                String passWord = ((EditText)findViewById(R.id.et_password)).getText().toString();
                String confirmPassWord = ((EditText)findViewById(R.id.comfirm_password)).getText().toString();
                mRegisterMap.clear();
                mRegisterMap.put(CMSdkContants.CM_USER_NAME,userName);
                if(nickName != null)
                    mRegisterMap.put(CMSdkContants.CM_NICK_NAME, nickName);
//                  mRegisterMap.put(CMSdkContants.CM_PHONE,phone);
                    mRegisterMap.put(CMSdkContants.CM_PASSWORD,passWord);
                    mRegisterMap.put(CMSdkContants.CM_CONFIRM_PASSWORD, confirmPassWord);
                    CMIMHelper.getCmAccountManager().createAccount(mRegisterMap, new CMChatListener.OnCMListener() {
                        @Override
                        public void onSuccess() {
                            Log.e("doRegister－onSuccess", "TAG");
                            Toast.makeText(RegisterActivity.this,"注册成功！",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailed(String s) {
                            Log.e("doRegister－onFailed", "TAG");
                            Toast.makeText(RegisterActivity.this,"注册失败："+s,Toast.LENGTH_SHORT).show();
                        }
                    });


//                SdkUtils.checkRegisterDataWithPhone(mRegisterMap, new CMChatListener.OnCMListener() {
//
//                    @Override
//                    public void onSuccess() {
////                        showSendSmsConfirmDialog(phone);
//                    }
//
//                    @Override
//                    public void onFailed(String errorMsg) {
//                        Toast.makeText(CMIMHelper.getCmAccountManager().getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
//                    }
//                });
            }
        }
    };
}
