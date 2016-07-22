package test.grs.com.ims.message;

import android.os.Environment;

public class IMConst {
//	public static final String APPKEY = "189983jz";
//	public static final String APPKEY = "072964qs";//沙箱环境：072964qs
	public static final String APPKEY = "172964qs";//线上环境：172964qs
	public static final String USER_ID = "userId";
	public static final int LOAD_ALL_CONTACTS_START = 0;
	public static final int LOAD_ALL_CONTACTS_OK = 1;
	public static final int LOAD_ALL_CONTACTS_FAILED = 2;
	public static final int LOAD_GROUPS_START=3;
	public static final int LOAD_GROUPS_OK=4;
	public static final int LOAD_GROUPS_FAILED=5;
	public static final int SETUP_REGISTER=0X66;
	public static final int REQUEST_CODE_FINISH = 321;
	public static final String GLOBALSTORAGE_PATH = Environment.getExternalStorageDirectory()+ "/xmpp/";
	public static final String GLOBALSTORAGE_DOWNLOAD_PATH = GLOBALSTORAGE_PATH + "download/";
	public static final String GLOBALSTORAGE_DB_PATH = GLOBALSTORAGE_PATH+ "db/";
	public static final String GLOBALSTORAGE_DB_DEFAULT_PATH = GLOBALSTORAGE_PATH+ "db/table/";

	public static final String ACTION_LOGIN_START = "login_start";
	public static final String ACTION_LOGIN_SUCCESS = "login_success";
	public static final String ACTION_LOGIN_FAIL = "login_fail";
	public static final String ACTION_LOGIN_ACCOUNT_CONFLICT = "login_account_conflict";
	public static final String ACTION_LOAD_CONTACT_SUCCESS = "load_contact_success";
	public static final String ACTION_LOAD_CONTACT_FAIL = "load_contact_fail";
	
	public static final String LOAD_CONTACT_FAIL_MSG = "login_fail_reason";
	public static final String LOGIN_FAIL_MSG = "login_fail_reason";


	public static final int NEW_MESSAGE_NOTIFICATION = -1;
	public static final int SINGLE_NOTIFICATION = 0;
	public static final int GROUP_NOTIFICATION = 1;
	public static final int SYSTEM_NOTIFICATION = 2;
	public static final String NOTIFICATION_ACTION_SUCCUSS="notification_action_succuss";
	public static final String NOTIFICATION_ACTION_FAIL="notification_action_fail";
	public static final String NOTIFICATION_TYPE="notification_type";
	public static final String SENDER_COUNT="sender_count";
	public static final String SINGLE_SENDER_USERNAME="single_sender_username";
	public static final String SINGLE_USER_OR_GROUP_OR_SYSTEM_NOTIFICATION="single_user_or_group_or_system_notification";
	public static final int NEW_FRIEND_APPLY_NOTIFICATION=1;

	public static final String MESSAGELIST = "messagelist";
	public static final String GUID = "guid";
	public static final String USERNAME = "username";
	public static final String AVATARURL = "avatarurl";
	public static final String NICKNAME = "nickname";
	public static final String CHATTYPE = "chat_type";
	public static final String GROUPNAME = "groupname";
	public static final String GROUPID = "groupid";
	public static final String NET_DISCONNECT = "disconnect";
	public static final String NET_RECONNECT = "reconnect";
	public static final String GROUPMEMEBER = "groupmember";
	public static final String GROUPREMOVE ="groupremove" ;
	public static final int ADD_RESULTT_CODE = 111;
	public static final String STRINGLIST = "stringlist";
	public static final int ADD_REQUSET_CODE = 112;
	public static final int REMOVER_RESULTT_CODE = 113;
	public static final int REMOVE_REQUSET_CODE = 114;
	public static final String ACTION_NET_CONNECT = "ACTION_NET_CONNECT";
	public static final String OTHER = "other";
	public static final String ACTION_IM = "action_im";
	public static final String ACTION_RECOMMEND = "action_recommend";
	public static final String ACTION_MYATTENTION = "action_muattention";
	public static final String ACTION_PAYATTENTIONTOME = "action_payattentiontome";
	public static final String ACTION_BLACKLIST = "action_blacklist";
	public static final String ACTION_ADD_BLACKLIST = "action_add_blacklist";
	public static final String ACTION_ADD_ATTENTION = "action_add_attention";
	public static final String ACTION_ADD_ATTENTION1 = "action_add_attention1";
	public static final String ACTION_REMOVE_BLACKLIST = "action_remove_blacklist";
	public static final String ACTION_USER_DETAIL = "action_user_detail";
	public static final String ACTION_MAILLISTUSER = "action_maillistuser";
	public static final String ACTION_GROUP = "action_group";
	public static final String ACTION_RECOMMEND_AGAIN = "action_recommend_again";
	public static final String ACTION_STARTACTIVITY = "action_startactivity";
	public static final String ACTION_STARTACTIVITY1 = "action_startactivity1";
	public static final String ACTION_GET_USERDETAIL = "action_get_userdetail";
	public static final String ACTION_FRIEND_TOKEN = "action_friend_token";
	public static final String ACTION_REMOVE_ATTENTION = "action_remove_attention";
	public static final String ACTION_START_GUDERMESSAGE = "action_start_guidermessage";
	public static final String GROUPAVATAR = "group_avatar";
	public static final String ACTION_RGISTER = "action_register";
	public static final String ACTION_SAVE_ATTENTION = "action_save";
	public static final String  ACTION_MYATTENTION_TA = "acton_myattention_at";
	public static final String ACTION_PAYATTENTIONTOME_TA = "action_payattention_ta";
	public static final String ACTION_GET_USERLIST = "action_user_list";
	public static final String ACTION_ONLY_GROUP = "action_only_group";
	public static String OWNER = "owner";

	private int convId = -1;
	public static final String ADDRESS = "address";
	public static final String CONVERSATIONID = "conversation_id";
	public static final String NAME = "name";

	public static final String NAVIGATE_DESTINATION = "navigate_destination";
	public static final String CHOOSE_CONTACT_TYPE = "choose_contact_type";

	public static final int SINGLE_RESULT_CODE = 100;
	public static final String SINGLE_RESULT_USER_NAME = "single_result_user_name";
	public static final String SINGLE_RESULT_USER_NICK = "single_result_user_nick";

}
