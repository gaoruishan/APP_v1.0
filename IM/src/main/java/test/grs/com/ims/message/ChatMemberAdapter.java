/**
 * 项目名称：EIMClient
 * 类 名 称：ChatMemberAdapter
 * 类 描 述：(描述信息)
 * 创 建 人：XUYONGJIE
 * 创建时间：2014年12月10日 下午9:36:47
 * 修 改 人：XUYONGJIE
 * 修改时间：2014年12月10日 下午9:36:47
 * 修改备注：
 * @version
 * 
*/
package test.grs.com.ims.message;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

import test.grs.com.ims.R;
import test.grs.com.ims.contact.ContactsActivity;
import test.grs.com.ims.contact.GroupContactsActivity;

/**
 * @包名：com.littlec.chatdemo.group
 * @类名：GroupMemberAdapter
 * 
 */
public class ChatMemberAdapter extends BaseAdapter {
	private final BitmapUtils bitmapUtils;
	private Context context;
	private List<IMMember> memberList;
	private LayoutInflater mInflater;
    private String groupId;

    public void  setDatasChanged (List<IMMember> memberList1){
        this.memberList = memberList1;
        notifyDataSetChanged();
    }
	public ChatMemberAdapter(Context context, List<IMMember> memberList,String groupId) {
		this.context=context;
		this.memberList=memberList;
		mInflater=LayoutInflater.from(context);
		bitmapUtils = new BitmapUtils(context);
		bitmapUtils.configDiskCacheEnabled(true);//防止溢出
		if (Build.VERSION.SDK_INT >= 21) {
			bitmapUtils.configMemoryCacheEnabled(false);
		}
        this.groupId = groupId;
	}

    @Override
	public int getCount() {
		return memberList.size();
	}
	
	@Override
	public Object getItem(int position) {
		return memberList.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		final IMMember member=memberList.get(position);
		if(convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_group_member, null);
			holder.avatarImageView = (ImageView)convertView.findViewById(R.id.member_avatar_imageView);
			holder.nameTextView = (TextView)convertView.findViewById(R.id.member_name_textView);
			holder.tv_owner = (ImageView)convertView.findViewById(R.id.tv_owner);
			convertView.setTag(holder);
		}else {
            holder = (ViewHolder)convertView.getTag();
        }
		final String avatarUri = member.getAvatarUri();//头像
		switch(member.getType()) {
			case IMMember.TYPE_COMMON:
				if (avatarUri!=null){
					bitmapUtils.configDefaultLoadingImage(R.drawable.recommand_bgs);
					bitmapUtils.configDefaultLoadFailedImage(R.drawable.recommand_bgs);
					bitmapUtils.display(holder.avatarImageView, avatarUri);
				}else {
					holder.avatarImageView.setImageResource(R.drawable.recommand_bgs);
				}
				break;
			case IMMember.TYPE_ADD://添加群成员
				holder.avatarImageView.setImageResource(R.drawable.xz);
				holder.avatarImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //添加通讯录 创建群
						if (memberList.size()<= 2){
							String username = memberList.get(0).getUserName();
							String nickname = memberList.get(0).getName();
							Intent intent = new Intent(context, ContactsActivity.class);
							intent.putExtra(IMConst.CHOOSE_CONTACT_TYPE, 0);//0单聊添加 1群组添加
							intent.putExtra(IMConst.NAVIGATE_DESTINATION,-1);//不转发
							intent.putExtra(IMConst.USERNAME, username+"");
							intent.putExtra(IMConst.NICKNAME, nickname+"");
							intent.putExtra(IMConst.AVATARURL, avatarUri+"");//
							if (username!=null&&!username.isEmpty())
								context.startActivity(intent);
                            Log.e("单聊添加+name","TAG");
						}else {
							Intent intent = new Intent(context, ContactsActivity.class);
							intent.putExtra(IMConst.CHOOSE_CONTACT_TYPE, 1);//0单聊添加 1群组添加
							intent.putExtra(IMConst.NAVIGATE_DESTINATION,-1);//不转发
							intent.putExtra(IMConst.GROUPID, groupId);//ID
							intent.putExtra(IMConst.GROUPMEMEBER, (ArrayList<IMMember>) memberList);
//							context.startActivity(intent);
                            ((ChatSettingActivity)context).startActivityForResult(intent,IMConst.ADD_REQUSET_CODE);
                            Log.e("群组添加","TAG");
						}
                    }
                });
				break;
			case IMMember.TYPE_REMOVE:
                Log.e("群组移除","TAG");
                holder.tv_owner.setVisibility(View.GONE);
                holder.avatarImageView.setImageResource(R.drawable.icon_remove_buddy);
//				holder.avatarImageView.setBackgroundResource(R.drawable.icon_remove_buddy);
				holder.avatarImageView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
                        //查看好友 通讯录模式
                        Intent i = new Intent(context, GroupContactsActivity.class);
                        i.putExtra(IMConst.GROUPREMOVE, true);
						i.putExtra(IMConst.OTHER,true);// 移除标识
						i.putExtra(IMConst.OWNER, ((ChatSettingActivity)context).isOwner);
                        i.putExtra(IMConst.GROUPID, groupId);//ID
						List<IMMember> tempList = new ArrayList<IMMember>();
						tempList.addAll(memberList);
						tempList.remove(0);//移除群主
                        i.putExtra(IMConst.GROUPMEMEBER, (ArrayList<IMMember>)tempList);
                        ((ChatSettingActivity)context).startActivityForResult(i, IMConst.REMOVE_REQUSET_CODE);
					}
				});
				break;
			default:
				break;
		}

		String memberName = member.getName();
		if(memberName == null|| memberName.equals("")||memberName.equals("null")) {
			memberName = member.getUserName();
		}
		if (memberList.size()>2&&position==0){//群主
			holder.tv_owner.setVisibility(View.VISIBLE);
		}else {
            holder.tv_owner.setVisibility(View.GONE);
        }
		holder.nameTextView.setText(memberName.trim()+"");
		return convertView;
	}
	
	public class ViewHolder {
		ImageView avatarImageView;
		TextView nameTextView;
        ImageView tv_owner;
	}
}
