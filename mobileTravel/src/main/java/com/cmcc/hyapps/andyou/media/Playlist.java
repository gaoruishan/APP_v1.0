
package com.cmcc.hyapps.andyou.media;

import com.cmcc.hyapps.andyou.model.AudioIntro;
import com.cmcc.hyapps.andyou.model.ScenicAudio;
import com.cmcc.hyapps.andyou.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Not thread-safe!
 *
 * @author calvin
 */
public class Playlist {
    public static final int REPEAT_NONE = 1;
    public static final int REPEAT_ALL = 2;

    private List<ScenicAudio> mList;
    private int mCurrentAlbumPos = -1;
    private int mCurrentTrackPos = -1;
    private int mRepeatMode = REPEAT_NONE;

    public Playlist() {
        super();
        mList = new ArrayList<ScenicAudio>();
    }

    public void addToPlaylist(List<ScenicAudio> tracks) {
        if (tracks == null || tracks.isEmpty()) {
            return;
        }

        // TODO
        for (ScenicAudio audio : tracks) {
            // audio.validate();
            if (mList.contains(audio)) {
                Log.d("Duplicate playlist entry %s, ignore it", audio);
                continue;
            }

            if (audio.audio != null) {
                mList.add(audio);
            }
        }

        // mList.get(3).location = new Location(40.013121, 116.45972);
        // mList.get(4).location = new Location(40.013766, 116.460484);
    }

    public AudioIntro nextTrack(boolean force) {
        if (mList.isEmpty()) {
            return null;
        }

        if (mCurrentAlbumPos < 0) {
            mCurrentAlbumPos = 0;
        }

        ScenicAudio album = mList.get(mCurrentAlbumPos);
        mCurrentTrackPos++;
        if (mCurrentTrackPos >= album.audio.size()) {
            mCurrentAlbumPos++;
            mCurrentTrackPos = 0;

            if (mCurrentAlbumPos >= mList.size()) {
                if (mRepeatMode == REPEAT_ALL || force) {
                    // start over
                    mCurrentAlbumPos = 0;
                } else {
                    // reset
                    mCurrentAlbumPos = -1;
                    mCurrentTrackPos = -1;
                }
            }
        }

        if (ensureIndexes()) {
            return mList.get(mCurrentAlbumPos).audio.get(mCurrentTrackPos);
        } else {
            return null;
        }

    }

    private boolean ensureIndexes() {
        return (mCurrentAlbumPos > -1 && mCurrentAlbumPos < mList.size())
                && (mCurrentTrackPos > -1 && mCurrentTrackPos < mList.get(mCurrentAlbumPos).audio
                        .size());
    }

    public AudioIntro previousTrack() {
        if (mList.isEmpty()) {
            return null;
        }

        if (mCurrentAlbumPos < 0) {
            mCurrentAlbumPos = mList.size() - 1;
            mCurrentTrackPos = mList.get(mCurrentAlbumPos).audio.size();
        }

        mCurrentTrackPos--;
        if (mCurrentTrackPos < 0) {
            mCurrentAlbumPos--;
            if (mCurrentAlbumPos < 0) {
                mCurrentAlbumPos = mList.size() - 1;
            }
            ScenicAudio album = mList.get(mCurrentAlbumPos);
            mCurrentTrackPos = album.audio.size() - 1;

        }

        if (ensureIndexes()) {
            return mList.get(mCurrentAlbumPos).audio.get(mCurrentTrackPos);
        } else {
            return null;
        }
    }

    public int currentPlayingSpot() {
        if (mCurrentAlbumPos < 0) {
            return -1;
        }

        return mList.get(mCurrentAlbumPos).spotId;
    }

    public boolean gotoTrack(int id) {
        Log.d("gotoTrack,id=%d", id);
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).spotId == id) {
                mCurrentAlbumPos = i;
                mCurrentTrackPos = 0;
                return true;
            }

            for (int j = 0; j < mList.get(i).audio.size(); j++) {
                if (mList.get(i).audio.get(j).id() == id) {
                    mCurrentAlbumPos = i;
                    mCurrentTrackPos = j;
                    return true;
                }
            }
        }

        return false;
    }

    public AudioIntro currentTrack() {
        if (ensureIndexes()) {
            return mList.get(mCurrentAlbumPos).audio.get(mCurrentTrackPos);
        } else {
            return null;
        }
    }

    public List<ScenicAudio> getPlaylist() {
        return mList;
    }

    public boolean isEmpty() {
        return mList == null || mList.isEmpty();
    }

    public void dump() {
        StringBuilder sb = new StringBuilder();
        sb.append("mCurrentAlbumPos:" + mCurrentAlbumPos + ", mCurrentTrackPos:" + mCurrentTrackPos);
        sb.append("\n");
        for (int i = 0; i < mList.size(); i++) {
            ScenicAudio scenicAudio = mList.get(i);
            sb.append("##SpotAlbum: ");
            sb.append("index:" + i + ", spotId:" + scenicAudio.spotId + ", spotName:"
                    + scenicAudio.spotName + ", highlighted:"
                    + scenicAudio.hasAudioHighlighted());
            sb.append("\n");
            for (int j = 0; j < scenicAudio.audio.size(); j++) {
                sb.append("  **SpotTrack:\n");
                sb.append("  index:" + j + ", trackId:" + scenicAudio.audio.get(j).id()
                        + ", trackName:" + scenicAudio.audio.get(j).title
                        + ", highlighted:"
                        + scenicAudio.audio.get(j).highlight);
                sb.append("\n");
            }
        }

        Log.d("Playlist dump:%s", sb.toString());
    }

    public void clear() {
        mList.clear();
        mCurrentAlbumPos = -1;
        mCurrentTrackPos = -1;
    }
}
