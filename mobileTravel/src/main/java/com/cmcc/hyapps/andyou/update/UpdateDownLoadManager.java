package com.cmcc.hyapps.andyou.update;

import android.app.ProgressDialog;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateDownLoadManager {

    private  boolean isDownload = true;
	private OnDownLoadListener mOnDownLoadListener;

	public void setOnDownLoadListener(OnDownLoadListener onDownLoadListener) {
		mOnDownLoadListener = onDownLoadListener;
	}

	private UpdateDownLoadManager(){}

	public void setIsDownload(boolean isDownload) {
		this.isDownload = isDownload;
	}

	private static class UpdateDownLoadManagerFactory{
		private static UpdateDownLoadManager intance = new UpdateDownLoadManager();
	}

	public static  UpdateDownLoadManager getInstance(){
		return UpdateDownLoadManagerFactory.intance;
	}
	public interface OnDownLoadListener{
		void downLoadCancle();
		void downLoadSuccess(File file);
	}
	public  File getFileFromServer(String path, ProgressDialog pd) throws Exception{

		//如果相等的话表示当前的sdcard挂载在手机上并且是可用的

		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){

			URL url = new URL(path);

			HttpURLConnection conn =  (HttpURLConnection) url.openConnection();

			conn.setConnectTimeout(5000);

			//获取到文件的大小

			pd.setMax(conn.getContentLength());

			InputStream is = conn.getInputStream();

			File file = new File(Environment.getExternalStorageDirectory(), "updata.apk");

			FileOutputStream fos = new FileOutputStream(file);

			BufferedInputStream bis = new BufferedInputStream(is);

			byte[] buffer = new byte[1024];

			int len ;

			int total=0;

			while((len =bis.read(buffer))!=-1){
				if(isDownload){
					fos.write(buffer, 0, len);

					total+= len;

					//获取当前下载量

					pd.setProgress(total);
					if (total == conn.getContentLength()){
						mOnDownLoadListener.downLoadSuccess(file);
					}
				}else
					break;


			}

			fos.close();

			bis.close();

			is.close();

			return file;

		}

		else{

			return null;

		}

	}

}
