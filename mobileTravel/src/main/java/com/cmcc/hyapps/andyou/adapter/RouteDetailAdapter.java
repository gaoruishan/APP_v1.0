package com.cmcc.hyapps.andyou.adapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.QHRoute;
import com.cmcc.hyapps.andyou.util.ImageUtil;

public class RouteDetailAdapter extends RecyclerView.Adapter {

    private QHRoute strategyDetail;
    private Context context;
    public RouteDetailAdapter(QHRoute strategyDetail,Context context) {
        this.context = context;
        this.strategyDetail = strategyDetail;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_route_detail1,null);
        return new GuideHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        GuideHolder routeHolder = (GuideHolder) holder;
        String large_url = strategyDetail.route_info.get(position).getImage_url();
        String content = strategyDetail.route_info.get(position).getContent();

        if (!TextUtils.isEmpty(large_url)) {
//            RequestManager.getInstance().getImageLoader().get(large_url,
//                    ImageLoader.getImageListener(routeHolder.imageView,
//                            R.drawable.recommand_bg, R.drawable.bg_image_error));
            ImageUtil.DisplayImage(large_url, routeHolder.imageView,
                    R.drawable.recommand_bg, R.drawable.bg_image_error);
        }else {
            routeHolder.imageView.setVisibility(View.GONE);
        }
        routeHolder.textView.setText(content);

    }

    @Override
    public int getItemCount() {
        return strategyDetail.route_info.size();
    }


    private class GuideHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        public GuideHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.route_detail_image);
            textView = (TextView) view.findViewById(R.id.route_detail_tv);
        }
    }
}