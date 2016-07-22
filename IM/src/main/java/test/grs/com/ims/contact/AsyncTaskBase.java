package test.grs.com.ims.contact;

import android.os.AsyncTask;
import android.view.View;

import test.grs.com.ims.R;
import test.grs.com.ims.view.CircularProgressBar;


public class AsyncTaskBase extends AsyncTask<Integer, Integer, Integer> {
	private CircularProgressBar mLoadingView;
	public AsyncTaskBase(CircularProgressBar loadingView){
		this.mLoadingView=loadingView;
	}
	@Override
	protected Integer doInBackground(Integer... params) {

		return null;
	}

	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		if(result==1){
			mLoadingView.setVisibility(View.GONE);
		}else{
//			mLoadingView.setText(R.string.no_data);
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mLoadingView.setVisibility(View.VISIBLE);
	}

}
