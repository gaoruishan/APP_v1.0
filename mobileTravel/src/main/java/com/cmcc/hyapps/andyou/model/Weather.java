
package com.cmcc.hyapps.andyou.model;

import com.google.gson.annotations.SerializedName;
import com.cmcc.hyapps.andyou.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Weather {
    private static final Map<String, Integer> sIconMap = new HashMap<String, Integer>(20);
    static {
        sIconMap.put("01", R.drawable.ic_weather_fine);
        sIconMap.put("02", R.drawable.ic_weather_shade);
        sIconMap.put("03", R.drawable.ic_weather_cloudy);
        sIconMap.put("04", R.drawable.ic_weather_light_rain);
        sIconMap.put("05", R.drawable.ic_weather_heavy_rain);
        sIconMap.put("06", R.drawable.ic_weather_moderate_rain);
        sIconMap.put("07", R.drawable.ic_weather_ragged_rain);
        sIconMap.put("08", R.drawable.ic_weather_heavy_intensity_rain);
        sIconMap.put("09", R.drawable.ic_weather_very_heavy_rain);
        sIconMap.put("10", R.drawable.ic_weather_extreme_rain);
        sIconMap.put("11", R.drawable.ic_weather_thundershower);
        sIconMap.put("12", R.drawable.ic_weather_hailstorm);
        sIconMap.put("13", R.drawable.ic_weather_haze);
        sIconMap.put("14", R.drawable.ic_weather_fog);
        sIconMap.put("15", R.drawable.ic_weather_sand_storm);
        sIconMap.put("16", R.drawable.ic_weather_light_snow);
        sIconMap.put("17", R.drawable.ic_weather_moderate_snow);
        sIconMap.put("18", R.drawable.ic_weather_heavy_snow);
        sIconMap.put("19", R.drawable.ic_weather_snow_with_rain);
    }

    @SerializedName("city")
    public String city;

    @SerializedName("update_time")
    public String updateTime;

    @SerializedName("list")
    public List<WeatherDay> weatherDays;

    public static class WeatherDay {
        @SerializedName("weather")
        public String weather;

        @SerializedName("icon")
        public String iconIndex;

        @SerializedName("pm25")
        public String pm25;

        @SerializedName("temperature")
        public double temperature;

        @SerializedName("humidity")
        public String humidity;

        @SerializedName("temperature_max")
        public double temperatureMax;

        @SerializedName("temperature_min")
        public double temperatureMin;

        @SerializedName("wind")
        public String wind;

        @SerializedName("date")
        public String date;

        public int getIconRes() {
            if (sIconMap.containsKey(iconIndex)) {
                return sIconMap.get(iconIndex);
            }

            return -1;
        }

        @Override
        public String toString() {
            return "WeatherDay [weather=" + weather + ", icon=" + iconIndex + ", pm25=" + pm25
                    + ", temperature=" + temperature + ", humidity=" + humidity
                    + ", temperatureMax="
                    + temperatureMax + ", temperatureMin=" + temperatureMin + ", wind=" + wind
                    + "]";
        }
    }

}
