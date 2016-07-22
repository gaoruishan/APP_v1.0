
package com.cmcc.hyapps.andyou.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuloud.android.widget.recyclerview.AppendableAdapter;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.model.Weather.WeatherDay;

import java.util.List;

public class WeatherListAdapter extends AppendableAdapter<WeatherDay> {

    public WeatherListAdapter(List<WeatherDay> dataItems) {
        mDataItems = dataItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather_list, parent, false);
        return new WeatherViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        WeatherViewHolder holder = (WeatherViewHolder) viewHolder;
        WeatherDay weatherDay = mDataItems.get(position);

        holder.itemView.setTag(weatherDay);

        holder.date.setText(weatherDay.date);
        holder.icon.setImageResource(weatherDay.getIconRes());
        holder.text.setText(weatherDay.weather + " "
                + holder.text.getContext().getString(
                        R.string.weather_temperature_range, (int) weatherDay.temperatureMin,
                        (int) weatherDay.temperatureMax));
    }

    static class WeatherViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView date;
        TextView text;

        public WeatherViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.weather_icon);
            date = (TextView) itemView.findViewById(R.id.weather_date);
            text = (TextView) itemView.findViewById(R.id.weather_text);
        }
    }

}
