package test.grs.com.ims.message;

import java.util.List;

import test.grs.com.ims.contact.SortModel;

public interface ContactChangeListener {
    public void onContactChanged();
    public void onContactChanged(List<SortModel> list);
}