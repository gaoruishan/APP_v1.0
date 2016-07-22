package com.cmcc.hyapps.andyou.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.SpotAudioAdapter;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.ScenicAudio;
import com.cmcc.hyapps.andyou.support.WrappingLayoutManager;
import com.cmcc.hyapps.andyou.util.ImageUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MapListenDialogFragment extends DialogFragment {
    private static final String ARG_SPOT = "spot";

    private ScenicAudio mSpot;

    @InjectView(R.id.rl_spot_image)
    NetworkImageView mSpotImage;

    @InjectView(R.id.iv_listen)
    ImageView mListen;

    @InjectView(R.id.tv_spot_name)
    TextView mSpotName;

    @InjectView(R.id.tv_spot_distance)
    TextView mSpotDistance;

    @InjectView(R.id.intro_recyclerview)
    RecyclerView mRecyclerView;

    public static MapListenDialogFragment newInstance(ScenicAudio spot) {
        MapListenDialogFragment fragment = new MapListenDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SPOT, spot);
        fragment.setArguments(args);
        return fragment;
    }

    public MapListenDialogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSpot = (ScenicAudio) getArguments().getParcelable(ARG_SPOT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map_listen_dialog, container, false);

        ButterKnife.inject(this, v);
        LinearLayoutManager layoutManager = new WrappingLayoutManager
                (getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(new SpotAudioAdapter());
        mSpotImage.setDefaultImageResId(R.drawable.bg_image_hint);
        mSpotImage.setErrorImageResId(R.drawable.bg_image_hint);
        bindSpotDetail();
        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    private void bindSpotDetail() {
        if (mSpot != null) {
            if (mSpot.image != null) {
                ImageUtil.DisplayImage(mSpot.image, mSpotImage);
//                mSpotImage.setImageUrl(mSpot.image, RequestManager.getInstance().getImageLoader());
            }
            mSpotName.setText(mSpot.spotName);
            ((SpotAudioAdapter) mRecyclerView.getAdapter()).setAudioList
                    (mSpot.audio);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
