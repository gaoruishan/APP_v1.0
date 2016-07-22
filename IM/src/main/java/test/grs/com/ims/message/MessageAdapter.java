package test.grs.com.ims.message;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.littlec.sdk.business.MessageConstants;
import com.littlec.sdk.entity.CMMessage;
import com.littlec.sdk.entity.messagebody.AudioMessageBody;
import com.littlec.sdk.entity.messagebody.ImageMessageBody;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.List;

import test.grs.com.ims.IMApp;
import test.grs.com.ims.R;
import test.grs.com.ims.view.BubbleImageView;
import test.grs.com.ims.view.CircleImageView;

/**
 * Created by gaoruishan on 15/8/31.
 */
@SuppressWarnings("ALL")
public class MessageAdapter extends AppBaseAdapter<CMMessage> {
    private int chatTime;
    //在继承关系中此处不能再写！！！！！
//    private List<CMMessage> list;
//    public Context context;
    private RefreshCallBack mRefreshCallBack;
    //    private Bitmap bitmap;
    private String smallUri;
    private String url = IMApp.getCurrentAvataUrl();

    public MessageAdapter(List<CMMessage> list, Context context) {
        super(list, context);
        this.list = list;
        this.context = context;
    }

    public void MessageAdapter() {
    }

    public void setDatasChanges(List<CMMessage> list) {
        Log.e("TAG", "setDatasChanges=" + list.size());
        this.list = list;
        notifyDataSetChanged();
    }

    private static HashMap<Integer, Holder> mHoldMap = new HashMap<Integer, Holder>();

    public void updateProgress(CMMessage message) {
        Holder holder = mHoldMap.get(message.getId());
        int progress = message.getProgress() + 1;
        if (holder != null) {
            if (progress >= 99) {
                if (holder.pb_outgoing != null) {
                    holder.pb_outgoing.setVisibility(View.GONE);
                }
                if (holder.tvOutgoing != null) {
                    holder.tvOutgoing.setVisibility(View.GONE);
                }
            } else if (progress >= 0) {
                holder.pb_outgoing.setVisibility(View.VISIBLE);
                holder.tvOutgoing.setVisibility(View.VISIBLE);
                if (holder.tvOutgoing != null) {
                    holder.tvOutgoing.setText(progress + "%");
                }
            } else {
                holder.ivError.setVisibility(View.VISIBLE);
                Log.e("Progress3", "TAG=" + progress);
            }
        }

    }

