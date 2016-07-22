
package com.kuloud.android.widget.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.HapticFeedbackConstants;
import android.view.SoundEffectConstants;
import android.view.View;

public class ItemClickSupport {

    private final RecyclerView mRecyclerView;
    private final TouchListener mTouchListener;

    private OnItemClickListener mItemClickListener;
    private OnItemSubViewClickListener mOnItemSubViewClickListener;
    private OnItemLongClickListener mItemLongClickListener;

    /**
     * Interface definition for a callback to be invoked when an item in the
     * RecyclerView has been clicked.
     */
    public interface OnItemClickListener {
        /**
         * Callback method to be invoked when an item in the RecyclerView has
         * been clicked.
         *
         * @param parent The RecyclerView where the click happened.
         * @param view The view within the RecyclerView that was clicked
         * @param position The position of the view in the adapter.
         * @param id The row id of the item that was clicked.
         */
        void onItemClick(RecyclerView parent, View view, int position, long id);
    }

    public interface OnItemSubViewClickListener {
        /**
         * Callback method to be invoked when an item's sub item in the
         * RecyclerView has been clicked.
         *
         * @param view The view within the item of RecyclerView that was clicked
         * @param position The position of the view in the adapter.
         */
        void onItemClick(View view, int position);
    }

    /**
     * Interface definition for a callback to be invoked when an item in the
     * RecyclerView has been clicked and held.
     */
    public interface OnItemLongClickListener {
        /**
         * Callback method to be invoked when an item in the RecyclerView has
         * been clicked and held.
         *
         * @param parent The RecyclerView where the click happened
         * @param view The view within the RecyclerView that was clicked
         * @param position The position of the view in the list
         * @param id The row id of the item that was clicked
         * @return true if the callback consumed the long click, false otherwise
         */
        boolean onItemLongClick(RecyclerView parent, View view, int position, long id);
    }

    private ItemClickSupport(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;

        mTouchListener = new TouchListener(recyclerView);
        recyclerView.addOnItemTouchListener(mTouchListener);
    }

    /**
     * Register a callback to be invoked when an item in the RecyclerView has
     * been clicked.
     *
     * @param listener The callback that will be invoked.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    /**
     * Register a callback to be invoked when an item's subview in the
     * RecyclerView has been clicked.
     *
     * NOTE:DON'T call both {@link #setOnItemClickListener(OnItemClickListener)}
     * and this method, while the OnItemClickListener will be useless.
     *
     * Example:
     *
     * ItemClickSupport clickSupport = ItemClickSupport.addTo(RecyclerView);
     * clickSupport.setOnItemSubViewClickListener(new OnItemSubViewClickListener() {
     *
     *      @Override
     *      public void onItemClick(View view, int position) {
     *          // TODO
     *      }
     *  });
     *
     * then:
     *
     * @Override
     * public void onBindViewHolder(ViewHolder holder, int position) {
     *     // TODO get subView
     *     attachClickListener(holder, subView, position);
     * }
     *
     * @param listener The callback that will be invoked.
     *
     */
    public void setOnItemSubViewClickListener(OnItemSubViewClickListener listener) {
        mOnItemSubViewClickListener = listener;
    }

    /**
     * Register a callback to be invoked when an item in the RecyclerView has
     * been clicked and held.
     *
     * @param listener The callback that will be invoked.
     */
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        if (!mRecyclerView.isLongClickable()) {
            mRecyclerView.setLongClickable(true);
        }

        mItemLongClickListener = listener;
    }

    public static ItemClickSupport addTo(RecyclerView recyclerView) {
        ItemClickSupport itemClickSupport = from(recyclerView);
        if (itemClickSupport == null) {
            itemClickSupport = new ItemClickSupport(recyclerView);
            recyclerView.setTag(R.id.twowayview_item_click_support, itemClickSupport);
        } else {
            // TODO: Log warning
        }

        return itemClickSupport;
    }

    public static void removeFrom(RecyclerView recyclerView) {
        final ItemClickSupport itemClickSupport = from(recyclerView);
        if (itemClickSupport == null) {
            // TODO: Log warning
            return;
        }

        recyclerView.removeOnItemTouchListener(itemClickSupport.mTouchListener);
        recyclerView.setTag(R.id.twowayview_item_click_support, null);
    }

    public static ItemClickSupport from(RecyclerView recyclerView) {
        if (recyclerView == null) {
            return null;
        }

        return (ItemClickSupport) recyclerView.getTag(R.id.twowayview_item_click_support);
    }

    public void onItemSubViewClicked(View view, int position) {
        if (mOnItemSubViewClickListener != null) {
            mOnItemSubViewClickListener.onItemClick(view, position);
        }
    }

    private class TouchListener extends ClickItemTouchListener {
        TouchListener(RecyclerView recyclerView) {
            super(recyclerView);
        }

        @Override
        boolean performItemClick(RecyclerView parent, View view, int position, long id) {
            if (mItemClickListener != null && mOnItemSubViewClickListener == null) {
                view.playSoundEffect(SoundEffectConstants.CLICK);
                mItemClickListener.onItemClick(parent, view, position, id);
                return true;
            }

            return false;
        }

        @Override
        boolean performItemLongClick(RecyclerView parent, View view, int position, long id) {
            if (mItemLongClickListener != null) {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return mItemLongClickListener.onItemLongClick(parent, view, position, id);
            }

            return false;
        }
    }
}
