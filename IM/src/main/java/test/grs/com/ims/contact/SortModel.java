package test.grs.com.ims.contact;

public class SortModel {
	public static final int TYPE_FORWARD = 1;
	public static final int TYPE_ATTENTION = 4;
	public static final int TYPE_UNATTENTION = 5;
	public static final int TYPE_GROUP = 6;
	public static final int TYPE_ADD = 2;
	public static final int TYPE_REMOVE = 3;
	public static final int MEMBER = 7;

	private String avatar_url;//头像
	private String name;   //唯一标识  显示的数据的名字
	private String nickname="";   //昵称
	private String introduction="";   //介绍
	private boolean isfriend ;   //是否关注
	private String sortLetters;  //显示数据拼音的首字母
	private boolean select;
	private int type;

	public boolean isfriend() {
		return isfriend;
	}

	public void setIsfriend(boolean isfriend) {
		this.isfriend = isfriend;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isSelect() {
		return select;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAvatar_url() {
		return avatar_url;
	}

	public void setAvatar_url(String avatar_url) {
		this.avatar_url = avatar_url;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {

		this.sortLetters = sortLetters;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	@Override
	public String toString() {
		return "SortModel{" +
				"avatar_url='" + avatar_url + '\'' +
				", name='" + name + '\'' +
				", nickname='" + nickname + '\'' +
				", introduction='" + introduction + '\'' +
				", sortLetters='" + sortLetters + '\'' +
				", select=" + select +
				", type=" + type +
				'}';
	}
}
