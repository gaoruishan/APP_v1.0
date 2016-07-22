
package com.cmcc.hyapps.andyou.adapter;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.ViewGroup;

import com.cmcc.hyapps.andyou.adapter.row.RowTripDate;
import com.cmcc.hyapps.andyou.adapter.row.RowTripDay;
import com.cmcc.hyapps.andyou.adapter.row.RowTripHeader;
import com.cmcc.hyapps.andyou.model.QHRouteDay;
import com.cmcc.hyapps.andyou.model.TripDay;
import com.cmcc.hyapps.andyou.model.TripDetail;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.TimeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kuloud
 */
public class TripDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public final static int TYPE_HEADER = 1;
    public final static int TYPE_DATE = 2;
    public final static int TYPE_TRIP_DAY = 3;
    public final int TYPE_ADD = 4;

    private TripDetail mTripDetail;
    private Time mCreateTime;

    // Archive data according to the day
    private Map<String, List<TripDay>> mDataItems;

    private ParseTask mParseTask;

    public interface ParseCallback {
        public void onDataParsed(Map<String, List<TripDay>> dataItems);
    }

    public interface DataUpdatedListener {
        public void onDataUpdated();
    }

    private static class ParseTask extends
            AsyncTask<TripDetail, Object, Map<String, List<TripDay>>> {
        private ParseCallback mParseCallback;

        ParseTask(ParseCallback callback) {
            mParseCallback = callback;
        }

        @Override
        protected Map<String, List<TripDay>> doInBackground(TripDetail... params) {
            return parseTripDetail(params[0]);
        }

        @Override
        protected void onPostExecute(Map<String, List<TripDay>> result) {
            super.onPostExecute(result);
            if (mParseCallback != null) {
                mParseCallback.onDataParsed(result);
            }
        }
    }

    public TripDetailAdapter() {
    }

    public TripDetail getTripDetail() {
        return mTripDetail;
    }

    public void setTripDetail(TripDetail tripDetail, final DataUpdatedListener listener) {
        mTripDetail = tripDetail;
        if (tripDetail != null && tripDetail.days != null) {
            if (!tripDetail.days.isEmpty()) {
                mCreateTime = TimeUtils.parseDate(tripDetail.days.get(0).time);
            }
        }
        if (mParseTask == null) {
            mParseTask = new ParseTask(new ParseCallback() {

                @Override
                public void onDataParsed(Map<String, List<TripDay>> dataItems) {
                    mDataItems = dataItems;
                    notifyDataSetChanged();
                    if (listener != null) {
                        listener.onDataUpdated();
                    }
                }
            });
        } else {
            mParseTask.cancel(true);
        }
        mParseTask.execute(tripDetail);
    }

    private static Map<String, List<TripDay>> parseTripDetail(TripDetail tripDetail) {
        Map<String, List<TripDay>> items = new HashMap<String, List<TripDay>>();
        for (TripDay tripDay : tripDetail.days) {
            if (!TextUtils.isEmpty(tripDay.time)
                    && TimeUtils.testFormat(tripDay.time, TimeUtils.DATE_FORMAT)) {
                if (items.containsKey(tripDay.time)) {
                    List<TripDay> tripDays = items.get(tripDay.time);
                    if (tripDays == null) {
                        tripDays = new ArrayList<TripDay>();
                    }
                    tripDays.add(tripDay);
                } else {
                    List<TripDay> tripDays = new ArrayList<TripDay>();
                    tripDays.add(tripDay);
                    items.put(tripDay.time, tripDays);
                }
            } else {
                Log.d("[parseTripDetail] time is INVALIDE");
            }
        }
        return items;
    }

    @Override
    public int getItemCount() {
        return mDataItems == null ? 0 : computeInfoSize();
    }

    private int computeInfoSize() {
        // header + dates
        int size = mDataItems.size() + 1;
        // trip days
        for (List<TripDay> tripDays : mDataItems.values()) {
            size += (tripDays == null) ? 0 : tripDays.size();
        }
        return size;
    }

    private int computeInfoType(int position) {
        int count = 0;
        if (position == count) {
            return TYPE_HEADER;
        }
        for (TripDay day : mTripDetail.days) {
            String key = day.time;
            count++;
            if (count == position) {
                return TYPE_DATE;
            }
            count += mDataItems.get(key).size();
            if (count >= position) {
                return TYPE_TRIP_DAY;
            }
        }
        return TYPE_ADD;
    }

    public int computeImageIndex(int position) {
        int result = -1;
        int count = 0;
        if (position == count) {
            return result;
        }
        for (TripDay day : mTripDetail.days) {
            count++;
            String key = day.time;
            if (count == position) {
                result++;
                return result;
            }
            count += mDataItems.get(key).size();
            if (count >= position) {
                result += (mDataItems.get(key).size() - count + position);
                return result;
            } else {
                result += mDataItems.get(key).size();
            }
        }
        return result;
    }

    private Object getItemObj(int position) {
        int count = 0;
        if (position == count) {
            return mTripDetail;
        }
        // FIXME get index
        for (TripDay day : mTripDetail.days) {
            String key = day.date;
            count++;
            if (count == position) {
                CategoryDate date = new CategoryDate();
                date.date = key;
//                date.index = TimeUtils.dayDuration(TimeUtils.parseDate(key),
//                        mCreateTime == null ? TimeUtils.parseTime(mTripDetail
//                                .createTime) : mCreateTime);
                return date;
            }
            int index = position - count - 1;
            count += mDataItems.get(key).size();
            if (count >= position) {
                return mDataItems.get(key).get(index);
            }
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return computeInfoType(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case TYPE_HEADER:
                holder = RowTripHeader.onCreateViewHolder(parent);
                break;
            case TYPE_DATE:
                holder = RowTripDate.onCreateViewHolder(parent);
                break;
            case TYPE_TRIP_DAY:
                holder = RowTripDay.onCreateViewHolder(parent);
                break;

            default:
                Log.e("UNKNOW trip content type!!!");
                holder = RowTripDate.onCreateViewHolder(parent);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Object itemObj = getItemObj(position);
        if (itemObj == null) {
            return;
        }
        holder.itemView.setTag(itemObj);
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                if (itemObj instanceof TripDetail) {
                    // Fixme get days
                    RowTripHeader.onBindViewHolder(holder, position,
                            mTripDetail, mDataItems == null ? 1 : mDataItems.size());
                }
                break;
            case TYPE_DATE:
                if (itemObj instanceof CategoryDate) {
                    RowTripDate.onBindViewHolder(holder, position, (CategoryDate) itemObj);
                }
                break;
            case TYPE_TRIP_DAY:
                if (itemObj instanceof TripDay) {
                    RowTripDay.onBindViewHolder(holder, position, (QHRouteDay) itemObj);
                }
                break;

            default:
                Log.e("UNKNOW trip detail type!!!");
                break;
        }
    }

    public static class CategoryDate {
        public String date;
        public int index;
    }
}
