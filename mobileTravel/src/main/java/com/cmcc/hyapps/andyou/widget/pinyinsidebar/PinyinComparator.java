
package com.cmcc.hyapps.andyou.widget.pinyinsidebar;

import java.util.Comparator;

public class PinyinComparator implements Comparator<SortModel> {

    @Override
    public int compare(SortModel o1, SortModel o2) {
        if (o1.getSortLetters().equals("@")
                || o2.getSortLetters().equals("#")) {
            return -1;
        } else if (o1.getSortLetters().equals("#")
                || o2.getSortLetters().equals("@")) {
            return 1;
        } else {
            return o1.getPinyin().compareTo(o2.getPinyin());
        }
    }
}
