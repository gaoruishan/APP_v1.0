
package test.grs.com.ims;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * The base activity, all activity should extends it.
 * 
 * @author kuloud
 */
public abstract class IMBaseActivity extends FragmentActivity {
    protected Activity activity;
    protected String requestTag;
    public final int REQUEST_CODE_LOGIN_NEW_COMMENT = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //进出和退出动画
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        requestTag = getClass().getName();
        activity = this;
    }


    @Override
    public void finish() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.finish();
    }


    /**
     * app字体不随系统字体变化
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config=new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }
}
