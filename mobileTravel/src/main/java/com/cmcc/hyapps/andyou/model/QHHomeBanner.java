package com.cmcc.hyapps.andyou.model;
/**
 * Created by Administrator on 2015/5/19.
 */
public class QHHomeBanner {
//        "id": 1,
//        "image_url": "http://selftravel-image.qiniudn.com/www.bmp",
//        "stype": 1,
//        "action": "22"

    //H5 景区 视频 攻略 吃喝玩乐(H5，视频 暂无)
    public static final int SCENIC = 0;
    public static final int VIDEO = 1;
    public static final int H5 = 2;
    public static final int PERSON_TRAVEL = 3;
    public static final int RECOMMADN = 4;
    public static final int ENJOY = 6;
    //路况
    public static final int ROAD = 7;
    public int id;
    public String image_url;
    public int stype;
    public String action;
    private String video_url;

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getVideo_url() {
        return video_url;
    }

    public class QHHomeBannerLists extends ResultList<QHHomeBanner> {

    }
}
