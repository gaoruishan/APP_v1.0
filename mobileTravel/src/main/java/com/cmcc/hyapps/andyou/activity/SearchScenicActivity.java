/**
 *
 */

package com.cmcc.hyapps.andyou.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;
import com.kuloud.android.widget.recyclerview.ItemClickSupport.OnItemSubViewClickListener;
import com.kuloud.android.widget.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.kuloud.android.widget.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.SearchCityHeaderAdapter;
import com.cmcc.hyapps.andyou.adapter.SearchScenicAdapter;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.app.ServerAPI.Search.SearchType;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.fragment.BasicScenicDataListFragment;
import com.cmcc.hyapps.andyou.fragment.DefaultScenicDataListFragmentImp;
import com.cmcc.hyapps.andyou.model.BasicScenicData;
import com.cmcc.hyapps.andyou.model.City;
import com.cmcc.hyapps.andyou.model.City.CityList;
import com.cmcc.hyapps.andyou.model.Scenic.ScenicList;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.LocationUtils;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.PreferencesUtils;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;
import com.cmcc.hyapps.andyou.widget.pinyinsidebar.CharacterParser;
import com.cmcc.hyapps.andyou.widget.pinyinsidebar.PinyinComparator;
import com.cmcc.hyapps.andyou.widget.pinyinsidebar.PinyinSideBar;
import com.cmcc.hyapps.andyou.widget.pinyinsidebar.PinyinSideBar.OnTouchingLetterChangedListener;
import com.cmcc.hyapps.andyou.widget.pinyinsidebar.SearchScenicDividerItemDecoration;
import com.cmcc.hyapps.andyou.widget.pinyinsidebar.SortModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author kuloud
 */
public class SearchScenicActivity extends BaseActivity implements OnClickListener {
    private static final int REQ_SHOW_CITY_SCENIC = 1;
    private static final int HTTP_GET_PARAM_LIMIT = 6;
    private static final String KEY_CITIES = "key_cities";

    private Context mContext;

    private EditText mSearchContent;
    private RecyclerView mRecyclerView;
    private View mLoadingProgress;
    private View mEmptyHintView;
    private PinyinSideBar mSideBar;
    private TextView mLetterDialog;
    private View mResultContainer;
    private View mSearchContainer;

    private String mSearchKeyword;
    private String mSearchCity;
    private List<SortModel> mCityListModel = new ArrayList<SortModel>();
    private SearchScenicAdapter mSearchAdapter;
    private SearchCityHeaderAdapter mSearchCityHeaderAdapter;
    private BasicScenicDataListFragment mScenicResultFragment;
    private StickyHeadersItemDecoration mDecor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_search_scenic);
        initViews();
