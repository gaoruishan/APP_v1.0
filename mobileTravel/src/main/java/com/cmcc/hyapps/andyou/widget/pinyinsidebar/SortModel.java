
package com.cmcc.hyapps.andyou.widget.pinyinsidebar;

import com.cmcc.hyapps.andyou.model.City;

public class SortModel {

    private City city;
    private String sortLetters;
    private String pinyin;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
        if (this.pinyin == null) {
            this.pinyin = sortLetters;
        }
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }
}
