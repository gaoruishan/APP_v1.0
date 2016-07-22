package test.grs.com.ims.message;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import com.lidroid.xutils.BitmapUtils;

import java.io.File;

import test.grs.com.ims.IMBaseActivity;
import test.grs.com.ims.R;
import test.grs.com.ims.view.TouchImageView;

public class ZoomPicActivity extends IMBaseActivity {

//    private ZoomImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_pic);
        TouchImageView imageView = (TouchImageView)findViewById(R.id.iv_img_zoom1);
        Intent intent = getIntent();
        if (intent != null) {
            String url = intent.getStringExtra("imageUrl");
            if (url != null && !url.equals("")) {
                File file = new File(Environment.getExternalStorageDirectory(), "xmpp/download/");
                BitmapUtils utils = new BitmapUtils(this, file.getPath());
                utils.configDiskCacheEnabled(true);//防止溢出
                if (Build.VERSION.SDK_INT >= 21) {
                    utils.configMemoryCacheEnabled(false);
                }
                utils.display(imageView, url);
            } else {
                byte[] buff = intent.getByteArrayExtra("image");
                Bitmap bitmap = BitmapFactory.decodeByteArray(buff, 0, buff.length);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
//                    imageView.setOnTouchListener(new ImageTouchListener(imageView,this));
                }
            }
//            imageView.setOnTouchListener(new ImageTouchListener(imageView,this));
        }
    }

}
