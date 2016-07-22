/**
 * 项目名称：EIMClient
 * 类 名 称：refreshCallBack
 * 类 描 述：(描述信息)
 * 创 建 人：CB
 * 创建时间：2015-3-6 上午11:29:09
 * 修 改 人：CB
 * 修改时间：2015-3-6 上午11:29:09
 * 修改备注：
 * @version
 * 
*/
package test.grs.com.ims.message;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.littlec.sdk.business.MessageConstants;
import com.littlec.sdk.entity.CMMessage;

import test.grs.com.ims.R;

/**
 * @包名：com.littlec.chatdemo.listener
 * @类名：refreshCallBack
 * @描述：(描述这个类的作用)
 * @作者：CB
 * @时间：2015-3-6上午11:29:09
 * @版本：1.0.0
 * 
 */
public class RefreshCallBack {
	   private CMMessage message=null;
	   private  View  voiceView=null;
	   public RefreshCallBack(CMMessage message){
		      this.message=message;
	   }
	   
	   public void notice(CMMessage msg,View newVoiceView){
		      if( (msg!=null  && message!=null)  &&  (msg.getId()==message.getId())){
		    	  if(this.voiceView!=null)//音频播放期间收到多个音频
		    		  stop();//停止当前播放
		    	  this.voiceView=newVoiceView;
		    	  if(voiceView!=null)
		    		  updateCallBack(voiceView);
		      }
	   }
	   
	   public void updateCallBack(View voiceView){}
	   
	   public void stop(){
		      if(this.voiceView!=null){
		    	    Drawable drawable = voiceView.getBackground();
					if (drawable instanceof AnimationDrawable) {
						((AnimationDrawable) drawable).stop();
					}

					voiceView
							.setBackgroundResource(message.getSendOrRecv() == MessageConstants.Message.MSG_SEND ? R.drawable.chatto_voice_playing_f3
									: R.drawable.chatfrom_voice_playing_f3);
		      }
	   }
}