    @Override
    public synchronized View createView(int position, View convertView, ViewGroup parent) {
        final Holder holder = new Holder();
        final CMMessage message = list.get(position);
        if (message != null && message.getMessageBody() != null) {
            long time = message.getTime();
            switch (message.getSendOrRecv()) {
                case MessageConstants.Message.MSG_SEND: // 发送 0
                    switch (message.getContentType()) {
                        case MessageConstants.Message.TYPE_TEXT:
                            convertView = inflater.inflate(R.layout.row_sent_txt, parent, false);
                            holder.pb_outgoing = (ProgressBar) convertView.findViewById(R.id.pb_outgoing);
                            holder.ivError = (ImageView) convertView.findViewById(R.id.iv_send_state_outgoing);
                            holder.tv_msg = (TextView) convertView.findViewById(R.id.tv_msg);
                            holder.portrait = (CircleImageView) convertView.findViewById(R.id.portrait);
                            displayImages(url, holder.portrait);
                            final String msg_content = message.getMessageBody().getContent();
                            if (msg_content == null) {
                                return null;
                            }

                            String unicode = EmojiParser.getInstance(context).parseEmoji(msg_content);
                            SpannableString spannableString = ParseEmojiMsgUtil.getExpressionString(context, unicode);

                            holder.tv_msg.setText(spannableString);
                            holder.im_item_date = (TextView) convertView.findViewById(R.id.im_item_date);
                            holder.pb_outgoing.setVisibility(View.GONE);
                            if (time == 0) {
                                holder.im_item_date.setText("");
                            } else {
                                holder.im_item_date.setText(IMUtil.getFormattedTime(context, message.getTime()));
                            }
                            holder.tv_msg.setTag(message);
                            holder.tv_msg.setOnLongClickListener(mLongClickListener);
                            break;
                        case MessageConstants.Message.TYPE_AUDIO:
                            convertView = inflater.inflate(R.layout.row_sent_audio, parent, false);
                            holder.tvVoiceTime = (TextView) convertView.findViewById(R.id.tv_audio_duration);
                            holder.rlVoice = (RelativeLayout) convertView.findViewById(R.id.rl_audio_bg);
                            holder.im_item_date = (TextView) convertView.findViewById(R.id.im_item_date);
                            holder.portrait = (CircleImageView) convertView.findViewById(R.id.portrait);
                            displayImages(url, holder.portrait);
                            if (time == 0) {
                                holder.im_item_date.setText("");
                            } else {
                                holder.im_item_date.setText(IMUtil.getFormattedTime(context, message.getTime()));
                            }
                            int audioTime = ((AudioMessageBody) message.getMessageBody())
                                    .getDuration();
                            holder.tvVoiceTime.setText(audioTime + "\"");
                            holder.rlVoice.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    playAudio((ImageView) view.findViewById(R.id.iv_audio),
                                            message);
                                }
                            });
                            holder.rlVoice.setTag(message);
                            holder.rlVoice.setOnLongClickListener(mLongClickListener);
                            break;
                        case MessageConstants.Message.TYPE_PIC:
                            convertView = inflater.inflate(R.layout.row_sent_pic, parent, false);
                            holder.ivMessage = (BubbleImageView) convertView.findViewById(R.id.iv_img);
                            holder.pb_outgoing = (ProgressBar) convertView.findViewById(R.id.pb_img_outgoing);
                            holder.tvOutgoing = (TextView) convertView
                                    .findViewById(R.id.tv_img_outgoing_progress);
                            holder.ivError = (ImageView) convertView
                                    .findViewById(R.id.iv_send_state_outgoing);
                            holder.im_item_date = (TextView) convertView.findViewById(R.id.im_item_date);
                            holder.pb_outgoing.setVisibility(View.GONE);
                            holder.tvOutgoing.setVisibility(View.GONE);
                            holder.portrait = (CircleImageView) convertView.findViewById(R.id.portrait);
                            displayImages(url, holder.portrait);
                            time = message.getTime();
                            if (time == 0) {
                                holder.im_item_date.setText("");
                            } else {
                                holder.im_item_date.setText(IMUtil.getFormattedTime(context, message.getTime()));
                            }
                            String picContent = message.getMessageBody().getContent();
                            Bitmap bitmap = null;
                            if (picContent != null) {
                                bitmap = BitmapFactory.decodeFile(picContent);
                                holder.ivMessage.setImageBitmap(bitmap);
                            } else {
                                smallUri = ((ImageMessageBody) message.getMessageBody()).getMiddleUri();
                                displayImages(smallUri, holder.ivMessage);
                            }
                            holder.ivMessage.setTag(message);
                            holder.ivMessage.setOnLongClickListener(mLongClickListener);
                            final Bitmap finalBitmap = bitmap;
                            holder.ivMessage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (context == null) {
                                        return;
                                    }
                                    Intent intent = new Intent(context,
                                            ZoomPicActivity.class);
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    if (finalBitmap != null) {
                                        finalBitmap.compress(Bitmap.CompressFormat.JPEG,
                                                60, baos);
                                        byte[] array = baos.toByteArray();
                                        intent.putExtra("image", array);
                                        intent.putExtra("imageUrl", "");
                                        context.startActivity(intent);
                                    }
                                    if (smallUri != null) {
                                        intent.putExtra("imageUrl", smallUri);
                                        context.startActivity(intent);
                                    }
                                }
                            });
                            break;
                        case MessageConstants.Message.TYPE_NOTIFY:
                            convertView = inflater.inflate(R.layout.row_received_notify, parent, false);
                            holder.tv_msg = (TextView) convertView.findViewById(R.id.im_item_notify);
                            holder.im_item_date = (TextView) convertView.findViewById(R.id.item_date);
                            holder.im_item_date.setText(IMUtil.getFormattedTime(context, message.getTime()) + "");
                            holder.tv_msg.setText(message.getMessageBody().getContent().replaceAll(" ", "") + "");
                            break;
                    }
