
package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Comment implements Parcelable {

    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
//    @SerializedName("allow_reply")
    public boolean allowReply;

//    @SerializedName("allow_vote")
    public boolean allowVote;

//    @SerializedName("author")
    public Author author;

    @SerializedName("content")
    public String content;

    @SerializedName("vote_count")
    public int voteCount;

    @SerializedName("comment_count")
    public int commentCount;

    @SerializedName("rating")
    public float rating;

    @SerializedName("create_time")
    public String createTime;

    @SerializedName("id")
    public int id;

    @SerializedName("object_type")
    public String type;

    @SerializedName("ctype")//ctype：1：景区，2：攻略，3：路线，4：评论
    public int ctype;

    @SerializedName("object_id")
    public int objectId;

    @SerializedName("images")
    public List<CompoundImage> images;

    @SerializedName("comment_images")
    public List<CommentImage> comment_images;

    @SerializedName("is_voted")
    public boolean isVoted;

    @SerializedName("anonymous")
    public String anonymous;


    @SerializedName("user")
    public QHUser user;
    @SerializedName("created")
    public String created;
    @SerializedName("modified")
    public String modified;
    @SerializedName("longitude")
    public double longitude;
    @SerializedName("latitude")
    public double latitude;
    @SerializedName("address")
    public String address;
    @SerializedName("obj_name")
    public String obj_name;
    @SerializedName("layer")
    public int layer;

    @SerializedName("allow_reply")//ÊÇ·ñÔÊÐí »Ø¸´
    public int allow_reply;
    @SerializedName("allow_vote")//1 已经点赞、 0 还没有点赞

    public int allow_vote;

    @SerializedName("voted")//1 已经点赞、 0 还没有点赞
    public int voted;


    public Comment() {
    }
    public String toString() {
        return "Comment [content=" + content + ", author=" + author + ", voteCount=" + voteCount+ ", commentCount=" + commentCount+
                ", rating=" + rating+ ", createTime="+ createTime + ", id="+ id + ", type=" + type + ", ctype=" + ctype
                +", objectId="+ objectId + ", images=" + images + ", comment_images=" + comment_images+ ", anonymous=" + anonymous
                +", user="+ user + ", created=" + created + ", modified=" + modified+ ", longitude=" + longitude
                +", latitude="+ latitude + ", address=" + address + ", obj_name=" + obj_name+ ", layer=" + layer
                +", allow_reply="+ allow_reply + ", allow_vote=" + allow_vote + ", allowReply=" + allowReply+ ", allowVote=" + allowVote
                +"]";
    }
    public Comment(Parcel in) {

        this.content = in.readString();
        ClassLoader loader1=  Author.class.getClassLoader();
        this.author = in.readParcelable(loader1);
        this.voteCount = in.readInt();
        this.commentCount = in.readInt();
        this.rating = in.readFloat();
        this.createTime = in.readString();
        this.id = in.readInt();
        this.type = in.readString();
        this.ctype = in.readInt();
        this.objectId = in.readInt();

        if (images == null) {
            images = new ArrayList<CompoundImage>();
        }
        in.readTypedList(images, CompoundImage.CREATOR);

        if (comment_images == null) {
            comment_images = new ArrayList<CommentImage>();
        }
        in.readTypedList(comment_images, CommentImage.CREATOR);
        setCompoundImage(comment_images);





        /*boolean[] temp = new boolean[3];
        in.readBooleanArray(temp);
        allowReply = temp[0];
        allowVote = temp[1];
        isVoted = temp[2];*/
        this.anonymous = in.readString();
        ClassLoader loader=  QHUser.class.getClassLoader();
        this.user = in.readParcelable(loader);
        this.created = in.readString();
        this.modified = in.readString();
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        this.address = in.readString();
        this.obj_name = in.readString();
        this.layer = in.readInt();
        this.allow_reply = in.readInt();
        this.allow_vote = in.readInt();
        this.voted = in.readInt();

        allowReply = this.allow_reply==0?false:true;
        allowVote = this.allow_reply==0?false:true;
        isVoted = this.voted==0?false:true;
        allow_vote =  voted;
        setAuthor(user);
    }


    public void setCompoundImage (List<CommentImage> list){
        images = new  ArrayList<CompoundImage>();
        for(int i =0;i<list.size();i++){
            CompoundImage com = new CompoundImage(list.get(i).image_url,list.get(i).image_url);
            images.add(com);
        }
    }
    // TODO: remove it
    public void setAuthor(User user) {
        if (user == null) {
            return;
        }
        Author author = new Author();
        author.avatarUrl = user.avatarUrl;
        author.gender = user.gender;
        author.name = user.name;
        author.uid = user.uid;

        this.author = author;
    }
    public void setAuthor(QHUser user) {
        if (user == null) {
            return;
        }
        Author author = new Author();
        if (user.user_info != null){
            author.avatarUrl = user.user_info.avatar_url;
            author.gender = user.user_info.gender;
            author.name = user.user_info.nickname;
        }
        author.uid = user.id;

        this.author = author;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
//        if (this.author != null) {
            dest.writeParcelable(this.author, flags);
//        }
        dest.writeInt(this.voteCount);
        dest.writeInt(this.commentCount);
        dest.writeFloat(this.rating);
        dest.writeString(this.createTime);
        dest.writeInt(this.id);
        dest.writeString(this.type);
        dest.writeInt(this.ctype);
        dest.writeInt(this.objectId);
        dest.writeTypedList(this.images);
        dest.writeTypedList(this.comment_images);
//        dest.writeBooleanArray(new boolean[] {
//                allowReply, allowVote, isVoted
//        });
        dest.writeString(this.anonymous);

        dest.writeParcelable(this.user,flags);
        dest.writeString(this.created);
        dest.writeString(this.modified);
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
        dest.writeString(this.address);
        dest.writeString(this.obj_name);
        dest.writeInt(this.layer);
        dest.writeInt(this.allow_reply);
        dest.writeInt(this.allow_vote);
        dest.writeInt(this.voted);

    }

    public static class CommentList extends ResultList<Comment> {
    }

    public static class VoteResponse {
        @SerializedName("vote_count")
        public int voteCount;
    }

    public static class NewCommentResult {
        @SerializedName("id")
        public int commentId;
    }
}
