package test.grs.com.ims.contact;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import test.grs.com.ims.R;


public class LoadingView extends RelativeLayout {
    private Context mContext;
    private ProgressBar mImageView;
    private TextView mTextView;
	public LoadingView(Context context) {
		super(context);
		mContext=context;
		initView();
	}
	
	public LoadingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext=context;
		initView();
	}
	
	private void initView(){
		LayoutInflater.from(mContext).inflate(R.layout.common_loading_view, this);
		mImageView=(ProgressBar) findViewById(R.id.iv_loading);
		mTextView=(TextView) findViewById(R.id.tv_loading);
	}
	
	public void setImgOnClickListener(OnClickListener listener){
		mImageView.setOnClickListener(listener);
	}
	
	public void setText(int txtRes){
		mTextView.setText(txtRes);
	}

}
