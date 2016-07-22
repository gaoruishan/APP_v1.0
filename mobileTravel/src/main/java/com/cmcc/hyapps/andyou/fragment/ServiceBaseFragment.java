/**
 * 
 */

package com.cmcc.hyapps.andyou.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.PostImagesActivity;
import com.cmcc.hyapps.andyou.activity.ServiceListActivity;
import com.cmcc.hyapps.andyou.activity.ServiceMapActivity;
import com.cmcc.hyapps.andyou.activity.TrafficActivity;
import com.cmcc.hyapps.andyou.activity.VideoUploadActivity;
import com.cmcc.hyapps.andyou.adapter.ScenicServiceAdapter;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI.ScenicShops;
import com.cmcc.hyapps.andyou.model.QHScenic;
import com.cmcc.hyapps.andyou.model.ScenicDetails;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.widget.CommonDialog;
import com.cmcc.hyapps.andyou.widget.CommonDialog.OnDialogViewClickListener;

/**
 * Base fragment with service
 * 
 * @author Kuloud
 */
public class ServiceBaseFragment extends BaseFragment {
    private final int REQUEST_CODE_LOGIN_POST_VIDEO = 1001;
    private final int REQUEST_CODE_LOGIN_POST_IMAGES = 1002;

    //QHScenicDetails.QHScenicDetailsList   ScenicDetails
    protected ScenicDetails mScenicDetailsModel;
    protected QHScenic mScenic;
    protected Dialog mServiceDialog;
    protected CommonDialog mServiceConfirmDialog;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_LOGIN_POST_VIDEO:
                    postVideo();
                    break;
                case REQUEST_CODE_LOGIN_POST_IMAGES:
                    postImages();
                    break;
                default:
                    break;
            }
        }
    }

    private OnClickListener mServiceClickListener = new OnClickListener() {

        @Override
        public void onValidClick(View v) {
            if (mScenicDetailsModel == null) {
                return;
            }

            switch (v.getId()) {
                case R.id.service_food: {
                    startServiceListActivity(ScenicShops.Type.FOOD,getResources().getString(R.string.service_food_title),getResources().getString(R.string.service_food_keyword), Const.KIND_FOOD);
                    break;
                }
                case R.id.service_hotel: {
                    startServiceListActivity(ScenicShops.Type.HOTEL,getResources().getString(R.string.service_hotel), getResources().getString(R.string.service_hotel_keyword), Const.KIND_HOTEL);
                    break;
                }
                case R.id.service_specialty: {
                    startServiceListActivity(ScenicShops.Type.SHOPPING, getResources().getString(R.string.service_specialty_title),getResources().getString(
                                    R.string.service_specialty_keyword), Const.KIND_SHOPPING);
                    break;
                }
                case R.id.service_wc: {
                    startServiceMapActivity(ScenicShops.Type.REST_ROOM,getResources().getString(R.string.service_wc_title),getResources().getString(R.string.service_wc_keyword), Const.KIND_WC);
                    break;
                }
                case R.id.service_traffic: {
                    if (mScenicDetailsModel.serviceInfo != null
                            && mScenicDetailsModel.serviceInfo.tranportInfo != null) {
                        Intent intent = new Intent(getActivity(), TrafficActivity.class);
                        intent.putParcelableArrayListExtra(Const.EXTRA_TRANSPORT_DATA,
                                mScenicDetailsModel.serviceInfo.tranportInfo);
                        startActivity(intent);
                    }
                    break;
                }
                case R.id.service_video: {
                    postVideo();
                    break;
                }
                case R.id.service_complaint: {
                    if (mScenicDetailsModel.serviceInfo == null) {
                        return;
                    }

                    String number = mScenicDetailsModel.serviceInfo.complaintNumber;
                    if (!TextUtils.isEmpty(number)) {
                        showDialConfirmDialog(
                                getString(R.string.scenery_detail_service_dial_complaint, number),
                                number);
                    }
                    break;
                }
                case R.id.service_help: {
                    if (mScenicDetailsModel.serviceInfo == null) {
                        return;
                    }

                    String number = mScenicDetailsModel.serviceInfo.helpNumber;
                    if (!TextUtils.isEmpty(number)) {
                        showDialConfirmDialog(
                                getString(R.string.scenery_detail_service_dial_help, number),
                                number);
                    }
                    break;
                }
                case R.id.service_picture: {
                    postImages();
                }
                default:
                    break;
            }

            if (mServiceDialog.isShowing()) {
                mServiceDialog.dismiss();
            }
        }
    };

    protected void showServicePopup() {
        if (mServiceDialog == null) {
            View popupView = getActivity().getLayoutInflater().inflate( R.layout.scenic_details_service, null);
            GridView gridView = (GridView) popupView.findViewById(R.id.scenic_service_layout);
            gridView.setAdapter(new ScenicServiceAdapter(getActivity(), mServiceClickListener));

            mServiceDialog = new Dialog(getActivity(), R.style.service_popup_dialog);
            mServiceDialog.setContentView(popupView);
            mServiceDialog.setCanceledOnTouchOutside(true);
            mServiceDialog.setCancelable(true);
        }

        mServiceDialog.show();
    }

    private void showDialConfirmDialog(String dialogContent, final String phoneNumber) {
        if (mServiceConfirmDialog == null) {
            mServiceConfirmDialog = new CommonDialog(getActivity());
            mServiceConfirmDialog.setLeftButtonText(R.string.confirm);
            mServiceConfirmDialog.setRightButtonText(R.string.cancel);
            mServiceConfirmDialog.setTitleText(R.string.scenery_detail_service_dialog_title);
        }
        mServiceConfirmDialog.setContentText(dialogContent);
        mServiceConfirmDialog.setOnDialogViewClickListener(new OnDialogViewClickListener() {

            @Override
            public void onRightButtonClick() {
                mServiceConfirmDialog.dismissDialog();
            }

            @Override
            public void onLeftButtonClick() {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
                mServiceConfirmDialog.dismissDialog();
            }
        });

        mServiceConfirmDialog.showDialog();
    }

    private void startServiceListActivity(ScenicShops.Type type, String title, String keyword,
            String kind) {
        Intent intent = new Intent(getActivity(), ServiceListActivity.class);
        intent.putExtra(Const.EXTRA_ACTION_BAR_TITLE, title);
        intent.putExtra(Const.EXTRA_SEARCH_KEYWORD, keyword);
        intent.putExtra(Const.EXTRA_SHOP_TYPE, type);
        intent.putExtra(Const.EXTRA_SEARCH_KIND, kind);
        intent.putExtra(Const.EXTRA_ID, mScenicDetailsModel.id);
        mScenicDetailsModel.location.city = mScenicDetailsModel.cityZh;
        intent.putExtra(Const.EXTRA_COORDINATES, mScenicDetailsModel.location);
        startActivity(intent);
    }

    private void startServiceMapActivity(ScenicShops.Type type, String title, String keyword,
            String kind) {
        Intent intent = new Intent(getActivity(), ServiceMapActivity.class);
        intent.putExtra(Const.EXTRA_ACTION_BAR_TITLE, title);
        intent.putExtra(Const.EXTRA_SEARCH_KEYWORD, keyword);
        intent.putExtra(Const.EXTRA_SHOP_TYPE, type);
        intent.putExtra(Const.EXTRA_SEARCH_KIND, kind);
        intent.putExtra(Const.EXTRA_ID, mScenicDetailsModel.id);
        mScenicDetailsModel.location.city = mScenicDetailsModel.cityZh;
        intent.putExtra(Const.EXTRA_COORDINATES, mScenicDetailsModel.location);
        startActivity(intent);
    }

    private void postVideo() {
        Intent intent = new Intent(getActivity(), VideoUploadActivity.class);
        intent.putExtra(Const.EXTRA_ID, mScenicDetailsModel.id);
        startActivity(intent);
    }

    private void postImages() {
        Intent intent = new Intent(getActivity(), PostImagesActivity.class);
        intent.putExtra(Const.EXTRA_ID, mScenicDetailsModel.id);
        startActivity(intent);
    }
}
