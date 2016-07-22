/**
 * 
 */

package com.cmcc.hyapps.andyou.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.app.ServerAPI.Search.SearchType;
import com.cmcc.hyapps.andyou.fragment.ItineraryScenicDataListFragmentImp;
import com.cmcc.hyapps.andyou.model.BasicScenicData;
import com.cmcc.hyapps.andyou.model.ItineraryScenic;
import com.cmcc.hyapps.andyou.model.Scenic.ScenicList;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.widget.ActionBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author kuloud
 */
public class SpotPickActivity extends BaseActivity implements OnClickListener {
    @InjectView(R.id.filter_edit)
    EditText mSearchContent;
    @InjectView(R.id.category_hot)
    View mCategory;
    private String mSearchKeyword;
    private ItineraryScenicDataListFragmentImp mHotScenicListFragment;
    private ItineraryScenicDataListFragmentImp mScenicResultFragment;
    private Fragment mCurrentResultFragment;
    private int mThreshold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_pick);
        ButterKnife.inject(this);
        initViews();

        // TODO get data from intent and set selected
        mHotScenicListFragment = new ItineraryScenicDataListFragmentImp();
        Bundle args = new Bundle();
        args.putString(Const.ARGS_KEY_MODEL_CLASS, ScenicList.class.getName());
        args.putString(Const.ARGS_LOADER_URL,
                ServerAPI.ScenicList.buildUrl(ServerAPI.ScenicList.Type.HOT, null));
        mHotScenicListFragment.setArguments(args);

        mThreshold = getIntent().getIntExtra(Const.ARGS_THRESHOLD_COUNT, 1);
        if (mThreshold == 1) {
            mHotScenicListFragment.setOnItemClickListener(this);
        }
        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mHotScenicListFragment).commit();
    }

    @Override
    public void onBackPressed() {
        onFinishSelect();
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initViews() {
        initActionBar();

        mSearchContent.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    String q = mSearchContent.getText().toString();
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

    private void createResultFragments() {
        mCategory.setVisibility(View.GONE);
        mScenicResultFragment = new ItineraryScenicDataListFragmentImp();
        Bundle args = new Bundle();
        args.putString(Const.ARGS_KEY_MODEL_CLASS, ScenicList.class.getName());
        args.putString(Const.ARGS_LOADER_URL,
                ServerAPI.Search
                        .buildSearchUrl(SearchType.SCENIC, mSearchKeyword, null));
        mScenicResultFragment.setArguments(args);
        if (mThreshold == 1) {
            mScenicResultFragment.setOnItemClickListener(this);
        }
    }

    private void onFinishSelect() {
        Map<String, BasicScenicData> selected = new HashMap<String, BasicScenicData>();
        if (mScenicResultFragment != null) {
            selected.putAll(mScenicResultFragment.getSelected());
        }
        if (mHotScenicListFragment != null) {
            selected.putAll(mHotScenicListFragment.getSelected());
        }
        if (!selected.isEmpty()) {
            Intent intent = new Intent();
            ArrayList<ItineraryScenic> scenics = new ArrayList<ItineraryScenic>();
            for (BasicScenicData scenicData : selected.values()) {
                ItineraryScenic scenic = new ItineraryScenic();
                scenic.scenicId = scenicData.id();
                scenic.scenicName = scenicData.name();
                scenic.location = scenicData.location();
                scenics.add(scenic);
            }
            intent.putParcelableArrayListExtra(Const.EXTRA_SCENIC_DATA, scenics);
            setResult(RESULT_OK, intent);
        }
    }

    private void clearSearchResults() {
        mCategory.setVisibility(View.VISIBLE);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (mHotScenicListFragment != null && mHotScenicListFragment.isAdded()) {
            transaction.remove(mHotScenicListFragment);
        }

        if (mScenicResultFragment != null && mScenicResultFragment.isAdded()) {
            transaction.remove(mScenicResultFragment);
        }

        transaction.commit();
    }

    private void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(R.string.action_bar_title_spot_pick);
        actionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        actionBar.getLeftView().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ExcessiveClickBlocker.isExcessiveClick()) {
                    return;
                }
                onFinishSelect();
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        if (v.getTag() != null) {
            Intent intent = new Intent();
            ArrayList<ItineraryScenic> scenics = new ArrayList<ItineraryScenic>();
            BasicScenicData scenicData = (BasicScenicData) v.getTag();
            ItineraryScenic scenic = new ItineraryScenic();
            scenic.scenicId = scenicData.id();
            scenic.scenicName = scenicData.name();
            scenic.location = scenicData.location();
            scenics.add(scenic);
            intent.putParcelableArrayListExtra(Const.EXTRA_SCENIC_DATA, scenics);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

}
