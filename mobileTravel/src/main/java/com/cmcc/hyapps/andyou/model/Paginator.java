
package com.cmcc.hyapps.andyou.model;

public class Paginator {
    private boolean mHasMorePages = true;
    private int mNextLoadOffset = 0;

    public void addPage(Pagination page) {
        if (page == null) {
            return;
        }

        mNextLoadOffset += page.limit;
        mHasMorePages = page.total > mNextLoadOffset;
    }

    public void onNewDataAdded() {
        mHasMorePages = true;
    }

    public void reset() {
        mHasMorePages = true;
        mNextLoadOffset = 0;
    }

    public boolean hasMorePages() {
        return mHasMorePages;
    }

    public int nextLoadOffset() {
        return mNextLoadOffset;
    }

    @Override
    public String toString() {
        return "Paginator [mHasMorePages=" + mHasMorePages + ", mNextLoadOffset=" + mNextLoadOffset
                + "]";
    }

}
