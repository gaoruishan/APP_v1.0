/**
 * 
 */

package com.cmcc.hyapps.andyou.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author kuloud
 */
public class ActiviteResponse {
    @SerializedName("next_send_delay")
    public int nextSendDelay;

    public String detail;
}
