
package com.cmcc.hyapps.andyou.model;

import android.os.Parcelable;

import java.util.ArrayList;

public interface BasicScenicData extends Parcelable {

    int id();

    String coverImage();

    String name();

    float rating();

    String ticketPrice();

    Location location();

    ArrayList<ScenicAudio> audioIntro();

}
