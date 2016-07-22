/**
 *
 */

package com.cmcc.hyapps.andyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.app.TravelApp;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.City;
import com.cmcc.hyapps.andyou.model.City.CityList;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.ClearEditText;
import com.cmcc.hyapps.andyou.widget.pinyinsidebar.CharacterParser;
import com.cmcc.hyapps.andyou.widget.pinyinsidebar.PinyinComparator;
import com.cmcc.hyapps.andyou.widget.pinyinsidebar.PinyinSideBar;
import com.cmcc.hyapps.andyou.widget.pinyinsidebar.PinyinSideBar.OnTouchingLetterChangedListener;
import com.cmcc.hyapps.andyou.widget.pinyinsidebar.SortAdapter;
import com.cmcc.hyapps.andyou.widget.pinyinsidebar.SortModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author kuloud
 */
public class CityChooseActivity extends BaseActivity {
    private ListView mCityListView;
    private PinyinSideBar mSideBar;
    private TextView mLetterDialog;
    private SortAdapter mSortAdapter;
    private ClearEditText mClearEditText;

    private CharacterParser mCharacterParser;
    private List<SortModel> mCityListModel = new ArrayList<SortModel>();

    private PinyinComparator mPinyinComparator;
    private RecyclerView mHotCities;
    private RecyclerView mDetectCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_chooser);
        mCharacterParser = CharacterParser.getInstance();
        mPinyinComparator = new PinyinComparator();

        initViews();
        loadCityList();
    }

    private void initViews() {
        initActionBar();

        //没用
        mHotCities = (RecyclerView) findViewById(R.id.hot_city_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        mHotCities.setLayoutManager(layoutManager);
        mHotCities.setItemAnimator(new DefaultItemAnimator());
        int verticalGap = ScreenUtils.dpToPxInt(getApplicationContext(), 13);
        int horizontalGap = ScreenUtils.dpToPxInt(getApplicationContext(), 6);;
        DividerItemDecoration decor = new DividerItemDecoration(verticalGap, horizontalGap);
        decor.initWithRecyclerView(mHotCities);
        mHotCities.addItemDecoration(decor);
        mHotCities.setAdapter(new CityAdapter());

        //没用
        mDetectCity = (RecyclerView) findViewById(R.id.detected_city_view);
        layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        mDetectCity.setLayoutManager(layoutManager);
        mDetectCity.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration decor2 = new DividerItemDecoration(verticalGap, horizontalGap);
        decor2.initWithRecyclerView(mDetectCity);
        mDetectCity.addItemDecoration(decor2);
        mDetectCity.setAdapter(new CityAdapter());

        Location location = ((TravelApp) getApplication()).getCurrentLocation();
        if (location != null) {
            City city = new City();
            city.location = location;
            city.name = location.city;
            List<City> cityList = new ArrayList<City>();
            cityList.add(city);
            ((CityAdapter) mDetectCity.getAdapter()).setCityList(cityList);
        } else {
            findViewById(R.id.detected_city_text).setVisibility(View.GONE);
            mDetectCity.setVisibility(View.GONE);
        }

        mCityListView = (ListView) findViewById(R.id.city_letter_list);
        mCityListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                City city = ((SortModel) mSortAdapter.getItem(position)).getCity();
                onCityChoosed(city);
            }
        });

        mSortAdapter = new SortAdapter(this, mCityListModel);
        mCityListView.setAdapter(mSortAdapter);

        mSideBar = (PinyinSideBar) findViewById(R.id.sidebar);
        mLetterDialog = (TextView) findViewById(R.id.city_letter_dialog);
        mSideBar.setTextView(mLetterDialog);

        mSideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                int position = mSortAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mCityListView.setSelection(position);
                }

            }
        });

        mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void onCityChoosed(City city) {
        Intent intent = new Intent();
        city.location.city = city.name;
        intent.putExtra(Const.EXTRA_COORDINATES, city.location);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(R.string.action_bar_title_city);
        actionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        actionBar.getLeftView().setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                finish();
            }
        });
    }

    private List<SortModel> fillModel(List<City> cities) {
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for (City city : cities) {
            SortModel sortModel = new SortModel();
            sortModel.setCity(city);
            String pinyin = mCharacterParser.getSelling(city.name);
            sortModel.setPinyin(pinyin);
            String sortString = pinyin.substring(0, 1).toUpperCase(Locale.CHINESE);

            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase(Locale.CHINESE));
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<SortModel>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = mCityListModel;
        } else {
            filterDateList.clear();
            for (SortModel sortModel : mCityListModel) {
                String name = sortModel.getCity().name;
                if (name.toUpperCase(Locale.CHINESE).indexOf(
                        filterStr.toString().toUpperCase(Locale.CHINESE)) != -1
                        || mCharacterParser.getSelling(name).toUpperCase(Locale.CHINESE)
                                .startsWith(filterStr.toString().toUpperCase(Locale.CHINESE))) {
                    filterDateList.add(sortModel);
                }
            }
        }

        Collections.sort(filterDateList, mPinyinComparator);
        mSortAdapter.updateListView(filterDateList);
    }

    private class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

        private List<City> mDataItems;

        public CityAdapter() {
        }

        public CityAdapter(List<City> items) {
            this.mDataItems = items;
        }

        public void setCityList(List<City> items) {
            this.mDataItems = items;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_name_box,
                    parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final City item = mDataItems.get(position);
            holder.itemView.setTag(item);
            holder.itemView.setOnClickListener(new OnClickListener() {

                @Override
                public void onValidClick(View v) {
                    onCityChoosed(item);
                }
            });

            holder.name.setText(item.name);
        }

        @Override
        public int getItemCount() {
            return mDataItems == null ? 0 : mDataItems.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView name;

            public ViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.name);
            }
        }
    }

    private void loadCityList() {
        RequestManager.getInstance().sendGsonRequest(ServerAPI.CityList.URL, CityList.class,
                new Response.Listener<CityList>() {
                    @Override
                    public void onResponse(CityList cityList) {
                        Log.d("onResponse, CityList=%s", cityList);

                        if (cityList.list != null && !cityList.list.isEmpty()) {
                            onCityListLoaded(cityList);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "onErrorResponse");
                    }
                }, requestTag);
    }

    private void onCityListLoaded(CityList cityList) {
        List<City> hotCities = new ArrayList<City>();

        for (City city : cityList.list) {
            if (city.isHot) {
                hotCities.add(city);
            }
        }
        ((CityAdapter) mHotCities.getAdapter()).setCityList(hotCities);

        List<City> cities = cityList.list;
        mCityListModel = fillModel(cities);
        Collections.sort(mCityListModel, mPinyinComparator);
        mSortAdapter.updateListView(mCityListModel);
    }
}
