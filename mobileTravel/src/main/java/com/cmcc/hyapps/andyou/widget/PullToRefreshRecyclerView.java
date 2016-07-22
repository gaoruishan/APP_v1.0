/*******************************************************************************
 * Copyright 2014 Dean Ding.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.cmcc.hyapps.andyou.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.cmcc.hyapps.andyou.R;

/**
 * Support RecyclerView
 * 
 * @author Dean.Ding
 */
public class PullToRefreshRecyclerView extends PullToRefreshBase<RecyclerView> {

    public PullToRefreshRecyclerView(Context context) {
        super(context);
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshRecyclerView(Context context, Mode mode) {
        super(context, mode);
    }

    public PullToRefreshRecyclerView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
    }

    @Override
    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected RecyclerView createRefreshableView(Context context, AttributeSet attrs) {
        RecyclerView recyclerView;
        recyclerView = new RecyclerView(context, attrs);
        recyclerView.setId(R.id.recyclerview);
        return recyclerView;
    }

    @Override
    protected boolean isReadyForPullStart() {
        if (getRefreshableView().getChildCount() <= 0) {
            return true;
        }
        int firstVisiblePosition = getRefreshableView().getChildPosition(
                getRefreshableView().getChildAt(0));
        if (firstVisiblePosition == 0) {
            return getRefreshableView().getChildAt(0).getTop() >= getRefreshableView().getTop();
        } else {
            return false;
        }

    }

    @Override
    protected boolean isReadyForPullEnd() {
        try {
            int lastVisiblePosition = getRefreshableView().getChildPosition(
                    getRefreshableView().getChildAt(getRefreshableView().getChildCount() - 1));
            if (lastVisiblePosition >= getRefreshableView().getAdapter().getItemCount() - 1) {
                View lastView = getRefreshableView().getChildAt(getRefreshableView().getChildCount() - 1);
                if (lastView == null) {
                    return false;
                }
                return lastView.getBottom() <= getRefreshableView().getBottom();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
