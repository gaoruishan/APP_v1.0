package com.cmcc.hyapps.andyou.upyun;

import android.content.Context;
import android.os.AsyncTask;

import com.cmcc.hyapps.andyou.model.QHFriendInfo;
import com.cmcc.hyapps.andyou.model.QHUser;

import java.io.File;
import java.util.List;
import java.util.Map;

//import com.lidroid.xutils.HttpUtils;
//import com.lidroid.xutils.exception.HttpException;
//import com.lidroid.xutils.http.RequestParams;
//import com.lidroid.xutils.http.ResponseInfo;
//import com.lidroid.xutils.http.callback.RequestCallBack;
//import com.lidroid.xutils.http.client.HttpRequest;
//import com.lidroid.xutils.http.client.multipart.HttpMultipartMode;
//import com.lidroid.xutils.http.client.multipart.MultipartEntity;


public class UploadTask extends AsyncTask<Object, String, String> {
    public interface UploadCallBack {
        public void onSuccess(String result);

        public void onFailed();
    }

    ;
    private UploadCallBack mCallBack;
    private Context mContext;
    private File file;
    List<File> imgFiles;
    int object_id;
    int ctype;
    int gender;
    int type;//0 评论，1更新用户信息 2，发布攻略。3发布圈子动态
    String nick_name;//
    String title;//
    String start_date;//
    String url;//
    String content;
    Map<String, String> mParams;
    Map<String, File> fileParams;
    QHUser mQHUser;

    private QHFriendInfo mQHFriendInfo;
    private boolean isLocation;
    public UploadTask(Context mContext, List<File> imgFiles, String content, int object_id, int ctype, UploadCallBack mCallBack) {
        this.mContext = mContext;
        this.mCallBack = mCallBack;
        this.imgFiles = imgFiles;
        this.content = content;
        this.object_id = object_id;
        this.ctype = ctype;
    }

    public UploadTask(Context mContext, String url, int type, List<File> imgFiles, String nick_name, int gender, UploadCallBack mCallBack) {
        this.mContext = mContext;
        this.mCallBack = mCallBack;
        this.imgFiles = imgFiles;
        this.type = type;
        this.url = url;
        this.nick_name = nick_name;
        this.gender = gender;
    }

    public UploadTask(Context mContext, String url, int type, List<File> imgFiles, QHUser user, UploadCallBack mCallBack) {
        this.mContext = mContext;
        this.mCallBack = mCallBack;
        this.imgFiles = imgFiles;
        this.type = type;
        this.url = url;
        this.mQHUser = user;
    }

    public UploadTask(Context mContext, String url, int type, Map<String, File> fileParams, Map<String, String> params, UploadCallBack mCallBack) {
        this.mContext = mContext;
        this.mCallBack = mCallBack;
        this.imgFiles = imgFiles;
        this.type = type;
        this.url = url;
        this.title = title;
        this.start_date = start_date;
        this.mParams = params;
        this.fileParams = fileParams;
    }

    public UploadTask(Context mContext, List<File> imgFiles,int type ,QHFriendInfo qhFriendInfo, boolean isLocation,UploadCallBack mCallBack) {
        this.mContext = mContext;
        this.mCallBack = mCallBack;
        this.imgFiles = imgFiles;
        this.mQHFriendInfo = qhFriendInfo;
        this.isLocation = isLocation;
        this.type = type;
    }
    private String commentText;
    private int infoId;
    private int commentId;
    public UploadTask(Context mContext,int infoId ,String text,int commentId,int type,UploadCallBack mCallBack) {
        this.mContext = mContext;
        this.mCallBack = mCallBack;
        this.commentId = commentId;
        this.infoId = infoId;
        this.commentText = text;
        this.type = type;
    }

    @Override
    protected String doInBackground(Object... params) {
        String string = null;
        publishProgress("图片上传中...");
        try {
            if (type == 0)
                string = Uploader.upload(mContext, imgFiles, content, object_id, ctype);
            else if (type == 1) {
                string = UserInfoUploader.upload(mContext, url, imgFiles, mQHUser);
            } else if (type == 2) {
                string = RaidersUploader.upload(mContext, url, fileParams, mParams);
            }else if (type == 3){
                string = UploadTrendsTask.upload(mContext,imgFiles,mQHFriendInfo,isLocation);
            }
            else if (type == 5){
                string = UpFriendsCommentLoader.upload(mContext,commentText,infoId,commentId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //	publishProgress();
        if (null != imgFiles) {
            for (File file : imgFiles) {
                if (file != null && file.exists())
                    file.delete();
            }
        }
        if (result != null) {
            mCallBack.onSuccess(result);
        } else {
            mCallBack.onFailed();
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        /*if (mPrgDlg == null) {
			mPrgDlg = new CustomProgressDialogForXF(mContext, "图片上传中...");
		}
		if (values == null || (values != null && values.length == 0)) {
			// 取消进度条显示
			mPrgDlg.dismiss();
		} else {
			mPrgDlg.show();
		}*/
    }

    @Override
    protected void onCancelled() {
		/*if (mPrgDlg != null) {
			mPrgDlg.dismiss();
		}*/
        if (file != null && file.exists()) {
            file.delete();
        }
    }

}
