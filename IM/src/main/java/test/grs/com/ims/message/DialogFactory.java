package test.grs.com.ims.message;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import test.grs.com.ims.R;

public class DialogFactory {
	// two buttons, OK button and Cancel button
	public final static int DIALOG_TYPE_CONFIRM = 0; 
	// only one button
	public final static int DIALOG_TYPE_TIP = DIALOG_TYPE_CONFIRM + 1;
	// no button
	public final static int DIALOG_TYPE_LOADING = DIALOG_TYPE_TIP + 1;
	public static Dialog getConfirmDialog(Context activity, String title,
										  String msg, String cancelBtnString, String okBtnString,
										  final View.OnClickListener cancelBtnEvent,
										  final View.OnClickListener okBtnEvent) {

		final Dialog dialog = new Dialog(activity,
				R.style.Theme_CustomDialog);
		final View dialogview = LayoutInflater.from(activity).inflate(
				R.layout.dialog_common, null);
		dialog.setContentView(dialogview);

		TextView titleView = (TextView) dialogview
				.findViewById(R.id.dialog_title);
		if (title == null) {
			titleView.setVisibility(View.GONE);
		} else {
			titleView.setText(title);
		}

		TextView msgTextView = (TextView) dialogview
				.findViewById(R.id.dialog_msg);
		if (msg != null) {
			msgTextView.setText(msg);
		}

		Button okButton = (Button) dialogview.findViewById(R.id.dialog_ok_btn);
		if(!TextUtils.isEmpty(okBtnString)){
			okButton.setText(okBtnString);
		}
		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (okBtnEvent != null) {
					okBtnEvent.onClick(v);
				}
				dialog.dismiss();
			}
		});

		final Button cancelButton = (Button) dialogview
				.findViewById(R.id.dialog_cancel_btn);
		if(!TextUtils.isEmpty(cancelBtnString)){
			cancelButton.setText(cancelBtnString);
		}
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (cancelBtnEvent != null) {
					cancelBtnEvent.onClick(v);
				}
				dialog.dismiss();
			}
		});
		dialog.setCancelable(true);

		return dialog;
	}
	public static Dialog getConfirmDialog2(Context activity, String title,
										   String msg, String cancelBtnString, String okBtnString,
										   final View.OnClickListener cancelBtnEvent,
										   final View.OnClickListener okBtnEvent) {

		final Dialog dialog = new Dialog(activity,
				R.style.Theme_CustomDialog);
		final View dialogview = LayoutInflater.from(activity).inflate(
				R.layout.dialog_common2, null);
		dialog.setContentView(dialogview);

		TextView titleView = (TextView) dialogview
				.findViewById(R.id.dialog_title);
		if (title == null) {
			titleView.setVisibility(View.GONE);
		} else {
			titleView.setText(title);
		}

		TextView msgTextView = (TextView) dialogview
				.findViewById(R.id.dialog_msg);
		if (msg != null) {
			msgTextView.setText(msg);
		}

		TextView okButton = (TextView) dialogview.findViewById(R.id.dialog_ok_btn);
		if(!TextUtils.isEmpty(okBtnString)){
			okButton.setText(okBtnString);
		}
		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (okBtnEvent != null) {
					okBtnEvent.onClick(v);
				}
				dialog.dismiss();
			}
		});

		final TextView cancelButton = (TextView) dialogview
				.findViewById(R.id.dialog_cancel_btn);
		if(!TextUtils.isEmpty(cancelBtnString)){
			cancelButton.setText(cancelBtnString);
		}
		cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelBtnEvent != null) {
                    cancelBtnEvent.onClick(v);
                }
                dialog.dismiss();
            }
        });
		dialog.setCancelable(true);

		return dialog;
	}

	public static Dialog getListDialog(Activity activity, String title,
			String[] data, OnItemClickListener listener) {
		final Dialog dialog = new Dialog(activity,
				R.style.Theme_CustomDialog);
		final View dialogview = LayoutInflater.from(activity).inflate(
				R.layout.dialog_list, null);
		ListView dialog_list = (ListView) dialogview.findViewById(R.id.dialog_list);
		((TextView) dialogview.findViewById(R.id.dialog_list_title)).setText(title);
		dialog_list.setAdapter(new MyDialogListAdapter(activity, data));
		dialog_list.setOnItemClickListener(listener);;
		dialog.setContentView(dialogview);
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();  
        layoutParams.width = mDisplayMetrics.widthPixels/4*3;  
        layoutParams.height = LayoutParams.WRAP_CONTENT;  
        dialog.getWindow().setAttributes(layoutParams); 
        dialog.show();
		return dialog;
	}

	public static Dialog getLoadingDialog(Activity activity, String msg,
                                          boolean cancelable,
                                          final DialogInterface.OnCancelListener cancelEvent) {
		final Dialog dialog = new Dialog(activity,
				R.style.Theme_CustomDialog);

		View contentView = LayoutInflater.from(activity).inflate(
				R.layout.dialog_loading, null);

		ImageView aniImage = (ImageView) contentView.findViewById(R.id.ani_img);
		TextView msgView = (TextView) contentView.findViewById(R.id.message);

		Animation ani = AnimationUtils.loadAnimation(activity,
                R.anim.loading_ani);
		aniImage.startAnimation(ani);
		if (TextUtils.isEmpty(msg)) {
			msgView.setVisibility(View.GONE);
		} else {
			msgView.setVisibility(View.VISIBLE);
			msgView.setText(msg);
		}

		if (cancelable) {
			if (cancelEvent != null) {
				dialog.setOnCancelListener(cancelEvent);
			}
		} else {
			dialog.setCancelable(false);
		}

		dialog.setContentView(contentView);
		return dialog;
	}

	public static Dialog getLoginDialog(Activity activity,final View.OnClickListener cancelBtnEvent,
									  final View.OnClickListener okBtnEvent) {
		final Dialog dialog = new Dialog(activity,
				R.style.Theme_CustomDialog);
		final View dialogview = LayoutInflater.from(activity).inflate(
				R.layout.dialog_login, null);
		dialog.setContentView(dialogview);
        final List<String> list = new ArrayList<String>();
        final EditText titleView = (EditText) dialogview
				.findViewById(R.id.dialog_username);


        final EditText msgTextView = (EditText) dialogview
				.findViewById(R.id.dialog_psw);

		TextView okButton = (TextView) dialogview.findViewById(R.id.dialog_ok_btn);

		okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (okBtnEvent != null) {
                    list.add(titleView.getText().toString());
                    list.add(msgTextView.getText().toString());
//                    MessageListActivity.userName=titleView.getText().toString();
//                    MessageListActivity.passWord=msgTextView.getText().toString();
                    Log.e(titleView.getText().toString() + "====" + msgTextView.getText().toString(), "TAG");
                    v.setTag(list);
                    okBtnEvent.onClick(v);
                }
                dialog.dismiss();
            }
        });

		final TextView cancelButton = (TextView) dialogview
				.findViewById(R.id.dialog_cancel_btn);

		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (cancelBtnEvent != null) {
					cancelBtnEvent.onClick(v);
				}
				dialog.dismiss();
			}
		});
		dialog.setCancelable(true);
        return dialog;
	}

    public static class MyDialogListAdapter extends BaseAdapter {
		private String[] data;
		private LayoutInflater mInflater;

		public MyDialogListAdapter(Context context, String[] data) {
			// TODO Auto-generated constructor stub
			this.data = data;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView = mInflater.inflate(R.layout.dialog_list_item, null);
			((TextView) convertView.findViewById(R.id.dialog_item_title)).setText(data[position]);;
			return convertView;
		}

	}

}