//                    if (url != null && !url.isEmpty() && !url.equals(" ")) {
//                        holder.portrait = (CircleImageView) convertView.findViewById(R.id.portrait);
//                        displayImages(url, holder.portrait);
//                    }
                    break;
                case MessageConstants.Message.MSG_RECV: //接受 1
                    switch (message.getContentType()) {
                        case MessageConstants.Message.TYPE_TEXT:
                            convertView = inflater.inflate(R.layout.row_received_txt, parent,
                                    false);
                            holder.portrait = (CircleImageView) convertView.findViewById(R.id.portrait);
                            holder.tv_msg = (TextView) convertView.findViewById(R.id.tv_msg);
                            holder.tv_received_name = (TextView) convertView.findViewById(R.id.tv_received_name);
                            if (message.getChatType() == 1) {
                                holder.tv_received_name.setVisibility(View.VISIBLE);
                            }
//                            holder.tv_received_name.setText("" + message.getFromNick());
                            String msg_content = message.getMessageBody().getContent();
                            if (msg_content == null) {
                                return null;
                            }

                            String unicode = EmojiParser.getInstance(context).parseEmoji(msg_content);
                            SpannableString spannableString = ParseEmojiMsgUtil.getExpressionString(context, unicode);
                            holder.tv_msg.setText(spannableString);
                            holder.tv_msg.setTag(message);
                            holder.tv_msg.setOnLongClickListener(mLongClickListener);
                            break;
                        case MessageConstants.Message.TYPE_AUDIO:
                            convertView = inflater.inflate(R.layout.row_received_audio, parent,
                                    false);
                            holder.portrait = (CircleImageView) convertView.findViewById(R.id.portrait);
                            holder.tvVoiceTime = (TextView) convertView.findViewById(R.id.tv_audio_duration);
                            holder.tv_received_name = (TextView) convertView.findViewById(R.id.tv_received_name);
                            if (message.getChatType() == 1) {
                                holder.tv_received_name.setVisibility(View.VISIBLE);
                            }
//                            holder.tv_received_name.setText("" + message.getFromNick());
                            holder.rlVoice = (RelativeLayout) convertView.findViewById(R.id.rl_audio_bg);
                            int audioTime = ((AudioMessageBody) message.getMessageBody())
                                    .getDuration();
                            holder.tvVoiceTime.setText(audioTime + "\"");
                            holder.rlVoice.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    playAudio((ImageView) view.findViewById(R.id.iv_audio),
                                            message);
                                }
                            });
                            holder.rlVoice.setTag(message);
                            holder.rlVoice.setOnLongClickListener(mLongClickListener);
                            break;
                        case MessageConstants.Message.TYPE_PIC:
                            convertView = inflater.inflate(R.layout.row_received_pic, parent, false);
                            holder.portrait = (CircleImageView) convertView.findViewById(R.id.portrait);
                            holder.ivMessage = (BubbleImageView) convertView.findViewById(R.id.iv_img);
                            holder.tv_received_name = (TextView) convertView.findViewById(R.id.tv_received_name);
                            if (message.getChatType() == 1) {
                                holder.tv_received_name.setVisibility(View.VISIBLE);
                            }
