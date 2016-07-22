
package com.cmcc.hyapps.andyou.activity;

import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.widget.ActionBar;

public class ScenicSpotsMapActivity extends MyLocationMapActivity {
//    private ArrayList<BasicScenicData> mSpotsData;
//    private PopupOverlay mPopupOverlay = null;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        mSpotsData = getIntent().getParcelableArrayListExtra(Const.EXTRA_SCENIC_SPOTS_DATA);
//        if (mSpotsData == null) {
//            finish();
//            return;
//        }
//
//        super.onCreate(savedInstanceState);
//
//        mPopupOverlay = new PopupOverlay(mMapView, new PopupClickListener() {
//            @Override
//            public void onClickedPopup(int index) {
//                mPopupOverlay.hidePop();
//            }
//        });
//
//        mPopupOverlay.setOverlayPriority(10);
//
//        mMapView.getOverlays().add(mPopupOverlay);
//    }
//
    protected void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(getIntent().getStringExtra(Const.EXTRA_NAME));
        actionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        actionBar.getLeftView().setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
//
//    protected MyOverlay getMarkerOverlays() {
//        MyOverlay mOverlay = new MyOverlay(getResources().getDrawable(
//                R.drawable.scenic_spots_location),
//                mMapView);
//
//        mOverlay.setOverlayPriority(9);
//        for (BasicScenicData scenicData : mSpotsData) {
//            if (scenicData == null || scenicData.location() == null
//                    || !scenicData.location().isValid()) {
//                continue;
//            }
//
//            GeoPoint point = scenicData.location().toGeoPoint();
//            OverlayItem item1 = new OverlayItem(point,
//                    scenicData.name(), scenicData.name());
//            Log.d("Kuloud", "[ScenicData] getLatitudeE6:" + point.getLatitudeE6() + ", getLongitudeE6:"
//                            + point.getLongitudeE6() + ", name:" + scenicData.name());
//            mOverlay.addItem(item1);
//        }
//
//        return mOverlay;
//    }
//
//    public class MyOverlay extends ItemizedOverlay {
//
//        public MyOverlay(Drawable defaultMarker, MapView mapView) {
//            super(defaultMarker, mapView);
//        }
//
//        @Override
//        public boolean onTap(int index) {
//            if (index < mSpotsData.size()) {
//                BasicScenicData data = mSpotsData.get(index);
//                Intent intent = new Intent(getApplicationContext(), ListenActivity.class);
//                intent.putParcelableArrayListExtra(Const.EXTRA_AUDIO, data.audioIntro());
//                intent.putExtra(Const.EXTRA_ID, data.id());
//                intent.putExtra(Const.EXTRA_NAME, data.name());
//                startActivity(intent);
//            }
//
//            return true;
//        }
//
//        @Override
//        public boolean onTap(GeoPoint pt, MapView mMapView) {
//            if (mPopupOverlay != null) {
//                mPopupOverlay.hidePop();
//            }
//            return false;
//        }
//    }
}
