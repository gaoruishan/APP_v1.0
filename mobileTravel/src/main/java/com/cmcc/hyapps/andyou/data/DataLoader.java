
package com.cmcc.hyapps.andyou.data;

public interface DataLoader<T> {
    public static final int MODE_LOAD_MORE = 1;
    public static final int MODE_REFRESH = 2;

    void loadMoreData(DataLoaderCallback<T> cb, int mode);

    void onLoaderDestory();

    public interface DataLoaderCallback<T> {
        void onLoadFinished(T list, int mode);

        void onLoadError(int mode);
    }
}