//                            holder.tv_received_name.setText("" + message.getFromNick());
                            final String url_img = ((ImageMessageBody) message.getMessageBody()).getMiddleUri();
                            displayImages(url_img, (ImageView) holder.ivMessage);
                            holder.ivMessage.setTag(message);
                            holder.ivMessage.setOnLongClickListener(mLongClickListener);
                            holder.ivMessage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(context,
                                            ZoomPicActivity.class);
                                    if (url_img != null) {
                                        intent.putExtra("imageUrl", url_img);
                                        context.startActivity(intent);
                                    }
                                }
                            });
                            break;
                        case MessageConstants.Message.TYPE_NOTIFY:
                            convertView = inflater.inflate(R.layout.row_received_notify, parent, false);
                            holder.tv_msg = (TextView) convertView.findViewById(R.id.im_item_notify);
                            holder.im_item_date = (TextView) convertView.findViewById(R.id.item_date);
                            holder.im_item_date.setText(IMUtil.getFormattedTime(context, message.getTime()) + "");
                            holder.tv_msg.setText(message.getMessageBody().getContent().replaceAll(" ", "") + "");
                            break;
                    }
                    //跳转聊天界面 传递过来的用户URL
                    if (((MessageActivity) context).chatType == 0) {//单聊
                        if (((MessageActivity) context).avatarurl != null && holder.portrait != null) {
                            displayImages(((MessageActivity) context).avatarurl, holder.portrait);
                        } else if (message.getExtra() != null && !message.getExtra().isEmpty()) {
                            if (message.getExtra().contains(",") && holder.portrait != null)
                                displayImages(message.getExtra().split(",")[0], holder.portrait);
                        }
                    } else if (((MessageActivity) context).chatType == 1) {//群聊
                        if (message.getExtra() != null && !message.getExtra().isEmpty()) {
                            if (message.getExtra().contains(",") && holder.portrait != null)//头像和昵称
                                displayImages(message.getExtra().split(",")[0], holder.portrait);
                                holder.tv_received_name.setText("" + message.getExtra().split(",")[1]);
                        }else {
                            if (holder.portrait != null)
                                holder.portrait.setImageResource(R.drawable.recommand_bgs);
                        }
                    }
                    break;

            }
            convertView.setTag(holder);
        }
        if (message != null) {
            if (!mHoldMap.containsKey(message.getId())) {
                mHoldMap.put(message.getId(), holder);
            }
        }
        return convertView;
    }

    private static final int POSTION_MESSAGE_COPY = 0;
    private static final int POSTION_MESSAGE_DELETE = 1;
    private static final int POSTION_MESSAGE_FORWARD = 2;
    private Dialog mListDialog;
    private String title = "提示";
    private final String[] charSequences = {
            IMApp.mContext.getResources()
                    .getString(R.string.menu_copy_message),
            IMApp.mContext.getResources()
                    .getString(R.string.menu_delete_message),
            IMApp.mContext.getResources()
                    .getString(R.string.menu_forward_message)};
    private final String[] charSequences_other = {
            IMApp.mContext.getResources()
                    .getString(R.string.menu_delete_message),
            IMApp.mContext.getResources()
                    .getString(R.string.menu_forward_message)};
    private View.OnLongClickListener mLongClickListener = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View view) {
            if (view.getTag() instanceof CMMessage) {
                final CMMessage message = (CMMessage) view.getTag();
                // 长按消息弹出框
                if (message.getContentType() == MessageConstants.Message.TYPE_TEXT || message.getContentType() == MessageConstants.Message.TYPE_AT_TEXT
                        || message.getContentType() == MessageConstants.Message.TYPE_EMOTION
                        ) {
                    mListDialog = DialogFactory.getListDialog(
                            (Activity) context, title, charSequences,
                            new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent,
                                                        View view, int position, long id) {
                                    switch (position) {
                                        case POSTION_MESSAGE_COPY:
                                            //复制
                                            doCopyItemMessage(message.getMessageBody().getContent());
                                            break;
                                        case POSTION_MESSAGE_DELETE:
                                            //删除
                                            doDeleteItemMessage(message);
                                            break;
                                        case POSTION_MESSAGE_FORWARD:
                                            //转发
                                            doForwardItemMessage(message);
                                            break;
                                    }
                                    mListDialog.dismiss();
                                }
                            });

                } else if (message.getContentType() == MessageConstants.Message.TYPE_PIC
                        || message.getContentType() == MessageConstants.Message.TYPE_AUDIO
                        || message.getContentType() == MessageConstants.Message.TYPE_VIDEO
                        || message.getContentType() == MessageConstants.Message.TYPE_LOCATION) {
                    mListDialog = DialogFactory.getListDialog(
                            (Activity) context, title, charSequences_other,
                            new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent,
                                                        View view, int position, long id) {
                                    switch (position) {
                                        case POSTION_MESSAGE_DELETE - 1:
                                            //删除
                                            doDeleteItemMessage(message);
                                            break;
                                        case POSTION_MESSAGE_FORWARD - 1:
                                            //转发
                                            doForwardItemMessage(message);
                                            break;
                                    }
                                    mListDialog.dismiss();
                                }
                            });
                }

            }
            return true;
        }
    };

    /**
     * @方法名：doForwardItemMessage
     * @描述：转发某条消息
     */
    private void doForwardItemMessage(CMMessage message) {
        if (TextUtils.isEmpty(message.getMessageBody().getContent())) {// 文件、表情是text,图片、音频是文件路径
            mListDialog.dismiss();
            Toast.makeText(context, "选择的消息无效!", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        ((MessageActivity) context).doForwardMessage(message);

    }

    private void onStopPlayVoice() {
        if (VoiceRecorder.isPlaying
                && VoiceRecorder.currentPlayListener != null) {
            // 停止语音播放
            VoiceRecorder.currentPlayListener.stopPlayVoice();
        }
    }

    /**
     * @方法名：doDeleteItemMessage
     * @描述：删除某条消息
     */
    private void doDeleteItemMessage(final CMMessage message) {
        View.OnClickListener okBtnEvent = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message.getContentType() == MessageConstants.Message.TYPE_AUDIO) {
                    onStopPlayVoice();
                }
                list.remove(message);
                notifyDataSetChanged();
                MessageHandle.getInstance().doDeleteMessage(
                        message.getId());
            }
        };
        DialogFactory.getConfirmDialog2(
                context,
                context.getResources().getString(R.string.chatting_delete),
                context.getResources().getString(
                        R.string.dialog_delete_conversation_prompt_content3),
                context.getResources().getString(R.string.btn_subject_cancel),
                context.getResources().getString(R.string.btn_subject_confirm), null,
                okBtnEvent).show();
    }

    /**
     * @方法名：doCopyItemMessage
     * @描述：复制某条消息
     */
    private void doCopyItemMessage(String content) {
//        MsgUtil.copyMessage(context, msgId);
        //粘贴板
        ClipData clipData = ClipData.newPlainText("text", content);
        if (clipData != null) {
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setPrimaryClip(clipData);
        }
        Toast.makeText(context,
                context.getResources().getString(R.string.copyed), Toast.LENGTH_SHORT)
                .show();
    }

    public static int small_img_width_3;
    public static int small_img_width_4;

    public void displayImages(String url, ImageView img) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.recommand_bgs)
                .showImageOnFail(R.drawable.recommand_bgs)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        if (img != null && url != null && !url.isEmpty()) {
            ImageLoader.getInstance().displayImage(url, img, options);
        } else {
            img.setImageResource(R.drawable.recommand_bgs);
        }
    }

    public static class Holder {
        private TextView im_item_date;
        private TextView tv_msg;
        private TextView tv_received_name;
        private ProgressBar pb_outgoing;
        private TextView tvOutgoing;
        private ImageView ivError;
        private TextView tvVoiceTime;
        private RelativeLayout rlVoice;
        private BubbleImageView ivMessage;
        private CircleImageView portrait;
    }

    private int outgoAnim = R.anim.chatting_outgo_playing_audio;
    private int outgoVoiceMailAnim = R.anim.chatting_outgo_playing_voicemail;
    private int incomAnim = R.anim.chatting_income_playing_audio;
    private int defOut = R.drawable.chatto_voice_playing_f3;
    private int defVoiceMailOut = R.drawable.icon_send_voicemail3;
    private int defIn = R.drawable.chatfrom_voice_playing_f3;

    public void setRefreshCallBack(RefreshCallBack callBack) {
        this.mRefreshCallBack = callBack;
    }

    public RefreshCallBack getRefreshCallBack() {
        return this.mRefreshCallBack;
    }

    private void playAudio(final ImageView imageView, final CMMessage message) {
        if (message.getMessageBody().getContent() == null) {
            return;
        }
        if (!(new File(message.getMessageBody().getContent())).exists())
            return;
        MessageActivity.voiceRecorder.playVoice(message.getMessageBody().getContent(),
                new VoiceRecorder.MediaPlayerCallback() {

                    @Override
                    public void onStart() {
                        if (message.getSendOrRecv() == MessageConstants.Message.MSG_SEND) {
//                           imageView.setAnimation(animation_outgoAnim);
                            imageView.setBackgroundResource(outgoAnim);
                        } else {
//                            imageView.setAnimation(animation_incomAnim);
                            imageView.setBackgroundResource(incomAnim);
                        }

                        final AnimationDrawable ad = (AnimationDrawable) imageView
                                .getBackground();
                        ad.start();
                        setRefreshCallBack(new RefreshCallBack(message) {
                            @Override
                            public void updateCallBack(View newVoiceView) {
                                if (ad != null && ad.isRunning()) {//停止old动画
                                    ad.stop();
                                }
                                imageView
                                        .setBackgroundResource(message.getSendOrRecv() == MessageConstants.Message.MSG_SEND ? defOut
                                                : defIn);
                                //新view动画开始
                                if (message.getSendOrRecv() == MessageConstants.Message.MSG_SEND) {
//                                        newVoiceView.setAnimation(animation_outgoAnim);
                                    newVoiceView.setBackgroundResource(outgoAnim);
                                } else {
                                    newVoiceView.setBackgroundResource(incomAnim);
//                                        newVoiceView.setAnimation(animation_incomAnim);
                                }
//                                    newVoiceView
//                                            .setBackgroundResource(message.getSendOrRecv() == MessageConstants.Message.MSG_SEND ? outgoAnim
//                                                    : incomAnim);
                                AnimationDrawable _ad = (AnimationDrawable) newVoiceView
                                        .getBackground();
                                _ad.start();
                            }
                        });
//                        }
                    }

                    @Override
                    public void onStop() {
                        // TODO Auto-generated method stub
                        Drawable drawable = imageView.getBackground();
                        if (drawable instanceof AnimationDrawable) {
                            ((AnimationDrawable) drawable).stop();
                        }
                        imageView
                                .setBackgroundResource(message.getSendOrRecv() == MessageConstants.Message.MSG_SEND ? defOut
                                        : defIn);
//                        if (mAdapter != null) {
                        if (getRefreshCallBack() != null)//声音播放完后刷新后的新view动画也停止
                            getRefreshCallBack().stop();
                        setRefreshCallBack(null);
//                        }
                    }
                });
    }

}
