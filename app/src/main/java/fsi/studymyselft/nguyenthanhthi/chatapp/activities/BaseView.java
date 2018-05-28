package fsi.studymyselft.nguyenthanhthi.chatapp.activities;

import android.content.Context;

/**
 * Created by thanhthi on 23/05/2018.
 */

public interface BaseView {

    public void bindViews();

    public Context getContext();

    public void showProgress();

    public void hideProgress();

}
