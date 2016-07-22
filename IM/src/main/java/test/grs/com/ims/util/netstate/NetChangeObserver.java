package test.grs.com.ims.util.netstate;

/**
 * @项目名称：ZwUtils
 * @类名称：NetChangeObserver
 * @类描述： 是检测网络改变的观察者
 * @创建时间：2014-3-5 上午10:53:15
 * @修改人：
 * @修改时间：2014-3-5 上午10:53:15
 * @修改备注：
 */
public interface   NetChangeObserver {
	/**
	 * 网络连接连接时调用
	 */
	 void onConnect(NetWorkUtil.NetType type);

	/**
	 * 当前没有网络连接
	 */
	 void onDisConnect();
}
