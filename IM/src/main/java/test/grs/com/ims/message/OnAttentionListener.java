package test.grs.com.ims.message;

import java.util.List;

import test.grs.com.ims.contact.SortModel;
import test.grs.com.ims.util.model.QHAttention;

public interface OnAttentionListener {
    public void getAttentionSuccess(List<QHAttention> list);
    public void getAttentionFail(String error);
}