//        loadHotScenics();
        loadCityList();
        String cities = PreferencesUtils.getEncryptString(mContext, KEY_CITIES);
        if (!TextUtils.isEmpty(cities)) {
            CityList cityList = new Gson().fromJson(cities, CityList.class);
            onCityListLoaded(cityList);
        }
    }

    private void initViews() {
        findViewById(R.id.action_bar_left).setOnClickListener(this);
        mSearchContent = (EditText) findViewById(R.id.search_content);
        mSearchContent.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    String q = mSearchContent.getText().toString().trim();
                    if (!TextUtils.isEmpty(q)) {
                        mSearchKeyword = q;
                        clearSearchResults();
                        createResultFragments();
                        showFragment(mScenicResultFragment);
                    }
                }
                return false;
            }
        });
        mSearchContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String q = mSearchContent.getText().toString().trim();
                if (TextUtils.isEmpty(q)) {
                    mSearchContainer.setVisibility(View.VISIBLE);
                    mSearchContainer.bringToFront();
                    mResultContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mEmptyHintView = findViewById(R.id.empty_hint_view);
        mLoadingProgress = findViewById(R.id.loading_progress);
        mSearchContainer = findViewById(R.id.search_container);
        mResultContainer = findViewById(R.id.fragment_container);
        mResultContainer.setVisibility(View.GONE);
        mSearchAdapter = new SearchScenicAdapter(this);
        mSearchCityHeaderAdapter = new SearchCityHeaderAdapter();
        mRecyclerView = ((PullToRefreshRecyclerView) findViewById(R.id.pulltorefresh_twowayview))
                .getRefreshableView();
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        int scap = ScreenUtils.dpToPxInt(activity, 1);
        int divideColor = getResources().getColor(R.color.base_grey_line);
        mRecyclerView.addItemDecoration(new SearchScenicDividerItemDecoration(divideColor, scap));
        ItemClickSupport clickListener = ItemClickSupport.addTo(mRecyclerView);
        clickListener.setOnItemSubViewClickListener(new OnItemSubViewClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                if (ExcessiveClickBlocker.isExcessiveClick()) {
                    return;
                }
                City city = ((SortModel) mSearchAdapter.getItems().get(position)).getCity();
                if (city == null) {
                    ToastUtils.show(mContext, R.string.error_unknown);
                    return;
                }
                Intent intent = new Intent(getApplicationContext(),
                        CityScenicListActivity.class);
                intent.putExtra(Const.EXTRA_CITY, city.code);
                intent.putExtra(Const.CITYNAME_ZH, city.name);
                startActivityForResult(intent, REQ_SHOW_CITY_SCENIC);
            }
        });

        mSideBar = (PinyinSideBar) findViewById(R.id.sidebar);
        mLetterDialog = (TextView) findViewById(R.id.city_letter_dialog);
        mSideBar.setTextView(mLetterDialog);
        mSideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                int position = mSearchAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mRecyclerView.scrollToPosition(position);
                }

            }
        });
    }

    private void loadCityList() {
        RequestManager.getInstance().sendGsonRequest(ServerAPI.SearchList.URL, CityList.class,
                new Response.Listener<CityList>() {
                    @Override
                    public void onResponse(CityList cityList) {
                        Log.d("onResponse, CityList=%s", cityList);

                        if (cityList.list != null && !cityList.list.isEmpty()) {
                            PreferencesUtils.putEncryptString(mContext, KEY_CITIES,
                                    new Gson().toJson(cityList));
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

    private void loadHotScenics() {
        final String url = ServerAPI.ScenicList
                .buildUrl(ServerAPI.ScenicList.Type.HOT,
                        LocationUtils.getLastKnownLocation(mContext), HTTP_GET_PARAM_LIMIT,
                        0);
        RequestManager.getInstance().sendGsonRequest(url, ScenicList.class,
                new Response.Listener<ScenicList>() {
                    @Override
                    public void onResponse(ScenicList scenicList) {
                        Log.d("onResponse, ScenicList=%s", scenicList);
                        if (scenicList == null || scenicList.list == null
                                || scenicList.list.isEmpty()) {
                            return;
                        }

                        onHotScenicListLoaded(scenicList);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "onErrorResponse");
                    }
                }, requestTag);
    }

    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.action_bar_left: {
                finish();
                return;
            }
            default:
                break;
        }

    }

    private void onCityListLoaded(CityList cityList) {
        mLoadingProgress.setVisibility(View.GONE);
        List<City> cities = cityList.list;
        mCityListModel = fillModel(cities);
        Collections.sort(mCityListModel, new PinyinComparator());
        mSearchAdapter.setItems(mCityListModel);
        mSearchCityHeaderAdapter.setItems(mSearchAdapter.getItems());
        mRecyclerView.setAdapter(mSearchAdapter);
        if (mDecor != null) {
            mRecyclerView.removeItemDecoration(mDecor);
        }
        mDecor = new StickyHeadersBuilder()
                .setAdapter(mSearchAdapter)
                .setRecyclerView(mRecyclerView)
                .setStickyHeadersAdapter(mSearchCityHeaderAdapter)
                .build();
        mRecyclerView.addItemDecoration(mDecor);

    }

    private void onHotScenicListLoaded(ScenicList scenicList) {
        mLoadingProgress.setVisibility(View.GONE);
        mSearchAdapter.setRecommendScenic(scenicList.list);
        mRecyclerView.scrollToPosition(0);
    }

    public void onScenicSelected(BasicScenicData scenicData) {
        Intent data = new Intent();
        data.putExtra(Const.EXTRA_ID, scenicData.id());
        data.putExtra(Const.EXTRA_NAME, scenicData.name());
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQ_SHOW_CITY_SCENIC && data != null) {
            setResult(RESULT_OK, data);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private List<SortModel> fillModel(List<City> cities) {
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for (City city : cities) {
            SortModel sortModel = new SortModel();
            sortModel.setCity(city);
            String pinyin = CharacterParser.getInstance().getSelling(city.name);
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

    private void createResultFragments() {
        mScenicResultFragment = new DefaultScenicDataListFragmentImp();
        Bundle args = new Bundle();
        args.putString(Const.ARGS_KEY_MODEL_CLASS, ScenicList.class.getName());
        args.putString(Const.ARGS_LOADER_URL, ServerAPI.Search
                .buildSearchUrl(SearchType.SCENIC, mSearchKeyword, mSearchCity));
        mScenicResultFragment.setArguments(args);

        mScenicResultFragment.setOnItemClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ExcessiveClickBlocker.isExcessiveClick()) {
                    return;
                }
                BasicScenicData scenicData = (BasicScenicData) v.getTag();
                if (scenicData != null) {
                    onScenicSelected(scenicData);
                }
            }
        });
    }

    private void showFragment(Fragment fragment) {
        mSearchContainer.setVisibility(View.GONE);
        mResultContainer.setVisibility(View.VISIBLE);
        mResultContainer.bringToFront();
        if (!fragment.isAdded()) {
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment).commit();
        }
    }

    private void clearSearchResults() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        if (mScenicResultFragment != null && mScenicResultFragment.isAdded()) {
            transaction.remove(mScenicResultFragment);
        }

        transaction.commit();
    }
}
