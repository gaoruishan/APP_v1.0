/**
 *
 */

package com.cmcc.hyapps.andyou.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.VideoListAdapter;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.app.ServerAPI.Search.SearchType;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.fragment.BasicScenicDataListFragment;
import com.cmcc.hyapps.andyou.fragment.DefaultScenicDataListFragmentImp;
import com.cmcc.hyapps.andyou.fragment.VideoListFragment;
import com.cmcc.hyapps.andyou.model.BasicScenicData;
import com.cmcc.hyapps.andyou.model.City;
import com.cmcc.hyapps.andyou.model.City.CityList;
import com.cmcc.hyapps.andyou.model.Scenic.ScenicList;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
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
public class SearchActivity extends BaseActivity implements OnClickListener {
    public static final int SEARCH_SCENIC = 1;
    public static final int SEARCH_VIDEO = 2;
    private static final int REQ_SHOW_CITY_SCENIC = 1;
    private int mSearchType;

    private EditText mSearchContent;
    private BasicScenicDataListFragment mScenicResultFragment;
    private VideoListFragment mVideoResultFragment;

    private Fragment mCurrentResultFragment;

    private String mSearchKeyword;
    private String mSearchCity;
    private CharacterParser mCharacterParser;
    private PinyinComparator mPinyinComparator;
    private List<SortModel> mCityListModel = new ArrayList<SortModel>();

    private ListView mCityListView;
    private PinyinSideBar mSideBar;
    private TextView mLetterDialog;
    private SortAdapter mSortAdapter;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        mSearchType = getIntent().getIntExtra(Const.EXTRA_SEARCH_TYPE, SEARCH_SCENIC);
        setContentView(R.layout.activity_search);
        // TODO
        if (mSearchType == SEARCH_SCENIC) {
            int scap = ScreenUtils.getDimenPx(mContext, R.dimen.scenic_image_spacing);
            int padding = ScreenUtils.getDimenPx(mContext, R.dimen.common_margin);
            int itemHeight = (ScreenUtils.getScreenWidth(mContext) - scap * 2 - padding * 2) / 3;
            int height = itemHeight * 2 + scap * 2 + padding * 4;
            findViewById(R.id.fragment_container).setLayoutParams(
                    new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, height));
        }

        mCharacterParser = CharacterParser.getInstance();
        mPinyinComparator = new PinyinComparator();
        initViews();

        if (mSearchType == SEARCH_SCENIC) {
            mSearchContent.setHint(R.string.hint_action_bar_search_scenic);
        } else if (mSearchType == SEARCH_VIDEO) {
            mSearchContent.setHint(R.string.hint_action_bar_search_video);
            // TODO
        }

        mCityListView = (ListView) findViewById(R.id.city_letter_list);
        mCityListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                City city = ((SortModel) mSortAdapter.getItem(position)).getCity();
                Intent intent = new Intent(getApplicationContext(),
                        CityScenicListActivity.class);
                intent.putExtra(Const.EXTRA_CITY, city.code);
                startActivityForResult(intent, REQ_SHOW_CITY_SCENIC);
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

        if (mSearchType == SEARCH_SCENIC) {
            loadCityList();
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
        List<City> cities = cityList.list;
        mCityListModel = fillModel(cities);
        Collections.sort(mCityListModel, mPinyinComparator);
        mSortAdapter.updateListView(mCityListModel);
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

        mVideoResultFragment = VideoListFragment.newInstance(StaggeredGridLayoutManager.class,
                VideoListAdapter.class);
        args = new Bundle();
        args.putString(Const.ARGS_LOADER_URL,
                ServerAPI.Search.buildSearchUrl(SearchType.VIDEO, mSearchKeyword, mSearchCity));
        mVideoResultFragment.setArguments(args);
    }

    private void initViews() {
        initActionBar();
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

                        switch (mSearchType) {
                            case SEARCH_SCENIC:
                                showFragment(mScenicResultFragment);
                                break;
                            case SEARCH_VIDEO:
                                showFragment(mVideoResultFragment);
                                break;
                            default:
                                break;
                        }
                    }
                }
                return false;
            }
        });
    }

    private void clearSearchResults() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        if (mScenicResultFragment != null && mScenicResultFragment.isAdded()) {
            transaction.remove(mScenicResultFragment);
        }

        if (mVideoResultFragment != null && mVideoResultFragment.isAdded()) {
            transaction.remove(mVideoResultFragment);
        }

        transaction.commit();
    }

    private void initActionBar() {
        findViewById(R.id.action_bar_left).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        Fragment fragment = null;
        switch (v.getId()) {
            case R.id.action_bar_left: {
                finish();
                return;
            }
            default:
                break;
        }

        showFragment(fragment);
    }

    private void showFragment(Fragment fragment) {
        if (mCurrentResultFragment == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment).commit();
        } else if (fragment.isAdded()) {
            getFragmentManager().beginTransaction().hide(mCurrentResultFragment).show(fragment)
                    .commit();
        } else {
            getFragmentManager().beginTransaction().hide(mCurrentResultFragment)
                    .add(R.id.fragment_container, fragment).commit();
        }
        mCurrentResultFragment = fragment;
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

}
