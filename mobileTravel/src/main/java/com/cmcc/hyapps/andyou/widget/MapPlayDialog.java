package com.cmcc.hyapps.andyou.widget;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.Image;
import com.cmcc.hyapps.andyou.util.ImageUtil;

public class MapPlayDialog extends Dialog {
	private View.OnClickListener ok=null, cancel=null;
	private Context context;
    private TextView secnic_mame,secnic_distance,secnic_intro;
    private ImageView play_pause_img,close_img;
	private int play_state = 0;//1播放状态，0暂停状态
    private NetworkImageView secnicImg;
    private String imgUrl ;
    private String secnicname = "";
	
	public MapPlayDialog(Context context, int play_state, View.OnClickListener ok, View.OnClickListener cancel,String name){
		super(context, R.style.dialog);
        this.context = context;
		this.ok=ok;
		this.cancel=cancel;
        secnicname = name;

	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.secnic_map_dialog);
		initView();
	}

	private void initView() {
        secnic_mame = (TextView) findViewById(R.id.item_name);
        secnic_distance = (TextView) findViewById(R.id.item_distance);
        secnic_intro = (TextView) findViewById(R.id.secnic_intro);
        play_pause_img = (ImageView) findViewById(R.id.playpause);
        close_img = (ImageView) findViewById(R.id.item_close);
        secnicImg = (NetworkImageView) findViewById(R.id.iv_cover_image);

        play_pause_img.setOnClickListener(ok);
        close_img.setOnClickListener(cancel);

        if(play_state==0){
            play_pause_img.setImageResource(R.drawable.map_audio_pause);
        }else if(play_state==1){
            play_pause_img.setImageResource(R.drawable.map_audio_play);
        }
        if (!TextUtils.isEmpty(imgUrl)) {
            ImageUtil.DisplayImage(imgUrl, secnicImg, R.drawable.bg_image_hint, R.drawable.bg_image_hint);
//            RequestManager.getInstance().getImageLoader().get(imgUrl,ImageLoader.getImageListener(secnicImg, R.drawable.bg_image_hint, R.drawable.bg_image_hint));
        }
        secnic_mame.setText(secnicname);
	}

    public void updateDialog(String url,String name,String distance,String content,int play_state){
        this.imgUrl = url;
        this.play_state = play_state;
        if (!TextUtils.isEmpty(imgUrl)) {
            ImageUtil.DisplayImage(imgUrl, secnicImg, R.drawable.bg_image_hint, R.drawable.bg_image_hint);
//            RequestManager.getInstance().getImageLoader().get(imgUrl, ImageLoader.getImageListener(secnicImg, R.drawable.bg_image_hint, R.drawable.bg_image_hint));
        }
        if(!"".equals(name))secnic_mame.setText(name);
        secnic_distance.setText(distance);
        secnic_intro.setText(content);

        if(play_state==0){
            play_pause_img.setImageResource(R.drawable.map_audio_pause);
        }else if(play_state==1){
            play_pause_img.setImageResource(R.drawable.map_audio_play);
        }

    }
    public void updateDialog(int play_state){
        this.play_state = play_state;
        if(play_state==0){
            play_pause_img.setImageResource(R.drawable.map_audio_pause);
        }else if(play_state==1){
            play_pause_img.setImageResource(R.drawable.map_audio_play);
        }

    }
	@Override    
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
		/*if(keyCode == KeyEvent.KEYCODE_BACK){
		   return  false;
		} */
		return  super.onKeyDown(keyCode, event);     

	} 
}
